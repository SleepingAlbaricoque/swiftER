package kr.co.swiftER.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(CustomException.class)
	public CustomErrorResponse handleWrongExtException(CustomException e) {
		
		log.error("errorMessage: ", e.getCustomErrMsg());
		
		return CustomErrorResponse.builder().statusMessage(e.getCustomErrMsg()).build();
	}
}
