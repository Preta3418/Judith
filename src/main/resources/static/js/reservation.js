// Judith Management System - Reservation Page Logic

let allEvents = [];
let currentEvent = null;
let currentSchedule = null;
let currentSeason = null;  // Track active season - required for uploads

// ==================== Initialize ====================
async function initReservationPage() {
    // Load current season first - required for admin features
    await loadCurrentSeasonForReservation();

    // Setup drag and drop for upload areas
    setupDragDrop('createUploadArea', 'create');
    setupDragDrop('editUploadArea', 'edit');

    await loadEvents();
}

// Load current active season
async function loadCurrentSeasonForReservation() {
    try {
        currentSeason = await seasonApi.getCurrent();
        if (!currentSeason || !currentSeason.id) {
            currentSeason = null;
        }
    } catch (error) {
        console.log('No active season');
        currentSeason = null;
    }
}

// ==================== File Upload ====================
function setupDragDrop(areaId, prefix) {
    const area = document.getElementById(areaId);
    if (!area) return;

    ['dragenter', 'dragover', 'dragleave', 'drop'].forEach(eventName => {
        area.addEventListener(eventName, (e) => {
            e.preventDefault();
            e.stopPropagation();
        });
    });

    ['dragenter', 'dragover'].forEach(eventName => {
        area.addEventListener(eventName, () => area.classList.add('dragover'));
    });

    ['dragleave', 'drop'].forEach(eventName => {
        area.addEventListener(eventName, () => area.classList.remove('dragover'));
    });

    area.addEventListener('drop', (e) => {
        const files = e.dataTransfer.files;
        if (files.length > 0) {
            const fileInput = document.getElementById(`${prefix}PosterFile`);
            fileInput.files = files;
            handleFileSelect(fileInput, prefix);
        }
    });
}

async function handleFileSelect(input, prefix) {
    const file = input.files[0];
    if (!file) return;

    // Check if season exists
    if (!currentSeason || !currentSeason.id) {
        showToast('활성화된 학기이 없습니다. 먼저 학기을 활성화해주세요.', 'error');
        return;
    }

    // Validate file type
    if (!file.type.startsWith('image/')) {
        showToast('이미지 파일만 업로드 가능합니다', 'error');
        return;
    }

    // Validate file size (max 100MB)
    if (file.size > 100 * 1024 * 1024) {
        showToast('파일 크기는 100MB 이하여야 합니다', 'error');
        return;
    }

    const placeholder = document.getElementById(`${prefix}UploadPlaceholder`);
    const preview = document.getElementById(`${prefix}UploadPreview`);
    const previewImg = document.getElementById(`${prefix}PreviewImg`);
    const hiddenInput = document.getElementById(`${prefix}EventPoster`);

    // Show loading state
    placeholder.innerHTML = '<div class="upload-loading"><div class="spinner"></div><p>업로드 중...</p></div>';

    try {
        // Upload to server with seasonId
        const imageUrl = await uploadPoster(file, currentSeason.id);

        // Show preview
        previewImg.src = imageUrl;
        hiddenInput.value = imageUrl;
        placeholder.style.display = 'none';
        preview.style.display = 'block';

        // Restore placeholder content
        placeholder.innerHTML = '<div class="upload-icon">📁</div><p>클릭하거나 이미지를 드래그하세요</p>';

        showToast('이미지가 업로드되었습니다', 'success');
    } catch (error) {
        // Restore placeholder
        placeholder.innerHTML = '<div class="upload-icon">📁</div><p>클릭하거나 이미지를 드래그하세요</p>';
        showToast('업로드 실패: ' + error.message, 'error');
    }
}

function clearUpload(prefix, event) {
    event.stopPropagation();

    const placeholder = document.getElementById(`${prefix}UploadPlaceholder`);
    const preview = document.getElementById(`${prefix}UploadPreview`);
    const hiddenInput = document.getElementById(`${prefix}EventPoster`);
    const fileInput = document.getElementById(`${prefix}PosterFile`);

    placeholder.style.display = 'block';
    preview.style.display = 'none';
    hiddenInput.value = '';
    fileInput.value = '';
}

function showExistingPoster(prefix, imageUrl) {
    if (!imageUrl) return;

    const placeholder = document.getElementById(`${prefix}UploadPlaceholder`);
    const preview = document.getElementById(`${prefix}UploadPreview`);
    const previewImg = document.getElementById(`${prefix}PreviewImg`);
    const hiddenInput = document.getElementById(`${prefix}EventPoster`);

    previewImg.src = imageUrl;
    hiddenInput.value = imageUrl;
    placeholder.style.display = 'none';
    preview.style.display = 'block';
}

// ==================== Load Events ====================
async function loadEvents() {
    showLoading('eventList');

    try {
        allEvents = await eventApi.getAll();
        renderEvents();
    } catch (error) {
        document.getElementById('eventList').innerHTML = `
            <div class="empty-state" style="grid-column: 1 / -1;">
                <div class="icon">&#x274C;</div>
                <p>공연 목록을 불러오지 못했습니다</p>
                <button class="btn btn-primary btn-sm" onclick="loadEvents()">다시 시도</button>
            </div>
        `;
        showToast('공연 목록 로딩 실패', 'error');
    }
}

// ==================== Render Events ====================
function renderEvents() {
    const container = document.getElementById('eventList');
    const admin = isAdmin();

    if (allEvents.length === 0) {
        container.innerHTML = `
            <div class="empty-state" style="grid-column: 1 / -1;">
                <div class="icon">&#x1F3AD;</div>
                <p>공연이 없습니다</p>
                ${admin ? '<button class="btn btn-primary btn-sm" onclick="openCreateEventModal()">공연 추가</button>' : ''}
            </div>
        `;
        return;
    }

    container.innerHTML = allEvents.map(event => {
        const statusBadge = event.status === 'OPEN'
            ? '<span class="badge badge-success">예매중</span>'
            : '<span class="badge badge-secondary">마감</span>';
        const clickHandler = admin ? `editEvent(${event.id})` : `viewEvent(${event.id})`;

        return `
            <div class="card" onclick="${clickHandler}" style="cursor: pointer;">
                ${event.posterImageUrl ? `<img src="${event.posterImageUrl}" style="width:100%; border-radius: 8px; margin-bottom: 12px;">` : ''}
                <div class="card-header">
                    <span class="card-title">${escapeHtml(event.title)}</span>
                    ${statusBadge}
                </div>
            </div>
        `;
    }).join('');
}

// ==================== View Event (User) ====================
async function viewEvent(eventId) {
    try {
        currentEvent = await eventApi.getById(eventId);

        document.getElementById('eventTitle').textContent = currentEvent.title;
        document.getElementById('eventDescription').textContent = currentEvent.description;
        document.getElementById('eventLocation').textContent = currentEvent.location;
        document.getElementById('eventCapacity').textContent = currentEvent.capacityLimit + '명';

        // Render schedules
        const schedulesContainer = document.getElementById('eventSchedules');
        if (currentEvent.schedules && currentEvent.schedules.length > 0) {
            schedulesContainer.innerHTML = currentEvent.schedules.map(schedule => `
                <div class="list-item" onclick="selectSchedule(${schedule.eventScheduleId})" style="cursor: pointer;">
                    <div class="list-item-content">
                        <div class="list-item-title">${formatDate(schedule.eventDate)}</div>
                        <div class="list-item-subtitle">예매 마감: ${formatDate(schedule.registrationDeadLine)}</div>
                    </div>
                    <span>→</span>
                </div>
            `).join('');
        } else {
            schedulesContainer.innerHTML = '<p class="text-center" style="padding: 16px; color: var(--text-light);">공연 일정이 없습니다</p>';
        }

        openModal('eventDetailModal');
    } catch (error) {
        showToast('연극 정보를 불러오지 못했습니다', 'error');
    }
}

// ==================== Select Schedule for Reservation ====================
function selectSchedule(scheduleId) {
    currentSchedule = currentEvent.schedules.find(s => s.eventScheduleId === scheduleId);

    document.getElementById('reserveEventName').textContent = currentEvent.title;
    document.getElementById('reserveScheduleDate').textContent = formatDate(currentSchedule.eventDate);

    closeModal('eventDetailModal');
    openModal('reservationModal');
}

// ==================== Create Reservation ====================
async function createReservation(e) {
    e.preventDefault();

    const reservation = {
        eventScheduleId: currentSchedule.eventScheduleId,
        name: document.getElementById('reserveName').value.trim(),
        phoneNumber: document.getElementById('reservePhone').value.trim().replace(/-/g, ''),
        ticketCount: parseInt(document.getElementById('reserveTickets').value)
    };

    try {
        await reservationApi.create(reservation);
        showToast('예매가 완료되었습니다!', 'success');
        closeModal('reservationModal');
        document.getElementById('reservationForm').reset();
    } catch (error) {
        showToast('예매 실패: ' + error.message, 'error');
    }
}

// ==================== Admin: Edit Event ====================
async function editEvent(eventId) {
    try {
        currentEvent = await eventApi.getById(eventId);

        document.getElementById('editEventId').value = currentEvent.id;
        document.getElementById('editEventTitle').value = currentEvent.title;
        document.getElementById('editEventDescription').value = currentEvent.description;
        document.getElementById('editEventLocation').value = currentEvent.location;
        document.getElementById('editEventCapacity').value = currentEvent.capacityLimit;
        document.getElementById('editEventStatus').value = currentEvent.status;

        // Show existing poster or reset upload area
        if (currentEvent.posterImageUrl) {
            showExistingPoster('edit', currentEvent.posterImageUrl);
        } else {
            clearUpload('edit', { stopPropagation: () => {} });
        }

        // Load schedules
        renderEditSchedules();

        // Load casting from current season
        loadEditCasting();

        // Set public URL
        const publicUrl = `${window.location.origin}/public/book.html?id=${currentEvent.id}`;
        document.getElementById('publicEventUrl').value = publicUrl;

        // Show pamphlet if exists
        const pamphletWrap = document.getElementById('pamphletCurrentWrap');
        if (currentEvent.pamphletUrl) {
            document.getElementById('pamphletCurrentLink').href = currentEvent.pamphletUrl;
            document.getElementById('pamphletViewLink').href = `/public/pamphlet.html?id=${currentEvent.id}`;
            pamphletWrap.style.display = 'block';
        } else {
            pamphletWrap.style.display = 'none';
        }
        document.getElementById('pamphletUploadStatus').textContent = '';

        openModal('editEventModal');
    } catch (error) {
        showToast('연극 정보를 불러오지 못했습니다', 'error');
    }
}

// Load casting for edit modal
async function loadEditCasting() {
    const section = document.getElementById('editCastingSection');
    const container = document.getElementById('editCastingContent');

    if (!currentSeason || !currentSeason.id) {
        section.style.display = 'none';
        return;
    }

    try {
        const seasonUsers = await userSeasonApi.getUsersBySeason(currentSeason.id);

        if (!seasonUsers || seasonUsers.length === 0) {
            container.innerHTML = '<p style="color: var(--text-muted);">이 학기에 배정된 부원이 없습니다.</p>';
            section.style.display = 'block';
            return;
        }

        // Group by role type
        const groups = {
            production: { title: '제작진', roles: ['LEADER', 'PRODUCER', 'SUB_PRODUCER', 'PLANNER'], members: [] },
            cast: { title: '출연', roles: ['ACTOR'], members: [] },
            design: { title: '디자인', roles: ['SOUND_DESIGN', 'LIGHT_DESIGN', 'IMAGE_DESIGN', 'STAGE_DESIGN'], members: [] },
            tech: { title: '기술', roles: ['SOUND_OPERATOR', 'LIGHT_OPERATOR', 'STAFF'], members: [] }
        };

        seasonUsers.forEach(user => {
            user.roles.forEach(role => {
                for (const group of Object.values(groups)) {
                    if (group.roles.includes(role) && !group.members.find(m => m.userId === user.userId && m.role === role)) {
                        group.members.push({ userId: user.userId, name: user.userName, role: role });
                    }
                }
            });
        });

        let html = '<div class="casting-admin-grid">';
        for (const group of Object.values(groups)) {
            if (group.members.length === 0) continue;
            html += `
                <div class="casting-admin-group">
                    <div class="casting-admin-title">${group.title}</div>
                    <div class="casting-admin-list">
                        ${group.members.map(m => `
                            <span class="casting-admin-member">
                                <span class="role-tag ${isFullAccessRole(m.role) ? 'full-access' : ''}">${getRoleLabel(m.role)}</span>
                                ${escapeHtml(m.name)}
                            </span>
                        `).join('')}
                    </div>
                </div>
            `;
        }
        html += '</div>';

        container.innerHTML = html;
        section.style.display = 'block';
    } catch (error) {
        console.error('Error loading casting:', error);
        section.style.display = 'none';
    }
}

function renderEditSchedules() {
    const container = document.getElementById('editEventSchedules');
    if (!currentEvent.schedules || currentEvent.schedules.length === 0) {
        container.innerHTML = '<p style="color: var(--text-light); padding: 16px;">일정이 없습니다</p>';
        return;
    }

    container.innerHTML = currentEvent.schedules.map(schedule => `
        <div class="list-item">
            <div class="list-item-content">
                <div class="list-item-title">${formatDate(schedule.eventDate)}</div>
                <div class="list-item-subtitle">마감: ${formatDate(schedule.registrationDeadLine)}</div>
            </div>
            <button class="btn btn-sm btn-outline" onclick="viewScheduleReservations(${schedule.eventScheduleId})">
                보기
            </button>
        </div>
    `).join('');
}

// ==================== Admin: Open Create Event Modal ====================
function openCreateEventModal() {
    if (!currentSeason || !currentSeason.id) {
        showToast('활성화된 학기이 없습니다. 먼저 학기을 활성화해주세요.', 'error');
        return;
    }
    openModal('createEventModal');
}

// ==================== Admin: Create Event ====================
async function submitCreateEvent() {
    // Double check season exists
    if (!currentSeason || !currentSeason.id) {
        showToast('활성화된 학기이 없습니다. 먼저 학기을 활성화해주세요.', 'error');
        return;
    }

    const title = document.getElementById('createEventTitle').value.trim();
    const description = document.getElementById('createEventDescription').value.trim();
    const location = document.getElementById('createEventLocation').value.trim();
    const capacityValue = document.getElementById('createEventCapacity').value;

    // Validation
    if (!title || !description || !location || !capacityValue) {
        showToast('모든 필수 항목을 입력해주세요', 'error');
        return;
    }

    const event = {
        title: title,
        description: description,
        location: location,
        capacityLimit: parseInt(capacityValue),
        status: document.getElementById('createEventStatus').value,
        posterImageUrl: document.getElementById('createEventPoster').value.trim() || null
    };

    try {
        await eventApi.create(event);
        showToast('연극이 추가되었습니다', 'success');
        closeModal('createEventModal');
        document.getElementById('createEventForm').reset();
        clearUpload('create', { stopPropagation: () => {} });
        await loadEvents();
    } catch (error) {
        showToast('연극 추가 실패: ' + error.message, 'error');
    }
}

// ==================== Admin: Update Event ====================
async function updateEvent(e) {
    e.preventDefault();

    const eventId = document.getElementById('editEventId').value;
    const event = {
        title: document.getElementById('editEventTitle').value.trim(),
        description: document.getElementById('editEventDescription').value.trim(),
        location: document.getElementById('editEventLocation').value.trim(),
        capacityLimit: parseInt(document.getElementById('editEventCapacity').value),
        status: document.getElementById('editEventStatus').value,
        posterImageUrl: document.getElementById('editEventPoster').value.trim() || null
    };

    try {
        await eventApi.update(eventId, event);
        showToast('연극 정보가 수정되었습니다', 'success');
        closeModal('editEventModal');
        await loadEvents();
    } catch (error) {
        showToast('수정 실패: ' + error.message, 'error');
    }
}

// ==================== Admin: Delete Event ====================
async function deleteEvent() {
    const eventId = document.getElementById('editEventId').value;

    if (!confirm('이 연극을 삭제하시겠습니까? 되돌릴 수 없습니다.')) {
        return;
    }

    try {
        await eventApi.delete(eventId);
        showToast('연극이 삭제되었습니다', 'success');
        closeModal('editEventModal');
        await loadEvents();
    } catch (error) {
        showToast('삭제 실패: ' + error.message, 'error');
    }
}

// ==================== Admin: Create Schedule ====================
async function createSchedule(e) {
    e.preventDefault();

    const schedule = {
        eventId: parseInt(document.getElementById('scheduleEventId').value),
        eventDate: document.getElementById('scheduleDate').value,
        registrationDeadLine: document.getElementById('scheduleDeadline').value
    };

    try {
        await scheduleApi.create(schedule);
        showToast('공연 일정이 추가되었습니다', 'success');
        closeModal('createScheduleModal');
        document.getElementById('createScheduleForm').reset();

        // Refresh event details
        if (currentEvent) {
            await editEvent(currentEvent.id);
        }
    } catch (error) {
        showToast('일정 추가 실패: ' + error.message, 'error');
    }
}

// ==================== Admin: View Schedule Reservations ====================
async function viewScheduleReservations(scheduleId) {
    try {
        const reservations = await scheduleApi.getReservations(scheduleId);

        const container = document.getElementById('scheduleReservations');
        if (reservations.length === 0) {
            container.innerHTML = '<p class="text-center" style="padding: 16px;">예매 내역이 없습니다</p>';
        } else {
            container.innerHTML = reservations.map(r => `
                <div class="list-item">
                    <div class="list-item-content">
                        <div class="list-item-title">${escapeHtml(r.name)}</div>
                        <div class="list-item-subtitle">${formatPhone(r.phoneNumber)} · ${r.ticketCount}명</div>
                    </div>
                </div>
            `).join('');
        }

        document.getElementById('reservationsModalTitle').textContent = `예매 목록 (${reservations.length}건)`;
        openModal('reservationsModal');
    } catch (error) {
        showToast('예매 목록을 불러오지 못했습니다', 'error');
    }
}

// ==================== Open Add Schedule Modal ====================
function openAddScheduleModal() {
    document.getElementById('scheduleEventId').value = currentEvent.id;
    closeModal('editEventModal');
    openModal('createScheduleModal');
}

// ==================== Pamphlet Upload ====================
async function uploadPamphlet() {
    const file = document.getElementById('pamphletFile').files[0];
    if (!file) return;

    const eventId = document.getElementById('editEventId').value;
    const status = document.getElementById('pamphletUploadStatus');
    const btn = document.getElementById('pamphletUploadBtn');

    status.textContent = '업로드 중...';
    btn.disabled = true;

    try {
        const formData = new FormData();
        formData.append('file', file);

        const resp = await fetch(`/api/admin/events/${eventId}/pamphlet`, {
            method: 'POST',
            headers: { 'Authorization': `Bearer ${getToken()}` },
            body: formData
        });

        if (!resp.ok) throw new Error('업로드 실패');

        const event = await resp.json();
        currentEvent = event;

        document.getElementById('pamphletCurrentLink').href = event.pamphletUrl;
        document.getElementById('pamphletViewLink').href = `/public/pamphlet.html?id=${event.id}`;
        document.getElementById('pamphletCurrentWrap').style.display = 'block';
        status.textContent = '업로드 완료';
        showToast('팸플릿이 업로드되었습니다', 'success');
    } catch (e) {
        status.textContent = '업로드 실패';
        showToast('팸플릿 업로드에 실패했습니다', 'error');
    } finally {
        btn.disabled = false;
        document.getElementById('pamphletFile').value = '';
    }
}

// ==================== Helper ====================
function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function copyPublicUrl() {
    const urlInput = document.getElementById('publicEventUrl');
    urlInput.select();
    navigator.clipboard.writeText(urlInput.value)
        .then(() => showToast('링크가 복사되었습니다', 'success'))
        .catch(() => showToast('복사 실패', 'error'));
}

// Initialize on load
document.addEventListener('DOMContentLoaded', initReservationPage);
