package com.fss.translator.util;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


/**
 * This class will return the Response
 * 
 * @author Harikrishna Agnikondu
 *
 */


@Builder
@AllArgsConstructor
@XmlRootElement(name = "response")
@Data
public class Response implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String responseMessage;
	@Builder.Default
	private String responseData;
	private String responseCode;

	public Response() {
		// default constructor
	}

}
