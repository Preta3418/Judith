// Judith Management System - Season Management

let currentAction = null;
let currentSeasonId = null;
let managingSeasonId = null;  // For user management modal

const statusNames = {
    'PREPARING': '준비중',
    'ACTIVE': '진행중',
    'CLOSED': '종료'
};

// All available roles for checkboxes
const ALL_ROLES = [
    'LEADER', 'PRODUCER', 'SUB_PRODUCER', 'PLANNER',
    'ACTOR', 'STAFF', 'SOUND_OPERATOR', 'LIGHT_OPERATOR',
    'SOUND_DESIGN', 'LIGHT_DESIGN', 'IMAGE_DESIGN', 'STAGE_DESIGN'
];

// Load all seasons
async function loadSeasons() {
    const tableBody = document.getElementById('seasonsTableBody');

    try {
        const seasons = await seasonApi.getAll();

        if (!seasons || seasons.length === 0) {
            tableBody.innerHTML = `
                <tr>
                    <td colspan="6">
                        <div class="empty-state">
                            <div class="icon">&#x1F4C5;</div>
                            <h3>학기이 없습니다</h3>
                            <p>첫 학기을 만들어보세요.</p>
                            <button class="btn btn-primary" onclick="openCreateModal()">학기 생성</button>
                        </div>
                    </td>
                </tr>
            `;
            return;
        }

        tableBody.innerHTML = seasons.map(season => `
            <tr>
                <td>
                    <strong>${escapeHtml(season.name)}</strong>
                </td>
                <td>
                    <span class="status status-${season.status.toLowerCase()}">${statusNames[season.status] || season.status}</span>
                </td>
                <td>${formatDate(season.startDate)}</td>
                <td>${formatDate(season.endDate)}</td>
                <td>${season.eventDate ? formatDate(season.eventDate) : '-'}</td>
                <td>
                    <div class="actions">
                        <button class="btn btn-sm btn-primary" onclick="openUserManageModal(${season.id}, '${escapeHtml(season.name)}')"
                                title="부원 관리">부원</button>
                        ${season.status === 'PREPARING' ? `
                            <button class="btn btn-sm btn-success" onclick="activateSeason(${season.id})"
                                    title="활성화">활성화</button>
                        ` : ''}
                        ${season.status === 'ACTIVE' ? `
                            <button class="btn btn-sm btn-secondary" onclick="closeSeason(${season.id})"
                                    title="종료">종료</button>
                        ` : ''}
                        <button class="btn btn-sm btn-outline" onclick="editSeason(${season.id})"
                                title="수정">수정</button>
                        ${season.status !== 'ACTIVE' ? `
                            <button class="btn btn-sm btn-ghost" onclick="deleteSeason(${season.id})"
                                    title="삭제" style="color: var(--danger);">삭제</button>
                        ` : ''}
                    </div>
                </td>
            </tr>
        `).join('');

        // Load current season for banner
        loadCurrentSeason();

    } catch (error) {
        console.error('Error loading seasons:', error);
        tableBody.innerHTML = `
            <tr>
                <td colspan="6">
                    <div class="empty-state">
                        <p class="text-danger">학기 목록을 불러오는데 실패했습니다. 다시 시도해주세요.</p>
                        <button class="btn btn-secondary" onclick="loadSeasons()">다시 시도</button>
                    </div>
                </td>
            </tr>
        `;
    }
}

// Load current active season for the banner
async function loadCurrentSeason() {
    try {
        const season = await seasonApi.getCurrent();
        if (season && season.id) {
            document.getElementById('currentSeasonBanner').style.display = 'block';
            document.getElementById('currentSeasonName').textContent = season.name;
            document.getElementById('transitionBtn').style.display = 'inline-flex';

            if (season.eventDate) {
                startCountdown(season.eventDate);
            } else {
                document.getElementById('countdownTimer').innerHTML =
                    '<p style="opacity: 0.8;">공연일이 설정되지 않았습니다</p>';
            }
        } else {
            document.getElementById('currentSeasonBanner').style.display = 'none';
            document.getElementById('transitionBtn').style.display = 'none';
        }
    } catch (error) {
        document.getElementById('currentSeasonBanner').style.display = 'none';
        document.getElementById('transitionBtn').style.display = 'none';
    }
}

// Start countdown timer
function startCountdown(eventDate) {
    const targetDate = new Date(eventDate + 'T00:00:00');

    function updateCountdown() {
        const now = new Date();
        const diff = targetDate - now;

        if (diff <= 0) {
            document.getElementById('countdownDays').textContent = '0';
            document.getElementById('countdownHours').textContent = '0';
            document.getElementById('countdownMinutes').textContent = '0';
            document.getElementById('countdownSeconds').textContent = '0';
            return;
        }

        const days = Math.floor(diff / (1000 * 60 * 60 * 24));
        const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
        const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
        const seconds = Math.floor((diff % (1000 * 60)) / 1000);

        document.getElementById('countdownDays').textContent = days;
        document.getElementById('countdownHours').textContent = hours;
        document.getElementById('countdownMinutes').textContent = minutes;
        document.getElementById('countdownSeconds').textContent = seconds;
    }

    updateCountdown();
    setInterval(updateCountdown, 1000);
}

// Open create modal
async function openCreateModal() {
    document.getElementById('modalTitle').textContent = '새 학기';
    document.getElementById('seasonForm').reset();
    document.getElementById('seasonId').value = '';

    // Show member selection and make modal wider
    document.getElementById('createMemberSection').style.display = 'block';
    document.getElementById('seasonModalInner').style.maxWidth = '800px';

    // Load active users for member selection
    await loadCreateMembers();

    openModal('seasonModal');
}

// Load active users for season creation member selection
async function loadCreateMembers() {
    const container = document.getElementById('createMemberList');
    container.innerHTML = '<div class="loading"><div class="spinner"></div></div>';

    try {
        const activeUsers = await userApi.getActive();

        if (!activeUsers || activeUsers.length === 0) {
            container.innerHTML = '<p class="text-secondary">활동 중인 부원이 없습니다.</p>';
            return;
        }

        container.innerHTML = activeUsers.map(user => `
            <div class="transition-user-item" data-user-id="${user.id}">
                <div class="transition-user-select">
                    <label class="checkbox-label">
                        <input type="checkbox" class="create-member-checkbox"
                               data-user-id="${user.id}" checked>
                        <strong>${escapeHtml(user.name)}</strong>
                        <span class="text-secondary" style="margin-left: 8px;">(${user.studentNumber || '-'})</span>
                    </label>
                </div>
                <div class="transition-user-roles">
                    <div class="roles-checkbox-group compact">
                        ${ALL_ROLES.map(role => `
                            <label class="role-checkbox ${isFullAccessRole(role) ? 'full-access' : ''}">
                                <input type="checkbox"
                                       class="create-role-checkbox"
                                       data-user-id="${user.id}"
                                       value="${role}">
                                <span>${getRoleLabel(role)}</span>
                            </label>
                        `).join('')}
                    </div>
                </div>
            </div>
        `).join('');

        // Toggle role visibility based on user selection
        container.querySelectorAll('.create-member-checkbox').forEach(checkbox => {
            checkbox.addEventListener('change', (e) => {
                const item = e.target.closest('.transition-user-item');
                const rolesSection = item.querySelector('.transition-user-roles');
                rolesSection.style.opacity = e.target.checked ? '1' : '0.4';
                rolesSection.style.pointerEvents = e.target.checked ? 'auto' : 'none';
            });
        });

    } catch (error) {
        console.error('Error loading active users:', error);
        container.innerHTML = '<p class="text-danger">부원 목록을 불러오는데 실패했습니다.</p>';
    }
}

// Edit season
async function editSeason(id) {
    try {
        const season = await seasonApi.getById(id);

        document.getElementById('modalTitle').textContent = '학기 수정';
        document.getElementById('seasonId').value = season.id;
        document.getElementById('seasonName').value = season.name;
        document.getElementById('eventDate').value = season.eventDate || '';

        // Hide member selection for edit mode
        document.getElementById('createMemberSection').style.display = 'none';
        document.getElementById('seasonModalInner').style.maxWidth = '';

        openModal('seasonModal');
    } catch (error) {
        console.error('Error loading season:', error);
        showToast('학기 정보를 불러오는데 실패했습니다', 'error');
    }
}

// Save season (create or update)
async function saveSeason() {
    const id = document.getElementById('seasonId').value;
    const data = {
        name: document.getElementById('seasonName').value.trim(),
        eventDate: document.getElementById('eventDate').value || null
    };

    if (!data.name) {
        showToast('학기명을 입력해주세요', 'error');
        return;
    }

    try {
        if (id) {
            // Update existing season
            data.id = parseInt(id);
            await seasonApi.update(id, data);
            showToast('학기이 수정되었습니다', 'success');
        } else {
            // Create new season — collect selected members
            const members = [];
            document.querySelectorAll('.create-member-checkbox:checked').forEach(checkbox => {
                const userId = parseInt(checkbox.dataset.userId);
                const item = checkbox.closest('.transition-user-item');
                const roles = Array.from(item.querySelectorAll('.create-role-checkbox:checked'))
                    .map(cb => cb.value);
                if (roles.length > 0) {
                    members.push({ userId, roles });
                }
            });

            if (members.length === 0) {
                showToast('최소 1명의 부원에게 역할을 배정해주세요', 'error');
                return;
            }

            // Check for at least one full access role
            const hasFullAccessMember = members.some(m =>
                m.roles.some(r => FULL_ACCESS_ROLES.includes(r))
            );
            if (!hasFullAccessMember) {
                showToast('최소 1명의 운영진(학회장, 연출, 조연출, 기획)이 필요합니다', 'error');
                return;
            }

            data.members = members;
            await seasonApi.create(data);
            showToast('학기이 생성되었습니다', 'success');
        }

        closeModal('seasonModal');
        loadSeasons();
    } catch (error) {
        console.error('Error saving season:', error);
        showToast(error.message || '학기 저장에 실패했습니다', 'error');
    }
}

// Activate season
function activateSeason(id) {
    currentAction = 'activate';
    currentSeasonId = id;

    document.getElementById('confirmTitle').textContent = '학기 활성화';
    document.getElementById('confirmMessage').innerHTML =
        '이 학기을 활성화하시겠습니까?<br><br>' +
        '<strong>참고:</strong> 다른 학기이 현재 활성화되어 있으면 실패합니다. ' +
        '먼저 현재 활성 학기을 종료해야 합니다.';
    document.getElementById('confirmBtn').textContent = '활성화';
    document.getElementById('confirmBtn').className = 'btn btn-success';

    openModal('confirmModal');
}

// Close season
function closeSeason(id) {
    currentAction = 'close';
    currentSeasonId = id;

    document.getElementById('confirmTitle').textContent = '학기 종료';
    document.getElementById('confirmMessage').innerHTML =
        '이 학기을 종료하시겠습니까?<br><br>' +
        '<strong>참고:</strong> 이 작업은 되돌릴 수 없습니다. ' +
        '종료된 학기은 다시 활성화할 수 없습니다.';
    document.getElementById('confirmBtn').textContent = '학기 종료';
    document.getElementById('confirmBtn').className = 'btn btn-secondary';

    openModal('confirmModal');
}

// Delete season
function deleteSeason(id) {
    currentAction = 'delete';
    currentSeasonId = id;

    document.getElementById('confirmTitle').textContent = '학기 삭제';
    document.getElementById('confirmMessage').innerHTML =
        '이 학기을 삭제하시겠습니까?<br><br>' +
        '<strong>경고:</strong> 이 작업은 되돌릴 수 없습니다.';
    document.getElementById('confirmBtn').textContent = '삭제';
    document.getElementById('confirmBtn').className = 'btn btn-danger';

    openModal('confirmModal');
}

// Confirm action
async function confirmAction() {
    if (!currentAction || !currentSeasonId) return;

    try {
        switch (currentAction) {
            case 'activate':
                await seasonApi.activate(currentSeasonId);
                showToast('학기이 활성화되었습니다', 'success');
                break;
            case 'close':
                await seasonApi.close(currentSeasonId);
                showToast('학기이 종료되었습니다', 'success');
                break;
            case 'delete':
                await seasonApi.delete(currentSeasonId);
                showToast('학기이 삭제되었습니다', 'success');
                break;
        }

        closeModal('confirmModal');
        loadSeasons();
    } catch (error) {
        console.error('Error:', error);
        showToast(error.message || '작업 실패', 'error');
    }

    currentAction = null;
    currentSeasonId = null;
}

// Utility functions
function formatDate(dateString) {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleDateString('ko-KR', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
    });
}

function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// ==================== User Management ====================

// Open user management modal for a season
async function openUserManageModal(seasonId, seasonName) {
    managingSeasonId = seasonId;
    document.getElementById('userManageSeasonName').textContent = seasonName;

    // Populate role checkboxes for add user section
    populateRoleCheckboxes('addUserRolesContainer');

    // Load available users and current season users
    await Promise.all([
        loadAvailableUsers(seasonId),
        loadSeasonUsers(seasonId)
    ]);

    openModal('userManageModal');
}

// Populate role checkboxes in a container
function populateRoleCheckboxes(containerId, selectedRoles = []) {
    const container = document.getElementById(containerId);
    container.innerHTML = ALL_ROLES.map(role => `
        <label class="role-checkbox ${isFullAccessRole(role) ? 'full-access' : ''}">
            <input type="checkbox" value="${role}" ${selectedRoles.includes(role) ? 'checked' : ''}>
            <span>${getRoleLabel(role)}</span>
        </label>
    `).join('');
}

// Get selected roles from a checkbox container
function getSelectedRoles(containerId) {
    const container = document.getElementById(containerId);
    const checkboxes = container.querySelectorAll('input[type="checkbox"]:checked');
    return Array.from(checkboxes).map(cb => cb.value);
}

// Load users not yet assigned to this season
async function loadAvailableUsers(seasonId) {
    const select = document.getElementById('addUserSelect');

    try {
        const [allActiveUsers, seasonUsers] = await Promise.all([
            userApi.getActive(),
            userSeasonApi.getUsersBySeason(seasonId)
        ]);

        // Filter out users already assigned to this season
        const assignedUserIds = new Set(seasonUsers.map(su => su.userId));
        const availableUsers = allActiveUsers.filter(u => !assignedUserIds.has(u.id));

        if (availableUsers.length === 0) {
            select.innerHTML = '<option value="">추가할 수 있는 부원이 없습니다</option>';
        } else {
            select.innerHTML = '<option value="">부원 선택...</option>' +
                availableUsers.map(u => `<option value="${u.id}">${escapeHtml(u.name)} (${u.studentNumber})</option>`).join('');
        }
    } catch (error) {
        console.error('Error loading available users:', error);
        select.innerHTML = '<option value="">부원 목록 로딩 실패</option>';
    }
}

// Load users currently assigned to the season
async function loadSeasonUsers(seasonId) {
    const tableBody = document.getElementById('seasonUsersTableBody');

    try {
        const seasonUsers = await userSeasonApi.getUsersBySeason(seasonId);

        if (!seasonUsers || seasonUsers.length === 0) {
            tableBody.innerHTML = `
                <tr>
                    <td colspan="3">
                        <div class="empty-state" style="padding: 24px;">
                            <p>이 학기에 배정된 부원이 없습니다.</p>
                        </div>
                    </td>
                </tr>
            `;
            return;
        }

        tableBody.innerHTML = seasonUsers.map(su => `
            <tr>
                <td><strong>${escapeHtml(su.userName)}</strong></td>
                <td>
                    <div class="role-tags">
                        ${su.roles.map(role => `
                            <span class="role-tag ${isFullAccessRole(role) ? 'full-access' : ''}">${getRoleLabel(role)}</span>
                        `).join('')}
                    </div>
                </td>
                <td>
                    <div class="actions">
                        <button class="btn btn-sm btn-outline" onclick="openEditRolesModal(${su.userId}, '${escapeHtml(su.userName)}', ${JSON.stringify(su.roles).replace(/"/g, '&quot;')})">
                            수정
                        </button>
                        <button class="btn btn-sm btn-ghost" onclick="removeUserFromSeason(${su.userId})" style="color: var(--danger);">
                            제거
                        </button>
                    </div>
                </td>
            </tr>
        `).join('');

    } catch (error) {
        console.error('Error loading season users:', error);
        tableBody.innerHTML = `
            <tr>
                <td colspan="3">
                    <div class="empty-state" style="padding: 24px;">
                        <p class="text-danger">부원 목록을 불러오는데 실패했습니다.</p>
                    </div>
                </td>
            </tr>
        `;
    }
}

// Add user to current season
async function addUserToCurrentSeason() {
    const userId = document.getElementById('addUserSelect').value;
    const roles = getSelectedRoles('addUserRolesContainer');

    if (!userId) {
        showToast('부원을 선택해주세요', 'error');
        return;
    }

    if (roles.length === 0) {
        showToast('최소 하나의 역할을 선택해주세요', 'error');
        return;
    }

    try {
        await userSeasonApi.addUserToSeason({
            userId: parseInt(userId),
            seasonId: managingSeasonId,
            roles: roles
        });

        showToast('부원이 추가되었습니다', 'success');

        // Reset form and reload
        document.getElementById('addUserSelect').value = '';
        populateRoleCheckboxes('addUserRolesContainer');
        await Promise.all([
            loadAvailableUsers(managingSeasonId),
            loadSeasonUsers(managingSeasonId)
        ]);

    } catch (error) {
        console.error('Error adding user to season:', error);
        showToast(error.message || '부원 추가에 실패했습니다', 'error');
    }
}

// Open edit roles modal
function openEditRolesModal(userId, userName, currentRoles) {
    document.getElementById('editRolesUserId').value = userId;
    document.getElementById('editRolesUserName').textContent = userName;
    populateRoleCheckboxes('editRolesContainer', currentRoles);
    openModal('editRolesModal');
}

// Save updated roles
async function saveUserRoles() {
    const userId = parseInt(document.getElementById('editRolesUserId').value);
    const roles = getSelectedRoles('editRolesContainer');

    if (roles.length === 0) {
        showToast('최소 하나의 역할을 선택해주세요', 'error');
        return;
    }

    try {
        await userSeasonApi.updateUserRoles({
            userId: userId,
            seasonId: managingSeasonId,
            userRoles: roles
        });

        showToast('역할이 수정되었습니다', 'success');
        closeModal('editRolesModal');
        await loadSeasonUsers(managingSeasonId);

    } catch (error) {
        console.error('Error updating user roles:', error);
        showToast(error.message || '역할 수정에 실패했습니다', 'error');
    }
}

// Remove user from season
async function removeUserFromSeason(userId) {
    if (!confirm('이 부원을 학기에서 제거하시겠습니까?')) {
        return;
    }

    try {
        await userSeasonApi.removeUserFromSeason(managingSeasonId, userId);

        showToast('부원이 제거되었습니다', 'success');
        await Promise.all([
            loadAvailableUsers(managingSeasonId),
            loadSeasonUsers(managingSeasonId)
        ]);

    } catch (error) {
        console.error('Error removing user from season:', error);
        showToast(error.message || '부원 제거에 실패했습니다', 'error');
    }
}

// ==================== Season Transition ====================

let currentSeasonData = null;
let transitionUsers = [];

// Open transition modal
async function openTransitionModal() {
    try {
        // Get current active season
        currentSeasonData = await seasonApi.getCurrent();
        if (!currentSeasonData || !currentSeasonData.id) {
            showToast('현재 활성 학기가 없습니다', 'error');
            return;
        }

        // Pre-fill suggested new season name
        const suggestedName = suggestNextSeasonName(currentSeasonData.name);
        document.getElementById('transitionSeasonName').value = suggestedName;
        document.getElementById('transitionEventDate').value = '';

        // Load users from current season
        await loadTransitionUsers();

        openModal('transitionModal');
    } catch (error) {
        console.error('Error opening transition modal:', error);
        showToast('학기 전환 준비 중 오류가 발생했습니다', 'error');
    }
}

// Suggest next season name based on current (e.g., "2025 봄" -> "2025 가을")
function suggestNextSeasonName(currentName) {
    if (!currentName) return '';

    const match = currentName.match(/(\d{4})\s*(봄|가을)/);
    if (match) {
        const year = parseInt(match[1]);
        const semester = match[2];

        if (semester === '봄') {
            return `${year} 가을`;
        } else {
            return `${year + 1} 봄`;
        }
    }

    return '';
}

// Load users from current season for transition selection
async function loadTransitionUsers() {
    const container = document.getElementById('transitionUserList');
    container.innerHTML = '<div class="loading"><div class="spinner"></div></div>';

    try {
        transitionUsers = await userSeasonApi.getUsersBySeason(currentSeasonData.id);

        if (!transitionUsers || transitionUsers.length === 0) {
            container.innerHTML = `
                <div class="empty-state" style="padding: 24px;">
                    <p>현재 학기에 부원이 없습니다.</p>
                </div>
            `;
            return;
        }

        container.innerHTML = transitionUsers.map(user => `
            <div class="transition-user-item" data-user-id="${user.userId}">
                <div class="transition-user-select">
                    <label class="checkbox-label">
                        <input type="checkbox" class="transition-user-checkbox"
                               data-user-id="${user.userId}" checked>
                        <strong>${escapeHtml(user.userName)}</strong>
                    </label>
                    <div class="current-roles">
                        현재: ${user.roles.map(r => getRoleLabel(r)).join(', ')}
                    </div>
                </div>
                <div class="transition-user-roles">
                    <div class="roles-checkbox-group compact">
                        ${ALL_ROLES.map(role => `
                            <label class="role-checkbox ${isFullAccessRole(role) ? 'full-access' : ''}">
                                <input type="checkbox"
                                       class="transition-role-checkbox"
                                       data-user-id="${user.userId}"
                                       value="${role}"
                                       ${user.roles.includes(role) ? 'checked' : ''}>
                                <span>${getRoleLabel(role)}</span>
                            </label>
                        `).join('')}
                    </div>
                </div>
            </div>
        `).join('');

        // Add event listener to toggle role checkboxes visibility based on user selection
        container.querySelectorAll('.transition-user-checkbox').forEach(checkbox => {
            checkbox.addEventListener('change', (e) => {
                const item = e.target.closest('.transition-user-item');
                const rolesSection = item.querySelector('.transition-user-roles');
                rolesSection.style.opacity = e.target.checked ? '1' : '0.4';
                rolesSection.style.pointerEvents = e.target.checked ? 'auto' : 'none';
            });
        });

    } catch (error) {
        console.error('Error loading transition users:', error);
        container.innerHTML = `
            <div class="empty-state" style="padding: 24px;">
                <p class="text-danger">부원 목록을 불러오는데 실패했습니다.</p>
            </div>
        `;
    }
}

// Select/deselect all users in transition
function selectAllTransition(checked) {
    const checkboxes = document.querySelectorAll('.transition-user-checkbox');
    checkboxes.forEach(checkbox => {
        checkbox.checked = checked;
        checkbox.dispatchEvent(new Event('change', { bubbles: true }));
    });
}

// Execute season transition
async function executeTransition() {
    const newSeasonName = document.getElementById('transitionSeasonName').value.trim();
    const eventDate = document.getElementById('transitionEventDate').value || null;

    if (!newSeasonName) {
        showToast('새 학기명을 입력해주세요', 'error');
        return;
    }

    // Collect selected users and their roles
    const selectedUsers = [];
    const deactivateUserIds = [];

    document.querySelectorAll('.transition-user-item').forEach(item => {
        const userId = parseInt(item.dataset.userId);
        const isSelected = item.querySelector('.transition-user-checkbox').checked;

        if (isSelected) {
            const roles = Array.from(item.querySelectorAll('.transition-role-checkbox:checked'))
                .map(cb => cb.value);

            if (roles.length > 0) {
                selectedUsers.push({ userId, roles });
            } else {
                // If selected but no roles, treat as not continuing
                deactivateUserIds.push(userId);
            }
        } else {
            deactivateUserIds.push(userId);
        }
    });

    if (selectedUsers.length === 0) {
        showToast('최소 1명의 부원에게 역할을 배정해주세요', 'error');
        return;
    }

    // Check for at least one full access role
    const hasFullAccessMember = selectedUsers.some(u =>
        u.roles.some(r => FULL_ACCESS_ROLES.includes(r))
    );
    if (!hasFullAccessMember) {
        showToast('최소 1명의 운영진(학회장, 연출, 조연출, 기획)이 필요합니다', 'error');
        return;
    }

    // Confirm the action
    const continueCount = selectedUsers.length;
    const deactivateCount = deactivateUserIds.length;

    const confirmMsg = `새 학기: ${newSeasonName}\n\n` +
        `- 계속하는 부원: ${continueCount}명\n` +
        `- 비활동 처리될 부원: ${deactivateCount}명\n\n` +
        `진행하시겠습니까?`;

    if (!confirm(confirmMsg)) {
        return;
    }

    try {
        // Step 1: Close current season
        await seasonApi.close(currentSeasonData.id);

        // Step 2: Create new season with members (atomic)
        const newSeason = await seasonApi.create({
            name: newSeasonName,
            eventDate: eventDate,
            members: selectedUsers
        });

        // Step 3: Deactivate non-selected users
        for (const userId of deactivateUserIds) {
            try {
                await userApi.deactivate(userId);
            } catch (error) {
                console.error(`Error deactivating user ${userId}:`, error);
            }
        }

        // Step 4: Activate new season
        await seasonApi.activate(newSeason.id);

        showToast('학기 전환이 완료되었습니다', 'success');
        closeModal('transitionModal');
        loadSeasons();

    } catch (error) {
        console.error('Error during transition:', error);
        showToast(error.message || '학기 전환 중 오류가 발생했습니다', 'error');
    }
}

// Initialize
document.addEventListener('DOMContentLoaded', loadSeasons);
