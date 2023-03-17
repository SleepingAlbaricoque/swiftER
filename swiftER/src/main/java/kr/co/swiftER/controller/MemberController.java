package kr.co.swiftER.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.swiftER.service.MemberService;
import kr.co.swiftER.vo.CSQuestionsVO;
import kr.co.swiftER.vo.MemberTermsVO;
import kr.co.swiftER.vo.MemberVO;
import lombok.RequiredArgsConstructor;

@Controller
public class MemberController {
	
	@Autowired MemberService service;
	
	/* 로그인 페이지 */
	
	@GetMapping("member/login")
	public String loginError(@RequestParam(value = "error", required = false)
										String error, @RequestParam(value = "exception", required = false )
										String exception,Model model) {
		model.addAttribute("error", error);
		model.addAttribute("exception", exception);
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
	
	/* 일반회원 가입(post) */
	@ResponseBody
	@PostMapping("member/insertMember")
	public String insertMember(Principal principal, @ModelAttribute("MemberVO") MemberVO vo, HttpServletRequest req){
		String regip = req.getRemoteAddr();
		vo.setRegip(regip);
		
		int result = service.insertMember(vo);
		return "redirect:/index?success="+result;
	}
	
	
	
	/* 회원가입 유효성 검사 - id*/
	@ResponseBody
	@GetMapping("member/checkUid")
	public Map<String, Integer> countUid(String uid){
		int result = service.countUid(uid);
		
		Map<String, Integer> map = new HashMap<>();
		
		map.put("result", result);
		return map;
	}
	
	 
	
}