package com.fss.translator.intercept;

import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.annotation.Configuration;

import com.fss.translator.constants.ResponseMessages;
import com.fss.translator.constants.TranslatorConstants;
import com.fss.translator.dto.ValueDTO;
import com.fss.translator.exception.ServiceException;
import com.fss.translator.security.AES;
import com.fss.translator.util.Util;

import lombok.extern.log4j.Log4j2;

/**
 * This class execute advice for aspect
 * 
 * @author ravinaganaboyina
 *
 */
@Log4j2
@Aspect
@Configuration
public class TranslatorAspect {

	@Around(value = "com.fss.translator.config.TranslatorAspectConfig.loggingTime()")
	public Object logging(ProceedingJoinPoint joinpoint) throws ServiceException {
		
		Object object = null;
		try {
			long startTime = Util.getCurrentTime();
			object = joinpoint.proceed();
			long timeTaken =  Util.getCurrentTime() - startTime;
			log.debug("Time Taken by {} is {}", joinpoint, timeTaken);
		} catch (ServiceException se) {
			log.error("Exception occure on TranslatorAspect ", se);
			throw new ServiceException("",
					!Util.isEmpty(se.getCode()) ? se.getCode() : ResponseMessages.INAVALID_INVALID_REQUEST_PARSE);
		} catch (Throwable e) {
			log.error("Exception occure on TranslatorAspect ", e);
			throw new ServiceException(e.getMessage());
		}
		
		return object;
	}

	@Before(value = "com.fss.translator.config.TranslatorAspectConfig.doDecrypt() && args(value,..)")
	public void decrptionResponseData(JoinPoint joinpoint, ValueDTO value) throws ServiceException {
		log.debug(TranslatorConstants.ENTER);
		Map<String, String> instConfig = value.getInstitution();
		Map<String, Object> reqObject = value.getRequestObject();
		if (Util.isNotNull(instConfig) && !instConfig.isEmpty() && Util.isNotNull(reqObject) && !reqObject.isEmpty()) {
			String confgOpt = instConfig.get(TranslatorConstants.REQUESTDATAFORMATOPT);
			String requestData = reqObject.get(TranslatorConstants.REQUESTDATA) + "";
			String aeskey = instConfig.get(TranslatorConstants.AESKEY) + "";
			reqObject.put(TranslatorConstants.REQUESTDATA, getData(requestData, aeskey, confgOpt, true));

		}
		log.debug(TranslatorConstants.EXIT);

	}

	@AfterReturning(value = "com.fss.translator.config.TranslatorAspectConfig.doEncrptpt() && args(value,..)")
	public void encrptionResponseData(JoinPoint joinpoint, ValueDTO value) throws ServiceException {
		log.debug(TranslatorConstants.ENTER);
		Map<String, String> instConfig = value.getInstitution();
		Map<String, Object> reqObject = value.getRequestObject();

		if (Util.isNotNull(instConfig) && !instConfig.isEmpty() && Util.isNotNull(reqObject) && !reqObject.isEmpty()) {
			String confgOpt = instConfig.get(TranslatorConstants.REQUESTDATAFORMATOPT);
			String requestData = value.getResponseData();
			String aeskey = instConfig.get(TranslatorConstants.AESKEY) + "";
			value.setResponseData(getData(requestData, aeskey, confgOpt, false));

		}
		log.debug(TranslatorConstants.EXIT);

	}

	private String getData(String requestData, String apikey, String confgOpt, boolean isPlain)
			throws ServiceException {

		String data = "";
		log.debug(TranslatorConstants.ENTER);
		if (!Util.isEmpty(confgOpt) && TranslatorConstants.BASE64.equals(confgOpt)) {
			data = isPlain ? Util.doDeCode(requestData) : Util.doEnCode(requestData);

		} else if (!Util.isEmpty(confgOpt) && TranslatorConstants.AES.equals(confgOpt)) {
			data = isPlain ? AES.decrypt(requestData, apikey) : AES.encrypt(requestData, apikey);

		}
		log.debug(TranslatorConstants.EXIT);
		return data;

	}

}
