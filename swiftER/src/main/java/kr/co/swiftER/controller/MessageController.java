package kr.co.swiftER.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import kr.co.swiftER.entity.MessageEntity;
import kr.co.swiftER.repo.MessageRepo;

@Controller
public class MessageController {

	@Autowired
	private MessageRepo repo;
	
	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;
	
	@GetMapping("/conversation")
	public String getConversationList(Model model, Principal principal) {
		// 현재 로그인한 사용자 정보 가져오기
		String username = principal.getName();
		
		//messages 테이블에서 현재 로그인한 사용자가 보냈거나 받은 메세지 모두 받아오기
		List<MessageEntity> messages = repo.findByReceiverOrSender(username, username);
		
		// 현재 사용자와 타 사용자 간 마지막 메세지 정보를 저장하기 위한 map 객체 생성
		Map<String, MessageEntity> lastMessageMap = new HashMap<>();
		
		for(MessageEntity message : messages) {
			// 사용자의 대화 상대 정보를 담기 위한 객체
			String otherUser;
			
			// 메세지의 송신자가 현재 사용자와 같을 경우 메세지의 수신자를 otherUser로 지정
			if(message.getSender().equals(username)) {
				otherUser = message.getReceiver();
			// 메세지의 수신자가 현재 사용자와 같을 경우 메세지의 송신자를 otherUser로 지정
			}else {
				otherUser = message.getSender();
			}
			
			// 이미 해당 otherUser가 map 객체에 들어가있는 지 확인
			MessageEntity lastMessage = lastMessageMap.get(otherUser);
			
			// 해당 otherUser이 map 객체에 아직 추가되기 전이거나, 추가되어있는 메세지가 현재 for loop의 메세지보다 오래된 경우 현재 for loop의 otherUser 및 메세지로 다시 map 객체에 넣기
			if(lastMessage == null || message.getRdate().compareTo(lastMessage.getRdate()) >0) {
				lastMessageMap.put(otherUser, message);
			}
		}
		
		model.addAttribute("lastMessages", lastMessageMap.values());
		model.addAttribute("currentUser", username);
		return "messages/conversation-list";
	}
	
	@GetMapping("/conversation/{username}")
	public String getConversation(Model model, Principal principal, @PathVariable String username) {
		// 현재 사용자와 username을 가진 유저와의 메세지 가져오기
		List<MessageEntity> messages = repo.findByReceiverAndSenderOrSenderOrReceiver(principal.getName(), username, username, principal.getName());
		model.addAttribute("otherUser", username);
		model.addAttribute("currentUser", principal.getName());
		model.addAttribute("messages", messages);
		return "messages/conversation";
	}
	
	@MessageMapping("/chat")
	public void sendMessage(MessageEntity message, Principal principal) {
		message.setSender(principal.getName());
		message.setRdate(LocalDateTime.now());
		repo.save(message);
		simpMessagingTemplate.convertAndSendToUser(message.getReceiver(), "/topic/" + message.getReceiver(), message);
	}
	
}
