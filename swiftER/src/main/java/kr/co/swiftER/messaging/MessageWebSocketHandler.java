package kr.co.swiftER.messaging;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class MessageWebSocketHandler extends TextWebSocketHandler{

	private static final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
	
	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) {
		String userId = (String) session.getAttributes().get("userId");
		sessions.put(userId, session);
	}
	
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) {
		String senderId = (String) session.getAttributes().get("userId");
		String receiverId = message.getPayload();
		messagingTemplate.convertAndSendToUser(receiverId, "/queue/messages", senderId);
	}
	
	public static WebSocketSession getSession(Long userId) {
		return sessions.get(userId);
	}
}
