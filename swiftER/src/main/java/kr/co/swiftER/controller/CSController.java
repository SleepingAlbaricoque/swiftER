package kr.co.swiftER.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.swiftER.exceptions.CustomException;
import kr.co.swiftER.service.CSService;
import kr.co.swiftER.vo.CSQuestionsVO;
import kr.co.swiftER.vo.FileVO;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

@Controller
public class CSController {

	@Autowired
	private CSService service;
	
	@Autowired
	private SqlSession sqlSession;
	
	@GetMapping("cs/index")
	public String index(Model model) {
		// 공지사항 불러오기
		List<CSQuestionsVO> notice = service.selectArticles("1", "0", 0, "");
		
		// QnA 불러오기
		List<CSQuestionsVO> qna = service.selectArticles("3", "0", 0, "");
		
		// 10개 항목 중 최신 다섯 개 글만 전달하기(매퍼에서 limit 5를 가지는 새로운 식을 짜거나, selectArticles() 메서드에 limit에 들어갈 값을 새로운 파라미터로 줄 수도 있지만 제일 깔끔한 방법은 컨트롤러에서 간단히 처리하는 것이라고 생각했음)
		CSQuestionsVO[] noticeList = new CSQuestionsVO[5];
		CSQuestionsVO[] qnaList = new CSQuestionsVO[5];
		
		for(int i = 0; i <5; i++) {
			noticeList[i] = notice.get(i);
			qnaList[i] = qna.get(i);
			
			// rdate 날짜까지만 표시하기
			noticeList[i].setRdate(noticeList[i].getRdate().substring(0, 11));
			qnaList[i].setRdate(qnaList[i].getRdate().substring(0, 11));
			
			// 아이디 마스킹 처리
			qnaList[i].setMember_uid(qnaList[i].getMember_uid().substring(0, qnaList[i].getMember_uid().length() - 3).concat("***"));
		}
		
		// 출력할 글 저장하기
		model.addAttribute("noticeList", noticeList);
		model.addAttribute("qnaList", qnaList);
		
		return "cs/index";
	}
	
	@GetMapping("cs/notice")
	public String notice(@RequestParam(value="subcateCode", defaultValue = "0") String subcateCode, @RequestParam(value="pg", defaultValue="1") String pg, @RequestParam(value="keyword", defaultValue="") String keyword, Model model) {
		// 페이징 처리
		int total = service.selectCountTotal("1", subcateCode, keyword);
		int currentPage = service.getCurrentPage(pg);
		int start = service.getLimitStart(currentPage, 10);
		int lastPageNum = service.getLastPageNum(total, 10);
		int startPageNum = service.getPageStartNum(total, start);
		int groups[] = service.getPageGroup(currentPage, lastPageNum, 10);
		
		model.addAttribute("groups", groups);
	    model.addAttribute("currentPage", currentPage);
	    model.addAttribute("lastPageNum", lastPageNum);
	    model.addAttribute("startPageNum", startPageNum);
		
	    // notice 글들 불러오기(사용자가 subcate값까지 고른 경우 subcate로 필터링한 결과 가져옴)
	    List<CSQuestionsVO> noticeList = service.selectArticles("1", subcateCode, start, keyword);
	    
	    for(CSQuestionsVO notice : noticeList) {
			// rdate 날짜까지만 표시하기
			notice.setRdate(notice.getRdate().substring(0, 11));
		}
	    
	    // 화면에 출력할 글들 저장 
	    model.addAttribute("noticeList", noticeList);
	    // 페이지 로드시 pg값에 맞는 페이지 버튼이 하이라이트되도록 pg값 저장
	 	model.addAttribute("pg", pg);
	 	// select 박스에서 사용자가 선택한 옵션이 페이지 로드시 가장 상단에 보이도록 하기 위해서는 subcateCode값 저장해야 함
	 	model.addAttribute("subcateCode", subcateCode);
	 	// 사용자가 검색 시 검색 결과 창에도 검색 키워드 출력해주기 위해 keyword값 저장
	 	model.addAttribute("keyword", keyword);
		
	 	return "cs/notice";
	}
	
	@GetMapping("cs/notice/view")
	public String noticeView(String no, Model model, Principal principal) {
		// 해당 no를 가진 notice 글 조회하기
		List<CSQuestionsVO> articles = service.selectArticle(no);
		CSQuestionsVO article = articles.get(0);
		List<FileVO> files = article.getFvoList();
		
		// 첨부 파일이 있으면 첨부 파일도 불러와서 저장하기
		if(article.getFile() >0) {
			model.addAttribute("files", files);
		}
		
		// 이전글, 다음글 조회하기
		CSQuestionsVO priorArticle = null; // 이전글
		CSQuestionsVO nextArticle = null; // 다음글
		List<CSQuestionsVO> articlesPriorAndNext = service.selectArticlesPriorAndNext(no);
		
		for(CSQuestionsVO vo : articlesPriorAndNext) {
			if(vo.getNo() < Integer.valueOf(no))
				priorArticle = vo;
			else if(vo.getNo() > Integer.valueOf(no))
				nextArticle = vo;
		}
		
		// 글 조회수 카운터 올리기
		if(principal.getName() != article.getMember_uid()) { // 현재 로그인된 유저와 글 작성자가 일치하지 않을 때만 카운트 올리기
			service.updateArticleView(no);
		}
		
		// 화면에 출력할 글 저장
		model.addAttribute("article", article);
		model.addAttribute("priorArticle", priorArticle);
		model.addAttribute("nextArticle", nextArticle);
		
		return "cs/notice_view";
	}
	
	@GetMapping("cs/faq")
	public String faq(@RequestParam(value="subcateCode", defaultValue = "0") String subcateCode, @RequestParam(value="keyword", defaultValue="") String keyword, Model model) {
		// faq 글들 불러오기
		List<CSQuestionsVO> faqList = new ArrayList<>();
		
		// FAQ 글 들 중 사용자가 선택한 subCate에 해당하는 글들을 가져오기(페이지 첫 로드시 subCate값은 회원정보로 고정)
		faqList = service.selectArticles("2", subcateCode, 0, keyword);
		
		model.addAttribute("faqList", faqList);
		model.addAttribute("subcateCode", subcateCode);
		
		return "cs/faq";
	}
	
	@ResponseBody
	@GetMapping("cs/faqByCate")
	public Map<String, List<CSQuestionsVO>> faqByCate(@RequestParam(value="subcateCode", defaultValue = "10") String subcateCode) {
		// 사용자가 faq 페이지에서 subcate 버튼을 누르면 해당 subcate에 해당하는 글만 json 형식으로 리턴하는 메서드
		// faq 글들 불러오기
		List<CSQuestionsVO> faqList = service.selectArticles("2", subcateCode, 0, "");
		
		// 뷰로 불러온 글들 전송하기
		Map<String, List<CSQuestionsVO>> result = new HashMap<>();
		result.put("faqList", faqList);
		return result;
	}
	
	@GetMapping("cs/qna")
	public String qna(@RequestParam(value="subcateCode", defaultValue = "0") String subcateCode, @RequestParam(value="pg", defaultValue="1") String pg, Model model) {
		// 페이징 처리
		int total = service.selectCountTotal("3", subcateCode, "");
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
		qnaList = service.selectArticles("3", subcateCode, start, "");
		
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
		// 페이지 로드시 pg값에 맞는 페이지 버튼이 하이라이트되도록 pg값 저장
		model.addAttribute("pg", pg);
		
		return "cs/qna";
	}
	
	@GetMapping("cs/qna/my")
	public String qnaMy(@RequestParam(value="subcateCode", defaultValue = "0") String subcateCode, @RequestParam(value="pg", defaultValue="1") String pg, Principal principal, Model model) {
		// 현재 로그인한 회원 이름 조회
		String username = principal.getName();
		
		// 페이징 처리
		int total = service.selectMyCountTotal("3", subcateCode, username);
		int myCurrentPage = service.getCurrentPage(pg);
		int start = service.getLimitStart(myCurrentPage, 10);
		int myLastPageNum = service.getLastPageNum(total, 10);
		int myStartPageNum = service.getPageStartNum(total, start);
		int myGroups[] = service.getPageGroup(myCurrentPage, myLastPageNum, 10);

		model.addAttribute("myGroups", myGroups);
		model.addAttribute("myCurrentPage", myCurrentPage);
		model.addAttribute("myLastPageNum", myLastPageNum);
		model.addAttribute("myStartPageNum", myStartPageNum);
		
		// qna 글들 불러오기
		List<CSQuestionsVO> myQnaList = new ArrayList<>();
		
		// cate, subcate 코드에 따라 해당하는 글들을 가져오기
		myQnaList = service.selectMyArticles("3", subcateCode, start, username);
		
		for(CSQuestionsVO qna : myQnaList) {
			// rdate 날짜까지만 표시하기
			qna.setRdate(qna.getRdate().substring(0, 11));
			
			int answer = service.selectCountQnaAnswer(qna.getNo());
			if(answer > 0)
				qna.setIsAnswered("답변 완료");
			else
				qna.setIsAnswered("대기중");
		}

		// 화면에 출력할 글들 저장
		model.addAttribute("myQnaList", myQnaList);
		// select 박스에서 사용자가 선택한 옵션이 페이지 로드시 가장 상단에 보이도록 하기 위해서는 subcateCode값 저장해야 함
		model.addAttribute("subcateCode", subcateCode);
		// 페이지 로드시 pg값에 맞는 페이지 버튼이 하이라이트되도록 pg값 저장
		model.addAttribute("pg", pg);
		
		return "cs/qna_my";
	}
	
	
	@GetMapping("cs/qna/view")
	public String qnaView(String no, Model model) {
		// 사용자가 선택한 글 불러오기
		List<CSQuestionsVO> list = sqlSession.selectList("kr.co.swiftER.dao.CSDAO.selectArticle", no);
		CSQuestionsVO article = list.get(0);
		List<FileVO> files = article.getFvoList();
		
		// rdate 날짜까지만 표시하기
		article.setRdate(article.getRdate().substring(0, 11));
		// 아이디 마스킹 처리
		article.setMember_uid(article.getMember_uid().substring(0, article.getMember_uid().length() - 3).concat("***"));
		
		// 불러온 글 저장
		model.addAttribute("article", article);
		
		// 첨부 파일이 있으면 첨부 파일도 불러와서 저장하기
		if(article.getFile() >0) {
			model.addAttribute("files", files);
		}
		
		return "cs/qna_view";
	}
	
	@GetMapping("download")
	public ResponseEntity<Resource> download(String parent, int num) throws IOException{
		// 파일 조회
		FileVO fvo = service.selectFileForDownload(parent, num);
		
		// 파일 다운로드
		return service.fileDownload(fvo);
	}
	
	@GetMapping("cs/qna/write")
	public String qnaWrite() {
		return "cs/qna_write";
	}
	
	@PostMapping("cs/qna/write")
	public String qnaWrite(@ModelAttribute("CSQuestionsVO") CSQuestionsVO article, MultipartHttpServletRequest req, Principal principal){
		// 작성자(현재 로그인 되어있는 사용자)의 정보 가져오려면 principal 객체를 현재 메서드의 파라미터로 줘서 principal 객체에 .getName()하면 됨
		String username = principal.getName();
		
		// CSQuestionsVO 객체에 속성 값 채우기(rdate는 쿼리문에서 처리)
		article.setMember_uid(username);
		article.setRegip(req.getRemoteAddr());
		
		// XSS 공격을 방지하기위해 Jsoup import해서 safelist에 등록된 태그만 허용 ex.<script> 태그 등은 허용하지 않음 - 화면에는 <script>로 출력되지만 db에는 &lt;script&gt;로 저장됨
		article.setContent(Jsoup.clean(article.getContent(), Safelist.basic())); 
		System.out.println(article.getContent());
		
		// 사용자가 업로드한 파일들 가져오고 article 객체의 file 속성값 정하기
		if(!article.getFname().isEmpty()) { // 첨부 파일이 한 개 이상인 경우
			List<MultipartFile> files = req.getFiles("fname");
			article.setFile(files.size());
			
			// 사용자가 작성한 QnA DB에 insert
			service.insertArticle(article);
			
			for(MultipartFile file : files) {
				// DB에 파일 업로드
				service.uploadFile(file, article);
			}
		}else { // 첨부 파일이 없는 경우
			
			// 사용자가 작성한 QnA DB에 insert
			service.insertArticle(article);
			
			article.setFile(0);
		}
		
		return "redirect:/cs/qna";
	}
}
