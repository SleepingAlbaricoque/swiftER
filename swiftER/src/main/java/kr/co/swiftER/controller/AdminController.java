package kr.co.swiftER.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import kr.co.swiftER.service.AdminService;
import kr.co.swiftER.vo.AdminMemberSearchVO;
import kr.co.swiftER.vo.MemberVO;

@Controller
public class AdminController {

	@Autowired
	private AdminService service;
	
	@GetMapping("admin/main")
	public String main() {
		return "admin/admin_main";
	}
	@GetMapping("admin/member")
	public String member(@RequestParam(value="pg", defaultValue="1") String pg, @RequestParam(value="pg2", defaultValue="1") String pg2,  Model model) {
		// 페이징 처리 - 회원 출력
		
		
		
		// 모든 회원 불러오기
		List<AdminMemberSearchVO> members = service.selectMembers();
		
		for(AdminMemberSearchVO member : members) {
			// rdate 날짜만 나오게 잘라주기(ThymeLeaf #temporals 계속 오류나서 컨트롤러에서 처리; 타임리프 버전을 더 높은 걸로 받아야 실행되는듯)
			member.getMember().setRdate(member.getMember().getRdate().substring(0, 10));
		}
		
		// 회원 정보 저장
		model.addAttribute("members", members);
		
		return "admin/admin_member";
	}
	
	@GetMapping("admin/member/view")
	public String memberView() {
		return "admin/admin_member_view";
	}
	
	@GetMapping("admin/cs/notice")
	public String noticeList() {
		return "admin/admin_cs_notice_list";
	}
	
	@GetMapping("admin/cs/notice/view")
	public String noticeView() {
		return "admin/admin_cs_notice_view";
	}
	
	@GetMapping("admin/cs/notice/write")
	public String noticeWrite() {
		return "admin/admin_cs_notice_write";
	}
	
	@GetMapping("admin/cs/notice/modify")
	public String noticeModify() {
		return "admin/admin_cs_notice_modify";
	}
	
	@GetMapping("admin/cs/faq")
	public String faqList() {
		return "admin/admin_cs_faq_list";
	}
	
	@GetMapping("admin/cs/faq/view")
	public String faqView() {
		return "admin/admin_cs_faq_view";
	}
	
	@GetMapping("admin/cs/faq/write")
	public String faqWrite() {
		return "admin/admin_cs_faq_write";
	}
	
	@GetMapping("admin/cs/faq/modify")
	public String faqModify() {
		return "admin/admin_cs_faq_modify";
	}
	
	@GetMapping("admin/cs/qna")
	public String qnaList() {
		return "admin/admin_cs_qna_list";
	}
	
	@GetMapping("admin/cs/qna/view")
	public String qnaView() {
		return "admin/admin_cs_qna_view";
	}
	
	@GetMapping("admin/cs/qna/write")
	public String qnaWrite() {
		return "admin/admin_cs_qna_write";
	}
	
	@GetMapping("/admin/er/review")
	public String erReview() {
		return "admin/admin_erReview";
	}
	
	@GetMapping("/admin/er/review/detail")
	public String erReviewView() {
		return "admin/admin_erReview_view";
	}
	
	@GetMapping("/admin/community")
	public String community() {
		return "admin/admin_community";
	}
	
	@GetMapping("/admin/community/view")
	public String communityView() {
		return "admin/admin_community_view";
	}
}
