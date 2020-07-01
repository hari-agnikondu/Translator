
package com.fss.translator.dto;

import java.util.Map;

import lombok.Data;

/**
 * This class used for passing object to across all methods for process request
 * 
 * @author Harikrishna Agnikondu
 *
 */
@Data
public class ValueDTO {

	private String responseData;

	private Map<String, String> institution;

	private Map<String, Object> requestObject;

	private Map<String, Object> transferObject;

}
