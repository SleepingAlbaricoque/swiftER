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
	private String hospital_name;
	private int region_code;
	private int subregion_code;
	private String title;
	private int rating;
	private String content;
	private String regip;
	private String rdate;
	
	public void setRegion_code(String region_code) {
		this.region_code = Integer.parseInt(region_code);
	}
	public void setSubregion_code(String subregion_code) {
		this.subregion_code = Integer.parseInt(subregion_code);;
	}
	public void setRating(String rating) {
		this.rating = Integer.parseInt(rating);
	}
	// 추가 필드
	private String region;
	private String subregion;
}
