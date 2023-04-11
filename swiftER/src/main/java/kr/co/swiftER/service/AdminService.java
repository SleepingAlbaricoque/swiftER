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

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import kr.co.swiftER.dao.AdminDAO;
import kr.co.swiftER.exceptions.CustomErrorCode;
import kr.co.swiftER.exceptions.CustomException;
import kr.co.swiftER.vo.AdminMemberModifyVO;
import kr.co.swiftER.vo.AdminMemberSearchVO;
import kr.co.swiftER.vo.CSQuestionsVO;
import kr.co.swiftER.vo.CommunityArticleVO;
import kr.co.swiftER.vo.ERCateVO;
import kr.co.swiftER.vo.ERReviewVO;
import kr.co.swiftER.vo.ERSubcateVO;
import kr.co.swiftER.vo.FileVO;
import kr.co.swiftER.vo.MemberVO;

@Service
public class AdminService {

	@Autowired
	private AdminDAO dao;
	
	@Autowired
	private SqlSession sqlSession;
	
	// member
	// 전체 회원 불러오기
	public List<AdminMemberSearchVO> selectMembers(int start, int isDoc){
		return dao.selectMembers(start, isDoc);
	}
	
	// 전체 회원의 수
	public int selectCountTotal() {
		return dao.selectCountTotal();
	}
	
	// 인증 승인 요청한 의사의 수
	public int selectDocCountTotal() {
		return dao.selectDocCountTotal();
	}
	
	// 회원 정보 조회(한 명)
	public AdminMemberSearchVO selectMember(String uid) {
		return dao.selectMember(uid);
	}
	
	// 회원 영구 정지
	public int banMember(String uid) {
		if(dao.banMember(uid) == 1)
			return 1;
		
		return 0;
	}
	
	// 의사 회원 uid값으로 첨부한 면허증 사진 정보 가져오기
	public FileVO selectDocCert(String uid) {
		return dao.selectDocCert(uid);
	}
	
	// 의사 회원 승인하기
	public int certVerify(String uid, String status) {
		return dao.certVerify(uid, status);
	}
	
	// 회원 정보 수정하기 + 의사 회원의 인증 보류시 보류 메시지 수정하기 transaction
	@Transactional
	public int updateMember(AdminMemberModifyVO member) {
		int result1 = sqlSession.update("kr.co.swiftER.dao.AdminDAO.updateMember", member);
		
		if(member.getVeriMsg() != null) {
			int result2 = sqlSession.update("kr.co.swiftER.dao.AdminDAO.updateVeriMsg", member);
			
			if(result1 == 1 && result2 ==1)
				return 1;
			else
				return 0;
		}
		
		return result1;
	}
	
	// cs
	// 선택한 글 삭제
	public int deleteArticles(String[] checkedNo) {
		return dao.deleteArticles(checkedNo);
	}
	
	public List<CSQuestionsVO> selectArticles(String cateCode, String subcateCode, int start){
		return dao.selectArticles(cateCode, subcateCode, start);
	}
	
	// 선택한 글 불러오기
	public CSQuestionsVO selectArticle(String no) {
		return dao.selectArticle(no);
	}
	
	// 글 업데이트
	public int updateArticle(CSQuestionsVO article) {
		return dao.updateArticle(article);
	}
	
	// 글 새로 작성하기
	public int insertArticle(CSQuestionsVO article) {
		return dao.insertArticle(article);
	}
	
	// qna 답변글 불러오기
	public CSQuestionsVO selectAnswer(String no) {
		return dao.selectAnswer(no);
	}
	
	// 관리자가 qna 답변글 달 경우 질문글 answer 카운트를 올려주기
	public int updateAnswerCount(String no) {
		return dao.updateAnswerCount(no);
	}
	
	
	// community
	// 모든 글 불러오기
	public List<CommunityArticleVO> selectCommunityArticles(String cateCode, String regionCode, int start){
		return dao.selectCommunityArticles(cateCode, regionCode, start);
	}
	
	// cate, region별 글 총 갯수 구하기
	public int selectCountCommunityArticlesTotal(String cateCode, String regionCode) {
		return dao.selectCountCommunityArticlesTotal(cateCode, regionCode);
	}
	
	// 선택한 글 삭제
	public int deleteCommunityArticles(String[] checkedNo) {
		return dao.deleteCommunityArticles(checkedNo);
	}
	
	
	// 선택한 글 불러오기
	public CommunityArticleVO selectCommunityArticle(String no) {
		return dao.selectCommunityArticle(no);
	}
	
	// 선택한 글의 댓글 불러오기
	public List<CommunityArticleVO> selectComments(String no){
		return dao.selectComments(no);
	}
	
	// 선택한 글이 글인지 댓글인지 체크
	public int selectIsComment(String no) {
		return dao.selectIsComment(no);
	}
	
	// 댓글 삭제 시 원글의 comment 카운터 -1 하기
	public int updateArticleCommentByMinusOne(int parent) {
		return dao.updateArticleCommentByMinusOne(parent);
	}
	
	// er
	// 모든 글 불러오기
	public List<ERReviewVO> selectERReviews(String region_code, String subregion_code, int start){
		return dao.selectERReviews(region_code, subregion_code, start);
	}
	
	// region, subregion별 글 총 갯수 구하기
	public int selectERReviewsTotal(String region_code, String subregion_code) {
		return dao.selectERReviewsTotal(region_code, subregion_code);
	};
	
	// 모든 region_code값 불러오기
	public List<ERCateVO> selectRegionCodes(){
		return dao.selectRegionCodes();
	}
	
	// 선택한 글 삭제
	public int deleteERReviews(String[] checkedNo) {
		return dao.deleteERReviews(checkedNo);
	}
	
	// 선택한 region의 subregion 코드들 가져오기
	public List<ERSubcateVO> loadSubregions(String region_code){
		return dao.loadSubregions(region_code);
	}
	
	// 선택한 리뷰글 불러오기
	public ERReviewVO selectERReview(String no) {
		return dao.selectERReview(no);
	}
	
	// region, subregion 코드값 이용해서 한글 이름 가져오기
	public String[] selectERCates(int region_code, int subregion_code) {
		return dao.selectERCates(region_code, subregion_code);
	}
	
	
	// main
	// 어제까지 DB에 저장된 수치 구하기
	public int selectCountMembersTillYesterday(String today) {
		return dao.selectCountMembersTillYesterday(today);
	}
	public int selectCountCommunityArticlesTillYesterday(String today) {
		return dao.selectCountCommunityArticlesTillYesterday(today);
	}
	public int selectCountERReviewsTillYesterday(String today) {
		return dao.selectCountERReviewsTillYesterday(today);
	}	
	
	
	
	// 페이징
	// 글 총 갯수(total)
	public int selectCountArticlesTotal(String cateCode, String subcateCode) {
		return dao.selectCountArticlesTotal(cateCode, subcateCode);
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
	
	// 사용자가 선택한 파일 DB에서 삭제하기
	public int deleteFile(String fno) {
		return dao.deleteFile(fno);
	}
	
	// 선택한 글의 기존 첨부파일 fno 불러오기
	public List<Integer> selectFnos(String no){
		return dao.selectFnos(no);
	}
}
