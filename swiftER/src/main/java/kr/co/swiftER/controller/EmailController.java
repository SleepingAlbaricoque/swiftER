package kr.co.swiftER.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.co.swiftER.service.EmailService;

@Controller
public class EmailController {

    @Autowired
    private EmailService emailService;
    
  //인증번호 생성
    protected final String key = createKey();
    
 // 인증코드 만들기
    public static String createKey() {
        StringBuffer key = new StringBuffer();
        Random rnd = new Random();

        for (int i = 0; i < 6; i++) { // 인증코드 6자리
            key.append((rnd.nextInt(10)));
        }
        return key.toString();
    }

    @ResponseBody
    @GetMapping("member/checkEmail")
    public String sendEmail(@RequestParam("email") String email) {
        String to = email;
        String subject = "swiftER 인증코드입니다";
        String text = "인증번호는" + key + "입니다";
        emailService.sendSimpleMessage(to, subject, text);
        return email;
    }
    
    /* 이메일 인증번호 맞는지 확인 */
	@ResponseBody
	@GetMapping("member/checkCode")
	public Map<String,Integer> checkCode(@RequestParam("code") String code) {
		
		int result;
		System.out.println("key : "+key);
		System.out.println("code : "+code);
		
		if(key.equals(code)) {
			result = 0;
		}else {
			result = 1;
		}
		
		Map<String,Integer> map = new HashMap<>();
		map.put("result", result);
        return map;
	}
}