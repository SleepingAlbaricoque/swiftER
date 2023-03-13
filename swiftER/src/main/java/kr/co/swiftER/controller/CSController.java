package kr.co.swiftER.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import kr.co.swiftER.service.CSService;

@Controller
public class CSController {

	@Autowired
	private CSService service;
	
	@GetMapping("cs/index")
	public String index() {
		return "cs/index";
	}
	
	@GetMapping("cs/notice")
	public String notice() {
		return "cs/notice";
	}
	
	@GetMapping("cs/noticeView")
	public String noticeView() {
		return "cs/notice_view";
	}
	
	@GetMapping("cs/faq")
	public String faq() {
		return "cs/faq";
	}
	
	@GetMapping("cs/qna")
	public String qna() {
		return "cs/qna";
	}
	
	@GetMapping("cs/qnaView")
	public String qnaView() {
		return "qna_view";
	}
	
	@GetMapping("cs/qnaWrite")
	public String qna_write() {
		return "cs/qna_write";
	}
}
