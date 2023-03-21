package kr.co.swiftER.service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import kr.co.swiftER.dao.CSDAO;
import kr.co.swiftER.exceptions.CustomErrorCode;
import kr.co.swiftER.exceptions.CustomException;
import kr.co.swiftER.vo.CSQuestionsVO;
import kr.co.swiftER.vo.FileVO;

@Service
public class CSService {

	@Autowired
	private CSDAO dao;
	
	public int insertArticle(CSQuestionsVO article) {
		// 글 등록
		int result = dao.insertArticle(article);
		return result;
	}
	
	public List<CSQuestionsVO> selectArticles(String cateCode, String subcateCode, int start, String keyword){
		return dao.selectArticles(cateCode, subcateCode, start, keyword);
	}
	
	public List<CSQuestionsVO> selectArticle(String parent) {
		return dao.selectArticle(parent);
	}
	
	// 글 조회수 업데이트
	public int updateArticleView(String no) {
		return dao.updateArticleView(no);
	}
	
	// notice view에서 이전글, 다음글 표시하기 위해 해당글의 이전글, 다음글 가져오기
	public List<CSQuestionsVO> selectArticlesPriorAndNext(String no){
		return dao.selectArticlesPriorAndNext(no);
	}
	
	// 내가 쓴 글 조회를 위한 메서드
	public int selectMyCountTotal(String cateCode, String subcateCode, String id) {
		return dao.selectMyCountTotal(cateCode, subcateCode, id);
	}
	
	public List<CSQuestionsVO> selectMyArticles(String cateCode, String subcateCode ,int start, String id){
		return dao.selectMyArticles(cateCode, subcateCode, start, id);
	}
	
	
	// 파일 업로드
	// applicaton.properties에서 설정한 파일 저장 경로 주입받기
	@Value("${spring.servlet.multipart.location}")
	private String uploadPath;
	
	public FileVO uploadFile(MultipartFile file, CSQuestionsVO article){
		// 첨부 파일 정보 가져오기
		int cate= article.getCateCode();
		FileVO fvo = null;
		
		if(!file.isEmpty()) {
			// application.properties에서 설정한 파일 저장 경로의 시스템 경로 구하기
			String path = new File(uploadPath).getAbsolutePath();
			
			// 새 파일명 생성
			String oriName = file.getOriginalFilename();
			String ext = oriName.substring(oriName.lastIndexOf(".")); // 확장자
			String newName = UUID.randomUUID().toString() + ext;
			
			// 업로드 파일 확장자 제한하기; 그렇지 않으면 web shell이나 악성 파일 업로드할 가능성이 있음
			String[] safeExts = {".jpg", ".jpeg", ".bmp", ".png", ".gif"};
			if(!Arrays.asList(safeExts).contains(ext)) {
				throw new CustomException(CustomErrorCode.WRONG_EXT_ERROR);
			}
			
			// 파일 저장
			try {
				file.transferTo(new File(path, newName));
			}catch(Exception e) {
				throw new RuntimeException(e);
			}
			fvo = new FileVO().builder().parent(article.getNo()).oriName(oriName).newName(newName).cate(cate).build();
			dao.insertFile(fvo);
		}
		return fvo;
	}
	
	
	// 파일 다운로드
	public ResponseEntity<Resource> fileDownload(FileVO file) throws IOException{
		Path path = Paths.get(uploadPath + "/" + file.getNewName());
		String contentType = Files.probeContentType(path);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentDisposition(ContentDisposition.builder("attachment").filename(file.getOriName(), StandardCharsets.UTF_8).build());
		headers.add(HttpHeaders.CONTENT_TYPE, contentType);
		
		Resource resource = new InputStreamResource(Files.newInputStream(path));
		
		return new ResponseEntity<>(resource, headers, HttpStatus.OK);
	}
	
	// 파일 조회(selectArticle 할 때 CSQuestionsVO 멤버인 List<FileVO>값 가져오기 위한 메서드)
	public FileVO selectFile(int parent){
		return dao.selectFile(parent);
	}
	
	// 다운로드를 위한 파일 조회
	public FileVO selectFileForDownload(String parent, int no) {
		return dao.selectFileForDownload(parent, no);
	}
	
	// 페이징
	// 글 총 갯수(total)
	public int selectCountTotal(String cateCode, String subcateCode, String keyword) {
		return dao.selectCountTotal(cateCode, subcateCode, keyword);
	}
	
	// 현재 페이지 버튼 번호
	public int getCurrentPage(String pg) {
		int currentPage = 1;
		
		if(pg != null)
			currentPage = Integer.parseInt(pg);
		
		return currentPage;
	}
	
	// 현재 페이지 버튼의 페이지(article) 시작값
	public int getLimitStart(int currentPage, int articlesPerPage) {
		return (currentPage - 1) * articlesPerPage;
	}
	
	// 마지막 버튼 페이지 번호
	public int getLastPageNum(int total, int articlesPerPage) {
		int lastPageNum = 0;
		
		if(total % articlesPerPage == 0)
			lastPageNum = total /articlesPerPage;
		else
			lastPageNum = total / articlesPerPage + 1;
		
		return lastPageNum;
	}
	
	// 페이지 시작 번호
	public int getPageStartNum(int total, int start) {
		return total - start;
	}
	
	// 페이지 그룹
	public int[] getPageGroup(int currentPage, int lastPageNum, int articlesPerPage) {
		int groupCurrent = (int) Math.ceil(currentPage/Double.valueOf(articlesPerPage)); // 현재 페이지 번호 그룹
		int groupStart = (groupCurrent - 1)*articlesPerPage + 1; // 현재 그룹의 시작 버튼 번호
		int groupEnd = groupCurrent * articlesPerPage; // 현재 그룹의 마지막 버튼 번호
		
		if(groupEnd > lastPageNum)
			groupEnd = lastPageNum;
		
		int[] groups = {groupStart, groupEnd};
		return groups;
	}
}
