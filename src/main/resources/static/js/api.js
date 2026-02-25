// Judith Management System - API Utilities

const API_BASE = '';  // Same origin, no prefix needed

// ==================== Fetch Wrapper ====================
async function api(endpoint, options = {}) {
    const url = API_BASE + endpoint;

    // Get token from auth.js
    const token = typeof getToken === 'function' ? getToken() : null;

    const defaultOptions = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    // Add Authorization header if token exists
    if (token) {
        defaultOptions.headers['Authorization'] = `Bearer ${token}`;
    }

    const mergedOptions = {
        ...defaultOptions,
        ...options,
        headers: {
            ...defaultOptions.headers,
            ...options.headers,
        },
    };

    try {
        const response = await fetch(url, mergedOptions);

        // Handle 401 Unauthorized - token expired or invalid
        if (response.status === 401) {
            // Clear auth and redirect to login
            if (typeof clearAuth === 'function') {
                clearAuth();
            }
            return;
        }

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText || `HTTP ${response.status}`);
        }

        // Handle empty responses
        const text = await response.text();
        return text ? JSON.parse(text) : null;
    } catch (error) {
        console.error('API Error:', error);
        throw error;
    }
}

// ==================== User API (Admin) ====================
const userApi = {
    getAll: () => api('/api/admin/users'),
    getById: (id) => api(`/api/admin/users/${id}`),
    create: (user) => api('/api/admin/users', { method: 'POST', body: JSON.stringify(user) }),
    update: (id, user) => api(`/api/admin/users/${id}`, { method: 'PUT', body: JSON.stringify(user) }),
    deactivate: (id) => api(`/api/admin/users/${id}/deactivate`, { method: 'POST' }),
    reactivate: (id) => api(`/api/admin/users/${id}/reactivate`, { method: 'POST' }),
    getActive: () => api('/api/admin/users/active'),
    getInactive: () => api('/api/admin/users/inactive'),
};

// ==================== Event API ====================
const eventApi = {
    // Public - single event for booking page
    getById: (id) => api(`/api/public/events/${id}`),
    // Members - list all events
    getAll: () => api('/api/events'),
    // Admin - CRUD
    create: (event) => api('/api/admin/events', { method: 'POST', body: JSON.stringify(event) }),
    update: (id, event) => api(`/api/admin/events/${id}`, { method: 'PUT', body: JSON.stringify(event) }),
    delete: (id) => api(`/api/admin/events/${id}`, { method: 'DELETE' }),
};

// ==================== Schedule API (Admin) ====================
const scheduleApi = {
    create: (schedule) => api('/api/admin/schedule', { method: 'POST', body: JSON.stringify(schedule) }),
    update: (id, schedule) => api(`/api/admin/schedule/${id}`, { method: 'PUT', body: JSON.stringify(schedule) }),
    getReservations: (scheduleId) => api(`/api/admin/schedule/${scheduleId}/reservations`),
};

// ==================== Reservation API (Public) ====================
const reservationApi = {
    create: (reservation) => api('/api/public/reservations', { method: 'POST', body: JSON.stringify(reservation) }),
    lookup: (phoneNumber) => api(`/api/public/reservations/lookup?phoneNumber=${encodeURIComponent(phoneNumber)}`),
    delete: (eventScheduleId, phoneNumber) => api('/api/public/reservations', {
        method: 'DELETE',
        body: JSON.stringify({ eventScheduleId, phoneNumber })
    }),
};

// ==================== Message API (Admin) ====================
const messageApi = {
    send: (messageContent) => api('/api/admin/messages/send-message', {
        method: 'POST',
        body: JSON.stringify({ messageContent })
    }),
    getHistory: (page = 0, size = 20) => api(`/api/admin/messages/history-all?page=${page}&size=${size}`),
    getById: (id) => api(`/api/admin/messages/${id}`),
};

// ==================== Season API ====================
const seasonApi = {
    // Public
    getCurrent: () => api('/api/public/seasons/current'),
    getCountdown: () => api('/api/public/seasons/countdown'),
    // Admin
    getAll: () => api('/api/admin/seasons'),
    getById: (id) => api(`/api/admin/seasons/${id}`),
    create: (season) => api('/api/admin/seasons', { method: 'POST', body: JSON.stringify(season) }),
    update: (season) => api('/api/admin/seasons', { method: 'PUT', body: JSON.stringify(season) }),
    activate: (id) => api(`/api/admin/seasons/${id}/activate`, { method: 'POST' }),
    close: (id) => api(`/api/admin/seasons/${id}/close`, { method: 'POST' }),
    delete: (id) => api(`/api/admin/seasons/${id}`, { method: 'DELETE' }),
};

// ==================== UserSeason API ====================
// Links users to seasons with roles (casting)
const userSeasonApi = {
    // Public - for casting display on booking page
    getUsersBySeason: (seasonId) => api(`/api/public/seasons/${seasonId}/users`),

    // Admin - manage user-season assignments
    addUserToSeason: (request) => api('/api/admin/seasons/users', {
        method: 'POST',
        body: JSON.stringify(request)
    }),

    updateUserRoles: (request) => api('/api/admin/seasons/users', {
        method: 'PUT',
        body: JSON.stringify(request)
    }),

    removeUserFromSeason: (seasonId, userId) => api(`/api/admin/seasons/${seasonId}/users/${userId}`, {
        method: 'DELETE'
    }),
};

// ==================== Notification API ====================
const notificationApi = {
    // Admin - create notification
    create: (notification) => api('/api/admin/notifications', { method: 'POST', body: JSON.stringify(notification) }),

    // Members - get own notifications
    getForUser: (userId) => api(`/api/notifications/${userId}`),
    getUnread: (userId) => api(`/api/notifications/${userId}/unread`),
    getUnreadCount: (userId) => api(`/api/notifications/${userId}/unread/count`),

    // Members - mark as read
    markAsRead: (userNotificationId) => api(`/api/notifications/${userNotificationId}/read`, { method: 'POST' }),
    markAllAsRead: (userId) => api(`/api/notifications/${userId}/read-all`, { method: 'POST' }),
};

// ==================== Upload API (Admin) ====================
// Folder options: EVENT_POSTER, PHOTOS, VIDEOS, SCRIPT, ART
// Requires seasonId - all files must belong to a season
async function uploadFile(file, folder = 'EVENT_POSTER', seasonId) {
    if (!seasonId) {
        throw new Error('학기이 설정되지 않았습니다. 먼저 학기을 활성화해주세요.');
    }

    const formData = new FormData();
    formData.append('file', file);

    // Get token for auth
    const token = typeof getToken === 'function' ? getToken() : null;
    const headers = {};
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    try {
        const response = await fetch(`/api/admin/upload/${folder}/season/${seasonId}`, {
            method: 'POST',
            headers: headers,
            body: formData
            // Note: Don't set Content-Type header - browser sets it with boundary
        });

        if (response.status === 401) {
            if (typeof clearAuth === 'function') {
                clearAuth();
            }
            return;
        }

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText || `업로드 실패: ${response.status}`);
        }

        const data = await response.json();
        return data.url;
    } catch (error) {
        console.error('Upload error:', error);
        throw error;
    }
}

// Convenience function for poster uploads
function uploadPoster(file, seasonId) {
    return uploadFile(file, 'EVENT_POSTER', seasonId);
}

// ==================== Toast Notifications ====================
function showToast(message, type = 'default') {
    let container = document.querySelector('.toast-container');
    if (!container) {
        container = document.createElement('div');
        container.className = 'toast-container';
        document.body.appendChild(container);
    }

    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.textContent = message;
    container.appendChild(toast);

    setTimeout(() => {
        toast.remove();
    }, 3000);
}

// ==================== Modal Helpers ====================
function openModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.classList.add('active');
    }
}

function closeModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.classList.remove('active');
    }
}

// Close modal when clicking overlay
document.addEventListener('click', (e) => {
    if (e.target.classList.contains('modal-overlay')) {
        e.target.classList.remove('active');
    }
});

// ==================== Loading State ====================
function showLoading(containerId) {
    const container = document.getElementById(containerId);
    if (container) {
        container.innerHTML = '<div class="loading"><div class="spinner"></div></div>';
    }
}

// ==================== Format Helpers ====================
function formatDate(dateString) {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleDateString('ko-KR', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

function formatPhone(phone) {
    if (!phone) return '-';
    // Format as 010-1234-5678
    const cleaned = phone.replace(/\D/g, '');
    if (cleaned.length === 11) {
        return `${cleaned.slice(0,3)}-${cleaned.slice(3,7)}-${cleaned.slice(7)}`;
    }
    return phone;
}

// ==================== Role Helpers ====================
const ROLE_LABELS = {
    // Full access roles
    LEADER: '학회장',
    PRODUCER: '연출',
    SUB_PRODUCER: '조연출',
    PLANNER: '기획',
    // Normal member roles
    ACTOR: '배우',
    STAFF: '스태프',
    SOUND_OPERATOR: '음향 오퍼',
    LIGHT_OPERATOR: '조명 오퍼',
    SOUND_DESIGN: '음향 디자인',
    LIGHT_DESIGN: '조명 디자인',
    IMAGE_DESIGN: '인쇄 디자인',
    STAGE_DESIGN: '무대 디자인',
};

const FULL_ACCESS_ROLES = ['LEADER', 'PRODUCER', 'SUB_PRODUCER', 'PLANNER'];

function getRoleLabel(role) {
    return ROLE_LABELS[role] || role;
}

function formatRoles(roles) {
    if (!roles || roles.length === 0) return '-';
    return roles.map(r => getRoleLabel(r)).join(', ');
}

function isFullAccessRole(role) {
    return FULL_ACCESS_ROLES.includes(role);
}

function hasFullAccess(roles) {
    if (!roles) return false;
    return roles.some(r => FULL_ACCESS_ROLES.includes(r));
}
