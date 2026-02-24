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
    const showDate = season.showStartDate;
    const endDate = season.showEndDate;

    const dateStr = showDate
        ? formatDateRange(showDate, endDate)
        : '';

    container.innerHTML = `
        <div class="season-card">
            <span class="season-badge">현재 시즌</span>
            <h3 class="season-name">${escapeHtml(season.title || season.name)}</h3>
            ${dateStr ? `<p class="season-date">${dateStr}</p>` : ''}
            ${showDate ? `<div class="countdown" id="countdown" data-target="${showDate}"></div>` : ''}
            <div class="season-actions">
                <a href="/public/book.html" class="btn btn-primary btn-lg">예매하기</a>
            </div>
        </div>
    `;

    if (showDate) {
        startCountdown(showDate);
    }
}

function formatDateRange(start, end) {
    const startDate = new Date(start);
    const options = { year: 'numeric', month: 'long', day: 'numeric' };
    let str = startDate.toLocaleDateString('ko-KR', options);
    if (end) {
        const endDate = new Date(end);
        str += ' ~ ' + endDate.toLocaleDateString('ko-KR', options);
    }
    return str;
}

function startCountdown(targetDateStr) {
    const countdownEl = document.getElementById('countdown');
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
    const div = document.createElement('div');
    div.textContent = str;
    return div.innerHTML;
}

// ==================== Init ====================
document.addEventListener('DOMContentLoaded', () => {
    loadCurrentSeason();
});
