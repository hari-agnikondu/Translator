
package com.fss.translator.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.fss.translator.exception.ServiceException;
import com.fss.translator.service.TranslatorService;
import com.fss.translator.util.Request;
import com.fss.translator.util.Response;
import com.fss.translator.util.Util;

import io.swagger.annotations.Api;
import lombok.extern.log4j.Log4j2;

/**
 * Translator Controller provides all the REST operations pertaining to Translate
 * the message.
 * 
 * @author Harikrishna Agnikondu
 *
 */
@Log4j2
@RestController
@Api(value = "Translator Services")
public class TranslatorController {

	@Autowired
	TranslatorService translatorService;

	@PostMapping(value = "/translateData")
	public ResponseEntity<Response> translateData(@RequestBody Request requestBody,
			@RequestHeader Map<String, String> headers) throws ServiceException {
		
		Long startTime = Util.getCurrentTime();
		log.info("Request is {}",requestBody);
		ResponseEntity<Response> response = translatorService.requestProcessTranslate(requestBody.getRequestBody(),
				headers);
		
		log.info("Proceessing Time " + (Util.getCurrentTime() - startTime));
		return response;
	}

	@GetMapping(value = "/ping/{pingRequest}")
	public String transactToY(@PathVariable String pingRequest) {
	
		Long startTime = Util.getCurrentTime();
		log.info("Test url process:" + pingRequest);
		Long endTime = Util.getCurrentTime();
		log.info("Proceess Time " + (endTime - startTime));
		
		return pingRequest;
	}

}
