package kr.co.swiftER.controller;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import kr.co.swiftER.service.ERService;
import kr.co.swiftER.vo.ERCateVO;
import kr.co.swiftER.vo.ERReviewVO;
import kr.co.swiftER.vo.ERSubcateVO;

@Controller
public class ERController {
	
	@Autowired
	private ERService service;
	
	@GetMapping("er/er")
	public String er(Model model) {
		List<ERCateVO> region = service.selectErCate();
		
		model.addAttribute("region",region);
		
		return "er/er";
	}
	
	@PostMapping("er/subregion")
	@ResponseBody
	public List<ERSubcateVO> cate(String city) {
		
		List<ERSubcateVO> sub = service.selectErSubCate(city);
		
		return sub;
	}
	
	@PostMapping("er/erReview")
	@ResponseBody
	public String erReview(Model model, String code, String title, String content){
		
		int result = service.insertErReview(code, title, content); 
		
		if(result > 0) {
			List<ERReviewVO> reviews = service.selectErReview(code);
			model.addAttribute("reviews", reviews);
			model.addAttribute("code", code);
		}
       
        return "er/erDetail";
	}
	
}
