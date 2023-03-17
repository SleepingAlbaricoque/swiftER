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
	public List<CSQuestionsVO> selectArticles(String cateCode, @Param(value="subcateCode") String subcateCode, @Param(value="start") int start);
	public List<CSQuestionsVO> selectArticle(String parent);
	
	// 파일 처리
	// FileVO를 file 테이블에 업로드
	public int insertFile(FileVO fvo);
	
	// selectArticle을 위해 파일 조회
	public FileVO selectFile(int parent);
	
	// 파일 다운로드를 위해 파일 조회
	public FileVO selectFileForDownload(@Param(value="parent") String parent, @Param(value="num") int num); // num 파라미터는 쿼리문에서 offset을 수행하기 위해 필요한 값
	
	// 페이징 처리를 위한 메서드
	// 전체 글 갯수
	public int selectCountTotal(@Param(value="cateCode") String cateCode, @Param(value="subcateCode") String subcateCode);
}
