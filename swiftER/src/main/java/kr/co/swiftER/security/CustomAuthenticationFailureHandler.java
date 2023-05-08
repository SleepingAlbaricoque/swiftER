package kr.co.swiftER.security;

import java.io.IOException;
import java.net.URLEncoder;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationFailureHandler 
extends SimpleUrlAuthenticationFailureHandler {

  @Override
  public void onAuthenticationFailure(
    HttpServletRequest request,
    HttpServletResponse response,
    AuthenticationException exception) 
    throws IOException, ServletException {

	  String errorMessage;
	  if (exception instanceof BadCredentialsException) { //비밀번호가 일치하지 않을 때 던지는 예외
		  errorMessage = URLEncoder.encode("아이디와 비밀번호가 일치하지 않습니다", "UTF-8");
		  } else if (exception instanceof InternalAuthenticationServiceException) { // DB 허용값 이상으로 이상한 것 입력했을 때 출력
			  errorMessage = URLEncoder.encode("DB연결 오류입니다 (재시도 필요)", "UTF-8");
			  } else if (exception instanceof UsernameNotFoundException) { //존재하지 않는 아이디일 때 던지는 예외
				  errorMessage = URLEncoder.encode("존재하지 않는 아이디입니다", "UTF-8");
				  } else if (exception instanceof AuthenticationCredentialsNotFoundException) {
					  errorMessage = URLEncoder.encode("요청이 거부되었습니다 관리자에게 연락해주세요", "UTF-8");
					  } else {
						  errorMessage = URLEncoder.encode("알 수 없는 이유로 로그인에 실패했습니다(재시도 필요)", "UTF-8");
						  }
	  setDefaultFailureUrl("/member/login?error=true&exception="+errorMessage);
	  super.onAuthenticationFailure(request, response, exception);
  }
}