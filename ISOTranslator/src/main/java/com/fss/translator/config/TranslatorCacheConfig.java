package com.fss.translator.config;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Configuration;

import com.fss.translator.constants.TranslatorConstants;
import com.fss.translator.exception.ServiceException;
import com.fss.translator.resource.TranslatorResources;
import com.fss.translator.util.Util;

import lombok.extern.log4j.Log4j2;

/**
 * This class used for cache for each entities
 * 
 * @author ravinaganaboyina
 *
 */
@Log4j2
@Configuration
public class TranslatorCacheConfig {

	
	@Autowired
	CacheManager cacheManager;

	@Autowired
	TranslatorResources translatorResources;

	@Value("${translator.config.path}")
	String translatorPath;

	public static final String TRANSLATOR_DATA_CACHE = "translatorDataCache";

	public static final String TRANSALATOR_CACHE_INSTITUTIONS = "translator_institutions";

	public static final String TRANSALATOR_CACHE_REQUESTFIEDS = "translator_requestfieds";

	public static final String TRANSALATOR_CACHE_ISO20022PROPERTYMAPPING = "iso20022property_mapping";

	public static final String TRANSALATOR_CACHE_ISO20022XSDMAPPING = "iso20022XSD_mapping";

	@PostConstruct
	public void loadInstitutionsToLocalCache() {
		
		Map<String, Map<String, String>> institutionMap = Util.getFileMap(translatorPath,false);
		log.info("Institutions loading ito local cache:{}",institutionMap);
		cacheManager.getCache(TranslatorConstants.TRANSLATOR_DATA_CACHE).put(TRANSALATOR_CACHE_INSTITUTIONS,
				institutionMap);

	
	}

	@PostConstruct
	public void loadRequestFieldsToLocalCache() throws ServiceException {
		Map<String, String> requestFields =translatorResources.getRequestConfigElements();
		log.info("Request Fields loading to local cache: {}",requestFields);
		cacheManager.getCache(TranslatorConstants.TRANSLATOR_DATA_CACHE).put(TRANSALATOR_CACHE_REQUESTFIEDS,
				requestFields);

		
	}

	@PostConstruct
	public void loadISO200022ToLocalCache() {
		Map<String, Map<String, String>> isoProperties = Util.getFileMap(translatorPath,true);
		log.info("Loading Iso 20022 properties to local cache {}",isoProperties);
		cacheManager.getCache(TranslatorConstants.TRANSLATOR_DATA_CACHE).put(TRANSALATOR_CACHE_ISO20022PROPERTYMAPPING,
				isoProperties);
		
	}

	@PostConstruct
	public void loadISO20022XSDToLocalCache() throws ServiceException {
		
		Map<String, Map<String, String>> isoXsd = translatorResources.getXSDMap();
		log.info("Loading ISO20022 xsd file to local cache {}",isoXsd);
		cacheManager.getCache(TranslatorConstants.TRANSLATOR_DATA_CACHE).put(TRANSALATOR_CACHE_ISO20022XSDMAPPING,
				isoXsd);
	
	}

	
	

}