package kr.co.swiftER.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.swiftER.controller.CommunityController;
import kr.co.swiftER.dao.CommunityDAO;
import kr.co.swiftER.vo.CommunityArticleVO;
import kr.co.swiftER.vo.CommunityCateVO;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;

@Log4j2
@Service
public class CommunityService {
	
	@Autowired
	private CommunityDAO dao;
	
	/* Comment */
	public int insertComment(CommunityArticleVO vo){	
        int result = dao.insertComment(vo);
        return result;
    }
	public long getCommentTotalCount(String parent){
        return dao.selectCommentCountTotal(parent);
    }
	public List<CommunityArticleVO> selectComments(int start,String parent){
        return dao.selectComments(start, parent);
    }
	public List<CommunityArticleVO> selectQnaComments(String parent){
        return dao.selectQnaComments(parent);
    }
	public int updateComments(Integer parent) {
		return dao.updateComments(parent);
	}
	public int deleteComment(String no) {
		return dao.deleteComment(no);
	}
	/* Free */
	public int insertFreeArticle(CommunityArticleVO vo){	
        int result = dao.insertFreeArticle(vo);
        return result;
    }
	public CommunityCateVO selectCate(String cateCode){
        return dao.selectCate(cateCode);
    }
	public CommunityArticleVO selectFreeArticle(String no){
        return dao.selectFreeArticle(no);
    }
	public List<CommunityArticleVO> selectFreeArticles(int start, String cateCode,String regionCode){
        return dao.selectFreeArticles(start, cateCode, regionCode);
    }
	public CommunityArticleVO selectCommunityFreeNo(String no){
        return dao.selectCommunityFreeNo(no);
    }
	public List<CommunityArticleVO> selectFindTitleSearch(int start, String title, String cateCode, String keyword,String regionCode){
		return dao.selectFindTitleSearch(start, title, cateCode, regionCode, keyword);
	}
	public int updateArticleView(String no) {
		return dao.updateArticleView(no);
	}
	public int deleteArticle(String no) {
		return dao.deleteArticle(no);
	}
	public int getLimitStart(int currentPage){
        return (currentPage - 1) * 10;
    }
	public int getCurrentPage(String pg){
	        int currentPage = 1;
	        if (pg != null){
	            currentPage = Integer.parseInt(pg);
	        }
	        return currentPage;
	 }
	public long getTotalCount(String cateCode){
	        return dao.selectCountTotal(cateCode);
	}
	public int getLastPageNum(long total){
        int lastPage = 0;

        if (total % 10 == 0){
            lastPage = (int) (total/10);
        }else{
            lastPage = (int) ((total/10) + 1);
        }
        return lastPage;
    }
	public int getPageStartNum(long total, int start){
	        return (int) (total - start);
	}
	public int[] getPageGroup(int currentPage, int lastPage){
        int groupCurrent = (int) Math.ceil(currentPage/10.0);
        int groupStart = (groupCurrent - 1) * 10 + 1;
        int groupEnd = groupCurrent * 10;

        if (groupEnd > lastPage){
            groupEnd = lastPage;
        }

        int[] groups = {groupStart, groupEnd};

        return groups;
    }
}
