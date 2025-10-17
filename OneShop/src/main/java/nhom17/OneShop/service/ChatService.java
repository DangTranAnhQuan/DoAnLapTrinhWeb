package nhom17.OneShop.service;

import nhom17.OneShop.dto.ChatMessageDTO;
import nhom17.OneShop.dto.ConversationDTO;

import java.util.List;

public interface ChatService {

    String getOrCreateSessionId(Integer maNguoiDung, String tenKhach, String emailKhach);

    ChatMessageDTO sendMessage(String sessionId, String noiDung, String loaiNguoiGui, Integer maNguoiDung);

    List<ChatMessageDTO> getChatHistory(String sessionId);

    List<ConversationDTO> getAllConversations();

    List<ConversationDTO> getUnreadConversations();

    void markConversationAsRead(String sessionId);

    void closeConversation(String sessionId);

    long getTotalUnreadCount();
}

