package kr.co.swiftER.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import kr.co.swiftER.vo.AdminMemberModifyVO;
import kr.co.swiftER.vo.AdminMemberSearchVO;
import kr.co.swiftER.vo.FileVO;
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
	
	// 의사 회원 uid값으로 첨부한 면허증 사진 정보 가져오기
	public FileVO selectDocCert(String uid);

	// 의사 회원 승인하기
	public int certVerify(@Param(value="uid") String uid, @Param(value="status") String status);
	
	// 회원 정보 수정하기
	public int updateMember(AdminMemberModifyVO member);
	
	// 의사 회원의 인증 보류시 보류 메시지 수정하기(회원 정보 수정과 transaction으로 묶을 것)
	public int updateVeriMsg(AdminMemberModifyVO member);
}
