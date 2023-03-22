package kr.co.swiftER.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminMemberSearchVO {
	// admin 회원 리스트 가져오기 위해 만든 vo
	// 의사회원과 일반회원을 쿼리문 하나로 한꺼번에 가져오기 위해 해당 vo 생성
	private MemberVO member;
	private MemberDoctorVO doctor;
}
