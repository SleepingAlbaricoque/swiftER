package kr.co.swiftER.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import kr.co.swiftER.service.MemberService;
import kr.co.swiftER.vo.MemberTermsVO;

@Controller
public class MemberController {
	
	@Autowired MemberService service;
	
	/* 로그인 페이지 */
	
	@GetMapping("member/login")
	public String login() {
		return "member/login";
	}

	
	/* 이용약관 페이지(get_ */
	@GetMapping("member/terms")
	public String terms(Model model) {
		
		MemberTermsVO vo = service.selectTerms();
		model.addAttribute("vo", vo);
		
		return "member/terms";
	}
	
	/* 일반회원 가입 페이지(get) */
	@GetMapping("member/registerNor")
	public String registerNor() {
		
		return "member/registerNor";
	}
	
	/* 의사회원 가입 페이지(get) */
	@GetMapping("member/registerDoc")
	public String registerDoc() {
		
		return "member/registerDoc";
	}
	
}
