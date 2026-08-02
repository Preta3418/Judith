// Judith Dashboard — Shared utilities

// ==================== State ====================
let currentSeasonId = null;
let currentSeason = null;
let mySeasons = [];

// ==================== Bootstrap ====================
async function loadDashboard() {
    if (!requireAuth()) return;

    setupNav();
    setupAdminNav();

    try {
        mySeasons = await api('/api/dashboard/seasons');
        if (!mySeasons || mySeasons.length === 0) {
            const c = document.getElementById('pageContainer');
            if (isAdmin()) {
                if (c) c.innerHTML = '<div class="dash-empty"><div class="dash-empty-icon">&#x1F3AD;</div><p class="dash-empty-text">배정된 시즌이 없습니다.</p><a href="/seasons.html" class="btn btn-primary" style="margin-top:16px;">시즌 관리</a></div>';
            } else {
                if (c) c.innerHTML = '<div class="dash-empty"><div class="dash-empty-icon">&#x1F3AD;</div><p class="dash-empty-text">현재 배정된 시즌이 없습니다.<br>운영진에게 문의하세요.</p></div>';
            }
            return;
        }

        const urlParams = new URLSearchParams(window.location.search);
        const paramSeasonId = urlParams.get('seasonId');
        const hasActiveSeason = mySeasons.some(s => s.status === 'ACTIVE');
        const onSeasonsPage = window.location.pathname === '/dashboard/seasons.html';

        // Only redirect when no season is explicitly selected and there's no active season to default to.
        // Without the !paramSeasonId guard, clicking a past-season card loops: index?seasonId=X → redirect → seasons.html → repeat.
        if (!paramSeasonId && !hasActiveSeason && !onSeasonsPage) {
            window.location.href = '/dashboard/seasons.html';
            return;
        }

        currentSeason = (paramSeasonId && mySeasons.find(s => s.seasonId == paramSeasonId))
            || mySeasons.find(s => s.status === 'ACTIVE')
            || mySeasons[0];
        currentSeasonId = currentSeason.seasonId;
        sessionStorage.setItem('judithSeasonId', currentSeasonId);

        populateSeasonSelector();

        if (typeof loadPageContent === 'function') {
            loadPageContent();
        }
    } catch (e) {
        console.error(e);
        const c = document.getElementById('pageContainer');
        if (c) c.innerHTML = '<div class="alert alert-danger">데이터를 불러오는 데 실패했습니다.</div>';
    }
}

function setupNav() {
    const userName = getCurrentUserName();
    const el = id => document.getElementById(id);

    if (el('userName')) el('userName').textContent = userName;
    if (el('userAvatar')) el('userAvatar').textContent = userName.charAt(0).toUpperCase();

    checkPasswordChangeNeeded();
}

function setupAdminNav() {
    if (!isAdmin()) return;

    const tabs = document.querySelector('.dash-nav-tabs');
    if (!tabs) return;

    // Don't inject twice
    if (tabs.querySelector('.dash-tab-divider')) return;

    const divider = document.createElement('span');
    divider.className = 'dash-tab-divider';
    divider.textContent = '|';

    const adminLinks = [
        { href: '/users.html',    label: '부원' },
        { href: '/seasons.html',  label: '시즌' },
        { href: '/events.html',   label: '공연' },
        { href: '/messages.html', label: '문자' },
    ];

    tabs.appendChild(divider);

    adminLinks.forEach(({ href, label }) => {
        const a = document.createElement('a');
        a.href = href;
        a.className = 'dash-tab';
        a.textContent = label;
        tabs.appendChild(a);
    });
}

function populateSeasonSelector() {
    const btn = document.getElementById('seasonSelector');
    if (!btn) return;
    btn.textContent = currentSeason.seasonName;
    btn.onclick = () => { window.location.href = '/dashboard/seasons.html'; };
}

// ==================== Formatters ====================
const STATUS_LABEL = { PREPARING: '준비 중', ACTIVE: '진행 중', CLOSED: '종료' };
const STATUS_CLASS = { PREPARING: 'badge-warning', ACTIVE: 'badge-success', CLOSED: 'badge-secondary' };

function statusBadge(status) {
    return `<span class="badge ${STATUS_CLASS[status] || 'badge-secondary'}">${STATUS_LABEL[status] || status}</span>`;
}

function fmtDate(dateStr) {
    if (!dateStr) return '-';
    return new Date(dateStr).toLocaleDateString('ko-KR', { year: 'numeric', month: 'long', day: 'numeric' });
}

function fmtDateTime(dateStr) {
    if (!dateStr) return '-';
    return new Date(dateStr).toLocaleDateString('ko-KR', {
        year: 'numeric', month: 'short', day: 'numeric',
        hour: '2-digit', minute: '2-digit'
    });
}

function fmtBytes(bytes) {
    if (!bytes) return '';
    if (bytes < 1024) return bytes + ' B';
    if (bytes < 1048576) return (bytes / 1024).toFixed(1) + ' KB';
    return (bytes / 1048576).toFixed(1) + ' MB';
}

function escHtml(str) {
    const d = document.createElement('div');
    d.textContent = str ?? '';
    return d.innerHTML;
}

function dashboardUrl(page) {
    const url = new URL(`/dashboard/${page}`, window.location.origin);
    if (currentSeasonId) url.searchParams.set('seasonId', currentSeasonId);
    return url.toString();
}

// ==================== Calendar API ====================
const calendarApi = {
    getEvents: (from, to) => api(`/api/dashboard/calendar?from=${from}&to=${to}`),
    createEvent: (req) => api('/api/admin/calendar', { method: 'POST', body: JSON.stringify(req) }),
    updateEvent: (id, req) => api(`/api/admin/calendar/${id}`, { method: 'PUT', body: JSON.stringify(req) }),
    deleteEvent: (id) => api(`/api/admin/calendar/${id}`, { method: 'DELETE' }),
};

// ==================== Calendar Widgets (reusable) ====================

// Renders Google Calendar iframe into any container element
// Usage: await renderCalendarEmbed('myDivId')
async function renderCalendarEmbed(containerId) {
    const container = document.getElementById(containerId);
    if (!container) return;
    try {
        const data = await fetch('/api/public/calendar/embed').then(r => r.json());
        container.innerHTML = `
            <iframe src="${data.embedUrl}"
                style="border:0; width:100%; height:520px; border-radius:12px;"
                frameborder="0" scrolling="no">
            </iframe>`;
    } catch (e) {
        container.innerHTML = '<p style="color:var(--text-muted);font-size:0.875rem;">캘린더를 불러올 수 없습니다.</p>';
    }
}

// Renders today's events list into any container element
// Usage: await renderTodayEvents('myDivId')
async function renderTodayEvents(containerId) {
    const container = document.getElementById(containerId);
    if (!container) return;
    try {
        const today = new Date().toISOString().split('T')[0];
        const events = await calendarApi.getEvents(today, today);
        if (!events || events.length === 0) {
            container.innerHTML = '<p style="color:var(--text-muted);font-size:0.875rem;">오늘 일정이 없습니다.</p>';
            return;
        }
        container.innerHTML = events.map(e => `
            <div class="dash-cal-event">
                <div class="dash-cal-event-title">${escHtml(e.title)}</div>
                <div class="dash-cal-event-time">${fmtDateTime(e.start)} — ${fmtDateTime(e.end)}</div>
                ${e.description ? `<div class="dash-cal-event-desc">${escHtml(e.description)}</div>` : ''}
            </div>
        `).join('');
    } catch (e) {
        container.innerHTML = '<p style="color:var(--text-muted);font-size:0.875rem;">일정을 불러올 수 없습니다.</p>';
    }
}

// ==================== Init ====================
document.addEventListener('DOMContentLoaded', loadDashboard);
