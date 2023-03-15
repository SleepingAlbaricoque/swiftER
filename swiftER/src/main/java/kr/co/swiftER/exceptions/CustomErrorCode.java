package kr.co.swiftER.exceptions;

import lombok.Getter;

@Getter
public enum CustomErrorCode {

	WRONG_EXT_ERROR("이미지 파일 이외의 형식은 업로드 할 수 없습니다");
	
	private String errMsg;

	CustomErrorCode(String errMsg) {
		this.errMsg = errMsg;
	}
}
