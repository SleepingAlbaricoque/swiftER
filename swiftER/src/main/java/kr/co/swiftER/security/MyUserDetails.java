package kr.co.swiftER.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import kr.co.swiftER.entity.MemberEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MyUserDetails implements UserDetails{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// JPA를 사용할 클래스, 테이블과 매핑하는 역할
	private MemberEntity member;
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// 계정이 갖는 권한 목록 리턴
		List<GrantedAuthority> collector = new ArrayList<>();
		collector.add(new SimpleGrantedAuthority("ROLE_"+member.getGrade()));
		
		return collector;
	}


	/* 비밀번호 */
	@Override
	public String getPassword() {
		return member.getPass();
	}
	
	/* 아이디 */
	@Override
	public String getUsername() {
		return member.getUid();
	}

	/* 
	 * 계정 만료 여부
	 * true : 만료 안됨
	 * false : 만료 
	 * 
	 */
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	/* 
	 * 계정 잠김 여부
	 * true : 잠기지 않음
	 * false : 잠김 
	 * 
	 */
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	/* 
	 * 비밀번호 만료 여부
	 * true : 만료 안됨
	 * false : 만료 
	 * 
	 */
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	/* 
	 * 사용자 활성화 여부
	 * true : 활성화
	 * false : 비활성화 
	 * 
	 */
	@Override
	public boolean isEnabled() {
		return true;
	}
	
	

}
