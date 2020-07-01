package com.fss.translator.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.fss.translator.constants.ResponseMessages;
import com.fss.translator.util.Response;
import com.fss.translator.util.ResponseBuilder;

import lombok.extern.log4j.Log4j2;

/**
 * GlobalControllerExceptionHandler class handles all the exceptions at the
 * controller level in a consistent manner.
 * 
 * @author ravinaganaboyina
 *
 */

@Log4j2
@ControllerAdvice
public class GlobalControllerExceptionHandler {

	@Autowired
	private ResponseBuilder responseBuilder;

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Response> exceptionHandler(Exception exception) {

		String errMessage = ResponseMessages.GENERIC_ERR_MESSAGE;

		if (exception instanceof ServiceException) {
			errMessage = exception.getMessage();
		}
		log.error(errMessage);

		return new ResponseEntity<>(responseBuilder.buildGenericResponse(errMessage), HttpStatus.OK);
	}

}
