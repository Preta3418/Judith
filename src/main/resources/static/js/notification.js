// Judith Management System - Notification Handler

// Get current user ID from auth system
function getNotificationUserId() {
    return typeof getCurrentUserId === 'function' ? getCurrentUserId() : null;
}

// Load notifications for the current user
async function loadNotifications() {
    const userId = getNotificationUserId();
    const notificationList = document.getElementById('notificationList');
    const notificationBadge = document.getElementById('notificationBadge');

    if (!notificationList || !userId) return;

    try {
        // Load unread count
        const unreadCount = await notificationApi.getUnreadCount(userId);
        if (notificationBadge) {
            if (unreadCount > 0) {
                notificationBadge.textContent = unreadCount > 99 ? '99+' : unreadCount;
                notificationBadge.classList.remove('hidden');
            } else {
                notificationBadge.classList.add('hidden');
            }
        }

        // Load notifications
        const notifications = await notificationApi.getForUser(userId);

        if (!notifications || notifications.length === 0) {
            notificationList.innerHTML = `
                <div class="empty-state" style="padding: 32px 16px;">
                    <p class="text-muted">알림이 없습니다</p>
                </div>
            `;
            return;
        }

        notificationList.innerHTML = notifications.map(n => `
            <div class="notification-item ${n.isRead ? '' : 'unread'}"
                 data-id="${n.userNotificationId}"
                 onclick="markNotificationAsRead(${n.userNotificationId})">
                <div class="notification-item-title">${escapeHtml(n.title)}</div>
                <div class="notification-item-content">${escapeHtml(n.content)}</div>
                <div class="notification-item-time">${formatRelativeTime(n.createdAt)}</div>
            </div>
        `).join('');

    } catch (error) {
        console.error('Error loading notifications:', error);
        notificationList.innerHTML = `
            <div class="empty-state" style="padding: 32px 16px;">
                <p class="text-muted">알림을 불러오는데 실패했습니다</p>
            </div>
        `;
    }
}

// Mark a single notification as read
async function markNotificationAsRead(userNotificationId) {
    try {
        await notificationApi.markAsRead(userNotificationId);

        // Update UI
        const item = document.querySelector(`[data-id="${userNotificationId}"]`);
        if (item) {
            item.classList.remove('unread');
        }

        // Update badge count
        updateNotificationBadge();
    } catch (error) {
        console.error('Error marking notification as read:', error);
    }
}

// Mark all notifications as read
async function markAllNotificationsAsRead() {
    const userId = getNotificationUserId();
    if (!userId) return;

    try {
        await notificationApi.markAllAsRead(userId);

        // Update UI
        document.querySelectorAll('.notification-item.unread').forEach(item => {
            item.classList.remove('unread');
        });

        // Update badge
        const notificationBadge = document.getElementById('notificationBadge');
        if (notificationBadge) {
            notificationBadge.classList.add('hidden');
        }
    } catch (error) {
        console.error('Error marking all notifications as read:', error);
    }
}

// Update the notification badge count
async function updateNotificationBadge() {
    const userId = getNotificationUserId();
    const notificationBadge = document.getElementById('notificationBadge');

    if (!notificationBadge || !userId) return;

    try {
        const unreadCount = await notificationApi.getUnreadCount(userId);
        if (unreadCount > 0) {
            notificationBadge.textContent = unreadCount > 99 ? '99+' : unreadCount;
            notificationBadge.classList.remove('hidden');
        } else {
            notificationBadge.classList.add('hidden');
        }
    } catch (error) {
        console.error('Error updating notification badge:', error);
    }
}

// Format relative time
function formatRelativeTime(dateString) {
    if (!dateString) return '';

    const date = new Date(dateString);
    const now = new Date();
    const diffMs = now - date;
    const diffSec = Math.floor(diffMs / 1000);
    const diffMin = Math.floor(diffSec / 60);
    const diffHour = Math.floor(diffMin / 60);
    const diffDay = Math.floor(diffHour / 24);

    if (diffSec < 60) return '방금 전';
    if (diffMin < 60) return `${diffMin}분 전`;
    if (diffHour < 24) return `${diffHour}시간 전`;
    if (diffDay < 7) return `${diffDay}일 전`;

    return date.toLocaleDateString('ko-KR', {
        month: 'long',
        day: 'numeric'
    });
}

// Escape HTML to prevent XSS
function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// Initialize notifications when DOM is ready
document.addEventListener('DOMContentLoaded', () => {
    // Load notifications
    loadNotifications();

    // Set up mark all as read button
    const markAllReadBtn = document.getElementById('markAllReadBtn');
    if (markAllReadBtn) {
        markAllReadBtn.addEventListener('click', markAllNotificationsAsRead);
    }

    // Refresh notifications periodically (every 60 seconds)
    setInterval(updateNotificationBadge, 60000);
});
