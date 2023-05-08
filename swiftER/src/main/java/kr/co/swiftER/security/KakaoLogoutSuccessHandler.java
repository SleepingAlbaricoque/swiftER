package kr.co.swiftER.security;

import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class KakaoLogoutSuccessHandler implements LogoutSuccessHandler {
	 
    private final RestTemplate restTemplate;
 
    public KakaoLogoutSuccessHandler(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
 
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                Authentication authentication) throws IOException, ServletException {
        // 카카오 API 호출을 통한 로그아웃 처리
    	System.out.println("카카오 로그아웃");
    	
        String accessToken = (String) request.getSession().getAttribute("access_Token");
        System.out.println(accessToken);
        if (accessToken != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
            parameters.add("client_id", "b59782950c07b248fe9ef97eacdd98a1");
            parameters.add("logout_redirect_uri", "http://127.0.0.1:8181/swiftER/");
            parameters.add("access_token", accessToken);
            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(parameters, headers);
            restTemplate.postForObject("https://kapi.kakao.com/v1/user/logout", entity, String.class);
            request.getSession().removeAttribute("access_Token"); // 세션에서 삭제
        }
        
        // access_token 세션에서 삭제
        request.getSession().removeAttribute("access_Token");
        // 로그아웃 성공 시 리다이렉트할 URL 설정
        response.sendRedirect("/swiftER/");
    }
}