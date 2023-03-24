package kr.co.swiftER.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import kr.co.swiftER.entity.MemberEntity;
import kr.co.swiftER.repo.MemberRepo;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SecurityUserService implements UserDetailsService{

	@Autowired
	private MemberRepo repo;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// 사용자 확인
		MemberEntity member = repo.findById(username).get();
		
		if(member == null) {
			throw new UsernameNotFoundException(username); // 사용자 없을 경우 예외처리
		} 
		
		UserDetails userDts = MyUserDetails.builder().member(member).build();
		
		return userDts;
	}

}
