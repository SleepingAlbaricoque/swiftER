package kr.co.swiftER.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import kr.co.swiftER.vo.CommunityArticleVO;
import kr.co.swiftER.vo.CommunityCateVO;

@Mapper
@Repository
public interface CommunityDAO {
	
	/* Free */
	public CommunityCateVO selectCate(@Param("cateCode")String cateCode);
	public int insertFreeArticle(CommunityArticleVO vo);
	public CommunityArticleVO selectFreeArticle(@Param("no")int no);
	public List<CommunityArticleVO> selectFreeArticles(@Param("start")int start,@Param("cateCode") String cateCode);
	public CommunityArticleVO selectCommunityFreeNo(int no);
	public int selectCountTotal(@Param("cateCode")String cateCode);
	public List<CommunityArticleVO> selectFindTitleSearch(@Param("start")int start,@Param("title")String title, @Param("cateCode") String cateCode, @Param("keyword")String keyword);
}
