package kr.co.swiftER.service;

import java.util.List;

import org.apache.catalina.connector.Response;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co.swiftER.dao.ERDAO;
import kr.co.swiftER.vo.ERCateVO;
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
	
	
}
