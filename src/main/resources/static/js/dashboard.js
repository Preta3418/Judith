// Judith Dashboard — Shared utilities

// ==================== State ====================
let currentSeasonId = null;
let currentSeason = null;
let mySeasons = [];

// ==================== Bootstrap ====================
async function loadDashboard() {
    if (!requireAuth()) return;

    setupNav();

    try {
        mySeasons = await api('/api/dashboard/seasons');
        if (!mySeasons || mySeasons.length === 0) {
            document.getElementById('pageContainer').innerHTML =
                '<div class="dash-empty"><div class="dash-empty-icon">&#x1F3AD;</div><p class="dash-empty-text">현재 배정된 시즌이 없습니다.<br>운영진에게 문의하세요.</p></div>';
            return;
        }

        const urlParams = new URLSearchParams(window.location.search);
        const paramSeasonId = urlParams.get('seasonId');
        currentSeason = (paramSeasonId && mySeasons.find(s => s.seasonId == paramSeasonId)) || mySeasons[0];
        currentSeasonId = currentSeason.seasonId;

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

function populateSeasonSelector() {
    const selector = document.getElementById('seasonSelector');
    if (!selector) return;

    selector.innerHTML = mySeasons.map(s =>
        `<option value="${s.seasonId}" ${s.seasonId == currentSeasonId ? 'selected' : ''}>${escHtml(s.seasonName)}</option>`
    ).join('');

    selector.addEventListener('change', () => {
        const url = new URL(window.location.href);
        url.searchParams.set('seasonId', selector.value);
        window.location.href = url.toString();
    });
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

// ==================== Init ====================
document.addEventListener('DOMContentLoaded', loadDashboard);
