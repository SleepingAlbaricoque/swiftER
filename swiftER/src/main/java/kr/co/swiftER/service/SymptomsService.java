package kr.co.swiftER.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.swiftER.dao.SymptomsDAO;
import kr.co.swiftER.vo.SymptomsCateVO;
import kr.co.swiftER.vo.SymptomsSubcateVO;
import kr.co.swiftER.vo.SymptomsSymptomsVO;

@Service
public class SymptomsService {
	
	@Autowired
	private SymptomsDAO dao;
	
	public List<SymptomsSubcateVO> selectCate(String body1_code) {
		return dao.selectCate(body1_code);
	}
	
	
	public List<SymptomsSymptomsVO> selectBody1(String code) {
		return dao.selectBody1(code);
	}
	
	public int insertsymptoms(List<SymptomsSymptomsVO> symptom) {
		return dao.insertsymptoms(symptom);
	}

}
