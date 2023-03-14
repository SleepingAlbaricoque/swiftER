package kr.co.swiftER.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.swiftER.dao.MemberDAO;
import kr.co.swiftER.repo.MemberRepo;
import kr.co.swiftER.vo.MemberTermsVO;

@Service
public class MemberService {
	
	@Autowired MemberDAO dao;
	@Autowired MemberRepo repo;
	
	/* 회원 약관 불러오기 */
	public MemberTermsVO selectTerms() {
		return dao.selectTerms();
	}
	
	/* 회원가입 유효성 검사 */
	public int countUid(String uid) {
		return repo.countByUid(uid);
	}
	
}
