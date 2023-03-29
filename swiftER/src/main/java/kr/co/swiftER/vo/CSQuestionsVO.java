package kr.co.swiftER.vo;

import java.util.List;

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
	private int cateCode;
	private int subcateCode;
	private String title;
	private String content;
	private int view;
	private int file;
	private String regip;
	private String rdate;
	
	// 파일 업로드를 위해 추가
	private MultipartFile fname;
	
	// QnA 리스트 출력을 위해 추가
	private String subcate;
	
	// 파일 다운로드를 위해 추가
	private List<FileVO> fvoList;
	
	// 내가 쓴 qna 답변 여부 체크위해 추가
	private String isAnswered;
	
	
}
