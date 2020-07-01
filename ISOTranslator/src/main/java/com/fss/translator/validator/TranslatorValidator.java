package com.fss.translator.validator;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fss.translator.constants.ResponseMessages;
import com.fss.translator.constants.TranslatorConstants;
import com.fss.translator.dto.ValueDTO;
import com.fss.translator.exception.ServiceException;
import com.fss.translator.resource.TranslatorResources;
import com.fss.translator.util.Util;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class TranslatorValidator {

	@Autowired
	TranslatorResources translatorResources;

	public void validateRequest(ValueDTO valueDto) throws ServiceException {

		log.debug("Validating request fields");
		Map<String, String> configRequest = translatorResources.getRequestFields();

		Map<String, Object> validateMap = new HashMap<>();
		validateMap.put(TranslatorConstants.RESPONSE_CODE, ResponseMessages.SUCCESS);
		Map<String, Object> requestBody = valueDto.getRequestObject();
		Map<String, String> instMap = translatorResources
				.getInstitutionData(requestBody.get(TranslatorConstants.SRCAPPID) + "");
		log.info("Institution map is {}",instMap);
		configRequest.entrySet().forEach(map -> {
			if (requestBody.containsKey(map.getKey())) {
				if (TranslatorConstants.IS_MANDATORY.equals(map.getValue())
						&& Util.isEmpty(requestBody.get(map.getKey()) + "")) {
					setValidation(map.getKey(), validateMap);
				} else if (!Util.isEmpty(requestBody.get(map.getKey()) + "")) {
					if (TranslatorConstants.APIKEY.equals(map.getKey())) {
						Map<String, String> mapValue = valueDto.getInstitution();
						if (Util.isNotNull(mapValue) && mapValue.containsKey(TranslatorConstants.APIKEY)) {
							String apiKeyValue = mapValue.get(TranslatorConstants.APIKEY);
							if (!Util.isEmpty(apiKeyValue) && !apiKeyValue.equals(requestBody.get(map.getKey()) + "")) {
								setValidation(map.getKey(), validateMap);
							}

						}
					} else if (TranslatorConstants.SRCAPPID.equals(map.getKey())) {
						if (instMap == null) {
							setValidation(map.getKey(), validateMap);
						}
					} else if (instMap != null && instMap.containsKey(TranslatorConstants.APIKEY)
							&& !requestBody.containsKey(TranslatorConstants.APIKEY)) {
						setValidation(TranslatorConstants.APIKEY, validateMap);

					}
				}

			}

		});

		if (!ResponseMessages.SUCCESS.equals(validateMap.get(TranslatorConstants.RESPONSE_CODE))) {
			throw new ServiceException("", validateMap.get(TranslatorConstants.RESPONSE_CODE) + "");
		} else {
			requestBody.put(TranslatorConstants.RESPONSE_CODE, ResponseMessages.SUCCESS);

		}

	}

	private void setValidation(String stringKey, Map<String, Object> validatmap) {

		if (TranslatorConstants.CONTENT_TYPE.equals(stringKey))
			validatmap.put(TranslatorConstants.RESPONSE_CODE, ResponseMessages.INAVALID_INVALID_REQUEST_PARSE);
		if (TranslatorConstants.ACCEPT.equals(stringKey))
			validatmap.put(TranslatorConstants.RESPONSE_CODE, ResponseMessages.INAVALID_INVALID_REQUEST_PARSE);

		if (TranslatorConstants.SRCAPPID.equals(stringKey))
			validatmap.put(TranslatorConstants.RESPONSE_CODE, ResponseMessages.INAVALID_HEADER_SRCAPPID);

		if (TranslatorConstants.IPADDRESS.equals(stringKey))
			validatmap.put(TranslatorConstants.RESPONSE_CODE, ResponseMessages.INAVALID_HEADER_IPADDRESS);

		if (TranslatorConstants.CORRELATIONID.equals(stringKey))
			validatmap.put(TranslatorConstants.RESPONSE_CODE, ResponseMessages.INAVALID_HEADER_CORELATION);

		if (TranslatorConstants.SOURCEMESSAGETYPE.equals(stringKey))
			validatmap.put(TranslatorConstants.RESPONSE_CODE, ResponseMessages.INAVALID_SOURCE_MSGTYPE);

		if (TranslatorConstants.APIKEY.equals(stringKey))
			validatmap.put(TranslatorConstants.RESPONSE_CODE, ResponseMessages.INAVALID_INVALID_API_KEY);

		if (TranslatorConstants.REQUESTDATA.equals(stringKey))
			validatmap.put(TranslatorConstants.RESPONSE_CODE, ResponseMessages.INAVALID_INVALID_REQUEST_PARSE);

	}

}
