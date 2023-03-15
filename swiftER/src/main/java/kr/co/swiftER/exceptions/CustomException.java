package kr.co.swiftER.exceptions;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private CustomErrorCode customErrorCode;
	private String customErrMsg;
	
	public CustomException(CustomErrorCode customErrorCode) {
		super(customErrorCode.getErrMsg());
		this.customErrorCode = customErrorCode;
		this.customErrMsg = customErrorCode.getErrMsg();
	}
}
