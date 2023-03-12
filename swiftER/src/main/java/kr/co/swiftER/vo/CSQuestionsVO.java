package kr.co.swiftER.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CSQuestionsVO {
	private int no;
	private int qno;
	private String memberUid;
	private int cate;
	private String title;
	private String content;
	private String file;
	private String regip;
	private String rdate;
}
