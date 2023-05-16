package kr.co.swiftER.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.co.swiftER.entity.MemberEntity;
import kr.co.swiftER.security.MyUserDetails;
import kr.co.swiftER.service.MemberService;
import kr.co.swiftER.vo.CommunityArticleVO;
import kr.co.swiftER.vo.ERReviewVO;
import kr.co.swiftER.vo.MemberDoctorVO;
import kr.co.swiftER.vo.MemberHistoryVO;
import kr.co.swiftER.vo.MemberTermsVO;
import kr.co.swiftER.vo.MemberVO;

@Controller
public class MemberController {
	
	@Autowired MemberService service;
	
	/********************************************************************************************************/
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
	@PostMapping("member/registerNor")
	public String registerNor(MemberVO vo, HttpServletRequest req){
		String regip = req.getRemoteAddr();
		vo.setRegip(regip);
		
		int result = service.insertMember(vo);
		
		return "redirect:/index?success="+result;
	}
	
	/* 의사회원 가입*/
	@PostMapping("/member/registerDoc")
    public String registerDoc(@ModelAttribute("MemberVO") MemberVO vo, @RequestParam(value="kind") String kind, @RequestParam(value="specialty") String specialty, MultipartFile fname, MultipartHttpServletRequest req) {
		MemberDoctorVO dvo = new MemberDoctorVO();
		dvo.setKind(kind);
		dvo.setSpecialty(specialty);
		dvo.setMember_uid(vo.getUid());
		
		String regip = req.getRemoteAddr();
		vo.setRegip(regip);
		// 의사는 별명 = 이름
		vo.setNickname(vo.getName());
		
		service.insertMember(vo);
		
		if(!fname.isEmpty()) {
			List<MultipartFile> files = req.getFiles("fname");
			dvo.setFile(files.size());
			
			// 의사회원 DB에 insert
			for(MultipartFile file : files) {
				service.insertMemberDoctor(dvo, file);
			}
		}else {
			return null;
		}

		
		
		return "redirect:/index";
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
	
	/* 회원가입 유효성 검사 - nickname*/
	@ResponseBody
	@GetMapping("member/checkNick")
	public Map<String, Integer> countNick(String nickname){
		int result = service.countNick(nickname);
		
		Map<String, Integer> map = new HashMap<>();
		
		map.put("result", result);
		return map;
	}
	
	/* 회원탈퇴 get */
	@GetMapping("member/deleteMember")
	public String deleteMember(String uid) {
		return "member/deleteMember";
	}
	
	/* 회원탈퇴 post */
	@ResponseBody
	@PostMapping("member/deleteMember")
	public Map<String, Integer> deleteMember(@RequestParam(value="pass2") String pass2, Authentication authentication, HttpServletRequest request, HttpServletResponse response){
		
		MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();		
		
		MemberEntity vo = userDetails.getMember();
		System.out.println("uid : "+ vo.getUid());
		System.out.println("pass : " + pass2);
		System.out.println("vo.pass : " + vo.getPass());
		Map<String, Integer> map = new HashMap<>();
		
		boolean isSamePasswd = BCrypt.checkpw(pass2, vo.getPass());
		
		if(!isSamePasswd) {
			int result = 2;
			map.put("result", result);
		}else {
			// 의사인지 일반회원인지 체크 하는 것 넣어야 함
			int grade = service.checkGrade(vo.getUid());
			System.out.println(grade);
			if(grade == 2) {
				System.out.println("의사회원 회원 탈퇴 진행");
				service.deleteDoctor(vo.getUid());
				int result = service.deleteMember(vo.getUid());
				map.put("result", result);
				SecurityContextHolder.clearContext();
				new SecurityContextLogoutHandler().logout(request, response, authentication);
			}else {
				System.out.println("일반회원 회원 탈퇴 진행");
				
				int result = service.deleteMember(vo.getUid());
				map.put("result", result);
				SecurityContextHolder.clearContext();
				new SecurityContextLogoutHandler().logout(request, response, authentication);
			}
			
		}
		return map;
	}
	 
	/* 비밀번호 찾기 */
	@GetMapping("member/findPw")
	public String findPw() {
		return "member/findPw";
	}
	
	/* 비밀번호 변경 get */
	@GetMapping("member/changePw")
	public String changePw() {
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
		MemberDoctorVO dvo = service.selectDoctor(uid);
		
		List<CommunityArticleVO> cas = service.selectCaList(uid);
		
		for(CommunityArticleVO ca : cas) 
			ca.setRdate(ca.getRdate().substring(0,10));
		
		List<ERReviewVO> ers = service.selectErReviewList(uid);
		
		for(ERReviewVO er : ers)
			er.setRdate(er.getRdate().substring(0,10));
		
		/* 게시글 수 */
		int ca = service.countCa(uid);
		
		model.addAttribute("vo", vo);
		model.addAttribute("dvo", dvo);
		model.addAttribute("ca", ca);
		model.addAttribute("cas", cas);
		model.addAttribute("ers", ers);
		
		System.out.println(vo.getNickname());
		System.out.println(vo.getGrade());
		return "member/myPage";
	}
	
	/* 작성한 글 페이지 */
	@GetMapping("member/articleList")
	public String articleList(String uid, Model model, String pg) {
		/* 페이징 처리 */
		
		int currentPage = service.getCurrentPage(pg); // 현재 페이지 번호
		int total = service.selectCountArticleList(uid); // 내가 작성한 글 갯수
		
		
		int lastPageNum = service.getLastPageNum(total);// 마지막 페이지 번호
		int[] result = service.getPageGroup(currentPage, lastPageNum); // 페이지 그룹 번호
		int pageStartNum = service.getPageStartNum(total, currentPage); // 페이지 시작 번호
		int start = service.getLimitStart(currentPage); // 시작 인덱스
		// 페이징용
		int groups[] = service.getPageGroup(currentPage, lastPageNum);
		
		
		List<CommunityArticleVO> cas = service.selectCaListAll(uid, start);
		MemberVO vo = service.selectMember(uid);
		for(CommunityArticleVO ca : cas)
			ca.setRdate(vo.getRdate().substring(0,10));
		model.addAttribute("cas", cas);
		model.addAttribute("uid", uid);
		model.addAttribute("lastPageNum", lastPageNum);		
		model.addAttribute("currentPage", currentPage);		
		model.addAttribute("pageGroupStart", result[0]);
		model.addAttribute("pageGroupEnd", result[1]);
		model.addAttribute("pageStartNum", pageStartNum+1);
		model.addAttribute("groups", groups);
		return "member/articleList";
	}

	/* 작성한 리뷰 페이지 */
	@GetMapping("member/reviewList")
	public String reviewList(String uid, Model model, String pg) {
		/* 페이징 처리 */
		
		int currentPage = service.getCurrentPage(pg); // 현재 페이지 번호
		int total = service.selectCountReviewList(uid); // 내가 작성한 글 갯수
		
		
		int lastPageNum = service.getLastPageNum(total);// 마지막 페이지 번호
		int[] result = service.getPageGroup(currentPage, lastPageNum); // 페이지 그룹 번호
		int pageStartNum = service.getPageStartNum(total, currentPage); // 페이지 시작 번호
		int start = service.getLimitStart(currentPage); // 시작 인덱스
		// 페이징용
		int groups[] = service.getPageGroup(currentPage, lastPageNum);
		List<ERReviewVO> ers = service.selectErListAll(uid, start);
		model.addAttribute("ers", ers);
		model.addAttribute("uid", uid);
		model.addAttribute("lastPageNum", lastPageNum);		
		model.addAttribute("currentPage", currentPage);		
		model.addAttribute("pageGroupStart", result[0]);
		model.addAttribute("pageGroupEnd", result[1]);
		model.addAttribute("pageStartNum", pageStartNum+1);
		model.addAttribute("groups", groups);
		return "member/reviewList";
	}	
	
	/* 아이디 찾기(get) */
	@GetMapping("member/findId")
	public String findId() {
		return "member/findId";
	}
	
	/* 아이디 찾기(Post) */
	@ResponseBody
	@PostMapping("member/findId")
	public Map<String, MemberVO> findId(@RequestParam(value="name") String name, @RequestParam(value="email") String email, HttpSession sess) throws Exception {
		MemberVO vo = service.findId(name, email);
		Map<String, MemberVO> map = new HashMap<>();
		map.put("vo", vo);
		if(vo != null) {
			sess.setAttribute("member", vo);
		}
		return map;
	}
	
	/* 아이디 찾기 결과*/
	@GetMapping("member/findIdResult")
	public String findIdResult(Model model, HttpSession sess) {
		MemberVO member = (MemberVO) sess.getAttribute("member");
		//System.out.println("memberuid :" + member.getUid());
		model.addAttribute("member", member);
		return "member/findIdResult";
	}
	
	/* 회원정보수정(일반 get) */
	@GetMapping("member/changeNor")
	public String changeNor(Principal principal, Model model) {
		String uid = principal.getName();
		
		MemberVO vo = service.selectMember(uid);
		model.addAttribute("vo", vo);
		
		return "member/changeNor";
	}
	
	/* 회원정보수정(의사 get) */
	@GetMapping("member/changeDoc")
	public String changeDoc(Principal principal, Model model) {
		String uid = principal.getName();
		
		MemberVO vo = service.selectMember(uid);
		MemberDoctorVO dvo = service.selectDoctor(uid);
		
		model.addAttribute("dvo", dvo);
		model.addAttribute("vo", vo);
		
		return "member/changeDoc";
	}
	
	/* 회원정보수정(일반 post) */
	@ResponseBody
	@PostMapping("member/changeNor")
	public Map<String, Integer> changeNor(@ModelAttribute("MemberVO") MemberVO vo) {
		int result = service.changeNor(vo);
		Map<String, Integer> map = new HashMap<>();
		
		map.put("result", result);
		return map;
	}
	
	/* 회원정보수정(의사 post) */
	@ResponseBody
	@PostMapping("member/changeDoc")
	public Map<String, Integer> changeDoc(@ModelAttribute("MemberDoctorVO") MemberDoctorVO dvo) {
		int result = service.changeDoc(dvo);
		Map<String, Integer> map = new HashMap<>();
		
		map.put("result", result);
		return map;
	}
	
	/* 마이페이지 간편이력 부분 */
	@ResponseBody
	@PostMapping("member/note")
	public Map<String, Integer> insertNote(@ModelAttribute("MemberHistoryVO") MemberHistoryVO hvo){
		int check = service.checkHistory(hvo);
		// 체크해서 0이면 insert실행
		if(check == 0) {
			int result = service.insertNote(hvo); // 1반환
			Map<String, Integer> map = new HashMap<>();
			map.put("result", result);
			return map;
		// 이미 존재하는 경우는 update 실행
		}else {
			int result = service.updateNote(hvo); // 2반환
			Map<String, Integer> map = new HashMap<>();
			map.put("result", result);
			return map;
		}
		
	}
	
	/*날짜 선택하여 정보 받아오기 */
	@ResponseBody
	@PostMapping("member/findHistory")
	public Map<String, MemberHistoryVO> findHistory(@ModelAttribute("MemberHistoryVO") MemberHistoryVO hvo){
		
		List<MemberHistoryVO> history = service.selectHistories(hvo);
		System.out.println(history);
		
		Map<String, MemberHistoryVO> map = new HashMap<>();
		for(MemberHistoryVO hs: history)
			map.put("hs", hs);
		
		return map;
	}
	
	/* 카카오 */
	@ResponseBody
	@RequestMapping("/kakao/kakaoAuth")
	public void kakaoCallback(@RequestParam String code, Model model) throws Exception {

        System.out.println("code : " + code);
        String access_Token = service.getKaKaoAccessToken(code);
        service.createKakaoUser(access_Token);
	}
	
	
	// 403
	@GetMapping("member/accessDenied")
	public String showAccessDeniedPage() {
	    return "member/accessDenied";
	}
	
}