package kr.co.swiftER.controller;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import kr.co.swiftER.vo.ErResultVO;
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
    
    @PostMapping("er/erSearch")
    @ResponseBody
    public ModelAndView erSearch(String city, String town) throws IOException {
        Map<String, Object> result = new HashMap<>();
        ModelAndView mav = new ModelAndView();
        
        String[] arrUrl = {erbasisUrl, erdetailUrl, erlocationUrl};
        String[] arrName = {"BASIS", "DETAIL", "LOACTION"};
        
        try {
            String pageNo = "1";
            String numOfRows = "1000";
            String urlstr = arrUrl[0] + restApiKey + "&STAGE1=" + city + "&STAGE2=" + town 
                    + "&pageNo=" + pageNo + "&numOfRows=" + numOfRows;
            URL url = new URL(urlstr); 
                
            System.out.println("url : "+url);
            HttpURLConnection connect = (HttpURLConnection) url.openConnection();
            connect.setRequestMethod("GET");
                
            JAXBContext jaxbContext = JAXBContext.newInstance(ErResultVO.class);
            Unmarshaller unmarsharller = jaxbContext.createUnmarshaller();
            ErResultVO ErResultVO = (ErResultVO) unmarsharller.unmarshal(url);
            result.put(arrName[0], ErResultVO.body.basisItems.basis);
            Collection<Object> values = result.values();
            System.out.println(values);
                
            if(ErResultVO.body.basisItems.basis != null && ErResultVO.header.resultCode.equals("00")) {
                    
                for(int i = 1; i < arrUrl.length; i++) {
                    urlstr = arrUrl[i] + restApiKey + "&STAGE1=" + city + "&STAGE2=" + town 
                           + "&pageNo=" + pageNo + "&numOfRows=" + numOfRows ; 
                    url = new URL(urlstr); 
                    connect = (HttpURLConnection) url.openConnection();
                    connect.setRequestMethod("GET");                    
                    unmarsharller = jaxbContext.createUnmarshaller();
                        
                    ErResultVO = (ErResultVO) unmarsharller.unmarshal(url);
                    result.put(arrName[i], ErResultVO.body.basisItems.basis);                
                }
            } else {
                throw new Exception();
            }
            result.put("RESULT", "SUCCESS");
        } catch (Exception e) {
            result.put("RESULT", "FAILED");
            log.error(e.getMessage());
        }

        
        mav.addObject("items", result);
        mav.setViewName("er/er");
        return mav;
    }

}
