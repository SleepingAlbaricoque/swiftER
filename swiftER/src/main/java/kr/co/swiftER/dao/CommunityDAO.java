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
	public int insertFreeArticle(CommunityArticleVO vo);
	public CommunityCateVO selectCate(@Param("cateCode")String cateCode);
	public CommunityArticleVO selectFreeArticle(@Param("no")String no);
	public List<CommunityArticleVO> selectFreeArticles(@Param("start")int start,@Param("cateCode") String cateCode, @Param("regionCode") String regionCode);
	public CommunityArticleVO selectCommunityFreeNo(String no);
	public int selectCountTotal(@Param("cateCode")String cateCode);
	public List<CommunityArticleVO> selectFindTitleSearch(@Param("start")int start,@Param("title")String title, @Param("cateCode") String cateCode,@Param("regionCode") String regionCode, @Param("keyword")String keyword);
	public int modifyArticle(@Param("no")String no, @Param("title")String title, @Param("content")String content);
	public int updateArticleView(String no);
	public int deleteArticle(@Param("no")String no);
	/* Comment */
	public int insertComment(CommunityArticleVO vo);
	public List<CommunityArticleVO> selectComments(@Param("start")int start,@Param("parent")String parent); 
	public List<CommunityArticleVO> selectQnaComments(@Param("parent")String parent); 
	public int selectCommentCountTotal(@Param("parent")String parent);
	public int updateComments(@Param("parent") Integer parent);
	public int deleteComment(@Param("no")String no);
}
