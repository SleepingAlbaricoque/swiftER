package kr.co.swiftER.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberHistoryVO {
	private String uid;
	private String rdate;
	private String hospital;
	private String symptom;
}
