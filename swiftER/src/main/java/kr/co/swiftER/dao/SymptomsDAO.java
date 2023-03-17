package kr.co.swiftER.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import kr.co.swiftER.vo.SymptomsCateVO;
import kr.co.swiftER.vo.SymptomsSubcateVO;
import kr.co.swiftER.vo.SymptomsSymptomsVO;

@Mapper
@Repository
public interface SymptomsDAO {

	public List<SymptomsSubcateVO> selectCate(String body1_code);
	public List<SymptomsSymptomsVO> selectBody1(String code);
	public int insertsymptoms(List<SymptomsSymptomsVO> symptom);
}
