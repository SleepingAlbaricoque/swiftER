package kr.co.swiftER.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ERReviewVO {
	private int no;
	private String member_uid;
	private String hospital_code;
	private int region_code;
	private int subregion_code;
	private String title;
	private int rating;
	private String content;
	private String regip;
	private String rdate;
	
	//추가
	private String region;
	private String subregion;
}
