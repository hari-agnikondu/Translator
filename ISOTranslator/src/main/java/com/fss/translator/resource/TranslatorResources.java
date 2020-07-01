package com.fss.translator.resource;

import java.util.Map;

import com.fss.translator.exception.ServiceException;

/**
 * Class helps reading properties and get the map of object
 * 
 * @author ravinaganaboyina
 *
 */

public interface TranslatorResources {

	Map<String, String> getRequestConfigElements() throws ServiceException;

	Map<String, Map<String, String>> getXSDMap() throws ServiceException;

	Map<String, String> xsdtoMap(String file, String root) throws ServiceException;

	Map<String, String> getInstitutionData(String id) throws ServiceException;

	void setInstitutionData(String id) throws ServiceException;

	Map<String, String> getRequestFields() throws ServiceException;

	void setRequestFields() throws ServiceException;

	void setISO20022XSDmapping(String name) throws ServiceException;

	Map<String, String> getISO20022XSDmapping(String name) throws ServiceException;

	void setISO20022propertymapping(String name) throws ServiceException;

	Map<String, String> getISO20022propertymapping(String name) throws ServiceException;

	String getXSDKeys(String name, String root) throws ServiceException;

}
