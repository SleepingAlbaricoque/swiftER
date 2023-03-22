package kr.co.swiftER.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberDoctorVO {
	private String member_uid;
	private String kind;
	private String specialty;
	private String cert_oriName;
	private String cert_newName;
	private int verified;
	private String veriMsg;
}
