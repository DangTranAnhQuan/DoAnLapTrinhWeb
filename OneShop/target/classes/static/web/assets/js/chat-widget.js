/**
 * ========================================
 * ONESHOP CHAT WIDGET
 * Version: 1.0
 * ========================================
 */

(function() {
	'use strict';

	// ========================================
	// CONFIGURATION
	// ========================================
	const CONFIG = {
		API_BASE: '/api/chat',
		STORAGE_KEY: 'oneshop_chat_session',
		AUTO_SCROLL: true,
		TYPING_TIMEOUT: 1000,
		POLL_INTERVAL: 5000  // Kiểm tra tin mới mỗi 5s (nếu không dùng WebSocket)
	};
	
	function getCurrentUserId() {
	    const userMetaTag = document.querySelector('meta[name="user-id"]');
	    if (userMetaTag) {
	        return userMetaTag.getAttribute('content');
	    }
	    
	    const cookies = document.cookie.split(';');
	    for (let cookie of cookies) {
	        const [name, value] = cookie.trim().split('=');
	        if (name === 'userId') {
	            return value;
	        }
	    }
	    
	    return 'anonymous_' + getOrCreateAnonymousId();
	}

	function getOrCreateAnonymousId() {
	    let anonId = localStorage.getItem('oneshop_anonymous_id');
	    if (!anonId) {
	        anonId = 'anon_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9);
	        localStorage.setItem('oneshop_anonymous_id', anonId);
	    }
	    return anonId;
	}

	function getStorageKey() {
	    const userId = getCurrentUserId();
	    return CONFIG.STORAGE_KEY + '_' + userId;
	}

	// ========================================
	// STATE MANAGEMENT
	// ========================================
	let state = {
	    sessionId: null,
	    isOpen: false,
	    messages: [],
	    isTyping: false,
	    lastMessageTime: null,
	    pollTimer: null
	};

	// ========================================
	// INITIALIZE
	// ========================================
	function init() {
	    console.log('🚀 Initializing OneShop Chat Widget...');
	    loadSession();
	    renderWidget();
	    bindEvents();
	    
	    if (state.sessionId) {
	        loadChatHistory();
	    }
	    startPolling();
	    
	    console.log('✅ Chat Widget initialized');
	}

	// ========================================
	// SESSION MANAGEMENT
	// ========================================
	function loadSession() {
	    const storageKey = getStorageKey();
	    const saved = localStorage.getItem(storageKey);
	    if (saved) {
	        try {
	            const data = JSON.parse(saved);
	            state.sessionId = data.sessionId;
	            console.log('📦 Loaded session:', state.sessionId, 'for user:', getCurrentUserId());
	        } catch (e) {
	            console.error('❌ Failed to load session:', e);
	        }
	    }
	}

	function saveSession() {
	    const storageKey = getStorageKey();
	    localStorage.setItem(storageKey, JSON.stringify({
	        sessionId: state.sessionId,
	        timestamp: new Date().toISOString(),
	        userId: getCurrentUserId()
	    }));
	    console.log('💾 Saved session for user:', getCurrentUserId());
	}

	async function createSession() {
           try {
              // Lấy ID ẩn danh duy nhất
              const anonId = getOrCreateAnonymousId();
              const response = await fetch(CONFIG.API_BASE + '/init', {
                 method: 'POST',
                 headers: {
                    'Content-Type': 'application/json'
                 },
                 body: JSON.stringify({
                    tenKhach: anonId,
                    emailKhach: ''
                 })
              });

              if (!response.ok) {
                  throw new Error('Failed to init session from server');
              }

              const data = await response.json();
              state.sessionId = data.sessionId;
              saveSession();

              console.log('✅ Session created:', state.sessionId, 'for anonId:', anonId);
              return true;
           } catch (error) {
              console.error('❌ Failed to create session:', error);
              return false;
           }
        }

	// ========================================
	// RENDER HTML
	// ========================================
	function renderWidget() {
		const html = `
            <div id="chat-widget">
                <!-- Nút toggle -->
                <button class="chat-toggle-btn" id="chatToggleBtn">
                    <i class="fa fa-comments"></i>
                    <span class="chat-badge" id="chatBadge" style="display: none;">0</span>
                </button>
                
                <!-- Chat box -->
                <div class="chat-box" id="chatBox">
                    <!-- Header -->
                    <div class="chat-header">
                        <div class="chat-header-title">
                            <div>
                                <h3>💬 OneShop Support</h3>
                                <div class="status">
                                    <span class="status-dot"></span>
                                    <span>Hỗ trợ trực tuyến</span>
                                </div>
                            </div>
                        </div>
                        <button class="chat-close-btn" id="chatCloseBtn">
                            <i class="fa fa-times"></i>
                        </button>
                    </div>
                    
                    <!-- Body -->
                    <div class="chat-body" id="chatBody">
                        <!-- Welcome message -->
                        <div class="chat-welcome">
                            <h4>👋 Xin chào!</h4>
                            <p>Chúng tôi có thể giúp gì cho bạn?</p>
                            <p style="margin-top: 8px;">
                                <strong>⏰ Giờ hỗ trợ:</strong> Thứ Hai đến Thứ Bảy: 9am - 10pm
                                                               Chủ Nhật: 10am - 6pm<br>
                                <strong>📞 Hotline:</strong> +1234567890
                                <br></br>
                                <strong>📧 Email:</strong> sponeshop99@gmail.com
                            </p>
                        </div>
                        
                        <!-- Messages will be inserted here -->
                        
                        <!-- Typing indicator -->
                        <div class="typing-indicator" id="typingIndicator">
                            <span style="font-size: 12px; color: #6c757d;">Admin đang nhập</span>
                            <div class="typing-dots">
                                <div class="typing-dot"></div>
                                <div class="typing-dot"></div>
                                <div class="typing-dot"></div>
                            </div>
                        </div>
                    </div>
                    
                    <!-- Footer -->
                    <div class="chat-footer">
                        <input 
                            type="text" 
                            class="chat-input" 
                            id="chatInput" 
                            placeholder="Nhập tin nhắn..."
                            autocomplete="off"
                        />
                        <button class="chat-send-btn" id="chatSendBtn">
                            <i class="fa fa-paper-plane"></i>
                        </button>
                    </div>
                </div>
            </div>
        `;

		document.body.insertAdjacentHTML('beforeend', html);
	}

	// ========================================
	// EVENT HANDLERS
	// ========================================
	function bindEvents() {
		// Toggle chat box
		document.getElementById('chatToggleBtn').addEventListener('click', toggleChat);
		document.getElementById('chatCloseBtn').addEventListener('click', closeChat);

		// Send message
		document.getElementById('chatSendBtn').addEventListener('click', sendMessage);
		document.getElementById('chatInput').addEventListener('keypress', function(e) {
			if (e.key === 'Enter') {
				sendMessage();
			}
		});
	}

	function toggleChat() {
		if (state.isOpen) {
			closeChat();
		} else {
			openChat();
		}
	}

	async function openChat() {
		state.isOpen = true;
		document.getElementById('chatBox').classList.add('active');
		document.getElementById('chatInput').focus();

		// Nếu chưa có session, tạo mới
		if (!state.sessionId) {
			await createSession();
		}

		// Load lịch sử nếu chưa load
		if (state.messages.length === 0) {
			await loadChatHistory();
		}
		if (!state.pollTimer)
		{
			startPolling();
		}
		
		// Hide badge
		updateBadge(0);
	}

	function closeChat() {
		state.isOpen = false;
		document.getElementById('chatBox').classList.remove('active');
	}

	// ========================================
	// MESSAGE HANDLING
	// ========================================
	let isRetryingMessage = false;

        async function sendMessage() {
           const input = document.getElementById('chatInput');
           const message = input.value.trim();

           if (!message) return;

           // Nếu đang retry, không làm gì cả
           if (isRetryingMessage) return;

           if (!state.sessionId) {
              console.log('⏳ Creating session first...');
              const created = await createSession();
              if (!created) {
                 alert('Không thể kết nối. Vui lòng thử lại!');
                 return;
              }
              console.log('✅ Session created:', state.sessionId);
           }

           // Clear input
           input.value = '';

           // Hiển thị tin nhắn ngay
           const tempMessage = {
              noiDung: message,
              loaiNguoiGui: 'CUSTOMER',
              thoiGian: new Date().toISOString(),
              tenNguoiGui: 'Bạn'
           };
           displayMessage(tempMessage);

           // Gửi lên server
           try {
              const response = await fetch(CONFIG.API_BASE + '/send', {
                 method: 'POST',
                 headers: {
                    'Content-Type': 'application/json'
                 },
                 body: JSON.stringify({
                    sessionId: state.sessionId,
                    noiDung: message
                 })
              });

              if (!response.ok) {
                 throw new Error('Failed to send message');
              }

              const data = await response.json();
              console.log('✅ Message sent:', data);
              state.messages.push(data);

           } catch (error) {
              console.error('❌ Failed to send message (session might be invalid):', error);

              console.warn('🔃 Session invalid. Clearing and retrying...');

              // 1. Xóa session hỏng
              state.sessionId = null;
              localStorage.removeItem(getStorageKey());

              // 2. Tạo session mới
              const created = await createSession();

              if (created) {
                 // 3. Gửi lại tin nhắn
                 console.log('✅ New session created. Retrying message...');
                 isRetryingMessage = true; // Đặt cờ

                 // Đặt lại input để gửi lại
                 input.value = message;
                 await sendMessage(); // Gọi lại hàm

                 isRetryingMessage = false; // Bỏ cờ
              } else {
                 // Nếu tạo session mới cũng thất bại, mới báo lỗi
                 alert('Không thể gửi tin nhắn. Lỗi kết nối máy chủ!');
                 input.value = message; // Trả lại tin nhắn cho người dùng
              }
           }
        }

	async function loadChatHistory() {
           if (!state.sessionId) return;

           try {
              const response = await fetch(
                 `${CONFIG.API_BASE}/history?sessionId=${state.sessionId}`
              );

              if (!response.ok) { // Thêm kiểm tra lỗi
                  throw new Error('Failed to load history, session might be invalid');
              }

              const messages = await response.json();
              console.log('📜 Loaded history:', messages.length, 'messages');
              state.messages = messages;

              // Hiển thị tất cả tin nhắn
              messages.forEach(msg => displayMessage(msg, false));

              // Scroll to bottom
              scrollToBottom();

           } catch (error) {
              console.error('❌ Failed to load history:', error);
              state.sessionId = null;
              localStorage.removeItem(getStorageKey());
              console.warn('🚮 Cleared invalid session from storage.');
           }
        }

	function displayMessage(message, shouldScroll = true) {
		const chatBody = document.getElementById('chatBody');
		const isCustomer = message.loaiNguoiGui === 'CUSTOMER';

		const messageId = `msg-${message.maTinNhan || Date.now()}`;
		if (document.getElementById(messageId)){  	
		    return;
		}
		
		const html = `
		    <div id="${messageId}" class="message-container ${isCustomer ? 'customer' : 'admin'}">
                <div class="message-bubble ${isCustomer ? 'customer' : 'admin'}">
                    ${escapeHtml(message.noiDung)}
                </div>
            </div>
            <div class="message-info" style="text-align: ${isCustomer ? 'right' : 'left'}">
                <span class="message-time">${formatTime(message.thoiGian)}</span>
                ${isCustomer ? '<span class="message-status"><i class="fa fa-check"></i></span>' : ''}
            </div>
        `;

		// Insert trước typing indicator
		const typingIndicator = document.getElementById('typingIndicator');
		typingIndicator.insertAdjacentHTML('beforebegin', html);

		if (shouldScroll && CONFIG.AUTO_SCROLL) {
			scrollToBottom();
		}
	}

	// ========================================
	// UTILITY FUNCTIONS
	// ========================================
	function scrollToBottom() {
		const chatBody = document.getElementById('chatBody');
		chatBody.scrollTop = chatBody.scrollHeight;
	}

	function updateBadge(count) {
		const badge = document.getElementById('chatBadge');
		if (count > 0) {
			badge.textContent = count;
			badge.style.display = 'flex';
		} else {
			badge.style.display = 'none';
		}
	}

	function formatTime(timestamp) {
		const date = new Date(timestamp);
		const hours = date.getHours().toString().padStart(2, '0');
		const minutes = date.getMinutes().toString().padStart(2, '0');
		return `${hours}:${minutes}`;
	}

	function escapeHtml(text) {
		const div = document.createElement('div');
		div.textContent = text;
		return div.innerHTML;
	}

	// ========================================
	// START
	// ========================================
	if (document.readyState === 'loading') {
		document.addEventListener('DOMContentLoaded', init);
	} else {
		init();
	}

	function startPolling()
	{
		if (state.pollTimer)
		{
			clearInterval(state.pollTimer);
		}
		
		//start polling
		state.pollTimer = setInterval(async () => {
			if (state.isOpen && state.sessionId){
				await checkNewMessages();
			}
		}, CONFIG.POLL_INTERVAL);
		console.log('✅ Polling started (every', CONFIG.POLL_INTERVAL / 1000, 'seconds)');
	}
	
	function stopPolling()
	{
	    if (state.pollTimer)
	    {
	        clearInterval(state.pollTimer) 
	        state.pollTimer = null;
	        console.log('🛑 Polling stopped');
	    }
	}
	async function checkNewMessages() {
	    if (!state.sessionId) return;
	    
	    try {
	        const response = await fetch(
	            `${CONFIG.API_BASE}/history?sessionId=${state.sessionId}`
	        );
	        const messages = await response.json();
	        
	        // So sánh với messages hiện tại
	        if (messages.length > state.messages.length) {
	            console.log('📬 New messages detected:', messages.length - state.messages.length);
	            
	            // Lấy tin nhắn mới (những tin chưa có)
	            const newMessages = messages.slice(state.messages.length);
	            
	            // Hiển thị từng tin mới
	            newMessages.forEach(msg => {
	                displayMessage(msg, true);
	                
	                // Nếu là tin từ admin, play sound (optional)
	                if (msg.loaiNguoiGui === 'ADMIN') {
	                    playNotificationSound();
	                }
	            });
	            
	            // Update state
	            state.messages = messages;
	        }
	        
	    } catch (error) {
	        console.error('❌ Failed to check new messages:', error);
	    }
	}

	function playNotificationSound() {
	    // Âm thanh thông báo đơn giản
	    try {
	        const audio = new Audio('data:audio/wav;base64,UklGRnoGAABXQVZFZm10IBAAAAABAAEAQB8AAEAfAAABAAgAZGF0YQoGAACBhYqFbF1fdJivrJBhNjVgodDbq2EcBj+a2/LDciUFLIHO8tiJNwgZaLvt559NEAxQp+PwtmMcBjiR1/LMeSwFJHfH8N2QQAoUXrTp66hVFApGn+DyvmwhBSuBzvLZiTYHGWiz7OeeSwwQUKrj8LVkHAU7k9n0yXkpBSd+zPLaizsIHGS57OihUBELTKXh8bllHgU7k9n0yXkpBSd+zPLaizsIHGS57OihUBELTKXh8bllHgU7k9n0yXkpBSd+zPLaizsIHGS57OihUBELTKXh8bllHgU7k9n0yXkpBSd+zPLaizsIHGS57OihUBELTKXh8bllHgU7k9n0yXkpBSd+zPLaizsIHGS57OihUBELTKXh8bllHgU7k9n0yXkpBSd+zPLaizsIHGS57OihUBELTKXh8bllHgU7k9n0yXkpBSd+zPLaizsIHGS57OihUBELTKXh8bllHgU7k9n0yXkpBSd+zPLaizsIHGS57OihUBELTKXh8bllHgU7k9n0yXkpBSd+zPLaizsIHGS57OihUBELTKXh8bllHgU7k9n0yXkpBSd+zPLaizsIHGS57OihUBELTKXh8bllHgU7k9n0yXkpBSd+zPLaizsIHGS57OihUBELTKXh8bllHgU7k9n0yXkpBSd+zPLaizsIHGS57OihUBELTKXh8bllHg==');
	        audio.volume = 0.3;
	        audio.play().catch(e => console.log('Cannot play sound:', e));
	    } catch (e) {
	        // Ignore sound errors
	    }
	}
	
	
	
})();