package kr.co.swiftER.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import kr.co.swiftER.vo.AdminMemberModifyVO;
import kr.co.swiftER.vo.AdminMemberSearchVO;
import kr.co.swiftER.vo.CSQuestionsVO;
import kr.co.swiftER.vo.CommunityArticleVO;
import kr.co.swiftER.vo.ERCateVO;
import kr.co.swiftER.vo.ERReviewVO;
import kr.co.swiftER.vo.ERSubcateVO;
import kr.co.swiftER.vo.FileVO;
import kr.co.swiftER.vo.MemberVO;

@Mapper
@Repository
public interface AdminDAO {

	// member
	public List<AdminMemberSearchVO> selectMembers(@Param(value="start") int start, @Param(value="isDoc")int isDoc);
	public AdminMemberSearchVO selectMember(String uid);
	
	// 전체 회원 수
	public int selectCountTotal();
	
	// 인증 승인 요청한 의사의 수
	public int selectDocCountTotal();
	
	// 회원 영구 정지 시키기(member 테이블 grade가 4로, wdate 기록)
	public int banMember(String uid);
	
	// 의사 회원 uid값으로 첨부한 면허증 사진 정보 가져오기
	public FileVO selectDocCert(String uid);

	// 의사 회원 승인하기
	public int certVerify(@Param(value="uid") String uid, @Param(value="status") String status);
	
	// 회원 정보 수정하기
	public int updateMember(AdminMemberModifyVO member);
	
	// 의사 회원의 인증 보류시 보류 메시지 수정하기(회원 정보 수정과 transaction으로 묶을 것)
	public int updateVeriMsg(AdminMemberModifyVO member);
	
	
	// cs
	public List<CSQuestionsVO> selectArticles(@Param(value="cateCode") String cateCode, @Param(value="subcateCode") String subcateCode, @Param(value="start") int start);
	
	// 선택한 글 삭제
	public int deleteArticles(@Param(value="array")String[] checkedNo);
	
	// cate, subcate별 글 총 갯수 구하기
	public int selectCountArticlesTotal(@Param(value="cateCode") String cateCode, @Param(value="subcateCode") String subcateCode);
	
	// 선택한 글 불러오기
	public CSQuestionsVO selectArticle(String no);
	
	// 글 업데이트
	public int updateArticle(CSQuestionsVO article);
	
	// 글 새로 작성하기
	public int insertArticle(CSQuestionsVO article);
	
	// 답변글 불러오기
	public CSQuestionsVO selectAnswer(String no);
	
	// 관리자가 qna 답변글 달 경우 질문글 answer 카운트를 올려주기
	public int updateAnswerCount(String no);
	
	
	// community
	// 모든 글 불러오기
	public List<CommunityArticleVO> selectCommunityArticles(@Param(value="cateCode") String cateCode, @Param(value="regionCode") String regionCode, @Param(value="start") int start);
	
	// cate, region별 글 총 갯수 구하기
	public int selectCountCommunityArticlesTotal(@Param(value="cateCode") String cateCode, @Param(value="regionCode") String regionCode);
	
	// 선택한 글 삭제
	public int deleteCommunityArticles(@Param(value="array")String[] checkedNo);
	
	// 선택한 글 불러오기
	public CommunityArticleVO selectCommunityArticle(String no);
	
	// 선택한 글의 댓글 불러오기
	public List<CommunityArticleVO> selectComments(String no);
	
	// 선택한 글이 글인지 댓글인지 체크
	public int selectIsComment(String no);
	
	// 댓글 삭제 시 원글의 comment 카운터 -1 하기
	public int updateArticleCommentByMinusOne(int parent);
	
	
	// er
	// 모든 글 불러오기
	public List<ERReviewVO> selectERReviews(@Param(value="region_code") String region_code, @Param(value="subregion_code") String subregion_code, @Param(value="start") int start);
	
	// region, subregion별 글 총 갯수 구하기
	public int selectERReviewsTotal(@Param(value="region_code") String region_code, @Param(value="subregion_code") String subregion_code);
	
	// 모든 region_code값 불러오기
	public List<ERCateVO> selectRegionCodes();
	
	// 선택한 글 삭제
	public int deleteERReviews(@Param(value="array")String[] checkedNo);
	
	// 선택한 region의 subregion 코드들 가져오기
	public List<ERSubcateVO> loadSubregions(String region_code);
	
	// 선택한 리뷰글 불러오기
	public ERReviewVO selectERReview(String no);
	
	// region, subregion 코드값 이용해서 한글 이름 가져오기
	public String[] selectERCates(@Param(value="region_code") int region_code, @Param(value="subregion_code") int subregion_code);
	
	
	// main
	// 어제까지 DB에 저장된 수치 구하기
	public int selectCountMembersTillYesterday(String today);
	public int selectCountCommunityArticlesTillYesterday(String today);
	public int selectCountERReviewsTillYesterday(String today);
	
	
	
	
	
	
	// 파일 처리
	// FileVO를 file 테이블에 업로드
	public int insertFile(FileVO fvo);
	
	// 파일 삭제
	public int deleteFile(String fno);
	
	// 선택한 글의 기존 첨부파일 fno 불러오기
	public List<Integer> selectFnos(String no);
}
