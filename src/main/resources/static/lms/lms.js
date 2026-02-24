// Judith LMS

// ==================== Init ====================
document.addEventListener('DOMContentLoaded', () => {
    if (!requireAuth()) return;

    // Set user info in sidebar
    const userName = getCurrentUserName();
    const userRole = getCurrentUserRole();

    const userNameEl = document.getElementById('userName');
    const userRoleEl = document.getElementById('userRole');
    const userAvatarEl = document.getElementById('userAvatar');

    if (userNameEl) userNameEl.textContent = userName;
    if (userRoleEl) userRoleEl.textContent = userRole;
    if (userAvatarEl) userAvatarEl.textContent = userName.charAt(0);

    // Check password change
    checkPasswordChangeNeeded();

    // Mobile menu toggle
    const menuToggle = document.getElementById('menuToggle');
    const sidebar = document.getElementById('sidebar');
    const overlay = document.getElementById('sidebarOverlay');

    if (menuToggle) {
        menuToggle.addEventListener('click', () => {
            sidebar.classList.toggle('open');
            overlay.classList.toggle('active');
        });
    }

    if (overlay) {
        overlay.addEventListener('click', () => {
            sidebar.classList.remove('open');
            overlay.classList.remove('active');
        });
    }
});
