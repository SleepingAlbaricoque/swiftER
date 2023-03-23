package kr.co.swiftER.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import kr.co.swiftER.vo.AdminMemberSearchVO;
import kr.co.swiftER.vo.MemberVO;

@Mapper
@Repository
public interface AdminDAO {

	public List<AdminMemberSearchVO> selectMembers(@Param(value="start") int start, @Param(value="isDoc")int isDoc);
	public AdminMemberSearchVO selectMember(String uid);
	
	// 전체 회원 수
	public int selectCountTotal();
	
	// 인증 승인 요청한 의사의 수
	public int selectDocCountTotal();
	
	// 회원 영구 정지 시키기(member 테이블 grade가 4로, wdate 기록)
	public int banMember(String uid);
}
