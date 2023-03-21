package kr.co.swiftER.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.swiftER.service.MemberService;
import kr.co.swiftER.vo.CSQuestionsVO;
import kr.co.swiftER.vo.CommunityArticleVO;
import kr.co.swiftER.vo.ERReviewVO;
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
	public Map<String, Integer> insertMember(Principal principal, @ModelAttribute("MemberVO") MemberVO vo, HttpServletRequest req){
		String regip = req.getRemoteAddr();
		vo.setRegip(regip);
		
		int result = service.insertMember(vo);
		Map<String, Integer> map = new HashMap<>();
		
		map.put("result", result);
		return map;
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
	
	/* 회원탈퇴 */
	@GetMapping("member/deleteMember")
	public String deleteMember(String uid) {
		return "member/deleteMember";
	}
	 
	/* 비밀번호 찾기 */
	@GetMapping("member/findPw")
	public String findPw() {
		return "member/findPw";
	}
	
	/* 비밀번호 변경 get */
	@GetMapping("member/changePw")
	public String changePw(Model model, Principal principal) {
		String uid = principal.getName();
		System.out.println(uid);
		model.addAttribute("uid", uid);
		return "member/changePw";
	}
	
	/* 비밀번호 변경 post */
	@ResponseBody
	@PostMapping("member/changePw")
	public Map<String, Integer> changePw(@RequestParam(value="pass2") String pass2, @RequestParam(value="uid") String uid) {
		int result = service.updatePass(pass2, uid);
		
		Map<String, Integer> map = new HashMap<>();
		
		map.put("result", result);
		return map;
	}
	
	/* 마이페이지 */
	@GetMapping("member/myPage")
	public String myPage(Principal principal, Model model) {
		String uid = principal.getName();
		
		MemberVO vo = service.selectMember(uid);
		
		List<CommunityArticleVO> cas = service.selectCaList(uid);
		
		for(CommunityArticleVO ca : cas) 
			ca.setRdate(vo.getRdate().substring(0,10));
		
		List<ERReviewVO> ers = service.selectErReviewList(uid);
		
		for(ERReviewVO er : ers)
			er.setRdate(vo.getRdate().substring(0,10));
		
		/* 게시글 수 */
		int ca = service.countCa(uid);
		
		model.addAttribute("vo", vo);
		model.addAttribute("ca", ca);
		model.addAttribute("cas", cas);
		model.addAttribute("ers", ers);
		
		System.out.println(vo.getNickname());
		System.out.println(vo.getGrade());
		return "member/myPage";
	}
	
	/* 작성한 글 페이지 */
	@GetMapping("member/articleList")
	public String articleList(String uid, Model model) {
		List<CommunityArticleVO> cas = service.selectCaListAll(uid);
		MemberVO vo = service.selectMember(uid);
		for(CommunityArticleVO ca : cas)
			ca.setRdate(vo.getRdate().substring(0,10));
		model.addAttribute("cas", cas);
		return "member/articleList";
	}
}