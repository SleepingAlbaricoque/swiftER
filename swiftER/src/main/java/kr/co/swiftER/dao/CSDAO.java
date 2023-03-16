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
	
	// FileVO를 file 테이블에 업로드
	public int insertFile(FileVO fvo);
	
	// 페이징 처리를 위한 메서드
	// 전체 글 갯수
	public int selectCountTotal(@Param(value="cateCode") String cateCode, @Param(value="subcateCode") String subcateCode);
}
