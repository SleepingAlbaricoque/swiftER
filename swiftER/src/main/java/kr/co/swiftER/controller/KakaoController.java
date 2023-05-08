package kr.co.swiftER.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import kr.co.swiftER.service.MemberService;

@Controller
public class KakaoController {
	
	@Autowired MemberService service;
 
    @GetMapping("kakao/kakaoAuth")
    public String kakaoAuth(@RequestParam("code") String code) {
        System.out.println("code: " + code);
        String access_Token = service.getKaKaoAccessToken(code);
        System.out.println("access_Token : "+ access_Token);
        return "redirect:/index";
    }
}