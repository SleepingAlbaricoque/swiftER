package kr.co.swiftER.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import ch.qos.logback.core.model.Model;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class ERRestController {
	@Value("${restApi.key}")
    private String restApiKey;
    
    @Value("${restApi.erbasisUrl}")
    private String erbasisUrl;
    
    @Value("${restApi.erdetailUrl}")
    private String erdetailUrl;
    
    @Value("${restApi.erlocationUrl}")
    private String erlocationUrl;
    
    @Value("${restApi.erlistUrl}")
    private String erlistUrl;
    
    @ResponseBody
    @PostMapping("er/erSearch")    
    public String erSearch(Model model, String city, String town) throws IOException {
        String[] arrUrl = {erbasisUrl, erdetailUrl, erlocationUrl, erlistUrl};
        
        String pageNo = "1";
        String numOfRows = "1000";
        
        // RestTemplate 생성            
        RestTemplate restTemplate = new RestTemplate();
         
        // 오브젝트로 결과값 받아오기
        String url = arrUrl[3] + restApiKey + "&Q0=" + city + "&Q1=" + town + "&pageNo=" + pageNo + "&numOfRows=" + numOfRows;
        System.out.println("url : "+url);        
        String response = restTemplate.getForObject(url, String.class);
        
//        log.info("response : " + response);
        
        return response;
    }
}
