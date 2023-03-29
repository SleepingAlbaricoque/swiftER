package kr.co.swiftER.controller;

import java.io.IOException;
import java.net.http.HttpRequest;
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
import kr.co.swiftER.vo.FileVO;
import kr.co.swiftER.vo.MemberVO;

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
		
		// 사용자가 업로드한 파일들 가져오고 article 객체의 file 속성값 정하기
		if(!article.getFname().isEmpty()) { // 첨부 파일이 한 개 이상인 경우
			List<MultipartFile> files = req.getFiles("fname");
			article.setFile(files.size() + article.getFile()); // 원래 첨부파일이 있는 경우 기존의 첨부파일 갯수도 더해주기
			
			// 사용자가 작성한 notice DB에 update
			service.updateArticle(article);
			
			for(MultipartFile file : files) {
				// DB에 파일 업로드
				service.uploadFile(file, article);
			}
			
			// 사용자가 기존 첨부파일을 삭제한 경우 DB에서도 삭제해주기
			if(article.getFile() > 0) {
				List<String> oriFilesFnos = new ArrayList<>();
				List<FileVO> oriFiles = service.selectArticle(String.valueOf(article.getNo())).getFvoList();
				
				for(FileVO file : oriFiles)
					oriFilesFnos.add(String.valueOf(file.getFno())); // 불러온 글의 기존 첨부파일 fno만 모아서 리스트 만들기
				
				for(String fno : oriFilesFnos) {
					if(!uploadedFile.contains(fno)) // 폼에서 보내온 값이 기존 글 fno 리스트에 없다면 파일 삭제하기
						service.deleteFile(fno);
						service.subtractFileByOne(article.getNo());
				}
			}
				
		}else { // 첨부 파일이 없는 경우
			
			// 사용자가 작성한 notice DB에 update
			service.updateArticle(article);
			
			// 사용자가 기존 첨부파일을 삭제한 경우 DB에서도 삭제해주기
			if(article.getFile() > 0) {
				List<String> oriFiles = new ArrayList<>();
				for(FileVO file : article.getFvoList())
					oriFiles.add(String.valueOf(file.getFno())); // 불러온 글의 기존 첨부파일 fno만 모아서 리스트 만들기
				
				for(String fno : oriFiles) {
					if(!uploadedFile.contains(fno)) // 폼에서 보내온 값이 기존 글 fno 리스트에 없다면 파일 삭제하기
						service.deleteFile(fno);
						service.subtractFileByOne(article.getNo());
				}
			}
		}
		
		return "redirect:/admin/cs/notice/view?no=" + article.getNo();
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
