package com.fss.translator.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fss.translator.constants.ResponseMessages;
import com.fss.translator.constants.TranslatorConstants;
import com.fss.translator.dto.ValueDTO;
import com.fss.translator.exception.ServiceException;
import com.fss.translator.resource.TranslatorResources;
import com.fss.translator.service.TranslatorService;
import com.fss.translator.service.TranslatorTargetformat;
import com.fss.translator.util.Response;
import com.fss.translator.util.Util;
import com.fss.translator.validator.TranslatorValidator;

import lombok.extern.log4j.Log4j2;

/**
 * Class is used for identification mime type and translation request based on
 * format.
 * 
 * @author ravinaganaboyina
 *
 */
@Log4j2
@Service
public class TranslatorServiceImpl implements TranslatorService {

	@Autowired
	TranslatorTargetformat translatorTargetformat;

	@Autowired
	TranslatorValidator translatorValidator;

	@Autowired
	TranslatorResources translatorResources;

	@Override
	public ResponseEntity<Response> requestProcessTranslate(String requetBody, Map<String, String> headers)
			throws ServiceException {

		log.debug("Starting the Translation process");
		ValueDTO valueDto = null;
		ResponseEntity<Response> responseEntity = null;
		try {

			valueDto = new ValueDTO();
			doDefaultPopulate(valueDto, headers, requetBody);
			translatorValidator.validateRequest(valueDto);
			translatorTargetformat.doTranslateProcess(valueDto);
			translatorTargetformat.targetConverstion(valueDto);

		} catch (ServiceException ex) {
			log.error("Process got failed due to-ServiceException :", ex.getMessage(), ex);

			Map<String, Object> responObj = valueDto.getRequestObject();
			if (Util.isNotNull(responObj) && !responObj.isEmpty())
				valueDto.getRequestObject().put(TranslatorConstants.RESPONSE_CODE, ex.getCode());

		} catch (Exception e) {
			log.info("Process got failed due to -Exception: {} {}",e.getMessage(), e);
			if (valueDto != null) {
				Map<String, Object> responObj = valueDto.getRequestObject();
				if (Util.isNotNull(responObj) && !responObj.isEmpty())
					valueDto.getRequestObject().put(TranslatorConstants.RESPONSE_CODE,
							ResponseMessages.GENERIC_ERR_MESSAGE);
			}

		} finally {
			Response response = translatorTargetformat.contentTypeResponse(valueDto);
			responseEntity = new ResponseEntity<>(response, HttpStatus.OK);
		}

		return responseEntity;
	}

	private void doDefaultPopulate(ValueDTO valuedto, Map<String, String> header, String requetBody)
			throws ServiceException {

		log.debug("Populate the default values");
		Map<String, Object> requestObject = new HashMap<>();
		Map<String, Object> requestBody = null;
		valuedto.setRequestObject(requestObject);

		if (Util.isNotNull(valuedto) && Util.isNotNull(valuedto.getRequestObject())) {
			valuedto.getRequestObject().put(TranslatorConstants.CONTENT_TYPE,
					header.get(TranslatorConstants.CONTENT_TYPE) + "");
			valuedto.getRequestObject().put(TranslatorConstants.ACCEPT, header.get(TranslatorConstants.ACCEPT) + "");
			valuedto.getRequestObject().put(TranslatorConstants.SRCAPPID,
					header.get(TranslatorConstants.SRCAPPID) + "");
			valuedto.getRequestObject().put(TranslatorConstants.IPADDRESS,
					header.get(TranslatorConstants.IPADDRESS) + "");
			valuedto.getRequestObject().put(TranslatorConstants.CORRELATIONID,
					header.get(TranslatorConstants.CORRELATIONID) + "");
			valuedto.getRequestObject().put(TranslatorConstants.RRN,
					header.get(TranslatorConstants.CORRELATIONID) + "");
			ThreadContext.put("RRN", header.get(TranslatorConstants.CORRELATIONID) + "");
		}

		if (!Util.isEmpty(requetBody)) {
			requestBody = Util.convertJsonToHashMap(requetBody);
		}
		valuedto.getRequestObject().putAll(requestBody);
		if (Util.isNotNull(valuedto) && !valuedto.getRequestObject().isEmpty()) {
			String instid = valuedto.getRequestObject().get(TranslatorConstants.SRCAPPID) + "";
			Map<String, String> instConfig = translatorResources.getInstitutionData(instid);
			valuedto.setInstitution(instConfig);
		}

	}

}
