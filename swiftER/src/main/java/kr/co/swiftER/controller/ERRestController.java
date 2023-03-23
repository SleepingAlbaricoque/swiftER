package kr.co.swiftER.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import kr.co.swiftER.dto.ErDTO;
import kr.co.swiftER.dto.ItemDTO;
import kr.co.swiftER.service.ERService;
import kr.co.swiftER.vo.ERReviewVO;

@Controller
public class ERRestController {
	@Autowired
	private ERService service;
	
	@Value("${restApi.key}")
    private String restApiKey;
    
    @Value("${restApi.erdetailUrl}")
    private String erdetailUrl;
    
    @Value("${restApi.erlistUrl}")
    private String erlistUrl;

    @Value("${restApi.erUserfulUrl}")
    private String erUserfulUrl;
    
    @Value("${restApi.erSmTypeUrl}")
    private String erSmTypeUrl;

    private String pageNo = "1";
    private String numOfRows = "1000";
    
    @ResponseBody
    @PostMapping("er/erSearch")    
    public String erSearch(String city, String town) throws IOException {
        
        // RestTemplate 생성            
        RestTemplate restTemplate = new RestTemplate();
         
        // 오브젝트로 결과값 받아오기
        String url = erlistUrl + restApiKey + "&Q0=" + city + "&Q1=" + town + "&pageNo=" + pageNo + "&numOfRows=" + numOfRows;
        System.out.println("url : "+url);        
        String response = restTemplate.getForObject(url, String.class);
        
        return response;
    }
    
    @ResponseBody
    @PostMapping("er/erSearch2")    
    public String erSearch2(String city, String town) throws IOException {
    	
    	// RestTemplate 생성            
    	RestTemplate restTemplate = new RestTemplate();
    	
    	// 오브젝트로 결과값 받아오기
    	String url = erUserfulUrl + restApiKey + "&STAGE1=" + city + "&STAGE2=" + town + "&pageNo=" + pageNo + "&numOfRows=" + numOfRows;
    	System.out.println("url : "+url);        
    	String response = restTemplate.getForObject(url, String.class);
    	
    	return response;
    }
    
    @ResponseBody
    @PostMapping("er/erSearch3")    
    public String erSearch3(String city, String town) throws IOException {
    	
    	// RestTemplate 생성            
    	RestTemplate restTemplate = new RestTemplate();
    	
    	// 오브젝트로 결과값 받아오기
    	String url = erSmTypeUrl + restApiKey + "&STAGE1=" + city + "&STAGE2=" + town + "&pageNo=" + pageNo + "&numOfRows=" + numOfRows;
    	System.out.println("url : "+url);        
    	String response = restTemplate.getForObject(url, String.class);
    	
    	return response;
    }
    
	@GetMapping("er/erDetail")
    public String erDetail(Model model,@RequestParam("code") String code) {
    	System.out.println("code : "+code);
    	
    	String deurl = erdetailUrl;
    	
		List<ERReviewVO> reviews = service.selectErReview(code);
		model.addAttribute("reviews", reviews);
    	
    	
        
        try {
        	// RestTemplate 생성            
            RestTemplate restTemplate = new RestTemplate();
             
            // 오브젝트로 결과값 받아오기
            String url = deurl + restApiKey + "&HPID=" + code + "&pageNo=" + pageNo + "&numOfRows=" + numOfRows;
            System.out.println("url : "+url);
            ErDTO response = restTemplate.getForObject(url, ErDTO.class);
            System.out.println("response : "+response.getBody());
            List<ItemDTO> result = response.getBody().getItem();
            System.out.println("result : "+ result);
        
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        
        return "er/erDetail";
    }
    
    
    
}
