/**
 * 
 */
package com.fss.translator.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fss.translator.constants.ResponseMessages;

/**
 * ResponseBuilder class builds the response returned by the Translator Service.
 * 
 * @author Harikrishna Agnikondu
 *
 */

@Component
public class ResponseBuilder {

	@Autowired
	private LocaleHandler localeHandler;

	/**
	 * Returns a Response for a successful response.
	 * 
	 * @param data
	 *            The data to be sent in the response.
	 * @param message
	 *            The message to be sent in the response.
	 * 
	 * @return the Response for the success response.
	 */
	public Response buildSuccessResponse(String responseData) {
		
		return Response.builder().responseCode(ResponseMessages.SUCCESS)
				.responseMessage(localeHandler.getLocalizedMessage(ResponseMessages.SUCCESS))
				.responseData(responseData).build();
	}

	/**
	 * Returns a Response for a failure response.
	 * 
	 * @param message
	 *            The message to be sent in the response.
	 * 
	 * @return the Response for the failure response.
	 */
	public Response buildFailureHeaderResponse(String code) {
		
		return Response.builder().responseCode(code)
				.responseMessage(localeHandler.getLocalizedMessage(code)).build();
	}

	public Response buildFailureResponse(String responseData, String code) {
		
		return Response.builder().responseCode(code)
				.responseMessage(localeHandler.getLocalizedMessage(code))
				.responseData(responseData).build();
	}

	public Response buildGenericResponse(String responseData) {
		
		return Response.builder().responseCode(ResponseMessages.GENERIC_ERR_MESSAGE)
				.responseMessage(localeHandler.getLocalizedMessage(ResponseMessages.GENERIC_ERR_MESSAGE))
				.responseData(responseData).build();
	}

}
