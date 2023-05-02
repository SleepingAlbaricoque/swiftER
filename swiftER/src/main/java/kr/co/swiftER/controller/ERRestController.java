package kr.co.swiftER.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelExtensionsKt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import kr.co.swiftER.service.ERService;
import kr.co.swiftER.vo.ERCateVO;
import kr.co.swiftER.vo.ERReviewVO;
import kr.co.swiftER.vo.ERSubcateVO;
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
    @PostMapping(value = "er/erSearch", produces = "application/text; charset=utf8")    
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
    @PostMapping(value = "er/erSearch2", produces = "application/text; charset=utf8")    
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
    public String erDetail(Model model,Principal principal ,@RequestParam("code") String code,@RequestParam("city") String city,@RequestParam("town") String town, String pg) {
		
		int currentPage = service.getCurrentPage(pg);
        int start = service.getLimitStart(currentPage);
        long total = service.getTotalCount(code);
        int lastPage = service.getLastPageNum(total);
        int pageStartNum = service.getPageStartNum(total, start);
        int groups[] = service.getPageGroup(currentPage, lastPage);
		
		List<ERReviewVO> reviews = service.selectErReview(code,start);
		List<ERCateVO> region = service.selectErRegion(city);
		List<ERSubcateVO> subregion = service.selectErSubRegion(town,city);
		
		System.out.println("reviews"+reviews);
		
		if(principal != null) {
			//로그인 체크
			String uid = principal.getName();
			model.addAttribute("uid", uid);
		}

		model.addAttribute("reviews", reviews);
		model.addAttribute("code", code);
		model.addAttribute("region", region);
		model.addAttribute("subregion", subregion);
		model.addAttribute("city", city);
		model.addAttribute("town", town);
		model.addAttribute("currentPage", currentPage);
        model.addAttribute("lastPage", lastPage);
        model.addAttribute("pageStartNum", pageStartNum);
        model.addAttribute("groups", groups);
        model.addAttribute("pg", pg);
        model.addAttribute("total", total);
		
       
        return "er/erDetail";
    }

	@ResponseBody
    @PostMapping(value = "er/erDetailInfo", produces = "application/text; charset=utf8")    
    public String erDetailInfo(@RequestParam("code") String code) throws IOException {
		String deurl = erdetailUrl;
		System.out.println("code : "+code);
		
    	// RestTemplate 생성            
    	RestTemplate restTemplate = new RestTemplate();
    	
    	// 오브젝트로 결과값 받아오기
    	String url = deurl + restApiKey + "&HPID=" + code + "&pageNo=" + pageNo + "&numOfRows=" + numOfRows;
    	System.out.println("url : "+url);        
    	String response = restTemplate.getForObject(url, String.class);
    	
    	return response;

	}
}
