package kr.co.swiftER.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.swiftER.dao.ERDAO;
import kr.co.swiftER.vo.ERCateVO;
import kr.co.swiftER.vo.ERReviewVO;
import kr.co.swiftER.vo.ERSubcateVO;

@Service
public class ERService {
	
	@Autowired
	private ERDAO dao;
	
	public List<ERCateVO> selectErCate(){
		return dao.selectErCate();
	}
	
	public List<ERSubcateVO> selectErSubCate(String city){
		return dao.selectErSubCate(city);
	}
	
	public List<ERReviewVO> selectErReview(String code,int start) {
		return dao.selectErReview(code, start);
	}
	
	public int insertErReview(ERReviewVO vo) {
		return dao.insertErReview(vo);
	}

	public List<ERCateVO> selectErRegion(String city){
		return dao.selectErRegion(city);
	}
	
	public List<ERSubcateVO> selectErSubRegion(String town, String city){
		return dao.selectErSubRegion(town, city);
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
	public long getTotalCount(String code){
        return dao.selectCountTotal(code);
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
