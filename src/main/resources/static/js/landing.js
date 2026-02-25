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

// ==================== Season Countdown ====================
async function loadCurrentSeason() {
    const container = document.getElementById('seasonContent');
    if (!container) return;

    try {
        const response = await fetch('/api/public/seasons/current');
        if (!response.ok) {
            container.innerHTML = '<p class="season-empty">현재 진행 중인 시즌이 없습니다</p>';
            return;
        }

        const season = await response.json();
        renderSeason(container, season);
    } catch (error) {
        container.innerHTML = '<p class="season-empty">시즌 정보를 불러올 수 없습니다</p>';
    }
}

function renderSeason(container, season) {
    const eventDate = season.eventDate;

    const dateStr = eventDate
        ? new Date(eventDate).toLocaleDateString('ko-KR', { year: 'numeric', month: 'long', day: 'numeric' })
        : '';

    container.innerHTML = `
        <div class="season-card">
            <span class="season-badge">현재 시즌</span>
            <h3 class="season-name">${escapeHtml(season.name)}</h3>
            ${dateStr ? `<p class="season-date">공연일: ${dateStr}</p>` : ''}
            ${eventDate ? `<div class="countdown" id="countdown" data-target="${eventDate}"></div>` : ''}
        </div>
    `;

    if (eventDate) {
        startCountdown(eventDate);
    }
}

function startCountdown(targetDateStr) {
    const countdownEl = document.getElementById('countdown');
    if (!countdownEl) return;

    function update() {
        const now = new Date();
        const target = new Date(targetDateStr + 'T00:00:00');
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
    const div = document.createElement('div');
    div.textContent = str;
    return div.innerHTML;
}

// ==================== Load Stats ====================
async function loadStats() {
    // These are public-facing stats — use public endpoints only
    try {
        const response = await fetch('/api/public/seasons/current');
        if (response.ok) {
            const season = await response.json();
            // If a season exists, we know there's at least 1
            const statSeasons = document.getElementById('statSeasons');
            if (statSeasons) statSeasons.textContent = '1+';

            // Try to load member count from season users
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
    loadCurrentSeason();
    loadStats();
});
