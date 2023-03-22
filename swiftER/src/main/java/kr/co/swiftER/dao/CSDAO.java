package kr.co.swiftER.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import kr.co.swiftER.vo.CSQuestionsVO;
import kr.co.swiftER.vo.FileVO;

@Mapper
@Repository
public interface CSDAO {

	public int insertArticle(CSQuestionsVO article);
	public List<CSQuestionsVO> selectArticles(@Param(value="cateCode") String cateCode, @Param(value="subcateCode") String subcateCode, @Param(value="start") int start, @Param(value="keyword") String keyword);
	public List<CSQuestionsVO> selectArticle(String parent);
	
	// 글 조회수 올리기
	public int updateArticleView(String no);
	
	// notice view에서 이전글, 다음글 표시하기 위해 해당글의 이전글, 다음글 가져오기
	public List<CSQuestionsVO> selectArticlesPriorAndNext(String no);
	
	// 내가 쓴 글 조회를 위한 메서드
	public int selectMyCountTotal(@Param(value="cateCode") String cateCode, @Param(value="subcateCode") String subcateCode, @Param(value="id") String id);
	public List<CSQuestionsVO> selectMyArticles(@Param(value="cateCode") String cateCode, @Param(value="subcateCode") String subcateCode, @Param(value="start") int start, @Param(value="id") String id);
	
	// 내가 쓴 qna 답변 여부 체크
	public int selectCountQnaAnswer(int no);
	
	// 파일 처리
	// FileVO를 file 테이블에 업로드
	public int insertFile(FileVO fvo);
	
	// selectArticle을 위해 파일 조회
	public FileVO selectFile(int parent);
	
	// 파일 다운로드를 위해 파일 조회
	public FileVO selectFileForDownload(@Param(value="parent") String parent, @Param(value="num") int num); // num 파라미터는 쿼리문에서 offset을 수행하기 위해 필요한 값
	
	
	// 페이징 처리를 위한 메서드
	// 전체 글 갯수
	public int selectCountTotal(@Param(value="cateCode") String cateCode, @Param(value="subcateCode") String subcateCode, @Param(value="keyword") String keyword);
}
