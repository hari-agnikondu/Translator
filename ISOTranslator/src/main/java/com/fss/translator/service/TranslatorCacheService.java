package com.fss.translator.service;

import java.util.Map;

/**
 * This class is used for cache
 * 
 * @author ravinaganaboyina
 * 
 */

public interface TranslatorCacheService {

	Map<String, Map<String, Object>> getInstitutionData(Map<String, Map<String, Object>> institutionMap);

	Map<String, Map<String, Object>> getISO22Data(Map<String, Map<String, Object>> institutionMap);

	Map<String, Map<String, Object>> getRequestFields(Map<String, Map<String, Object>> institutionMap);

}
