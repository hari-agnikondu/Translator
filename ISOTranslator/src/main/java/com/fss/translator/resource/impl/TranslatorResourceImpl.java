package com.fss.translator.resource.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fss.translator.constants.TranslatorConstants;
import com.fss.translator.exception.ServiceException;
import com.fss.translator.resource.TranslatorResources;
import com.fss.translator.service.impl.TranslatorCacheServiceImpl;
import com.fss.translator.util.Util;
import com.fss.translator.xsd.XSDElement;
import com.fss.translator.xsd.XSDParser;

import lombok.extern.log4j.Log4j2;

/**
 * This class handle all properties file
 * 
 * @author ravinaganaboyina
 *
 */
@Log4j2
@Service
public class TranslatorResourceImpl implements TranslatorResources {

	@Value("${translator.config.path}")
	String translatorPath;

	@Autowired
	TranslatorCacheServiceImpl translatorCacheServiceImpl;

	StringBuilder st = new StringBuilder();

	@Override
	public Map<String, String> getRequestConfigElements() throws ServiceException {
		return Util.getPropertyMap(translatorPath, TranslatorConstants.REQUEST_VALIDATION_FILE);

	}


	@Override
	public Map<String, Map<String, String>> getXSDMap() throws ServiceException {

		Map<String, Map<String, String>> xsdMap = new HashMap<>();
		Map<String, String> innerMap = Util.getPropertyMap(translatorPath, TranslatorConstants.REQUEST_ISO22_MAP_FILE);
		for (Map.Entry<String, String> map : innerMap.entrySet()) {
			xsdMap.put(Util.getXSDKey(map.getKey()), xsdtoMap(Util.getXSDFileName(map.getKey()), map.getValue()));
		}
		log.info("Getting the xsd map {}",xsdMap);
		return xsdMap;
	}

	public Map<String, String> xsdtoMap(String fileName, String root) throws ServiceException {

		String file = fileName;
		Map<String, String> map = null;
		try {
			log.debug("Converting xsd to map");
			if (!Util.isEmpty(fileName)) {
				file = Util.getXSDpath(translatorPath, fileName);
			}
			XSDElement mainElement = XSDParser.parseXSD(file, root);
			StringBuilder str = new StringBuilder();
			map = new TreeMap<>();
			printData(mainElement, 0, map, str);
			map.put(root, root);
			st = new StringBuilder();
		} catch (Exception e) {
			log.error("xsdtoMap error: {}", e);
		}

		return map;
	}

	private void printData(XSDElement xsdElement, int level, Map<String, String> map, StringBuilder str) {

		String subName = "";

		if (str.length() <= 0)
			st.append(xsdElement.getName());
		map.put(st.append(str).toString(), xsdElement.getType());

		if (!xsdElement.getChildren().isEmpty() && xsdElement.getChildren().size() > 0) {
			for (XSDElement child : xsdElement.getChildren()) {
				subName = child.getName();
				printData(child, level + 2, map, new StringBuilder("." + subName));
			}
		}
		int lastIndex = st.lastIndexOf(".");
		if (lastIndex != -1)
			st.delete(lastIndex, st.length());

	}

	@Override
	public Map<String, String> getInstitutionData(String id) throws ServiceException {

		log.debug("Getting the required institution data");
		Map<String, String> instMap = null;
		Map<String, Map<String, String>> detailsMap = translatorCacheServiceImpl.getInstitutionData(null);
		if (Util.isNotNull(detailsMap) && !detailsMap.isEmpty()) {
			instMap = detailsMap.get(TranslatorConstants.INST_PREFIX + id);
			log.info("Required institution is {}",instMap);
			if (Util.isNotNull(instMap) && CollectionUtils.isEmpty(instMap)) {
				String fileName = Util.getPropertInstFileName(id);
				instMap = Util.getPropertyMap(translatorPath, fileName);
				detailsMap.put(Util.getPropertKey(fileName), instMap);
				translatorCacheServiceImpl.setInstitutionData(detailsMap);
			}
		}

		return instMap;
	}

	@Override
	public void setInstitutionData(String id) throws ServiceException {

		log.info("Updating the institution data in local cache {}",id);
		Map<String, Map<String, String>> detailsMap = translatorCacheServiceImpl.getInstitutionData(null);
		if (Util.isNotNull(detailsMap) && !detailsMap.isEmpty()) {
			String fileName = Util.getPropertInstFileName(id);
			Map<String, String> instMap = Util.getPropertyMap(translatorPath, fileName);
			detailsMap.put(Util.getPropertKey(fileName), instMap);
			translatorCacheServiceImpl.setInstitutionData(detailsMap);
		}

	}

	@Override
	public Map<String, String> getRequestFields() throws ServiceException {

		Map<String, String> requestFieldMap = translatorCacheServiceImpl.getRequestFields(null);
		if (Util.isNotNull(requestFieldMap) && !requestFieldMap.isEmpty()) {
			requestFieldMap = getRequestConfigElements();
			translatorCacheServiceImpl.setRequestFields(requestFieldMap);
		}

		return requestFieldMap;
	}

	@Override
	public void setRequestFields() throws ServiceException {

		translatorCacheServiceImpl.setRequestFields(getRequestConfigElements());

	}

	public void setISO20022XSDmapping(String filename) throws ServiceException {

		Map<String, Map<String, String>> iso2022XSDmap = translatorCacheServiceImpl.getISO20022XSDmapping(null);
		if (Util.isNotNull(iso2022XSDmap) && !iso2022XSDmap.isEmpty()) {
			Map<String, String> innerMap = Util.getPropertyMap(translatorPath,
					TranslatorConstants.REQUEST_ISO22_MAP_FILE);
			String root = innerMap.get(filename);
			iso2022XSDmap.put(filename, xsdtoMap(Util.getXSDFileName(filename), root));
			translatorCacheServiceImpl.setISO20022XSDmapping(iso2022XSDmap);

		}

	}

	public Map<String, String> getISO20022XSDmapping(String name) throws ServiceException {

		Map<String, String> painMap = null;
		Map<String, Map<String, String>> iso2022XSDmap = translatorCacheServiceImpl.getISO20022XSDmapping(null);
		if (Util.isNotNull(iso2022XSDmap) && !iso2022XSDmap.isEmpty()) {
			painMap = iso2022XSDmap.get(name);
			if (!Util.isNotNull(painMap)) {
				Map<String, String> innerMap = Util.getPropertyMap(translatorPath,
						TranslatorConstants.REQUEST_ISO22_MAP_FILE);
				if (Util.isNotNull(innerMap) && !innerMap.isEmpty()) {
					String root = innerMap.get(name);
					iso2022XSDmap.put(name, xsdtoMap(Util.getXSDFileName(name), root));
				}
			}
		}

		return painMap;
	}

	public void setISO20022propertymapping(String name) throws ServiceException {

		Map<String, String> isoPropMap = null;
		Map<String, Map<String, String>> detailsMap = translatorCacheServiceImpl.getISO20022propertymapping(null);
		if (Util.isNotNull(detailsMap) && !detailsMap.isEmpty()) {
			String fileName = Util.getPropertFileName(name);
			isoPropMap = Util.getPropertyMap(translatorPath, fileName);
			detailsMap.put(name, isoPropMap);
			translatorCacheServiceImpl.setISO20022propertymapping(detailsMap);
		}

	}

	public Map<String, String> getISO20022propertymapping(String name) throws ServiceException {

		Map<String, String> isoPropMap = null;
		Map<String, Map<String, String>> detailsMap = translatorCacheServiceImpl.getISO20022propertymapping(null);
		if (Util.isNotNull(detailsMap) && !detailsMap.isEmpty()) {
			isoPropMap = detailsMap.get(name);
			if (!Util.isNotNull(isoPropMap)) {
				String fileName = Util.getPropertFileName(name);
				isoPropMap = Util.getPropertyMap(translatorPath, fileName);
				detailsMap.put(name, isoPropMap);
				translatorCacheServiceImpl.setInstitutionData(detailsMap);
			}
		}
		Map<String, String> treeMap = new TreeMap<>();
		treeMap.putAll(isoPropMap);

		return treeMap;
	}

	@Override
	public String getXSDKeys(String name, String root) throws ServiceException {

		Map<String, String> mapingkeys = xsdtoMap(Util.getXSDFileName(name), root);
		String mapString = "";
		if (Util.isNotNull(mapingkeys)) {
			mapString = mapingkeys.entrySet().parallelStream().map(map1 -> map1.getKey())
					.collect(Collectors.joining("\r\n"));
		}
		return mapString;
	}

}
