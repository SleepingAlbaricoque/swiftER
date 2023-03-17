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
	private int cateCode;
	private int regionCode;
	private String member_uid;
	private String title;
	private String content;
	private int view;
	private String file;
	private String regip;
	private String rdate;
	
	// 추가필드
	private String cate;
	private String region;
	private String keyword;
	
	public int getCateCode() {
        return cateCode;
    }
    public void setCateCode(String cateCode) {
        this.cateCode = Integer.parseInt(cateCode);
    }
	public int getRegionCode() {
        return regionCode;
    }
    public void setRegionCode(String regionCode) {
        this.regionCode = Integer.parseInt(regionCode);
    }
}
