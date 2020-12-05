package com.paypal.bfs.test.employeeserv.impl;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.paypal.bfs.test.employeeserv.api.model.ErrorObject;
import com.paypal.bfs.test.employeeserv.common.EntityNotFoundException;

/**
 * Handles exceptions and returns correct http status codes.
 */
@ControllerAdvice
public class RestExceptionHandler {
	
	@ExceptionHandler(value = {EntityNotFoundException.class})
	public ResponseEntity<ErrorObject> entityNotFound(EntityNotFoundException exception) {
		
		ErrorObject error = new ErrorObject();
		error.setStatus(HttpStatus.NOT_FOUND.value());
		error.setMsg(exception.getMessage());
		return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(value = {IllegalArgumentException.class, HttpMessageNotReadableException.class})
	public ResponseEntity<ErrorObject> badRequest(Exception exception) {
		
		ErrorObject error = new ErrorObject();
		error.setStatus(HttpStatus.BAD_REQUEST.value());
		error.setMsg(exception.getMessage());
		return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(value = {Exception.class})
	public ResponseEntity<ErrorObject> otherException(Exception exception) {
		
		ErrorObject error = new ErrorObject();
		error.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		error.setMsg("Internal server error");
		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	

}
