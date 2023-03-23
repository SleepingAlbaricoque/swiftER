package kr.co.swiftER.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileDoctorVO {
	private String member_uid;
	private String newName;
	private String oriName;
	private String rdate;
}
