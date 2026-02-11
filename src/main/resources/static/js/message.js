// Judith Management System - Message Page Logic

let messageHistory = [];

// ==================== Initialize ====================
async function initMessagePage() {
    await loadMessageHistory();
}

// ==================== Load Message History ====================
async function loadMessageHistory() {
    showLoading('messageHistory');

    try {
        const response = await messageApi.getHistory();
        messageHistory = response.content || [];
        renderMessageHistory();
    } catch (error) {
        document.getElementById('messageHistory').innerHTML = `
            <div class="empty-state">
                <div class="icon">❌</div>
                <p>전송 내역을 불러오지 못했습니다</p>
                <button class="btn btn-primary btn-sm" onclick="loadMessageHistory()">다시 시도</button>
            </div>
        `;
    }
}

// ==================== Render Message History ====================
function renderMessageHistory() {
    const container = document.getElementById('messageHistory');

    if (messageHistory.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <div class="icon">💬</div>
                <p>전송 내역이 없습니다</p>
            </div>
        `;
        return;
    }

    container.innerHTML = messageHistory.map(msg => `
        <div class="list-item" onclick="viewMessageDetail(${msg.id})" style="cursor: pointer;">
            <div class="list-item-content">
                <div class="list-item-title">${escapeHtml(truncate(msg.messageContent, 40))}</div>
                <div class="list-item-subtitle">${formatDate(msg.createdAt)}</div>
            </div>
            <div class="list-item-actions">
                <span class="badge ${msg.failedAttempt > 0 ? 'badge-warning' : 'badge-success'}">
                    ${msg.totalSent - msg.failedAttempt}/${msg.totalSent}
                </span>
            </div>
        </div>
    `).join('');
}

// ==================== Send Message ====================
async function sendMessage(e) {
    e.preventDefault();

    const messageContent = document.getElementById('messageContent').value.trim();

    if (!messageContent) {
        showToast('메시지를 입력해주세요', 'error');
        return;
    }

    const confirmSend = confirm('모든 졸업생에게 문자를 전송합니다. 계속하시겠습니까?');
    if (!confirmSend) {
        return;
    }

    const submitBtn = e.target.querySelector('button[type="submit"]');
    submitBtn.disabled = true;
    submitBtn.textContent = '전송 중...';

    try {
        const result = await messageApi.send(messageContent);

        // Show result
        let resultMessage = `전송 완료: ${result.successCount}/${result.totalAttempted}`;
        if (result.failureCount > 0) {
            resultMessage += ` (${result.failureCount}건 실패)`;
        }

        showToast(resultMessage, result.failureCount > 0 ? 'warning' : 'success');

        // Clear form and reload history
        document.getElementById('messageContent').value = '';
        await loadMessageHistory();

    } catch (error) {
        showToast('전송 실패: ' + error.message, 'error');
    } finally {
        submitBtn.disabled = false;
        submitBtn.textContent = '졸업생에게 전송';
    }
}

// ==================== View Message Detail ====================
async function viewMessageDetail(messageId) {
    try {
        const message = await messageApi.getById(messageId);

        document.getElementById('detailContent').textContent = message.messageContent;
        document.getElementById('detailSentAt').textContent = formatDate(message.createdAt);
        document.getElementById('detailTotal').textContent = message.totalSent + '건';
        document.getElementById('detailSuccess').textContent = (message.totalSent - message.failedAttempt) + '건';
        document.getElementById('detailFailed').textContent = message.failedAttempt + '건';

        // Render failures if any
        const failuresContainer = document.getElementById('detailFailures');
        if (message.failures && message.failures.length > 0) {
            failuresContainer.innerHTML = `
                <h4 class="mb-8">전송 실패 목록</h4>
                <div class="card" style="padding: 0; overflow: hidden;">
                    ${message.failures.map(f => `
                        <div class="list-item">
                            <div class="list-item-content">
                                <div class="list-item-title">${escapeHtml(f.userName)}</div>
                                <div class="list-item-subtitle">${formatPhone(f.phoneNumber)}</div>
                            </div>
                            <span class="badge badge-danger">실패</span>
                        </div>
                    `).join('')}
                </div>
            `;
        } else {
            failuresContainer.innerHTML = '';
        }

        openModal('messageDetailModal');
    } catch (error) {
        showToast('상세 정보를 불러오지 못했습니다', 'error');
    }
}

// ==================== Helpers ====================
function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function truncate(text, maxLength) {
    if (!text) return '';
    if (text.length <= maxLength) return text;
    return text.substring(0, maxLength) + '...';
}

// Initialize on load
document.addEventListener('DOMContentLoaded', initMessagePage);
