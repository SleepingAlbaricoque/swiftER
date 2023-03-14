package kr.co.swiftER.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.text.Document;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import kr.co.swiftER.vo.ErItemVO;
import kr.co.swiftER.vo.ErResultVO;

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
    
	@PostMapping("er/erSearch")
	@ResponseBody
	public String erSearch(Model model, String city, String town) throws IOException{
//		
//        Map<String, Object> result = new HashMap<String, Object>();    
//        HttpURLConnection connect = null;
        
        
        try {
	        	String [] arrUrl = {erbasisUrl, erdetailUrl, erlocationUrl};
	        	String [] arrName = {"BASIS", "DETAIL", "LOACTION"};
	        	
	        	String pageNo = "1";
	        	String numOfRows = "1000";
	        	URI uri = UriComponentsBuilder.fromUriString(arrUrl[0])
							        			.queryParam("", restApiKey)
							        			.queryParam("STAGE1", city)
							        			.queryParam("STAGE2", town)
							        			.queryParam("pageNo", pageNo)
							        			.queryParam("numOfRows", numOfRows)
							        			.encode().build().toUri();
	        	
//	        	
//                String urlstr = arrUrl[0] + restApiKey + "&STAGE1=" + city + "&STAGE2=" + town 
//                        + "&pageNo=" + pageNo + "&numOfRows=" + numOfRows ;
//                        //GET방식으로 받게끔 되어있는 API라서 URL로 달 수 밖에 없다.
//                        //returnType을 Json으로 받고 싶다면 "&_returnType=json"를 추가해주면 된다.
//                URL url = new URL(urlstr); 
                RequestEntity<Void> req = RequestEntity.get(uri).build();
                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<String> result = restTemplate.exchange(req, String.class);
                
                //List<ErItemVO> items = result.getBody();
                
                //System.out.println("url : "+url);
//                connect = (HttpURLConnection) url.openConnection();
//                connect.setRequestMethod("GET");
//                
//                JAXBContext jaxbContext = JAXBContext.newInstance(ErResultVO.class);
//                Unmarshaller unmarsharller = jaxbContext.createUnmarshaller();
//                ErResultVO ErResultVO = (ErResultVO) unmarsharller.unmarshal(url);
//                result.put(arrName[0], ErResultVO.body.basisItems.basis);
//                Collection<Object> values = result.values();
//                System.out.println(values);
                
//                if(ErResultVO.body.basisItems.basis != null && ErResultVO.header.resultCode.equals("00")) {
//                    
//                    for(int i = 1; i < arrUrl.length; i++) {
//                        urlstr = arrUrl[i] + restApiKey + "&STAGE1=" + city + "&STAGE2=" + town 
//                                + "&pageNo=" + pageNo + "&numOfRows=" + numOfRows ; 
//                        url = new URL(urlstr); 
//                        connect = (HttpURLConnection) url.openConnection();
//                        connect.setRequestMethod("GET");                    
//                        unmarsharller = jaxbContext.createUnmarshaller();
//                        
//                        ErResultVO = (ErResultVO) unmarsharller.unmarshal(url);
//                        result.put(arrName[i], ErResultVO.body.basisItems.basis);                
//                    }
//                }else {
//                    throw new Exception();
//                }
            //result.put("RESULT", "SUCCESS");
        } catch (Exception e) {
            //result.put("RESULT", "FAILED");
        }
        return "";
        		
		
	}
	
	
}
