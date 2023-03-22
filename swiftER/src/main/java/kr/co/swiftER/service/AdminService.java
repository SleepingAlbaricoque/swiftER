package kr.co.swiftER.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.swiftER.dao.AdminDAO;
import kr.co.swiftER.vo.AdminMemberSearchVO;
import kr.co.swiftER.vo.MemberVO;

@Service
public class AdminService {

	@Autowired
	private AdminDAO dao;
	
	public List<AdminMemberSearchVO> selectMembers(int start){
		return dao.selectMembers(start);
	}
	
	
	
}
