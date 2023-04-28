package kr.co.swiftER.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import kr.co.swiftER.entity.MemberEntity;
import kr.co.swiftER.entity.MessageEntity;
import kr.co.swiftER.repo.MessageRepository;

@Controller
public class MessageController {

	@Autowired
	private MessageRepository repo;
	
	@GetMapping("/inbox")
	public String inbox(Model model, @AuthenticationPrincipal MemberEntity member) {
		List<MemberEntity> members = new ArrayList<>();
		for(MessageEntity message : repo.findByReceiverOrSenderOrderByTimestampDesc(member, member)) {
			MemberEntity otherMember = message.getSender().equals(member)? message.getReceiver() : message.getSender();
			if(!members.contains(otherMember)) {
				members.add(otherMember);
			}
		}
		model.addAttribute("members", members);
		return "inbox";
	}
}
