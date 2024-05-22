package com.advantal.exception;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.advantal.utils.Constant;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		
		List<FieldError> fieldErrorList= ex.getBindingResult().getFieldErrors();
		List<String> errorList= fieldErrorList.stream()
				.map(error -> error.getField()+": "+error.getDefaultMessage())
				.collect(Collectors.toList());
		
		ApiError apiError = new ApiError();
		apiError.setDate(new Date());
		apiError.setPathUrl(request.getDescription(false));
		apiError.setMessage(HttpStatus.BAD_REQUEST);
		apiError.setResponseCode(Constant.BAD_REQUEST);
		apiError.setErrorList(errorList);
		return new ResponseEntity<>(apiError, headers, apiError.getMessage());
	}
	
	@ExceptionHandler(ResourceNotFoundException.class)
	ResponseEntity<?> resourceNotFoundHandler(Exception e, ServletWebRequest request) {
		ApiError apiError = new ApiError();

		apiError.setDate(new Date());
		apiError.setPathUrl(request.getDescription(true));
		apiError.setMessage(HttpStatus.NOT_FOUND);
		apiError.setResponseCode(Constant.OK);
		apiError.setErrorList(Arrays.asList(e.getMessage()));
		return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getMessage());
	}
	
	@ExceptionHandler({DataAccessResourceFailureException.class})
	ResponseEntity<?> dataBaseHandler(Exception e, ServletWebRequest request) {
		ApiError apiError = new ApiError();

		apiError.setDate(new Date());
		apiError.setPathUrl(request.getDescription(true));
		apiError.setMessage(HttpStatus.INTERNAL_SERVER_ERROR);
		apiError.setResponseCode(Constant.DB_CONNECTION_ERROR);
		apiError.setErrorList(Arrays.asList(e.getMessage()));
		return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getMessage());
	}
	
	@ExceptionHandler(BadRequestException.class)
	ResponseEntity<?> badRequestExceptionHandler(Exception e, ServletWebRequest request) {
		ApiError apiError = new ApiError();

		apiError.setDate(new Date());
		apiError.setPathUrl(request.getDescription(true));
		apiError.setMessage(HttpStatus.BAD_REQUEST);
		apiError.setResponseCode(Constant.BAD_REQUEST);
		apiError.setErrorList(Arrays.asList(e.getMessage()));
		return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getMessage());
	}
	
}
