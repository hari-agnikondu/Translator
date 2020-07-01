package com.fss.translator.config;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Configuration;

/**
 * This class used for declaration of point cuts and crosscut
 * 
 * @author ravinaganaboyina
 *
 */

@Aspect
@Configuration
public class TranslatorAspectConfig {
	@Pointcut("execution(public void doTranslateProcess(..))")
	public void doDecrypt() {
		//used for decryption
	}

	@Pointcut("within(com.fss.translator.service..*)")
	public void loggingTime() {
		//it is logging the response time
	}

	@Pointcut("execution(public void targetConverstion(..))")
	public void doEncrptpt() {
		//encrypt the message
	}

	@Pointcut("within(com.fss.translator.resource..*)")
	public void loadCheck() {
		// verifying the resource load
	}
}
