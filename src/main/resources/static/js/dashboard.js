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

        document.querySelectorAll('.dash-nav-tabs a[href^="/dashboard/"]').forEach(a => {
            try { const u = new URL(a.href); u.searchParams.set('seasonId', currentSeasonId); a.href = u.toString(); } catch(e) {}
        });

        populateSeasonSelector();
        loadNotifBadge();

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

    // Inject notification bell before the avatar
    const navRight = document.querySelector('.dash-nav-right');
    const avatar = el('userAvatar');
    if (navRight && avatar && !el('notifBell')) {
        const bell = document.createElement('button');
        bell.id = 'notifBell';
        bell.className = 'dash-notif-bell';
        bell.title = '공지사항';
        bell.innerHTML = `
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/>
                <path d="M13.73 21a2 2 0 0 1-3.46 0"/>
            </svg>
            <span id="notifBadge" class="dash-notif-badge" style="display:none;"></span>
        `;
        navRight.insertBefore(bell, avatar);
    }

    checkPasswordChangeNeeded();
}

async function loadNotifBadge() {
    const badge = document.getElementById('notifBadge');
    const bell = document.getElementById('notifBell');
    if (!badge || !bell || !currentSeasonId) return;

    // Set click target now that seasonId is known
    bell.onclick = () => { window.location.href = '/dashboard/notifications.html'; };

    try {
        const userId = getCurrentUserId();
        if (!userId) return;
        const count = await api(`/api/notifications/${userId}/unread/count`) || 0;
        if (count > 0) {
            badge.textContent = count > 99 ? '99+' : count;
            badge.style.display = 'flex';
        } else {
            badge.style.display = 'none';
        }
    } catch(e) {
        badge.style.display = 'none';
    }
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
        { href: '/events.html',   label: '공연 관리' },
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

// ==================== Board (departments config + helpers) ====================
// Single source of truth for department labels/icons/roles on the frontend.
// Must mirror backend Department enum — PLANNING is intentionally absent (Phase 4, admin tab).
const BOARD_DEPARTMENTS = [
    { key: 'STAGE_DESIGN', label: '무대 디자인', icon: '🎭', roles: ['STAGE_DESIGN'] },
    { key: 'SOUND_DESIGN', label: '음향 디자인', icon: '🔊', roles: ['SOUND_DESIGN', 'SOUND_OPERATOR'] },
    { key: 'PRINT_DESIGN', label: '인쇄/홍보',   icon: '🖨️', roles: ['IMAGE_DESIGN'] },
    { key: 'PROP_DESIGN',  label: '소품',        icon: '📦', roles: [] },  // empty = everyone can post
];

function getDeptConfig(key) {
    return BOARD_DEPARTMENTS.find(d => d.key === key) || null;
}

// Mirrors backend Department.canPost() — used only to show/hide buttons.
// The backend re-checks on every write; this is UX, not security.
function isImageFile(name) {
    if (!name) return false;
    const ext = name.split('.').pop().toLowerCase();
    return ['png','jpg','jpeg','gif','webp','svg','bmp'].includes(ext);
}

function canPostToDept(deptKey) {
    if (currentSeason && currentSeason.myFullAccess) return true;
    const dept = getDeptConfig(deptKey);
    if (!dept) return false;
    if (dept.roles.length === 0) return true;
    const myRoles = currentSeason && currentSeason.myRoles ? [...currentSeason.myRoles] : [];
    return myRoles.some(r => dept.roles.includes(r));
}

function boardApiBase(deptKey) {
    return `/api/board/seasons/${currentSeasonId}/${deptKey}`;
}

// Multipart helper — api() forces JSON content type, so board uploads need raw fetch.
// "data" part carries the JSON body, "files" carries the uploaded files.
async function boardMultipart(url, dataObj, files) {
    const formData = new FormData();
    formData.append('data', new Blob([JSON.stringify(dataObj)], { type: 'application/json' }));
    (files || []).forEach(f => formData.append('files', f));
    const response = await fetch(url, {
        method: 'POST',
        headers: { 'Authorization': `Bearer ${getToken()}` },
        body: formData
    });
    if (!response.ok) {
        const text = await response.text();
        console.error('boardMultipart failed', response.status, text);
        let msg = `요청에 실패했습니다 (${response.status})`;
        try {
            const parsed = JSON.parse(text);
            if (parsed.message) msg = parsed.message;
        } catch (e) {
            // Non-JSON body (Spring error HTML etc.) — show a snippet
            const snippet = text.replace(/<[^>]+>/g, '').trim().slice(0, 120);
            if (snippet) msg = `요청 실패 (${response.status}): ${snippet}`;
        }
        throw new Error(msg);
    }
    return response.json();
}

// Authenticated blob download — the board download proxy requires a JWT header,
// so a plain <a href> cannot be used (no way to attach Authorization).
async function boardDownload(deptKey, source, attachmentId, fileName) {
    const response = await fetch(`${boardApiBase(deptKey)}/download/${source}/${attachmentId}`, {
        headers: { 'Authorization': `Bearer ${getToken()}` }
    });
    if (!response.ok) { showToast('다운로드에 실패했습니다', 'error'); return; }
    const blob = await response.blob();
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = fileName || 'attachment';
    document.body.appendChild(a);
    a.click();
    a.remove();
    URL.revokeObjectURL(url);
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
async function renderCalendarEmbed(containerId, initialDate) {
    const container = document.getElementById(containerId);
    if (!container) return;
    try {
        const data = await fetch('/api/public/calendar/embed').then(r => r.json());
        let embedUrl = data.embedUrl;
        if (initialDate) embedUrl += `&date=${initialDate}`;
        container.innerHTML = `
            <iframe src="${embedUrl}"
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
