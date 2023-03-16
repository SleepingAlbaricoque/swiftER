package kr.co.swiftER.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.swiftER.service.SymptomsService;
import kr.co.swiftER.vo.SymptomsCateVO;
import kr.co.swiftER.vo.SymptomsSubcateVO;
import kr.co.swiftER.vo.SymptomsSymptomsVO;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class SymptomsController {

	@Autowired
	private SymptomsService service;
	
	@GetMapping("symptoms/symptoms")
	public String symptoms(Model model, String body1_code, String code) {
		
		List<SymptomsSubcateVO> cate = service.selectCate(body1_code);
		List<SymptomsSymptomsVO> bd1 = service.selectBody1(code);
		
		model.addAttribute("cate", cate);
		model.addAttribute("bd1", bd1);
		model.addAttribute("body1_code", body1_code);
		model.addAttribute("code", code);
		
		return "symptoms/symptoms";
	}
	
	@PostMapping(value="symptoms/symptoms")
	@ResponseBody
	public Map<String, Integer> symptoms(@RequestBody SymptomsSymptomsVO symptom) {
		
		int result = service.insertsymptoms(symptom);
		
		Map<String, Integer> map = new HashMap<>();
		map.put("result", result);
		return map;
	}
	
	@GetMapping("symptoms/resultsymptoms")
	public String resultsymptoms() {
		return "symptoms/resultsymptoms";
	}
	
}
