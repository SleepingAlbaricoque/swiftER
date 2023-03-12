package kr.co.swiftER.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommunityArticleVO {
	private int no;
	private int parent;
	private int comments;
	private int cate;
	private int region;
	private String memberUid;
	private String title;
	private String content;
	private String file;
	private String regip;
	private String rdate;
}
