package kr.co.swiftER.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.swiftER.dao.MemberDAO;
import kr.co.swiftER.vo.MemberTermsVO;

@Service
public class MemberService {
	
	@Autowired MemberDAO dao;
	
	public MemberTermsVO selectTerms() {
		return dao.selectTerms();
	}
	
}
