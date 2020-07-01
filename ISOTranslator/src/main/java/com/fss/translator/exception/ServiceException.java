/**
 * 
 */
package com.fss.translator.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * ServiceException class handles all the exceptions from the Service layer.
 * 
 * @author Harikrishna Agnikondu
 *
 */

@Getter @Setter
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class ServiceException extends Exception {

	private static final long serialVersionUID = 1L;

	private String code;

	private String message;

	public ServiceException(String message) {
		super(message);
		this.message = message;
	}

	public ServiceException(Exception message) {
		super(message);
	}

}
