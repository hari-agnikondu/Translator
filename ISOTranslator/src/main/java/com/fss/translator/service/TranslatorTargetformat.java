package com.fss.translator.service;

import com.fss.translator.dto.ValueDTO;
import com.fss.translator.exception.ServiceException;
import com.fss.translator.util.Response;

/**
 * interface is define the response format
 * 
 * @author ravinaganaboyina
 * 
 *
 */

public interface TranslatorTargetformat {

	void doTranslateProcess(ValueDTO valyueDto) throws ServiceException;

	void targetConverstion(ValueDTO valueDto) throws ServiceException;

	Response contentTypeResponse(ValueDTO valueDto) throws ServiceException;

}
