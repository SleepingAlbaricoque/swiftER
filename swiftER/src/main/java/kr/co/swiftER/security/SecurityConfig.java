package kr.co.swiftER.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.ConditionalOnDefaultWebSecurity;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.MessageDigestPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@ConditionalOnDefaultWebSecurity
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@Configuration
@EnableWebSecurity
public class SecurityConfig{

	@Bean
    @Order(SecurityProperties.BASIC_AUTH_ORDER)
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
		// 인가(접근 권한) 설정
		http.authorizeRequests().requestMatchers("/").permitAll();
		
		// 사이트 위조 방지 설정
		http.csrf().disable();
		
		http.httpBasic().disable();
		
		// 로그인 설정
		http.formLogin()
		.loginPage("/member/login")
		.defaultSuccessUrl("/index")
		.failureUrl("/user/login?success=100")
		.usernameParameter("uid")
		.passwordParameter("pass");
		
		// 로그아웃 설정
		http.logout()
		.invalidateHttpSession(true)
		.logoutRequestMatcher(new AntPathRequestMatcher("/member/logout"))
		.logoutSuccessUrl("/user/login?success=200");
		
		return http.build();
	}
	
	@Autowired
	private SecurityUserService userService;
	
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		
		// Security 사용자에 대한 권한 설정
		// auth.inMemoryAuthentication().withUser("admin").password("{noop}1234").roles("ADMIN");
		// auth.inMemoryAuthentication().withUser("manager").password("{noop}1234").roles("MANAGER");
		// auth.inMemoryAuthentication().withUser("member").password("{noop}1234").roles("MEMBER");
		
		// 로그인 인증 처리 서비스, 암호화 방식 설정; Spring Security는 패스워드 강제로 암호화함
		auth.userDetailsService(userService).passwordEncoder(new BCryptPasswordEncoder());
	}
	
	@Bean
	public PasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}
}