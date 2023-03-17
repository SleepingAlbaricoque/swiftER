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
		  errorMessage = "incorrect ID or PW.";
		  } else if (exception instanceof InternalAuthenticationServiceException) { //존재하지 않는 아이디일 때 던지는 예외
			  errorMessage = "Account does not exist. Join our member.";
			  } else if (exception instanceof UsernameNotFoundException) {
				  errorMessage = "Account does not exist. Join our member.";
				  } else if (exception instanceof AuthenticationCredentialsNotFoundException) {
					  errorMessage = "Your request has been rejected. Contact the manager.";
					  } else {
						  errorMessage = "Login failed for an unknown reason. Contact the administrator.";
						  }
	  setDefaultFailureUrl("/member/login?error=true&exception="+errorMessage);
	  super.onAuthenticationFailure(request, response, exception);
  }
}