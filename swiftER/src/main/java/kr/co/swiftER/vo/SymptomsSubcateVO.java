package kr.co.swiftER.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SymptomsSubcateVO {
	private String part;
	private int body1Code;
	private int code;
}
