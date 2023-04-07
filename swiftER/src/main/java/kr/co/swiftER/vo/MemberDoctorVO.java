package kr.co.swiftER.vo;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberDoctorVO {
	private String member_uid;
	private String kind;
	private String specialty;
	private String cert_oriName;
	private String cert_newName;
	private int verified;
	private String veriMsg;
	// 파일 업로드를 위해 추가
	private MultipartFile fname;
	private int file;
}
