// Judith Public Booking Page

let currentEvent = null;
let selectedScheduleId = null;

// Initialize on page load
document.addEventListener('DOMContentLoaded', () => {
    loadEvent();
    setupFormHandlers();
});

// Get event ID from URL
function getEventIdFromUrl() {
    const params = new URLSearchParams(window.location.search);
    return params.get('id');
}

// Load event data
async function loadEvent() {
    const eventId = getEventIdFromUrl();

    if (!eventId) {
        showError('공연 ID가 지정되지 않았습니다.');
        return;
    }

    try {
        const event = await eventApi.getById(eventId);
        currentEvent = event;
        displayEvent(event);
    } catch (error) {
        console.error('Error loading event:', error);
        showError('공연 정보를 불러오는데 실패했습니다.');
    }
}

// Display event data
function displayEvent(event) {
    // Hide loading, show content
    document.getElementById('loadingState').style.display = 'none';
    document.getElementById('eventContent').style.display = 'block';

    // Set poster and backdrop
    const posterUrl = event.posterImageUrl || '../images/default-poster.png';
    document.getElementById('eventPoster').src = posterUrl;
    document.getElementById('heroBackdrop').style.backgroundImage = `url(${posterUrl})`;

    // Set event info
    document.getElementById('eventTitle').textContent = event.title;
    document.getElementById('eventLocation').textContent = event.location || '장소 미정';
    document.getElementById('eventCapacity').textContent = event.capacityLimit || '-';
    document.getElementById('eventDescription').textContent = event.description || '공연 설명이 없습니다.';

    // Set status badge
    const statusBadge = document.getElementById('eventStatusBadge');
    const statusConfig = getStatusConfig(event.status);
    statusBadge.textContent = statusConfig.label;
    statusBadge.className = `event-status-badge ${statusConfig.class}`;

    // Render schedules
    renderSchedules(event.schedules || []);

    // Update page title
    document.title = `${event.title} - 예매 | Judith`;

    // Load casting from current season
    loadCasting();
}

// Load casting from current active season
async function loadCasting() {
    try {
        const season = await seasonApi.getCurrent();
        if (!season || !season.id) {
            return; // No active season, hide casting
        }

        const seasonUsers = await userSeasonApi.getUsersBySeason(season.id);
        if (seasonUsers && seasonUsers.length > 0) {
            displayCasting(seasonUsers);
        }
    } catch (error) {
        console.error('Error loading casting:', error);
        // Silently fail - casting is optional
    }
}

// Display casting grouped by role type
function displayCasting(seasonUsers) {
    const section = document.getElementById('castingSection');
    const container = document.getElementById('castingContent');

    // Group users by role categories
    const roleGroups = {
        production: { title: '제작진', roles: ['LEADER', 'PRODUCER', 'SUB_PRODUCER', 'PLANNER'], members: [] },
        cast: { title: '출연', roles: ['ACTOR'], members: [] },
        design: { title: '디자인', roles: ['SOUND_DESIGN', 'LIGHT_DESIGN', 'IMAGE_DESIGN', 'STAGE_DESIGN'], members: [] },
        tech: { title: '기술', roles: ['SOUND_OPERATOR', 'LIGHT_OPERATOR', 'STAFF'], members: [] }
    };

    // Sort users into groups
    seasonUsers.forEach(user => {
        user.roles.forEach(role => {
            for (const [key, group] of Object.entries(roleGroups)) {
                if (group.roles.includes(role)) {
                    // Avoid duplicate entries
                    if (!group.members.find(m => m.userId === user.userId && m.role === role)) {
                        group.members.push({
                            userId: user.userId,
                            name: user.userName,
                            role: role
                        });
                    }
                }
            }
        });
    });

    // Build HTML
    let html = '<div class="casting-grid">';

    for (const [key, group] of Object.entries(roleGroups)) {
        if (group.members.length === 0) continue;

        html += `
            <div class="casting-group">
                <h3 class="casting-group-title">${group.title}</h3>
                <div class="casting-members">
                    ${group.members.map(m => `
                        <div class="casting-member">
                            <span class="member-role">${getRoleLabelPublic(m.role)}</span>
                            <span class="member-name">${escapeHtml(m.name)}</span>
                        </div>
                    `).join('')}
                </div>
            </div>
        `;
    }

    html += '</div>';

    // Only show if there's content
    const hasAnyMembers = Object.values(roleGroups).some(g => g.members.length > 0);
    if (hasAnyMembers) {
        container.innerHTML = html;
        section.style.display = 'block';
    }
}

// Role labels for public display
function getRoleLabelPublic(role) {
    const labels = {
        LEADER: '학회장',
        PRODUCER: '연출',
        SUB_PRODUCER: '조연출',
        PLANNER: '기획',
        ACTOR: '배우',
        STAFF: '스태프',
        SOUND_OPERATOR: '음향 오퍼',
        LIGHT_OPERATOR: '조명 오퍼',
        SOUND_DESIGN: '음향',
        LIGHT_DESIGN: '조명',
        IMAGE_DESIGN: '인쇄',
        STAGE_DESIGN: '무대'
    };
    return labels[role] || role;
}

// Get status configuration
function getStatusConfig(status) {
    const configs = {
        'OPEN': { label: '예매중', class: 'open' },
        'CLOSED': { label: '예매종료', class: 'closed' },
        'SOLD_OUT': { label: '매진', class: 'sold-out' },
        'CANCELLED': { label: '취소됨', class: 'closed' }
    };
    return configs[status] || { label: status, class: '' };
}

// Render schedule list
function renderSchedules(schedules) {
    const container = document.getElementById('scheduleList');

    if (!schedules || schedules.length === 0) {
        container.innerHTML = `
            <div class="empty-schedules">
                <p style="color: #888;">등록된 회차가 없습니다.</p>
            </div>
        `;
        return;
    }

    container.innerHTML = schedules.map(schedule => {
        const isExpired = new Date(schedule.registrationDeadLine) < new Date();
        const remainingSeats = schedule.remainingSeats;
        const isSoldOut = remainingSeats === 0;
        const isDisabled = isExpired || isSoldOut;

        return `
            <div class="schedule-item ${isDisabled ? 'disabled' : ''}"
                 data-schedule-id="${schedule.eventScheduleId}"
                 onclick="${isDisabled ? '' : `selectSchedule(${schedule.eventScheduleId})`}">
                <div class="schedule-radio"></div>
                <div class="schedule-info">
                    <div class="schedule-date">${formatDateTime(schedule.eventDate)}</div>
                    <div class="schedule-deadline">
                        ${isExpired ? '예매 마감' : `마감: ${formatDateTime(schedule.registrationDeadLine)}`}
                    </div>
                </div>
                <div class="schedule-seats">
                    ${remainingSeats !== undefined ? `
                        <div class="seats-count ${remainingSeats < 10 ? (remainingSeats === 0 ? 'sold-out' : 'low') : ''}">
                            ${isSoldOut ? '매진' : `${remainingSeats}석 남음`}
                        </div>
                    ` : ''}
                </div>
            </div>
        `;
    }).join('');
}

// Select a schedule
function selectSchedule(scheduleId) {
    selectedScheduleId = scheduleId;

    // Update UI
    document.querySelectorAll('.schedule-item').forEach(item => {
        item.classList.remove('selected');
    });

    const selectedItem = document.querySelector(`[data-schedule-id="${scheduleId}"]`);
    if (selectedItem) {
        selectedItem.classList.add('selected');
    }
}

// Setup form handlers
function setupFormHandlers() {
    const form = document.getElementById('bookingForm');
    form.addEventListener('submit', handleBookingSubmit);

    // Phone number formatting
    const phoneInput = document.getElementById('guestPhone');
    phoneInput.addEventListener('input', (e) => {
        e.target.value = formatPhoneInput(e.target.value);
    });

    const lookupPhoneInput = document.getElementById('lookupPhone');
    lookupPhoneInput.addEventListener('input', (e) => {
        e.target.value = formatPhoneInput(e.target.value);
    });
}

// Format phone input
function formatPhoneInput(value) {
    const numbers = value.replace(/\D/g, '');
    if (numbers.length <= 3) return numbers;
    if (numbers.length <= 7) return `${numbers.slice(0, 3)}-${numbers.slice(3)}`;
    return `${numbers.slice(0, 3)}-${numbers.slice(3, 7)}-${numbers.slice(7, 11)}`;
}

// Handle booking form submission
async function handleBookingSubmit(e) {
    e.preventDefault();

    if (!selectedScheduleId) {
        showToast('회차를 선택해주세요.', 'error');
        return;
    }

    const name = document.getElementById('guestName').value.trim();
    const phoneNumber = document.getElementById('guestPhone').value.replace(/\D/g, '');
    const ticketCount = parseInt(document.getElementById('ticketCount').value);

    if (!name) {
        showToast('이름을 입력해주세요.', 'error');
        return;
    }

    if (phoneNumber.length < 10) {
        showToast('올바른 연락처를 입력해주세요.', 'error');
        return;
    }

    const submitBtn = document.getElementById('submitBtn');
    submitBtn.disabled = true;
    submitBtn.textContent = '예매 중...';

    try {
        const reservation = await reservationApi.create({
            eventScheduleId: selectedScheduleId,
            name: name,
            phoneNumber: phoneNumber,
            ticketCount: ticketCount
        });

        // Show success
        document.getElementById('successMessage').textContent =
            `${name}님의 예매가 완료되었습니다. (${ticketCount}매)`;
        openModal('successModal');

        // Reset form
        document.getElementById('bookingForm').reset();
        selectedScheduleId = null;
        document.querySelectorAll('.schedule-item').forEach(item => {
            item.classList.remove('selected');
        });

        // Reload event to update seat counts
        loadEvent();

    } catch (error) {
        console.error('Booking error:', error);
        showToast(error.message || '예매에 실패했습니다. 다시 시도해주세요.', 'error');
    } finally {
        submitBtn.disabled = false;
        submitBtn.textContent = '예매하기';
    }
}

// Show error state
function showError(message) {
    document.getElementById('loadingState').style.display = 'none';
    document.getElementById('errorState').style.display = 'flex';
    document.getElementById('errorMessage').textContent = message;
}

// My Reservations Modal
function openMyReservationsModal() {
    showLookupForm();
    openModal('myReservationsModal');
}

function showLookupForm() {
    document.getElementById('lookupForm').style.display = 'block';
    document.getElementById('reservationsList').style.display = 'none';
    document.getElementById('lookupPhone').value = '';
}

// Lookup reservations by phone
async function lookupReservations() {
    const phoneNumber = document.getElementById('lookupPhone').value.replace(/\D/g, '');

    if (phoneNumber.length < 10) {
        showToast('올바른 연락처를 입력해주세요.', 'error');
        return;
    }

    try {
        const reservations = await reservationApi.lookup(phoneNumber);
        displayReservations(reservations, phoneNumber);
    } catch (error) {
        console.error('Lookup error:', error);
        showToast('조회에 실패했습니다.', 'error');
    }
}

// Display reservations
function displayReservations(reservations, phoneNumber) {
    document.getElementById('lookupForm').style.display = 'none';
    document.getElementById('reservationsList').style.display = 'block';

    const container = document.getElementById('reservationsContent');

    if (!reservations || reservations.length === 0) {
        container.innerHTML = `
            <div class="empty-reservations">
                <p>예약 내역이 없습니다.</p>
            </div>
        `;
        return;
    }

    container.innerHTML = reservations.map(r => `
        <div class="reservation-card">
            <div class="reservation-card-header">
                <div class="reservation-event-name">${escapeHtml(r.eventName)}</div>
            </div>
            <div class="reservation-card-body">
                <dt>예매자</dt>
                <dd>${escapeHtml(r.name)}</dd>
                <dt>공연일시</dt>
                <dd>${formatDateTime(r.eventDate)}</dd>
                <dt>인원</dt>
                <dd>${r.ticketCount}명</dd>
                <dt>예매일</dt>
                <dd>${formatDateTime(r.reservedAt)}</dd>
            </div>
            <div class="reservation-card-footer">
                <button class="btn btn-ghost btn-sm" style="color: #ef4444;"
                        onclick="cancelReservation(${r.eventScheduleId}, '${phoneNumber}')">
                    예매 취소
                </button>
            </div>
        </div>
    `).join('');
}

// Cancel reservation
async function cancelReservation(eventScheduleId, phoneNumber) {
    if (!confirm('정말 예매를 취소하시겠습니까?')) {
        return;
    }

    try {
        await reservationApi.delete(eventScheduleId, phoneNumber);
        showToast('예매가 취소되었습니다.', 'success');

        // Refresh the list
        await lookupReservations();

        // Reload event to update seat counts
        loadEvent();
    } catch (error) {
        console.error('Cancel error:', error);
        showToast(error.message || '취소에 실패했습니다.', 'error');
    }
}

// Utility: Format date time
function formatDateTime(dateString) {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleDateString('ko-KR', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        weekday: 'short',
        hour: '2-digit',
        minute: '2-digit'
    });
}

// Utility: Escape HTML
function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// Modal functions (using existing api.js functions)
function openModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.classList.add('active');
    }
}

function closeModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.classList.remove('active');
    }
}

// Close modal on overlay click
document.addEventListener('click', (e) => {
    if (e.target.classList.contains('modal-overlay')) {
        e.target.classList.remove('active');
    }
});
