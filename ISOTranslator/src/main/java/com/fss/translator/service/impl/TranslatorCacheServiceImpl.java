package com.fss.translator.service.impl;

import java.util.Map;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.fss.translator.config.CacheConfiguration;

import lombok.extern.log4j.Log4j2;

/**
 * Class is used for caching the objects
 * 
 * @author ravinaganaboyina
 *
 */
@Log4j2
@Service
@CacheConfig(cacheManager = "cacheManager")
public class TranslatorCacheServiceImpl {

	public static final String TRANSALATOR_CACHE_INSTITUTIONS = "translator_institutions";

	public static final String TRANSALATOR_CACHE_REQUESTFIEDS = "translator_requestfieds";

	public static final String TRANSALATOR_CACHE_ISO8583MAPPING = "iso8583_mapping";

	public static final String TRANSALATOR_CACHE_ISO20022PROPERTYMAPPING = "iso20022property_mapping";

	public static final String TRANSALATOR_CACHE_ISO20022XSDMAPPING = "iso20022XSD_mapping";

	@Cacheable(cacheNames = CacheConfiguration.TRANSLATOR_DATA_CACHE, key = "#root.target.TRANSALATOR_CACHE_REQUESTFIEDS")
	public Map<String, String> getRequestFields(Map<String, String> requestFields) {
		log.info("Adding TRANSALATOR_CACHE_REQUESTFIELDS to LOCAL Cache: ", requestFields);

		return requestFields;
	}

	@CachePut(cacheNames = CacheConfiguration.TRANSLATOR_DATA_CACHE, key = "#root.target.TRANSALATOR_CACHE_REQUESTFIEDS")
	public Map<String, String> setRequestFields(Map<String, String> requestFields) {
		log.info("Adding TRANSALATOR_CACHE_REQUESTFIELDS to LOCAL Cache: ", requestFields);

		return requestFields;
	}

	@Cacheable(cacheNames = CacheConfiguration.TRANSLATOR_DATA_CACHE, key = "#root.target.TRANSALATOR_CACHE_INSTITUTIONS")
	public Map<String, Map<String, String>> getInstitutionData(Map<String, Map<String, String>> institutionMap) {
		log.info("getting INSTITUTIONS data into LOCAL Cache: ", institutionMap);

		return institutionMap;
	}

	@CachePut(cacheNames = CacheConfiguration.TRANSLATOR_DATA_CACHE, key = "#root.target.TRANSALATOR_CACHE_INSTITUTIONS")
	public Map<String, Map<String, String>> setInstitutionData(Map<String, Map<String, String>> institutionMap) {
		log.info("Adding INSTITUTIONS DATA to LOCAL Cache: ", institutionMap);

		return institutionMap;
	}

	@CachePut(cacheNames = CacheConfiguration.TRANSLATOR_DATA_CACHE, key = "#root.target.TRANSALATOR_CACHE_ISO20022XSDMAPPING")
	public Map<String, Map<String, String>> setISO20022XSDmapping(Map<String, Map<String, String>> iso22Mapping) {

		log.info("Adding TRANSALATOR_CACHE_ISO22MAPPING to LOCAL Cache: ", iso22Mapping);

		return iso22Mapping;
	}

	@Cacheable(cacheNames = CacheConfiguration.TRANSLATOR_DATA_CACHE, key = "#root.target.TRANSALATOR_CACHE_ISO20022XSDMAPPING")
	public Map<String, Map<String, String>> getISO20022XSDmapping(Map<String, Map<String, String>> iso22Mapping) {

		log.info("getting TRANSALATOR_CACHE_ISO22MAPPING into LOCAL Cache: ", iso22Mapping);

		return iso22Mapping;
	}

	@CachePut(cacheNames = CacheConfiguration.TRANSLATOR_DATA_CACHE, key = "#root.target.TRANSALATOR_CACHE_ISO20022PROPERTYMAPPING")
	public Map<String, Map<String, String>> setISO20022propertymapping(Map<String, Map<String, String>> iso22Mapping) {

		log.info("Adding ISO22PROPERTY MAPPING to LOCAL Cache: ", iso22Mapping);

		return iso22Mapping;
	}

	@Cacheable(cacheNames = CacheConfiguration.TRANSLATOR_DATA_CACHE, key = "#root.target.TRANSALATOR_CACHE_ISO20022PROPERTYMAPPING")
	public Map<String, Map<String, String>> getISO20022propertymapping(Map<String, Map<String, String>> iso22Mapping) {

		log.info("Getting ISO22PROPERTY MAPPING into LOCAL Cache: ", iso22Mapping);

		return iso22Mapping;
	}
}
