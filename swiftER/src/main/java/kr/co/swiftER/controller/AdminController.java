package kr.co.swiftER.controller;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.swiftER.service.AdminService;
import kr.co.swiftER.vo.AdminMemberModifyVO;
import kr.co.swiftER.vo.AdminMemberSearchVO;
import kr.co.swiftER.vo.CSQuestionsVO;
import kr.co.swiftER.vo.CommunityArticleVO;
import kr.co.swiftER.vo.ERCateVO;
import kr.co.swiftER.vo.ERReviewVO;
import kr.co.swiftER.vo.ERSubcateVO;
import kr.co.swiftER.vo.FileVO;
import kr.co.swiftER.vo.MemberVO;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Controller
public class AdminController {

	@Autowired
	private AdminService service;
	
	@GetMapping("admin/main")
	public String main() {
		return "admin/admin_main";
	}
	
	// 멤버
	
	@GetMapping("admin/member")
	public String member(@RequestParam(value="pg", defaultValue="1") String pg, @RequestParam(value="docPg", defaultValue="1") String docPg, Model model) {		
		// 페이징 처리 - 모든 회원 불러오기
		int total = service.selectCountTotal();
		int currentPage = service.getCurrentPage(pg);
		int start = service.getLimitStart(currentPage, 5);
		int lastPageNum = service.getLastPageNum(total, 5);
		int startPageNum = service.getPageStartNum(total, start);
		int groups[] = service.getPageGroup(currentPage, lastPageNum, 5);
		
		model.addAttribute("groups", groups);
	    model.addAttribute("currentPage", currentPage);
	    model.addAttribute("lastPageNum", lastPageNum);
	    model.addAttribute("startPageNum", startPageNum);
	    
		// 모든 회원 불러오기
		List<AdminMemberSearchVO> members = service.selectMembers(start, 0);
		
		for(AdminMemberSearchVO member : members) {
			// rdate 날짜만 나오게 잘라주기(ThymeLeaf #temporals 계속 오류나서 컨트롤러에서 처리; 타임리프 버전을 더 높은 걸로 받아야 실행되는듯)
			member.getMember().setRdate(member.getMember().getRdate().substring(0, 10));
		}
	    
	    // 페이징 처리 - 인증 승인 요청한 의사 불러오기
 		int docTotal = service.selectDocCountTotal();
 		int docCurrentPage = service.getCurrentPage(docPg);
 		int docStart = service.getLimitStart(docCurrentPage, 5);
 		int docLastPageNum = service.getLastPageNum(docTotal, 5);
 		int docStartPageNum = service.getPageStartNum(docTotal, docStart);
 		int docGroups[] = service.getPageGroup(docCurrentPage, docLastPageNum, 5);
 		
 		model.addAttribute("docGroups", docGroups);
 	    model.addAttribute("docCurrentPage", docCurrentPage);
 	    model.addAttribute("docLastPageNum", docLastPageNum);
 	    model.addAttribute("docStartPageNum", docStartPageNum);
		
		// 인증 승인 요청한 의사 불러오기
 	    List<AdminMemberSearchVO> docsToVerify = service.selectMembers(docStart, 1); // 전체 회원과 다른 pg 값을 갖기 때문에 selectMembers() 한 번만 불러서는 서로 다른 start값을 넣을 수 없음 => selectMembers() 한 번 해서 verified 0 인 객체만 새로 doc 리스트에 넣을 수 x
 	    
 	    for(AdminMemberSearchVO doc : docsToVerify) {
 	    	// rdate 날짜만 나오게 잘라주기
 	    	doc.getMember().setRdate(doc.getMember().getRdate().substring(0, 10));
 	    }
 	    
		// 회원 정보 저장
		model.addAttribute("members", members);
		model.addAttribute("docs", docsToVerify);
		// 페이지 로드시 pg값에 맞는 페이지 버튼이 하이라이트되도록, 컨트롤러 호출할 때 사용하도록 pg값 저장
		model.addAttribute("pg", pg);
		model.addAttribute("docPg", docPg);
		// 글에 인덱스 번호 매기기 위해서 필요
		model.addAttribute("start", start);
		model.addAttribute("docStart", docStart);
		
		return "admin/admin_member";
	}
	
	@GetMapping("admin/member/view")
	public String memberView(String uid, Model model) {
		// uid값에 해당하는 회원 정보 불러오기
		AdminMemberSearchVO member = service.selectMember(uid);
		
		// 회원 정보 저장
		model.addAttribute("member", member);
		
		return "admin/admin_member_view";
	}
	
	@ResponseBody
	@GetMapping("admin/member/ban")
	public Map<String, Integer> permaBan(String uid) {
		// uid값에 해당하는 회원의 grade를 4로 조정, wdate(탈퇴 날짜) 기록
		int result = service.banMember(uid);
		
		Map<String, Integer> resultMap = new HashMap<>();
		resultMap.put("result", result);
		
		return resultMap;
	}
	
	@GetMapping("admin/member/popup")
	public String memberPopup(String uid, Model model) {
		// uid값에 해당하는 의사 회원의 면허증 사진 정보 가져오기
		FileVO cert = service.selectDocCert(uid);
		
		// src 폴더에는 newName으로 저장되어 있으므로 newName 값을 저장하기
		String newName = cert.getNewName();
		model.addAttribute("newName", newName);
		System.out.println(newName);
		
		return "admin/admin_member_popup";
	}
	
	@ResponseBody
	@GetMapping("admin/member/verify")
	public Map<String, Integer> certVerify(String uid, String status){
		int result = service.certVerify(uid, status);
		
		Map<String, Integer> resultMap = new HashMap<>();
		resultMap.put("result", result);
		
		return resultMap;
	}
	
	@PostMapping("admin/member/modify")
	@ResponseBody
	public Map<String, Integer> modifyMember(@RequestBody AdminMemberModifyVO member){
		int result = service.updateMember(member);
		
		Map<String, Integer> resultMap = new HashMap<>();
		resultMap.put("result", result);
		return resultMap;
	}
	
	
	// CS
	
	@GetMapping("admin/cs/delete")
	@ResponseBody
	public Map<String, Integer> deleteArticles(String[] checkedNo){
		int result = service.deleteArticles(checkedNo);
		
		Map<String, Integer> resultMap = new HashMap<>();
		resultMap.put("result", 1);
		return resultMap;
	}
	
	@GetMapping("admin/cs/notice")
	public String noticeList(@RequestParam(value="subcateCode", defaultValue = "0") String subcateCode, @RequestParam(value="pg", defaultValue="1") String pg, Model model) {
		// 페이징 처리 
		int total = service.selectCountArticlesTotal("1", subcateCode);
		int currentPage = service.getCurrentPage(pg);
		int start = service.getLimitStart(currentPage, 10);
		int lastPageNum = service.getLastPageNum(total, 10);
		int startPageNum = service.getPageStartNum(total, start);
		int groups[] = service.getPageGroup(currentPage, lastPageNum, 10);
		
		model.addAttribute("groups", groups);
	    model.addAttribute("currentPage", currentPage);
	    model.addAttribute("lastPageNum", lastPageNum);
	    model.addAttribute("startPageNum", startPageNum);
	    
	    // 모든 공지사항 글 불러오기
	    List<CSQuestionsVO> noticeList = service.selectArticles("1", subcateCode, start);
	    
	    // rdate 날짜만 나오게 substring하기
	    for(CSQuestionsVO notice : noticeList)
	    	notice.setRdate(notice.getRdate().substring(0, 10));
	    
	    // 화면에 출력할 글들 저장
	    model.addAttribute("noticeList", noticeList);
	    // 페이지 로드시 pg값에 맞는 페이지 버튼이 하이라이트되도록 pg값 저장
	    model.addAttribute("pg", pg);
	    // select 박스에서 사용자가 선택한 옵션이 페이지 로드시 가장 상단에 보이도록 하기 위해서는 subcateCode값 저장해야 함
	 	model.addAttribute("subcateCode", subcateCode);
	 	// 글에 인덱스 번호 매기기 위해서 필요
	 	model.addAttribute("start", start);
		
		return "admin/admin_cs_notice_list";
	}
	
	@GetMapping("admin/cs/notice/view")
	public String noticeView(String no, Model model) {
		// 글번호 argument를 이용해 글 정보 불러오기
		CSQuestionsVO article = service.selectArticle(no);
		List<FileVO> files = article.getFvoList();
		
		// 글 정보 저장하기
		model.addAttribute("article", article);
		
		// 첨부 파일이 있으면 첨부 파일도 불러와서 저장하기
		if(article.getFile() >0) {
			model.addAttribute("files", files);
		}
		
		return "admin/admin_cs_notice_view";
	}
	
	@GetMapping("admin/cs/notice/write")
	public String noticeWrite() {
		return "admin/admin_cs_notice_write";
	}
		
	@PostMapping("admin/cs/notice/write")
	public String noticeWrite(@ModelAttribute("CSQuestionsVO") CSQuestionsVO article, MultipartHttpServletRequest req, Principal principal) {
		// 작성자(현재 로그인 되어있는 사용자)의 정보 가져오려면 principal 객체를 현재 메서드의 파라미터로 줘서 principal 객체에 .getName()하면 됨
		// 관리자 권한(grade 0)을 가진 사람만 관리자 페이지에 접근할 수 있으므로 여기서는 따로 권한 체크 하지 않음 
		String username = principal.getName();
		
		// CSQuestionsVO 객체에 속성 값 채우기(rdate는 쿼리문에서 처리)
		article.setMember_uid(username);
		article.setRegip(req.getRemoteAddr());
		
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
		
		return "redirect:/admin/cs/notice";
	}
	
	@GetMapping("admin/cs/notice/modify")
	public String noticeModify(String no, Model model) {
		// 글번호 argument를 이용해 글 정보 불러오기
		CSQuestionsVO article = service.selectArticle(no);
		List<FileVO> files = article.getFvoList();
		
		// 글 정보 저장하기
		model.addAttribute("article", article);
		model.addAttribute("subcateCode", article.getSubcateCode());
		
		// 첨부 파일이 있으면 첨부 파일도 불러와서 저장하기
		if(article.getFile() >0) {
			model.addAttribute("files", files);
		}
				
		return "admin/admin_cs_notice_modify";
	}
	
	@PostMapping("admin/cs/notice/modify")
	public String noticeModify(@ModelAttribute("CSQuestionsVO") CSQuestionsVO article, String uploadedFile, MultipartHttpServletRequest req) {
		// CSQuestionsVO 객체에 속성 값 채우기(rdate는 쿼리문에서 처리)
		article.setRegip(req.getRemoteAddr());
		
		// XSS 공격을 방지하기위해 Jsoup import해서 safelist에 등록된 태그만 허용 ex.<script> 태그 등은 허용하지 않음 - 화면에는 <script>로 출력되지만 db에는 &lt;script&gt;로 저장됨
		article.setContent(Jsoup.clean(article.getContent(), Safelist.basic())); 
		
		// 사용자가 기존 첨부파일을 삭제할 경우 원래 첨부파일 리스트와 대조하기 위하여 원래 첨부파일 리스트 가져오기
		List<Integer> uploadedFnos = service.selectFnos(String.valueOf(article.getNo()));
		
		// 사용자가 업로드한 파일들 가져오고 article 객체의 file 속성값 정하기
		if(!article.getFname().isEmpty()) { // 첨부 파일이 한 개 이상인 경우
			List<MultipartFile> files = req.getFiles("fname");
			article.setFile(article.getFile() + files.size()); // 해당 글의 file 속성은 '기존 파일 갯수(getFile()) + 새로 첨부한 파일 갯수(files.size())'로 설정
			
			// file 테이블에 첨부파일 업로드
			for(MultipartFile file : files) {
				service.uploadFile(file, article);
			}
			
			// 해당 글에 첨부된 기존 파일들의 fno와 수정 폼에서 submit 된 파일들의 fno를 비교해서 submit된 파일 fno 리스트에 없는 기존 파일은 삭제하기
			if(uploadedFile != null) { // 기존 첨부파일을 사용자가 수정 시 모두 삭제하지 않은 경우
				List<String> filesNotToDelete = Arrays.asList(uploadedFile.split(","));
				for(Integer f : uploadedFnos) {
					if(!filesNotToDelete.contains(String.valueOf(f))) {
						service.deleteFile(String.valueOf(f));
						article.setFile(article.getFile() - 1); // 파일 삭제하고 해당 글의 file 속성값을 1만큼 낮추기
					}
				}
			}else { // 기존 첨부파일을 모두 삭제한 경우
				for(Integer f : uploadedFnos) {
					service.deleteFile(String.valueOf(f));
					article.setFile(article.getFile() - 1);
				}
			}
			
			
			// 수정된 글 DB에 업로드하기
			service.updateArticle(article);
			
		}else { // 새로운 첨부파일이 없는 경우
			if(uploadedFile != null) { // 기존 첨부파일을 사용자가 수정 시 모두 삭제하지 않은 경우
				List<String> filesNotToDelete = Arrays.asList(uploadedFile.split(","));
				for(Integer f : uploadedFnos) {
					if(!filesNotToDelete.contains(String.valueOf(f))) {
						service.deleteFile(String.valueOf(f));
						article.setFile(article.getFile() - 1); // 파일 삭제하고 해당 글의 file 속성값을 1만큼 낮추기
					}
				}
			}else { // 기존 첨부파일을 모두 삭제한 경우
				for(Integer f : uploadedFnos) {
					service.deleteFile(String.valueOf(f));
					article.setFile(article.getFile() - 1);
				}
			}
			
			// 수정된 글 DB에 업로드하기
			service.updateArticle(article);
		}
		
		return "redirect:/admin/cs/notice/view?no=" + article.getNo();
	}
	
	@GetMapping("admin/cs/faq")
	public String faqList(@RequestParam(value="subcateCode", defaultValue = "0") String subcateCode, @RequestParam(value="pg", defaultValue="1") String pg, Model model) {
		// 페이징 처리 
		int total = service.selectCountArticlesTotal("2", subcateCode);
		int currentPage = service.getCurrentPage(pg);
		int start = service.getLimitStart(currentPage, 10);
		int lastPageNum = service.getLastPageNum(total, 10);
		int startPageNum = service.getPageStartNum(total, start);
		int groups[] = service.getPageGroup(currentPage, lastPageNum, 10);
		
		model.addAttribute("groups", groups);
	    model.addAttribute("currentPage", currentPage);
	    model.addAttribute("lastPageNum", lastPageNum);
	    model.addAttribute("startPageNum", startPageNum);
	    
	    // 모든 공지사항 글 불러오기
	    List<CSQuestionsVO> faqList = service.selectArticles("2", subcateCode, start);
	    
	    // rdate 날짜만 나오게 substring하기
	    for(CSQuestionsVO faq : faqList)
	    	faq.setRdate(faq.getRdate().substring(0, 10));
	    
	    // 화면에 출력할 글들 저장
	    model.addAttribute("faqList", faqList);
	    // 페이지 로드시 pg값에 맞는 페이지 버튼이 하이라이트되도록 pg값 저장
	    model.addAttribute("pg", pg);
	    // select 박스에서 사용자가 선택한 옵션이 페이지 로드시 가장 상단에 보이도록 하기 위해서는 subcateCode값 저장해야 함
	 	model.addAttribute("subcateCode", subcateCode);
	 	// 글에 인덱스 번호 매기기 위해서 필요
	 	model.addAttribute("start", start);
		
		return "admin/admin_cs_faq_list";
	}
	
	@GetMapping("admin/cs/faq/view")
	public String faqView(String no, Model model) {
		// 글번호 argument를 이용해 글 정보 불러오기
		CSQuestionsVO article = service.selectArticle(no);
		List<FileVO> files = article.getFvoList();
		
		// 글 정보 저장하기
		model.addAttribute("article", article);
		
		// 첨부 파일이 있으면 첨부 파일도 불러와서 저장하기
		if(article.getFile() >0) {
			model.addAttribute("files", files);
		}
		
		return "admin/admin_cs_faq_view";
	}
	
	@GetMapping("admin/cs/faq/write")
	public String faqWrite() {
		return "admin/admin_cs_faq_write";
	}
	
	@PostMapping("admin/cs/faq/write")
	public String faqWrite(@ModelAttribute("CSQuestionsVO") CSQuestionsVO article, MultipartHttpServletRequest req, Principal principal) {
		// 작성자(현재 로그인 되어있는 사용자)의 정보 가져오려면 principal 객체를 현재 메서드의 파라미터로 줘서 principal 객체에 .getName()하면 됨
		// 관리자 권한(grade 0)을 가진 사람만 관리자 페이지에 접근할 수 있으므로 여기서는 따로 권한 체크 하지 않음 
		String username = principal.getName();
		
		// CSQuestionsVO 객체에 속성 값 채우기(rdate는 쿼리문에서 처리)
		article.setMember_uid(username);
		article.setRegip(req.getRemoteAddr());
		
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
		
		return "redirect:/admin/cs/faq";
	}
	
	@GetMapping("admin/cs/faq/modify")
	public String faqModify(String no, Model model) {
		// 글번호 argument를 이용해 글 정보 불러오기
		CSQuestionsVO article = service.selectArticle(no);
		List<FileVO> files = article.getFvoList();
		
		// 글 정보 저장하기
		model.addAttribute("article", article);
		model.addAttribute("subcateCode", article.getSubcateCode());
		
		// 첨부 파일이 있으면 첨부 파일도 불러와서 저장하기
		if(article.getFile() >0) {
			model.addAttribute("files", files);
		}
		
		return "admin/admin_cs_faq_modify";
	}
	
	@PostMapping("admin/cs/faq/modify")
	public String faqModify(@ModelAttribute("CSQuestionsVO") CSQuestionsVO article, String uploadedFile, MultipartHttpServletRequest req) {
		// CSQuestionsVO 객체에 속성 값 채우기(rdate는 쿼리문에서 처리)
		article.setRegip(req.getRemoteAddr());
		
		// XSS 공격을 방지하기위해 Jsoup import해서 safelist에 등록된 태그만 허용 ex.<script> 태그 등은 허용하지 않음 - 화면에는 <script>로 출력되지만 db에는 &lt;script&gt;로 저장됨
		article.setContent(Jsoup.clean(article.getContent(), Safelist.basic())); 
		
		// 사용자가 기존 첨부파일을 삭제할 경우 원래 첨부파일 리스트와 대조하기 위하여 원래 첨부파일 리스트 가져오기
		List<Integer> uploadedFnos = service.selectFnos(String.valueOf(article.getNo()));
		
		// 사용자가 업로드한 파일들 가져오고 article 객체의 file 속성값 정하기
		if(!article.getFname().isEmpty()) { // 첨부 파일이 한 개 이상인 경우
			List<MultipartFile> files = req.getFiles("fname");
			article.setFile(article.getFile() + files.size()); // 해당 글의 file 속성은 '기존 파일 갯수(getFile()) + 새로 첨부한 파일 갯수(files.size())'로 설정
			
			// file 테이블에 첨부파일 업로드
			for(MultipartFile file : files) {
				service.uploadFile(file, article);
			}
			
			// 해당 글에 첨부된 기존 파일들의 fno와 수정 폼에서 submit 된 파일들의 fno를 비교해서 submit된 파일 fno 리스트에 없는 기존 파일은 삭제하기
			if(uploadedFile != null) { // 기존 첨부파일을 사용자가 수정 시 모두 삭제하지 않은 경우
				List<String> filesNotToDelete = Arrays.asList(uploadedFile.split(","));
				for(Integer f : uploadedFnos) {
					if(!filesNotToDelete.contains(String.valueOf(f))) {
						service.deleteFile(String.valueOf(f));
						article.setFile(article.getFile() - 1); // 파일 삭제하고 해당 글의 file 속성값을 1만큼 낮추기
					}
				}
			}else { // 기존 첨부파일을 모두 삭제한 경우
				for(Integer f : uploadedFnos) {
					service.deleteFile(String.valueOf(f));
					article.setFile(article.getFile() - 1);
				}
			}
			
			
			// 수정된 글 DB에 업로드하기
			service.updateArticle(article);
			
		}else { // 새로운 첨부파일이 없는 경우
			if(uploadedFile != null) { // 기존 첨부파일을 사용자가 수정 시 모두 삭제하지 않은 경우
				List<String> filesNotToDelete = Arrays.asList(uploadedFile.split(","));
				for(Integer f : uploadedFnos) {
					if(!filesNotToDelete.contains(String.valueOf(f))) {
						service.deleteFile(String.valueOf(f));
						article.setFile(article.getFile() - 1); // 파일 삭제하고 해당 글의 file 속성값을 1만큼 낮추기
					}
				}
			}else { // 기존 첨부파일을 모두 삭제한 경우
				for(Integer f : uploadedFnos) {
					service.deleteFile(String.valueOf(f));
					article.setFile(article.getFile() - 1);
				}
			}
			
			// 수정된 글 DB에 업로드하기
			service.updateArticle(article);
		}
		
		
		return "redirect:/admin/cs/faq/view?no=" + article.getNo();
	}
	
	@GetMapping("admin/cs/qna")
	public String qnaList(@RequestParam(value="subcateCode", defaultValue = "0") String subcateCode, @RequestParam(value="pg", defaultValue="1") String pg, Model model) {
		// 페이징 처리 
		int total = service.selectCountArticlesTotal("3", subcateCode);
		int currentPage = service.getCurrentPage(pg);
		int start = service.getLimitStart(currentPage, 10);
		int lastPageNum = service.getLastPageNum(total, 10);
		int startPageNum = service.getPageStartNum(total, start);
		int groups[] = service.getPageGroup(currentPage, lastPageNum, 10);
		
		model.addAttribute("groups", groups);
	    model.addAttribute("currentPage", currentPage);
	    model.addAttribute("lastPageNum", lastPageNum);
	    model.addAttribute("startPageNum", startPageNum);
	    
	    // 모든 공지사항 글 불러오기
	    List<CSQuestionsVO> qnaList = service.selectArticles("3", subcateCode, start);
	    
	    // rdate 날짜만 나오게 substring하기
	    for(CSQuestionsVO qna : qnaList)
	    	qna.setRdate(qna.getRdate().substring(0, 10));
	    
	    // 화면에 출력할 글들 저장
	    model.addAttribute("qnaList", qnaList);
	    // 페이지 로드시 pg값에 맞는 페이지 버튼이 하이라이트되도록 pg값 저장
	    model.addAttribute("pg", pg);
	    // select 박스에서 사용자가 선택한 옵션이 페이지 로드시 가장 상단에 보이도록 하기 위해서는 subcateCode값 저장해야 함
	 	model.addAttribute("subcateCode", subcateCode);
	 	// 글에 인덱스 번호 매기기 위해서 필요
	 	model.addAttribute("start", start);
		
		return "admin/admin_cs_qna_list";
	}
	
	@GetMapping("admin/cs/qna/view")
	public String qnaView(String no, Model model) {
		// 글번호 argument를 이용해 글 정보 불러오기
		CSQuestionsVO article = service.selectArticle(no);
		List<FileVO> files = article.getFvoList();
		
		// 글 정보 저장하기
		model.addAttribute("article", article);
		
		// 첨부 파일이 있으면 첨부 파일도 불러와서 저장하기
		if(article.getFile() >0) {
			model.addAttribute("files", files);
		}
		
		// 글번호를 이용해 답변글 정보 불러오기
		CSQuestionsVO answer = service.selectAnswer(no);
		List<FileVO> answerFiles = new ArrayList<>();
		
		// 답변글 정보 저장하기
				model.addAttribute("answer", answer);
		
		if(answer != null) {
			answerFiles = answer.getFvoList();
			
			// 답변글 첨부 파일이 있으면 첨부 파일도 불러와서 저장하기
			if(answer.getFile() > 0) {
				model.addAttribute("answerFiles", answerFiles);
			}
		}
		
		return "admin/admin_cs_qna_view";
	}
	
	@GetMapping("admin/cs/qna/write")
	public String qnaWrite(String no, Model model) {
		// 글번호 argument를 이용해 글 정보 불러오기
		CSQuestionsVO article = service.selectArticle(no);
		List<FileVO> files = article.getFvoList();
		
		// 글 정보 저장하기
		model.addAttribute("article", article);
		
		// 첨부 파일이 있으면 첨부 파일도 불러와서 저장하기
		if(article.getFile() >0) {
			model.addAttribute("files", files);
		}
		
		// 글번호를 이용해 답변글 정보 불러오기 
		CSQuestionsVO answer = service.selectAnswer(no);
		List<FileVO> answerFiles = new ArrayList<>();
		
		// 답변글 정보 저장하기
		model.addAttribute("answer", answer);
		
		if(answer != null) {
			answerFiles = answer.getFvoList();
			
			// 답변글 첨부 파일이 있으면 첨부 파일도 불러와서 저장하기
			if(answer.getFile() > 0) {
				model.addAttribute("answerFiles", answerFiles);
			}
		}
		
		return "admin/admin_cs_qna_write";
	}
	
	@PostMapping("admin/cs/qna/write")
	public String qnaWrite(@ModelAttribute("CSQuestionsVO") CSQuestionsVO article, MultipartHttpServletRequest req, String uploadedFile, Principal principal) {
		// article 객체에 속성값 채우기
		article.setMember_uid(principal.getName());
		article.setRegip(req.getRemoteAddr());
		
		// XSS 공격을 방지하기위해 Jsoup import해서 safelist에 등록된 태그만 허용 ex.<script> 태그 등은 허용하지 않음 - 화면에는 <script>로 출력되지만 db에는 &lt;script&gt;로 저장됨
		article.setContent(Jsoup.clean(article.getContent(), Safelist.basic())); 
		
		// 이미 답변글이 있는 경우 답변 수정, 답변글이 없는 경우 답변 작성 기능
		// 답변글이 없는 경우 답변 write
		if(article.getAnswer() == 0) {
			
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
				
				// 원 질문글 DB answer 속성값 1로 바꿔주기
				service.updateAnswerCount(String.valueOf(article.getQno()));
				
			}else { // 첨부 파일이 없는 경우
				
				// 사용자가 작성한 QnA DB에 insert
				service.insertArticle(article);
				
				// 원 질문글 DB answer 속성값 1로 바꿔주기
				service.updateAnswerCount(String.valueOf(article.getQno()));
			}
		// 답변글이 있는 경우 답변 modify - notice_modify와 같은 로직
		}else { 
			
			// 이전에 업로드한 답변글의 no 가져와서 article의 no값에 대입하기
			int answerNo = service.selectAnswer(String.valueOf(article.getQno())).getNo();
			article.setNo(answerNo);

			// 사용자가 기존 첨부파일을 삭제할 경우 원래 첨부파일 리스트와 대조하기 위하여 원래 첨부파일 리스트 가져오기
			List<Integer> uploadedFnos = service.selectFnos(String.valueOf(article.getNo()));
			
			// 사용자가 업로드한 파일들 가져오고 article 객체의 file 속성값 정하기
			if(!article.getFname().isEmpty()) { // 첨부 파일이 한 개 이상인 경우
				List<MultipartFile> files = req.getFiles("fname");
				article.setFile(article.getFile() + files.size()); // 해당 글의 file 속성은 '기존 파일 갯수(getFile()) + 새로 첨부한 파일 갯수(files.size())'로 설정
				
				// file 테이블에 첨부파일 업로드
				for(MultipartFile file : files) {
					service.uploadFile(file, article);
				}
				
				// 해당 글에 첨부된 기존 파일들의 fno와 수정 폼에서 submit 된 파일들의 fno를 비교해서 submit된 파일 fno 리스트에 없는 기존 파일은 삭제하기
				if(uploadedFile != null) { // 기존 첨부파일을 사용자가 수정 시 모두 삭제하지 않은 경우
					List<String> filesNotToDelete = Arrays.asList(uploadedFile.split(","));
					for(Integer f : uploadedFnos) {
						if(!filesNotToDelete.contains(String.valueOf(f))) {
							service.deleteFile(String.valueOf(f));
							article.setFile(article.getFile() - 1); // 파일 삭제하고 해당 글의 file 속성값을 1만큼 낮추기
						}
					}
				}else { // 기존 첨부파일을 모두 삭제한 경우
					for(Integer f : uploadedFnos) {
						service.deleteFile(String.valueOf(f));
						article.setFile(article.getFile() - 1);
					}
				}
				
				// 수정된 글 DB에 업로드하기
				service.updateArticle(article);
				
				// 원 질문글 DB answer 속성값 1로 바꿔주기
				service.updateAnswerCount(String.valueOf(article.getQno()));
				
			}else { // 새로운 첨부파일이 없는 경우
				if(uploadedFile != null) { // 기존 첨부파일을 사용자가 수정 시 모두 삭제하지 않은 경우
					List<String> filesNotToDelete = Arrays.asList(uploadedFile.split(","));
					for(Integer f : uploadedFnos) {
						if(!filesNotToDelete.contains(String.valueOf(f))) {
							service.deleteFile(String.valueOf(f));
							article.setFile(article.getFile() - 1); // 파일 삭제하고 해당 글의 file 속성값을 1만큼 낮추기
						}
					}
				}else { // 기존 첨부파일을 모두 삭제한 경우
					for(Integer f : uploadedFnos) {
						service.deleteFile(String.valueOf(f));
						article.setFile(article.getFile() - 1);
					}
				}
				
				// 수정된 글 DB에 업로드하기
				service.updateArticle(article);
				
				// 원 질문글 DB answer 속성값 1로 바꿔주기
				service.updateAnswerCount(String.valueOf(article.getQno()));
			}
				
		}
		
		return "redirect:/admin/cs/qna";
	}
	
	// ER Reviews
	@GetMapping("admin/er/delete")
	@ResponseBody
	public Map<String, Integer> deleteERReviews(String[] checkedNo){
		int result = service.deleteERReviews(checkedNo);
		
		Map<String, Integer> resultMap = new HashMap<>();
		resultMap.put("result", 1);
		return resultMap;
	}
	
	@GetMapping("/admin/er/review")
	public String erReview(@RequestParam(value="region_code", defaultValue="0") String region_code, @RequestParam(value="subregion_code", defaultValue="0") String subregion_code, @RequestParam(value="pg", defaultValue="1") String pg, Model model) {
		// 페이징 처리 
		int total = service.selectERReviewsTotal(region_code, subregion_code);
		int currentPage = service.getCurrentPage(pg);
		int start = service.getLimitStart(currentPage, 10);
		int lastPageNum = service.getLastPageNum(total, 10);
		int startPageNum = service.getPageStartNum(total, start);
		int groups[] = service.getPageGroup(currentPage, lastPageNum, 10);
		
		model.addAttribute("groups", groups);
	    model.addAttribute("currentPage", currentPage);
	    model.addAttribute("lastPageNum", lastPageNum);
	    model.addAttribute("startPageNum", startPageNum);
	    
	    // 모든 리뷰 글 불러오기
	    List<ERReviewVO> reviews = service.selectERReviews(region_code, subregion_code, start);
	    
	    // 화면에 출력할 글들 저장
	    model.addAttribute("reviews", reviews);
	    // 페이지 로드시 pg값에 맞는 페이지 버튼이 하이라이트되도록 pg값 저장
	    model.addAttribute("pg", pg);
	    // select 박스에서 사용자가 선택한 옵션이 페이지 로드시 가장 상단에 보이도록 하기 위해서는 region, subregion값 저장해야 함
	 	model.addAttribute("region_code", region_code);
	 	model.addAttribute("subregion_code", subregion_code);
	 	// 글에 인덱스 번호 매기기 위해서 필요
	 	model.addAttribute("start", start);
	 	
	 	// region_code 리스트 가져와서 저장하기; 위의 region_code는 사용자가 선택한 값이고 여기서 불러오는 값은 DB에 저장된 모든 region_code이다
	 	List<ERCateVO> regions = service.selectRegionCodes();
	 	model.addAttribute("regions", regions);
		
		return "admin/admin_erReview";
	}
	
	@GetMapping("/admin/er/subregion")
	@ResponseBody
	public List<ERSubcateVO> loadSubregions(String region_code){
		List<ERSubcateVO> subregions = service.loadSubregions(region_code);
		
		return subregions;
	}
	
	@GetMapping("/admin/er/review/detail")
	public String erReviewView(String no, Model model) {
		// 글번호 argument를 이용해 글 정보 불러오기
		ERReviewVO review = service.selectERReview(no);
		
		// region, subregion 코드값 이용해서 한글 이름 가져오기
		String[] cates = service.selectERCates(review.getRegion_code(), review.getSubregion_code());
		
		// 글 정보 저장하기
		model.addAttribute("review", review);
		model.addAttribute("cates", cates);
		
		return "admin/admin_erReview_view";
	}
	
	
	// COMMUNITY
	@GetMapping("admin/community/delete")
	@ResponseBody
	public Map<String, Integer> deleteCommunityArticles(String[] checkedNo){
		// // 댓글이 있다면 원글 댓글 카운트에서 삭제한 수만큼 빼기
		for(String no : checkedNo) {
			// 해당 글이 댓글인지 글인지 체크하기(parent값 여부 체크)
			int parent = service.selectIsComment(no);
			
			if(parent != 0) { // 댓글인 경우
				service.updateArticleCommentByMinusOne(parent);
			}
		}
		
		int result = service.deleteCommunityArticles(checkedNo);
		
		Map<String, Integer> resultMap = new HashMap<>();
		resultMap.put("result", 1);
		return resultMap;
	}
	
	@GetMapping("/admin/community")
	public String community(@RequestParam(value="cateCode", defaultValue = "0") String cateCode, @RequestParam(value="regionCode", defaultValue="100") String regionCode, @RequestParam(value="pg", defaultValue="1") String pg, Model model) {
		// 페이징 처리 
		int total = service.selectCountCommunityArticlesTotal(cateCode, regionCode);
		int currentPage = service.getCurrentPage(pg);
		int start = service.getLimitStart(currentPage, 10);
		int lastPageNum = service.getLastPageNum(total, 10);
		int startPageNum = service.getPageStartNum(total, start);
		int groups[] = service.getPageGroup(currentPage, lastPageNum, 10);
		
		model.addAttribute("groups", groups);
	    model.addAttribute("currentPage", currentPage);
	    model.addAttribute("lastPageNum", lastPageNum);
	    model.addAttribute("startPageNum", startPageNum);
		
		// community 전체 글 불러오기
		List<CommunityArticleVO> communityArticles = service.selectCommunityArticles(cateCode, regionCode, start);
		
		// rdate 날짜만 나오게 substring하기
	    for(CommunityArticleVO article : communityArticles)
	    	article.setRdate(article.getRdate().substring(0, 10));
	    
	    // 화면에 출력할 글들 저장
	    model.addAttribute("communityArticles", communityArticles);
	    // 페이지 로드시 pg값에 맞는 페이지 버튼이 하이라이트되도록 pg값 저장
	    model.addAttribute("pg", pg);
	    // select 박스에서 사용자가 선택한 옵션이 페이지 로드시 가장 상단에 보이도록 하기 위해서는 code값 저장해야 함
	 	model.addAttribute("cateCode", cateCode);
	 	model.addAttribute("regionCode", regionCode);
	 	// 글에 인덱스 번호 매기기 위해서 필요
	 	model.addAttribute("start", start);
		
		return "admin/admin_community";
	}
	
	@GetMapping("/admin/community/view")
	public String communityView(String no, Model model) {
		// 글번호 argument를 이용해 글 정보 들고오기
		CommunityArticleVO article = service.selectCommunityArticle(no);
		
		// rdate substring하기
		article.setRdate(article.getRdate().substring(0, 10));
		
		
		// 댓글이 있다면 댓글 들고오기
		List<CommunityArticleVO> comments = new ArrayList<>();
		
		if(article.getComments() > 0) {
			comments = service.selectComments(no);
			
			// rdate substring하기
			for(CommunityArticleVO comment : comments)
				comment.setRdate(comment.getRdate().substring(0, 10));
			
			// 댓글 정보 저장하기
			model.addAttribute("comments", comments);
		}
		
		// 글 정보 저장하기
		model.addAttribute("article", article);
		
		return "admin/admin_community_view";
	}
	
	
}
