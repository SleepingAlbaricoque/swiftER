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
	
	@GetMapping("admin/memberView")
	public String memberView() {
		return "admin/admin_member_view";
	}
	
	@GetMapping("admin/cs/noticeList")
	public String noticeList() {
		return "admin/admin_cs_notice_list";
	}
	
	@GetMapping("admin/cs/noticeView")
	public String noticeView() {
		return "admin/admin_cs_notice_view";
	}
	
	@GetMapping("admin/cs/noticeWrite")
	public String noticeWrite() {
		return "admin/admin_cs_notice_write";
	}
	
	@GetMapping("admin/cs/noticeModify")
	public String noticeModify() {
		return "admin/admin_cs_notice_modify";
	}
	
	@GetMapping("admin/cs/faqList")
	public String faqList() {
		return "admin/admin_cs_faq_list";
	}
	
	@GetMapping("admin/cs/faqView")
	public String faqView() {
		return "admin/admin_cs_faq_view";
	}
	
	@GetMapping("admin/cs/faqWrite")
	public String faqWrite() {
		return "admin/admin_cs_faq_write";
	}
	
	@GetMapping("admin/cs/faqModify")
	public String faqModify() {
		return "admin/admin_cs_faq_modify";
	}
	
	@GetMapping("admin/cs/qnaList")
	public String qnaList() {
		return "admin/admin_cs_qna_list";
	}
	
	@GetMapping("admin/cs/qnaView")
	public String qnaView() {
		return "admin/admin_cs_qna_view";
	}
	
	@GetMapping("admin/cs/qnaWrite")
	public String qnaWrite() {
		return "admin/admin_cs_qna_write";
	}
	
}
