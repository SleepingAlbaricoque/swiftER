package kr.co.swiftER.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import kr.co.swiftER.service.AdminService;

@Controller
public class AdminController {

	@Autowired
	private AdminService service;
	
	@GetMapping("admin/main")
	public String main() {
		return "admin/admin_main";
	}
	@GetMapping("admin/member")
	public String member() {
		return "admin/admin_member";
	}
	
	@GetMapping("admin/member/view")
	public String memberView() {
		return "admin/admin_member_view";
	}
	
	@GetMapping("admin/cs/notice/list")
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
	
	@GetMapping("admin/cs/faq/list")
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
	
	@GetMapping("admin/cs/qna/list")
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
	
}
