package kr.co.swiftER.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.swiftER.dao.CommunityDAO;
import kr.co.swiftER.vo.CommunityArticleVO;
import kr.co.swiftER.vo.CommunityCateVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CommunityService {
	
	@Autowired
	private CommunityDAO dao;
	
	
	/* Free */
	public CommunityCateVO selectCate(String cateCode){
        return dao.selectCate(cateCode);
    }
	public int insertFreeArticle(CommunityArticleVO vo){	
        int result = dao.insertFreeArticle(vo);
        return result;
    }
	public CommunityArticleVO selectFreeArticle(int no){
        return dao.selectFreeArticle(no);
    }
	public List<CommunityArticleVO> selectFreeArticles(int start, String cateCode,String regionCode){
        return dao.selectFreeArticles(start, cateCode, regionCode);
    }
	public CommunityArticleVO selectCommunityFreeNo(int no){
        return dao.selectCommunityFreeNo(no);
    }
	public List<CommunityArticleVO> selectFindTitleSearch(int start, String title, String cateCode, String keyword,String regionCode){
		return dao.selectFindTitleSearch(start, title, cateCode, regionCode, keyword);
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
