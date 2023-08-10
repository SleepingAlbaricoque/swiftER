package kr.co.swiftER.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.co.swiftER.autocomplete.Trie;
import kr.co.swiftER.entity.MemberEntity;
import kr.co.swiftER.entity.MessageEntity;
import kr.co.swiftER.repo.MemberRepo;
import kr.co.swiftER.repo.MessageRepo;

@Controller
public class MessageController {

	@Autowired
	private MessageRepo repo;
	
	@Autowired
	private MemberRepo memberRepo;
	
	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;
	
	@Autowired
	private Trie trie;
	
	@GetMapping("/conversation")
	public String getConversationList(Model model, Principal principal) {
		// 현재 로그인한 사용자 정보 가져오기
		String username = principal.getName();
		
		//messages 테이블에서 현재 로그인한 사용자가 보냈거나 받은 메시지 모두 받아오기
		List<MessageEntity> messages = repo.findByReceiverOrSender(username, username);
		
		// 현재 사용자와 타 사용자 간 마지막 메시지 정보를 저장하기 위한 map 객체 생성
		Map<String, MessageEntity> lastMessageMap = new HashMap<>();
		
		for(MessageEntity message : messages) {
			// 사용자의 대화 상대 정보를 담기 위한 객체
			String otherUser;
			
			// 메시지의 송신자가 현재 사용자와 같을 경우 메시지의 수신자를 otherUser로 지정
			if(message.getSender().equals(username)) {
				otherUser = message.getReceiver();
			// 메시지의 수신자가 현재 사용자와 같을 경우 메시지의 송신자를 otherUser로 지정
			}else {
				otherUser = message.getSender();
			}
			
			// 이미 해당 otherUser가 map 객체에 들어가있는 지 확인
			MessageEntity lastMessage = lastMessageMap.get(otherUser);
			
			// 해당 otherUser이 map 객체에 아직 추가되기 전이거나, 추가되어있는 메시지가 현재 for loop의 메시지보다 오래된 경우 현재 for loop의 otherUser 및 메시지로 다시 map 객체에 넣기
			if(lastMessage == null || message.getRdate().compareTo(lastMessage.getRdate()) >0) {
				lastMessageMap.put(otherUser, message);
			}
		}
		
		// lastMessageMap의 메시지를 최신순으로 정렬 => 대화 목록에서 대화탭을 최신순으로 정렬해서 보여 줄 수 있음
		List<Entry<String, MessageEntity>> sortedMsgList = new ArrayList<>(lastMessageMap.entrySet()); 
		sortedMsgList.sort(Entry.<String, MessageEntity>comparingByValue(Comparator.comparing(value -> value.getRdate())).reversed()); // lastMessageMap의 Entry를 rdate에 따라 desc 정렬
		
		Map<String, MessageEntity> sortedMsgMap = new LinkedHashMap<>(); // 넣는 순서대로 값을 꺼내기 위해서는 LinkedHashMap 필요
		for(Entry<String, MessageEntity> entry : sortedMsgList) // 정렬된 entry 값을 순서대로 LinkedHashMap 객체에 넣기
			sortedMsgMap.put(entry.getKey(), entry.getValue());
		
		
		model.addAttribute("lastMessages", sortedMsgMap);
		model.addAttribute("currentUser", username);
		return "messages/message";
	}
	
	@ResponseBody
	@GetMapping("/conversation/{username}")
	public List<MessageEntity> getConversation(Principal principal, @PathVariable String username) {
		// 현재 사용자와 username을 가진 유저와의 메시지 가져오기
		List<MessageEntity> messages = repo.findByReceiverAndSenderOrSenderAndReceiverOrderByRdateAsc(principal.getName(), username, principal.getName(), username);
		
		// 읽지 않은 메시지를 읽음으로 표시(DB에 isRead값을 0에서 1로 바꾸기)
		for(MessageEntity message : messages) {
			if(message.getIsRead() ==0 && message.getReceiver().equals(principal.getName())) { // isRead값이 0이고, 수신인이 현재 로그인된 유저인 메시지만 선택
				message.setIsRead(1);
				repo.save(message);
			}
		}
		
		return messages;
	}
	
	@MessageMapping("/chat")
	public MessageEntity sendMessage(MessageEntity message, Principal principal) {
		String date = LocalDateTime.now().toString();
		
		message.setSender(principal.getName());
		message.setRdate(date.substring(0, 10) + " " + date.substring(11, 19));
		repo.save(message);
		simpMessagingTemplate.convertAndSendToUser(message.getReceiver(), "/topic/" + message.getSender(), message);
		simpMessagingTemplate.convertAndSendToUser(message.getReceiver(), "/queue/messages", message);
		return message;
	}
	
	@ResponseBody
	@GetMapping("message/search")
	public List<String> usernameSearch(String query){
		// member 테이블에서 uid 불러오기
		List<MemberEntity> members = memberRepo.findAll();
		List<String> usernames = new ArrayList<>();
		
		for(MemberEntity member : members) {
			usernames.add(member.getUid());
		}
		
		// trie tree에 검색 결과 저장
		for(String username: usernames) {
			trie.insert(username);
		}
		
		// autocomplete의 결과 저장; 검색 쿼리가 게시글 제목 맨 처음에 오는 경우만 검색 가능하고, 중간에 오는 경우는 검색 불가
		List<String> suggestions = trie.autoComplete(query);
		return suggestions;
	}
}
