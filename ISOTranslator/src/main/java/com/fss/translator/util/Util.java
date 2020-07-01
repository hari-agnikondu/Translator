package com.fss.translator.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.TreeMap;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fss.translator.constants.ResponseMessages;
import com.fss.translator.constants.TranslatorConstants;
import com.fss.translator.exception.ServiceException;

import lombok.extern.log4j.Log4j2;

/**
 * Util class provides all utility methods which can be reused across the
 * service.
 * 
 * @author Harikrishna Agnikondu
 *
 */

@Log4j2
public class Util {

	/**
	 * Util class should not be instantiated.
	 */
	private Util() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Checks whether an input String is empty or not. Returns true is input string
	 * is null or has 0 length.
	 * 
	 * @param input The input string to be checked.
	 * 
	 * @return boolean indicating whether input string is empty or not.
	 */
	public static boolean isEmpty(String input) {
		return (input == null || input.trim().isEmpty() || input.equalsIgnoreCase("null"));
	}

	/**
	 * 
	 * Converting a Map to JSON String
	 * 
	 * @throws JsonProcessingException
	 * 
	 * @throws Exception
	 */
	public static String mapToJson(Map<String, Object> productAttributes) throws JsonProcessingException {

		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.writeValueAsString(productAttributes);

	}

	public static Map<String, Object> convertJsonToHashMap(String attributesJsonString) {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> productAttributes = null;

		if (!Util.isEmpty(attributesJsonString)) {
			try {
				// mapper.configure(Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
				productAttributes = mapper.readValue(attributesJsonString, new TypeReference<Map<String, Object>>() {
				});

			} catch (IOException e) {
				log.error(e.getMessage());
			}
		}

		return productAttributes;
	}

	/**
	 * This method will check object has null or not
	 * 
	 * @param obj
	 * @return
	 * 
	 * @Author Hari need to verify
	 */

	public static boolean isNotNull(Object obj) {

		Optional<?> optObj = Optional.ofNullable(obj);

		return optObj.isPresent();

	}

	/**
	 * 
	 * @param map
	 * @return
	 */
	public static Map<String, String> getTreeMap(Map<String, String> map) {

		return map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
				(oldValue, newValue) -> newValue, TreeMap::new));

	}

	/**
	 * 
	 * @param name
	 * @return
	 */

	public static String getPropertInstFileName(String name) {
		return TranslatorConstants.INST_PREFIX + name + TranslatorConstants.PEROPERTY_SUFFIX;
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public static String getPropertFileName(String name) {
		return name + TranslatorConstants.PEROPERTY_SUFFIX;
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public static String getPropertKey(String name) {
		return name.replace(TranslatorConstants.PEROPERTY_SUFFIX, "");
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public static String getXSDKey(String name) {
		return name.replace(TranslatorConstants.XSD_SUFFIX, "");
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public static String getXSDFileName(String name) {

		return name + TranslatorConstants.XSD_SUFFIX;
	}

	public static String doEnCode(String inputData) {
		byte[] encodeString = Base64.getEncoder().encode(inputData.getBytes());

		return new String(encodeString);

	}

	/**
	 * 
	 * @param inputData
	 * @return
	 */
	public static String doDeCode(String inputData) {

		return new String(Base64.getDecoder().decode(inputData.getBytes()));
	}

	public static Map<String, Object> stringToMap(String input) throws ServiceException {
		Map<String, Object> map = new HashMap<>();

		String[] nameValuePairs = input.split("\\,");
		for (String nameValuePair : nameValuePairs) {
			String[] nameValue = nameValuePair.split("=");
			try {
				map.put(URLDecoder.decode(nameValue[0], TranslatorConstants.UTF_8),
						nameValue.length > 1 ? URLDecoder.decode(nameValue[1], TranslatorConstants.UTF_8) : "");
			} catch (UnsupportedEncodingException e) {
				throw new ServiceException("This method requires UTF-8 encoding support", e.getMessage());
			}
		}

		return map;
	}

	public static boolean isNot(String fileName) {
		boolean flag = false;
		if (!TranslatorConstants.REQUEST_VALIDATION_FILE.equalsIgnoreCase(fileName)
				&& !TranslatorConstants.REQUEST_ISO22_MAP_FILE.endsWith(fileName))
			flag = true;
		return flag;
	}

	public static Properties getProperty(String fileName) throws ServiceException {
		log.debug(TranslatorConstants.ENTER);
		Properties prop = null;

		try {
			prop = new Properties();
			if (new File(fileName).exists()) {
				InputStream inputStream = new FileInputStream(fileName);
				prop.load(inputStream);
			}
		} catch (Exception e) {
			throw new ServiceException("Exception ", e.getMessage());
		}
		log.debug(TranslatorConstants.EXIT);

		return prop;
	}

	public static long getCurrentTime() {
		return System.currentTimeMillis();
	}

	public static String getPropertypath(String translatorPath, String fileName) {
		return translatorPath + TranslatorConstants.PROPERTIES_PATH + File.separator + fileName;
	}

	public static String getXSDpath(String translatorPath, String fileName) {
		return translatorPath + TranslatorConstants.XSD_PATH + File.separator + fileName;
	}

	public static Map<String, Map<String, String>> getFileMap(String translatorPath, boolean isRequire) {

		Map<String, Map<String, String>> setMap = new HashMap<>();
		File folder = new File(translatorPath + TranslatorConstants.PROPERTIES_PATH + File.separator);
		File[] listOfFiles = folder.listFiles();

		Arrays.stream(listOfFiles).forEach(file -> {
			String fileName = file.getName();
			if (((!isRequire && fileName.startsWith(TranslatorConstants.INST_PREFIX))
					|| (isRequire && !fileName.startsWith(TranslatorConstants.INST_PREFIX) && Util.isNot(fileName)))
					&& fileName.endsWith(TranslatorConstants.PEROPERTY_SUFFIX)) {

				try {
					setMap.put(Util.getPropertKey(fileName), getPropertyMap(translatorPath, fileName));
				} catch (ServiceException e) {
					log.error("Error occured", e);
				}

			}
		});

		return setMap;
	}

	public static Map<String, String> getPropertyMap(String translatorPath, String fileName) throws ServiceException {

		String file = fileName;
		if (!Util.isEmpty(fileName)) {
			file = Util.getPropertypath(translatorPath, fileName);
		}

		Properties prop = Util.getProperty(file);
		if (!Util.isNotNull(prop)) {
			log.info("Property file is null", ResponseMessages.GENERIC_ERR_MESSAGE);
			// throw new ServiceException("", ResponseMessages.GENERIC_ERR_MESSAGE);
		}

		return prop.entrySet().stream()
				.collect(Collectors.toMap(e -> e.getKey().toString(), e -> e.getValue().toString()));
	}

}
