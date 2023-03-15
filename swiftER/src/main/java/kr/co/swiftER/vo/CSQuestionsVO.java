package kr.co.swiftER.vo;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CSQuestionsVO {
	private int no;
	private int qno;
	private String member_uid;
	private int cate;
	private int subcate;
	private String title;
	private String content;
	private String file;
	private String regip;
	private String rdate;
	
	// 파일 업로드를 위해 추가
	private MultipartFile fname;
}
