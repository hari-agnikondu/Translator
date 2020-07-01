package com.fss.translator.controller;

import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fss.translator.constants.TranslatorConstants;
import com.fss.translator.exception.ServiceException;
import com.fss.translator.resource.TranslatorResources;
import com.fss.translator.util.Response;
import com.fss.translator.util.Util;

import io.swagger.annotations.Api;
import lombok.extern.log4j.Log4j2;

/**
 * Class is used for update to cache
 * 
 * @author ravinaganaboyina
 *
 */

@Log4j2
@RestController
@Api(value = "Translator Data update")
@RequestMapping(value = "/translateData")
public class TranslatorDataUpdateController {

	@Autowired
	TranslatorResources translatorResources;

	@PutMapping(value = "/updateInst/{institution}")
	public ResponseEntity<Response> updateInstitutionDetails(@Required @PathVariable("institution") String institution)
			throws ServiceException {
		
		log.info("Updathe the institution {}",institution);
		if (!Util.isEmpty(institution)) {
			translatorResources.setInstitutionData(institution);
		}
		
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PutMapping(value = "/updateReqfield")
	public ResponseEntity<Response> updateReqfield() throws ServiceException {
		
		log.debug("updating the request fields");
		translatorResources.setRequestFields();
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PutMapping(value = "/updateMapClasses/{name}")
	public ResponseEntity<Response> updateXSDmappingCls(@Required @PathVariable("name") String filename)
			throws ServiceException {
	
		log.info("Updathe the mapping class : {}",filename );
		translatorResources.setISO20022XSDmapping(filename);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PutMapping(value = "/updateMapProperty/{name}")
	public ResponseEntity<Response> updateMapProperty(@Required @PathVariable("name") String filename)
			throws ServiceException {
		
		log.info("Updating the property file {}",filename);
		translatorResources.setISO20022propertymapping(filename);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping(value = "/download/{root}/{name}")
	public ResponseEntity<Object> downLoadXSDKeys(@Required @PathVariable(name = "root") String root,
			@Required @PathVariable(name = "name") String filename) throws ServiceException {

		log.info("loading xsd filename :{} and root key :{}",filename,root);
		String keys = "";
		if (!Util.isEmpty(filename) && !Util.isEmpty(root))
			keys = translatorResources.getXSDKeys(filename, root);
		byte[] isr = keys.getBytes();
		HttpHeaders respHeaders = new HttpHeaders();
		respHeaders.setContentLength(isr.length);
		respHeaders.setContentType(MediaType.parseMediaType(TranslatorConstants.DOWNLOAD_TYPE));
		respHeaders.setCacheControl("must-revalidate, post-check=0, pre-check=0");
		respHeaders.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename + ".txt");
		return new ResponseEntity<>(isr, respHeaders, HttpStatus.OK);

	}
}
