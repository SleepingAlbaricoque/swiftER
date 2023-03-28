package kr.co.swiftER.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.swiftER.entity.MemberEntity;
import kr.co.swiftER.security.MyUserDetails;
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
	public String erReview(HttpServletRequest req, Model model,@RequestParam("uid") String uid,@RequestParam("region") String region, @RequestParam("subregion") String subregion,
								@RequestParam("code") String code, @RequestParam("city") String city, @RequestParam("town") String town, @RequestParam("hosName") String hosName,
								@RequestParam("title") String title,@RequestParam("content") String content,@RequestParam("rating") String rating ,ERReviewVO vo){
		
		System.out.println("rating : "+rating);
		
		vo.setMember_uid(uid);
		vo.setHospital_code(code);
		vo.setHospital_name(hosName);
		vo.setRegion_code(region);
		vo.setSubregion_code(subregion);
		vo.setTitle(title);
		vo.setRating(rating);
		vo.setRegip(req.getRemoteAddr());
		
		
		int result = service.insertErReview(vo); 
		
		if(result > 0) {
			List<ERReviewVO> reviews = service.selectErReview(code);
			model.addAttribute("reviews", reviews);
			model.addAttribute("code", code);
		}
       
        return "/swiftER/er/erDetail?code="+code+"&city="+city+"&town="+town;
	}
	
}
