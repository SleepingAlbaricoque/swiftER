package kr.co.swiftER.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.swiftER.exceptions.CustomException;
import kr.co.swiftER.service.CSService;
import kr.co.swiftER.vo.CSQuestionsVO;

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
	
	@GetMapping("cs/notice/view")
	public String noticeView() {
		return "cs/notice_view";
	}
	
	@GetMapping("cs/faq")
	public String faq() {
		return "cs/faq";
	}
	
	@GetMapping("cs/qna")
	public String qna(Model model) {
		List<CSQuestionsVO> qnaList = new ArrayList<>();
		qnaList = service.selectArticles();
		
		for(CSQuestionsVO qna : qnaList)
			qna.setRdate(qna.getRdate().substring(0, 11));
		
		model.addAttribute("qnaList", qnaList);
		
		return "cs/qna";
	}
	
	@GetMapping("cs/qna/view")
	public String qnaView(String no) {
		return "qna_view";
	}
	
	@GetMapping("cs/qna/write")
	public String qnaWrite() {
		return "cs/qna_write";
	}
	
	@PostMapping("cs/qna/write")
	public String qnaWrite(@ModelAttribute("CSQuestionsVO") CSQuestionsVO article, HttpServletRequest req){
		// 작성자(현재 로그인 되어있는 사용자)의 정보 가져오려면 principal 객체를 현재 메서드의 파라미터로 줘서 principal 객체에 .getName()하면 됨
		
		// CSQuestionsVO 객체에 not null 속성 값 채우기(rdate는 쿼리문에서 처리)
		article.setMember_uid("admin");
		article.setRegip(req.getRemoteAddr());
		
		// 사용자가 작성한 QnA DB에 insert
		service.insertArticle(article);
		
		return "redirect:/cs/qna";
	}
}
