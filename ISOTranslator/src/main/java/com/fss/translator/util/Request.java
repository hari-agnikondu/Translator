package com.fss.translator.util;

import com.fss.translator.constants.TranslatorConstants;

import lombok.Data;
import net.minidev.json.JSONObject;

/**
 * Request will map into this class
 * 
 * @author Harikrishna Agnikondu
 *
 */
@Data
public class Request {

	private String sourceMessageType;

	private String apiKey;

	private String requestData;


	public String getRequestBody() {
		JSONObject obj = new JSONObject();
		obj.put(TranslatorConstants.SOURCEMESSAGETYPE, this.sourceMessageType);
		obj.put(TranslatorConstants.APIKEY, this.apiKey);
		obj.put(TranslatorConstants.REQUESTDATA, this.requestData);
		return obj.toString();

	}

}
