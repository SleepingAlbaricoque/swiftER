package kr.co.swiftER.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import jakarta.mail.Multipart;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
	public String qna(@RequestParam(value="subcateCode", defaultValue = "0") String subcateCode, @RequestParam(value="pg", defaultValue="1") String pg, Model model) {
		// 페이징 처리
		int total = service.selectCountTotal("3", subcateCode);
		int currentPage = service.getCurrentPage(pg);
		int start = service.getLimitStart(currentPage, 10);
		int lastPageNum = service.getLastPageNum(total, 10);
		int startPageNum = service.getPageStartNum(total, start);
		int groups[] = service.getPageGroup(currentPage, lastPageNum, 10);
		
		model.addAttribute("groups", groups);
	    model.addAttribute("currentPage", currentPage);
	    model.addAttribute("lastPageNum", lastPageNum);
	    model.addAttribute("startPageNum", startPageNum);
		
		// qna 글들 불러오기
		List<CSQuestionsVO> qnaList = new ArrayList<>();
		
		// cate, subcate 코드에 따라 해당하는 글들을 가져오기
		qnaList = service.selectArticles("3", subcateCode, start);
		
		for(CSQuestionsVO qna : qnaList) {
			// rdate 날짜까지만 표시하기
			qna.setRdate(qna.getRdate().substring(0, 11));
			
			// 아이디 마스킹 처리 - 프론트에서 하는 것보다 여기서 마스킹하는 것이 사용자가 마스킹 전의 데이터에 접근할 가능성을 낮춤
			qna.setMember_uid(qna.getMember_uid().substring(0, qna.getMember_uid().length() - 3).concat("***"));
		}
		
		// 화면에 출력할 글들 저장
		model.addAttribute("qnaList", qnaList);
		// select 박스에서 사용자가 선택한 옵션이 페이지 로드시 가장 상단에 보이도록 하기 위해서는 subcateCode값 저장해야 함
		model.addAttribute("subcateCode", subcateCode);
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
	public String qnaWrite(@ModelAttribute("CSQuestionsVO") CSQuestionsVO article, MultipartHttpServletRequest req){
		// 작성자(현재 로그인 되어있는 사용자)의 정보 가져오려면 principal 객체를 현재 메서드의 파라미터로 줘서 principal 객체에 .getName()하면 됨
		
		// 사용자가 업로드한 파일들 가져오기
		List<MultipartFile> files = req.getFiles("fname");
		
		// CSQuestionsVO 객체에 not null 속성 값 채우기(rdate는 쿼리문에서 처리)
		article.setMember_uid("admin");
		article.setRegip(req.getRemoteAddr());
		article.setFile(files.size());
		
		// 사용자가 작성한 QnA DB에 insert
		service.insertArticle(article);
		
		for(MultipartFile file : files) {
			
			// DB에 파일 업로드
			service.uploadFile(file, article);
		}
		return "redirect:/cs/qna";
	}
}
