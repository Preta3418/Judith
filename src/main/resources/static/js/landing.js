// Judith Landing Page

// ==================== Nav Scroll Effect ====================
const nav = document.querySelector('.landing-nav');
window.addEventListener('scroll', () => {
    nav.classList.toggle('scrolled', window.scrollY > 10);
});

// ==================== Smooth Scroll ====================
document.querySelectorAll('a[href^="#"]').forEach(link => {
    link.addEventListener('click', (e) => {
        const target = document.querySelector(link.getAttribute('href'));
        if (target) {
            e.preventDefault();
            target.scrollIntoView({ behavior: 'smooth', block: 'start' });
        }
    });
});

// ==================== Current Play ====================
async function loadCurrentPlay() {
    const container = document.getElementById('seasonContent');
    if (!container) return;

    try {
        const response = await fetch('/api/public/events/latest');

        if (!response.ok || response.status === 204) {
            container.innerHTML = '<p class="season-empty">공연 준비 중입니다</p>';
            return;
        }

        const event = await response.json();
        renderPlay(container, event);
    } catch (error) {
        container.innerHTML = '<p class="season-empty">공연 정보를 불러올 수 없습니다</p>';
    }
}

function renderPlay(container, event) {
    const isOpen = event.status === 'OPEN';
    const posterUrl = event.posterImageUrl || '/images/default-poster.png';
    const bookUrl = `/public/book.html?id=${event.id}`;

    const statusBadge = isOpen
        ? ''
        : `<span class="season-badge" style="background: rgba(255,255,255,0.15); margin-bottom: 0.5rem;">${getStatusLabel(event.status)}</span>`;

    const targetSchedule = getTargetSchedule(event.schedules || []);

    container.innerHTML = `
        <div class="play-card">
            <div class="play-poster-wrap">
                <img class="play-poster" src="${escapeAttr(posterUrl)}" alt="${escapeAttr(event.title)}"
                     onerror="this.src='/images/default-poster.png'">
            </div>
            <div class="play-info">
                ${statusBadge}
                <h3 class="season-name">${escapeHtml(event.title)}</h3>
                ${event.location ? `<p class="season-date">장소: ${escapeHtml(event.location)}</p>` : ''}
                ${targetSchedule ? `<p class="season-date">공연일: ${formatDate(targetSchedule.eventDate)}</p>` : ''}
                ${targetSchedule ? `<div class="countdown" id="playCountdown"></div>` : ''}
                <a href="${bookUrl}" class="btn-hero-primary" style="display:inline-block; margin-top:1.5rem;">
                    ${isOpen ? '예매하기' : '공연 정보 보기'}
                </a>
            </div>
        </div>
    `;

    if (targetSchedule) {
        startCountdown(targetSchedule.eventDate, 'playCountdown');
    }
}

function getTargetSchedule(schedules) {
    if (!schedules || schedules.length === 0) return null;
    const now = new Date();
    const upcoming = schedules
        .filter(s => new Date(s.eventDate) > now)
        .sort((a, b) => new Date(a.eventDate) - new Date(b.eventDate));
    if (upcoming.length > 0) return upcoming[0];
    return schedules.sort((a, b) => new Date(b.eventDate) - new Date(a.eventDate))[0];
}

function getStatusLabel(status) {
    const labels = { OPEN: '예매중', CLOSED: '예매종료', COMPLETED: '공연종료' };
    return labels[status] || status;
}

function formatDate(dateStr) {
    if (!dateStr) return '';
    return new Date(dateStr).toLocaleDateString('ko-KR', {
        year: 'numeric', month: 'long', day: 'numeric', weekday: 'short'
    });
}

function startCountdown(targetDateStr, elementId) {
    const countdownEl = document.getElementById(elementId);
    if (!countdownEl) return;

    function update() {
        const now = new Date();
        const target = new Date(targetDateStr);
        const diff = target - now;

        if (diff <= 0) {
            countdownEl.innerHTML = `
                <div class="countdown-box">
                    <div class="countdown-number" style="font-size: 1.5rem; color: var(--success);">NOW</div>
                    <div class="countdown-label">공연 중</div>
                </div>
            `;
            return;
        }

        const days = Math.floor(diff / (1000 * 60 * 60 * 24));
        const hours = Math.floor((diff / (1000 * 60 * 60)) % 24);
        const minutes = Math.floor((diff / (1000 * 60)) % 60);
        const seconds = Math.floor((diff / 1000) % 60);

        countdownEl.innerHTML = `
            <div class="countdown-box">
                <div class="countdown-number">${days}</div>
                <div class="countdown-label">일</div>
            </div>
            <div class="countdown-box">
                <div class="countdown-number">${hours}</div>
                <div class="countdown-label">시간</div>
            </div>
            <div class="countdown-box">
                <div class="countdown-number">${minutes}</div>
                <div class="countdown-label">분</div>
            </div>
            <div class="countdown-box">
                <div class="countdown-number">${seconds}</div>
                <div class="countdown-label">초</div>
            </div>
        `;
    }

    update();
    setInterval(update, 1000);
}

// ==================== Utility ====================
function escapeHtml(str) {
    if (!str) return '';
    const div = document.createElement('div');
    div.textContent = str;
    return div.innerHTML;
}

function escapeAttr(str) {
    if (!str) return '';
    return str.replace(/"/g, '&quot;').replace(/'/g, '&#39;');
}

// ==================== Load Stats ====================
async function loadStats() {
    try {
        const response = await fetch('/api/public/seasons/current');
        if (response.ok) {
            const season = await response.json();
            const statSeasons = document.getElementById('statSeasons');
            if (statSeasons) statSeasons.textContent = '1+';

            if (season.id) {
                try {
                    const usersResponse = await fetch(`/api/public/seasons/${season.id}/users`);
                    if (usersResponse.ok) {
                        const users = await usersResponse.json();
                        const statMembers = document.getElementById('statMembers');
                        if (statMembers) statMembers.textContent = users.length || '-';
                    }
                } catch (e) { /* ignore */ }
            }
        }
    } catch (e) { /* ignore */ }
}

// ==================== Init ====================
document.addEventListener('DOMContentLoaded', () => {
    loadCurrentPlay();
    loadStats();
});
