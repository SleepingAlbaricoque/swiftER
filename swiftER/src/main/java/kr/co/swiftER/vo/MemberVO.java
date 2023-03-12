package kr.co.swiftER.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberVO {
	private String uid;
	private String pass;
	private String name;
	private String nickname;
	private String birth;
	private String email;
	private String contact;
	private String zip;
	private String addr1;
	private String addr2;
	private int grade;
	private String regip;
	private String rdate;
	private String wdate;
}
