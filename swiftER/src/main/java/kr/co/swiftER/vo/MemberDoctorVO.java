package kr.co.swiftER.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberDoctorVO {
	private String memberUid;
	private String specialty;
	private String certOriName;
	private String certNewName;
	private int verified;
	private String veriMsg;
}
