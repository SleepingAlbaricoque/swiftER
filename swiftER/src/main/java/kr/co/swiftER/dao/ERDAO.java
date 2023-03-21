package kr.co.swiftER.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import kr.co.swiftER.vo.ERCateVO;
import kr.co.swiftER.vo.ERReviewVO;
import kr.co.swiftER.vo.ERSubcateVO;

@Mapper
@Repository
public interface ERDAO {
	
	public List<ERCateVO> selectErCate();
	public List<ERSubcateVO> selectErSubCate(String city);
	public List<ERReviewVO> selectErReview(String code);
	
}
