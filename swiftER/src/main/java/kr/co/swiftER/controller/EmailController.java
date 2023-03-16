package kr.co.swiftER.controller;

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

    @ResponseBody
    @GetMapping("/member/checkEmail")
    public String sendEmail(@RequestParam("email") String email) {
        String to = email;
        String subject = "swiftER 인증코드입니다";
        String text = "This is a test email sent from Spring Boot application.";
        emailService.sendSimpleMessage(to, subject, text);
        return email;
    }
    
}