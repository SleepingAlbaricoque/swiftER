package kr.co.swiftER.dao;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import kr.co.swiftER.vo.MemberTermsVO;
import kr.co.swiftER.vo.MemberVO;

@Mapper
@Repository
public interface MemberDAO {

	public MemberTermsVO selectTerms();
	
	public int countMember(String uid);

	public int insertMember(MemberVO member);
	
}
