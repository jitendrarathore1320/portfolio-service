package com.advantal.exception;

import java.util.Date;
import java.util.List;

import org.springframework.http.HttpStatus;

import lombok.Data;

@Data
public class ApiError {

	private HttpStatus message;
//	private String statusMessage;
	private String responseCode;
	private List<String> errorList;
	private Date date;
	private String pathUrl;
		
}
