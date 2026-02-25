// Judith Management System - User Page Logic

let currentFilter = 'all';
let allUsers = [];

// ==================== Initialize ====================
async function initUserPage() {
    // Check for URL params
    const params = new URLSearchParams(window.location.search);
    if (params.get('tab') === 'create' && isAdmin()) {
        openModal('createUserModal');
    }

    await loadUsers();
}

// ==================== Load Users ====================
async function loadUsers() {
    showLoading('userList');

    try {
        allUsers = await userApi.getAll();
        renderUsers();
    } catch (error) {
        document.getElementById('userList').innerHTML = `
            <div class="empty-state">
                <div class="icon">❌</div>
                <p>학회원 목록을 불러오지 못했습니다</p>
                <button class="btn btn-primary btn-sm" onclick="loadUsers()">다시 시도</button>
            </div>
        `;
        showToast('학회원 목록 로딩 실패', 'error');
    }
}

// ==================== Filter Users ====================
function filterUsers(filter) {
    currentFilter = filter;

    // Update tab UI
    document.querySelectorAll('.tab').forEach(tab => {
        tab.classList.toggle('active', tab.dataset.filter === filter);
    });

    renderUsers();
}

// ==================== Role Name Mapping ====================
const roleNames = {
    'ADMIN': '관리자',
    'LEADER': '회장',
    'PRODUCER': '연출',
    'PLANNER': '기획',
    'ACTOR': '배우',
    'STAFF': '스태프',
    'EXTERNAL': '외부'
};

function getRoleName(role) {
    return roleNames[role] || role;
}

// ==================== Render Users ====================
function renderUsers() {
    const container = document.getElementById('userList');

    let filtered = allUsers;
    if (currentFilter === 'active') {
        filtered = allUsers.filter(u => u.status === 'ACTIVE');
    } else if (currentFilter === 'inactive') {
        filtered = allUsers.filter(u => u.status === 'INACTIVE');
    }

    if (filtered.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <div class="icon">👥</div>
                <p>학회원이 없습니다</p>
            </div>
        `;
        return;
    }

    container.innerHTML = filtered.map(user => {
        const roleName = getRoleName(user.role);
        const statusName = user.status === 'ACTIVE' ? '현역' : '비활동';
        return `
            <div class="list-item">
                <div class="list-item-content">
                    <div class="list-item-title">${escapeHtml(user.name)}</div>
                    <div class="list-item-subtitle">
                        ${escapeHtml(user.studentNumber || '')} · ${formatPhone(user.phoneNumber)}
                    </div>
                </div>
                <div class="list-item-actions">
                    <span class="badge ${user.status === 'ACTIVE' ? 'badge-success' : 'badge-secondary'}">
                        ${statusName}
                    </span>
                    <span class="badge badge-primary">${roleName}</span>
                </div>
            </div>
        `;
    }).join('');

    // Add click handlers for admin
    if (isAdmin()) {
        container.querySelectorAll('.list-item').forEach((item, index) => {
            item.style.cursor = 'pointer';
            item.onclick = () => showUserDetail(filtered[index]);
        });
    }
}

// ==================== Show User Detail ====================
function showUserDetail(user) {
    const roleName = getRoleName(user.role);
    const statusName = user.status === 'ACTIVE' ? '현역' : '비활동';

    document.getElementById('detailName').textContent = user.name;
    document.getElementById('detailStudentNumber').textContent = user.studentNumber || '-';
    document.getElementById('detailPhone').textContent = formatPhone(user.phoneNumber);
    document.getElementById('detailRole').textContent = roleName;
    document.getElementById('detailStatus').textContent = statusName;
    document.getElementById('detailCreatedAt').textContent = formatDate(user.createdAt);

    // Store user ID for actions
    document.getElementById('userDetailModal').dataset.userId = user.id;

    // Show/hide deactivate button
    const deactivateBtn = document.getElementById('deactivateBtn');
    if (deactivateBtn) {
        deactivateBtn.style.display = user.status === 'ACTIVE' ? 'block' : 'none';
    }

    // Pre-fill edit form
    document.getElementById('editName').value = user.name;
    document.getElementById('editPhone').value = user.phoneNumber || '';
    document.getElementById('editRole').value = user.role;

    openModal('userDetailModal');
}

// ==================== Create User ====================
async function createUser(e) {
    e.preventDefault();

    const userData = {
        name: document.getElementById('createName').value.trim(),
        studentNumber: document.getElementById('createStudentNumber').value.trim(),
        phoneNumber: document.getElementById('createPhone').value.trim().replace(/-/g, '')
    };

    try {
        // Check for existing inactive user with same identifiers
        const matchingInactive = await findMatchingInactive(userData);

        if (matchingInactive) {
            // Show reactivation prompt
            const reactivate = confirm(
                `동일한 정보의 비활동 부원이 존재합니다.\n\n` +
                `이름: ${matchingInactive.name}\n` +
                `학번: ${matchingInactive.studentNumber || '-'}\n` +
                `전화번호: ${formatPhone(matchingInactive.phoneNumber)}\n\n` +
                `이 부원을 재활성화 하시겠습니까?`
            );

            if (reactivate) {
                await userApi.reactivate(matchingInactive.id);
                showToast('부원이 재활성화되었습니다', 'success');
                closeModal('createUserModal');
                document.getElementById('createUserForm').reset();
                await loadUsers();
                return;
            } else {
                // User chose not to reactivate, don't create duplicate
                showToast('중복된 정보로 새 계정을 생성할 수 없습니다', 'error');
                return;
            }
        }

        // No matching inactive user, proceed with creation
        await userApi.create(userData);
        showToast('학회원이 추가되었습니다', 'success');
        closeModal('createUserModal');
        document.getElementById('createUserForm').reset();
        await loadUsers();
    } catch (error) {
        showToast('학회원 추가 실패: ' + error.message, 'error');
    }
}

// Find matching inactive user by name, studentNumber, or phoneNumber
async function findMatchingInactive(userData) {
    try {
        const inactiveUsers = await userApi.getInactive();
        return inactiveUsers.find(u => {
            // Match by phone number (most unique)
            if (userData.phoneNumber && u.phoneNumber === userData.phoneNumber) {
                return true;
            }
            // Match by student number (also unique)
            if (userData.studentNumber && u.studentNumber === userData.studentNumber) {
                return true;
            }
            // Match by name + partial phone
            if (userData.name === u.name) {
                return true;
            }
            return false;
        });
    } catch (error) {
        console.error('Error checking for inactive users:', error);
        return null;
    }
}

// ==================== Update User ====================
async function updateUser(e) {
    e.preventDefault();

    const userId = document.getElementById('userDetailModal').dataset.userId;
    const updates = {
        name: document.getElementById('editName').value.trim(),
        phoneNumber: document.getElementById('editPhone').value.trim().replace(/-/g, ''),
        role: document.getElementById('editRole').value
    };

    try {
        await userApi.update(userId, updates);
        showToast('정보가 수정되었습니다', 'success');
        closeModal('userDetailModal');
        await loadUsers();
    } catch (error) {
        showToast('수정 실패: ' + error.message, 'error');
    }
}

// ==================== Deactivate User ====================
async function deactivateUser() {
    const userId = document.getElementById('userDetailModal').dataset.userId;

    if (!confirm('이 부원을 비활동 처리하시겠습니까?')) {
        return;
    }

    try {
        await userApi.deactivate(userId);
        showToast('비활동 처리되었습니다', 'success');
        closeModal('userDetailModal');
        await loadUsers();
    } catch (error) {
        showToast('비활동 처리 실패: ' + error.message, 'error');
    }
}

// ==================== Helper ====================
function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// Initialize on load
document.addEventListener('DOMContentLoaded', initUserPage);
