package kr.co.swiftER.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.swiftER.service.SymptomsService;
import kr.co.swiftER.vo.MemberTermsVO;
import kr.co.swiftER.vo.SymptomsCateVO;
import kr.co.swiftER.vo.SymptomsIllnessesVO;
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
	public String symptoms(Model model,  @Param("symptom") String symptom) {
		
		List<SymptomsSymptomsVO> symptoms = new ArrayList<>();
		
		model.addAttribute("symptoms", symptoms);
		
		
		return "symptoms/symptoms";
	}

	
	@GetMapping("symptoms/resultsymptoms")
	public String resultsymptoms(Model model, String body1_code,  String illness, @RequestParam(value = "symptoms", required = false) List<String> symptoms, String symptom){
		
		List<SymptomsSymptomsVO> ill = service.selectillness(symptom);
		List<SymptomsSymptomsVO> symp1 = service.selectsymp1(illness);
		List<SymptomsIllnessesVO> dep = service.selectdep(illness);
		
		model.addAttribute("body1_code", body1_code);
		model.addAttribute("ill", ill);
		model.addAttribute("symp1", symp1);
		model.addAttribute("dep", dep);
		model.addAttribute("symptom", symptom);

		
		return "symptoms/resultsymptoms";
	}
	
}
