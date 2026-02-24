// Judith Management System - Authentication & Role Management

const AUTH_KEY = 'judith_auth';
const TOKEN_KEY = 'judith_token';

// ==================== Token Management ====================
function getToken() {
    return localStorage.getItem(TOKEN_KEY);
}

function setToken(token) {
    localStorage.setItem(TOKEN_KEY, token);
}

function clearToken() {
    localStorage.removeItem(TOKEN_KEY);
}

// ==================== Auth State ====================
function getAuth() {
    const stored = localStorage.getItem(AUTH_KEY);
    return stored ? JSON.parse(stored) : null;
}

function setAuth(authData) {
    localStorage.setItem(AUTH_KEY, JSON.stringify(authData));
    applyAdminStatus(authData.hasFullAccess);
}

function clearAuth() {
    localStorage.removeItem(AUTH_KEY);
    clearToken();
    window.location.href = '/login.html';
}

function isLoggedIn() {
    return getToken() !== null && getAuth() !== null;
}

function isAdmin() {
    const auth = getAuth();
    return auth ? auth.hasFullAccess === true : false;
}

function getLoginRedirect() {
    return isAdmin() ? '/main.html' : '/lms/index.html';
}

// ==================== Admin Status Application ====================
function applyAdminStatus(hasFullAccess) {
    if (hasFullAccess === true) {
        document.body.classList.add('is-admin');
    } else {
        document.body.classList.remove('is-admin');
    }
}

// ==================== Auth Check ====================
function requireAuth() {
    if (!isLoggedIn()) {
        window.location.href = '/login.html';
        return false;
    }
    const auth = getAuth();
    applyAdminStatus(auth.hasFullAccess);
    return true;
}

// ==================== Login ====================
async function login(studentNumber, password) {
    const response = await fetch('/api/public/auth/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ studentNumber, password }),
    });

    if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || '로그인에 실패했습니다');
    }

    const data = await response.json();

    // Store token and auth info
    setToken(data.token);
    setAuth({
        userId: data.userId,
        name: data.name,
        hasFullAccess: data.hasFullAccess,
        passwordChanged: data.passwordChanged,
        loginTime: new Date().toISOString()
    });

    return data;
}

// ==================== Logout ====================
function logout() {
    clearAuth();
}

// ==================== Get Current User Display ====================
function getCurrentUserName() {
    const auth = getAuth();
    return auth ? auth.name : '';
}

function getCurrentUserId() {
    const auth = getAuth();
    return auth ? auth.userId : null;
}

function getCurrentUserRole() {
    const auth = getAuth();
    if (!auth) return '';
    return auth.hasFullAccess ? '관리자' : '부원';
}

function isPasswordChanged() {
    const auth = getAuth();
    return auth ? auth.passwordChanged === true : true;
}

// ==================== Password Change ====================
async function changePassword(currentPassword, newPassword) {
    const token = getToken();
    const response = await fetch('/api/password', {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`,
        },
        body: JSON.stringify({ currentPassword, newPassword }),
    });

    if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || '비밀번호 변경에 실패했습니다');
    }

    // Update local auth state
    const auth = getAuth();
    if (auth) {
        auth.passwordChanged = true;
        setAuth(auth);
    }
}

// ==================== Password Change Modal ====================
function createPasswordChangeModal() {
    // Don't create duplicates
    if (document.getElementById('passwordChangeModal')) return;

    const modal = document.createElement('div');
    modal.id = 'passwordChangeModal';
    modal.className = 'modal-overlay';
    modal.innerHTML = `
        <div class="modal" style="max-width: 440px;">
            <div class="modal-header">
                <h3 class="modal-title">비밀번호 변경</h3>
                <button class="modal-close" onclick="closePasswordChangeModal()">&times;</button>
            </div>
            <div class="modal-body">
                <div id="passwordChangeAlert" class="alert alert-warning mb-16">
                    초기 비밀번호를 사용 중입니다. 보안을 위해 비밀번호를 변경해주세요.
                </div>
                <form id="passwordChangeForm">
                    <div class="form-group">
                        <label class="form-label" for="currentPasswordInput">현재 비밀번호</label>
                        <input type="password" class="form-input" id="currentPasswordInput" placeholder="현재 비밀번호" required>
                    </div>
                    <div class="form-group">
                        <label class="form-label" for="newPasswordInput">새 비밀번호</label>
                        <input type="password" class="form-input" id="newPasswordInput" placeholder="새 비밀번호 (8자 이상)" required minlength="8">
                    </div>
                    <div class="form-group">
                        <label class="form-label" for="confirmPasswordInput">새 비밀번호 확인</label>
                        <input type="password" class="form-input" id="confirmPasswordInput" placeholder="새 비밀번호 확인" required minlength="8">
                    </div>
                    <div id="passwordChangeError" class="error-message" style="display: none;"></div>
                </form>
            </div>
            <div class="modal-footer">
                <button class="btn btn-secondary" onclick="closePasswordChangeModal()">나중에</button>
                <button class="btn btn-primary" id="passwordChangeSubmitBtn" onclick="submitPasswordChange()">변경</button>
            </div>
        </div>
    `;
    document.body.appendChild(modal);
}

function showPasswordChangeModal() {
    createPasswordChangeModal();
    document.getElementById('passwordChangeModal').classList.add('active');
}

function closePasswordChangeModal() {
    const modal = document.getElementById('passwordChangeModal');
    if (modal) modal.classList.remove('active');
}

async function submitPasswordChange() {
    const currentPassword = document.getElementById('currentPasswordInput').value;
    const newPassword = document.getElementById('newPasswordInput').value;
    const confirmPassword = document.getElementById('confirmPasswordInput').value;
    const errorDiv = document.getElementById('passwordChangeError');
    const submitBtn = document.getElementById('passwordChangeSubmitBtn');

    errorDiv.style.display = 'none';

    if (!currentPassword || !newPassword || !confirmPassword) {
        errorDiv.textContent = '모든 항목을 입력해주세요';
        errorDiv.style.display = 'block';
        return;
    }

    if (newPassword.length < 8) {
        errorDiv.textContent = '새 비밀번호는 8자 이상이어야 합니다';
        errorDiv.style.display = 'block';
        return;
    }

    if (newPassword !== confirmPassword) {
        errorDiv.textContent = '새 비밀번호가 일치하지 않습니다';
        errorDiv.style.display = 'block';
        return;
    }

    submitBtn.disabled = true;
    submitBtn.textContent = '변경 중...';

    try {
        await changePassword(currentPassword, newPassword);
        closePasswordChangeModal();
        if (typeof showToast === 'function') {
            showToast('비밀번호가 변경되었습니다', 'success');
        }
    } catch (error) {
        errorDiv.textContent = '현재 비밀번호가 올바르지 않습니다';
        errorDiv.style.display = 'block';
    } finally {
        submitBtn.disabled = false;
        submitBtn.textContent = '변경';
    }
}

// Check and prompt password change after page load
function checkPasswordChangeNeeded() {
    if (isLoggedIn() && !isPasswordChanged()) {
        showPasswordChangeModal();
    }
}
