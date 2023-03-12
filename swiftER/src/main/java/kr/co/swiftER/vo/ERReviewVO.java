package kr.co.swiftER.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ERReviewVO {
	private int no;
	private String memberUid;
	private String hospitalCode;
	private int regionCode;
	private int subregionCode;
	private String title;
	private int rating;
	private String content;
	private String regip;
	private String rdate;
}
