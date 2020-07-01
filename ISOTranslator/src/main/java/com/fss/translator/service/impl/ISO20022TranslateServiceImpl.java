package com.fss.translator.service.impl;

import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fss.translator.constants.ResponseMessages;
import com.fss.translator.constants.TranslatorConstants;
import com.fss.translator.dto.ValueDTO;
import com.fss.translator.exception.ServiceException;
import com.fss.translator.resource.TranslatorResources;
import com.fss.translator.service.ISO20022TranslateService;
import com.fss.translator.util.Util;
import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class ISO20022TranslateServiceImpl implements ISO20022TranslateService {

	@Autowired
	TranslatorResources translatorResources;

	@Override
	public void doMarshalling(ValueDTO value) throws ServiceException {

		String rrn = value.getRequestObject().get(TranslatorConstants.RRN) + "";
		Map<String, Object> transferObject = value.getTransferObject();
		if (Util.isNotNull(transferObject) && !transferObject.isEmpty() && Util.isNotNull(value.getRequestObject()))
			transferObject.put(TranslatorConstants.RRN,
					!Util.isEmpty(rrn) ? value.getRequestObject().get(TranslatorConstants.RRN) : "");
		Map<String, String> instConfig = value.getInstitution();
		String sourceName = "";
		String mappingProperty = "";
		if (Util.isNotNull(instConfig) && !instConfig.isEmpty()) {
			sourceName = instConfig.get(TranslatorConstants.TARGETRESPONSEFORMATOPT);
			mappingProperty = instConfig.get(TranslatorConstants.MAPPINGPROPERTY);
		}
		log.info("Target name {} and Mapping property{}", sourceName, mappingProperty);
		Map<String, String> prop = translatorResources.getISO20022propertymapping(mappingProperty);
		Map<String, String> mappingClass = translatorResources.getISO20022XSDmapping(sourceName);
		if (Util.isNotNull(prop) && !prop.isEmpty() && Util.isNotNull(mappingClass) && !mappingClass.isEmpty()) {
			Long startTime = Util.getCurrentTime();
			Object obj = getObject(prop, mappingClass, transferObject, null, sourceName);
			getObject(prop, mappingClass, transferObject, obj, sourceName);
			processofISO20022Marshalling(value, obj, sourceName);

			log.info("Object completion Time " + (Util.getCurrentTime() - startTime));
		} else {
			log.info("Conguration was not done for sourceName:" + sourceName);
			throw new ServiceException("", ResponseMessages.INAVALID_INVALID_REQUEST_PARSE);
		}

	}

	private void processofISO20022Marshalling(ValueDTO valueDto, Object obj, String sourceName)
			throws ServiceException {
		try {

			Long startTime = System.currentTimeMillis();
			JAXBContext jc = JAXBContext.newInstance(getSource(sourceName, false));
			@SuppressWarnings("unchecked")
			JAXBElement<Object> jaxbElement = new JAXBElement<>(new QName("", "xs:" + obj.getClass().getSimpleName()),
					(Class<Object>) obj.getClass(), obj);
			Marshaller jaxbMarshaller = jc.createMarshaller();
			NamespacePrefixMapper mapper = new NamespacePrefixMapper() {
				@Override
				public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix) {
					return "xs";
				}
			};
			jaxbMarshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", mapper);
			StringWriter writer = new StringWriter();
			jaxbMarshaller.marshal(jaxbElement, writer);
			String formatedXmlData = writer.toString();
			log.info("XML" + formatedXmlData);
			valueDto.setResponseData(formatedXmlData);
			log.info("processofISO20022Marshalling Compplition Time " + (Util.getCurrentTime() - startTime));

		} catch (Exception e) {
			log.error("Error occured while Marshalling ISO20022", e.getMessage(), e);
			throw new ServiceException("", ResponseMessages.INAVALID_INVALID_REQUEST_PARSE);
		}

	}

	private String gettingValueBasedOnProp(String innerVal, Map<String, Object> reqValueObj) {

		String[] totalValues = innerVal.split("\\|");

		StringBuilder finalValue = new StringBuilder("");
		for (String k : totalValues) {
			if (k.contains(",") && k.contains("(")) {
				String val = k.substring(0, k.length() - 5);
				Object innerValue = reqValueObj.get(val);
				String val1 = innerValue.toString().substring(Character.getNumericValue(k.charAt(k.length() - 4)),
						Character.getNumericValue(k.charAt(k.length() - 2)));
				finalValue.append(val1);

			} else if (!k.contains(",") && k.contains("(")) {
				String val = k.substring(0, k.length() - 3);
				Object innerValue = reqValueObj.get(val);
				String val1 = innerValue.toString().substring(Character.getNumericValue(k.charAt(k.length() - 2)));
				finalValue.append(val1);
			} else if (innerVal.contains("%") && innerVal.startsWith("%")) {
				finalValue.append(innerVal.replace("%", ""));

			} else {
				finalValue.append(reqValueObj.get(k));
			}
		}

		return finalValue.toString();
	}

	private Object getObject(Map<String, String> map, Map<String, String> mapingKeys, Map<String, Object> reqValueObj,
			Object supperClass, String sourceName) {

		Object innerObject = supperClass;

		Object childObject = null;

		for (Entry<String, String> mapEntry : map.entrySet()) {
			innerObject = supperClass;
			String key = mapEntry.getKey();
			String value = mapEntry.getValue();
			String[] mapingValue = key.split("\\.");
			StringBuilder oldParent = new StringBuilder();
			if (!"{}".equals(value)) {
				for (int i = 0; i < mapingValue.length; i++) {
					Class<?> clasName = null;
					oldParent.append(mapingValue[i]);
					String mapClas = mapingKeys.get(oldParent.toString());

					if (mapClas != null && mapClas.length() > 0
							&& (supperClass == null || (mapingValue.length - 1 != i))) {// condition changed by Hari
						mapClas = getSource(sourceName, true) + mapClas;
						try {
							if (innerObject != null && !mapClas.endsWith(innerObject.getClass().getName())) {

								clasName = Class.forName(mapClas);
								Method m = innerObject.getClass().getDeclaredMethod("get" + mapingValue[i]);
								m.setAccessible(true);
								childObject = m.invoke(innerObject);
								innerObject = setObjectRef(mapingValue[i], innerObject, childObject, m, clasName, null);
							}
							if (supperClass == null) {
								clasName = Class.forName(mapClas);
								innerObject = clasName.newInstance();
								return innerObject;
							}

						} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
								| NoSuchMethodException | SecurityException | IllegalArgumentException
								| InvocationTargetException e) {
							log.error("Error while getting Object data:" + oldParent.toString() + " Error:"
									+ e.getMessage());
						}
					} else if (Util.isNotNull(innerObject) && mapingValue[mapingValue.length - 1].equals(mapingValue[i])
							&& innerObject != null) { // extra condition added by Hari

						try {

							Method m = innerObject.getClass().getDeclaredMethod("get" + mapingValue[i]);
							childObject = m.invoke(innerObject);
							if (value != null && value.indexOf('{') != -1) {
								String innerVal = value.substring(1, value.length() - 1);
								String fieldFinalVal = gettingValueBasedOnProp(innerVal, reqValueObj);
								fieldFinalVal = Util.isEmpty(fieldFinalVal) ? "" : fieldFinalVal;
								if (m.getReturnType().equals(String.class)) {
									setObjectRef(mapingValue[i], innerObject, childObject, m, clasName, fieldFinalVal);
									log.debug("Key:" + oldParent.toString() + ":Value:" + fieldFinalVal);
								} else {
									setfields(innerObject, mapingValue[i], fieldFinalVal);
									log.debug("Key:" + oldParent.toString() + ":Value:" + fieldFinalVal);
								}
							}
						} catch (SecurityException | IllegalAccessException | IllegalArgumentException
								| InvocationTargetException | NoSuchMethodException e) {
							log.error("Set Element data:" + oldParent.toString() + " Error:" + e.getMessage());
						}

					}
					oldParent.append(".");
				}
			}

		}

		return innerObject;
	}

	@SuppressWarnings("unchecked")
	private static Object setObjectRef(String clasKey, Object innerObject, Object childObject, Method m,
			Class<?> childName, String mapString) {

		Object child = childObject;
		try {
			if (childObject instanceof List) {

				List<Object> tempList = (List<Object>) childObject;
				if ((CollectionUtils.isEmpty(tempList))) {
					child = childName.newInstance();
					tempList.add(child);

					m.invoke(innerObject);
				} else {
					child = tempList.get(0);
				}
			} else if (Util.isNotNull(innerObject)) {

				if (!Util.isNotNull(childObject) && childName != null) {
					child = childName.newInstance();
				} else if (mapString != null) {
					child = mapString;
				}
				Method m1 = innerObject.getClass().getDeclaredMethod(TranslatorConstants.BINDING_CLASS_SET + clasKey,
						child.getClass());
				m1.setAccessible(true);
				m1.invoke(innerObject, child);
			}
		} catch (Exception e) {
			log.error("Error while getting Element data:" + e.getMessage(), e);
		}

		return child;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void setfields(Object innerObject, String clas, String value) {

		try {
			Class<?> bigdeci = BigDecimal.class;
			Class<?> xmlGregorianCal = XMLGregorianCalendar.class;
			Class<?> list = java.util.List.class;

			Field field = innerObject.getClass().getDeclaredField(doField(clas));

			log.debug("field  :" + field);

			field.setAccessible(true);

			if (field.getType().equals(bigdeci)) {
				field.set(innerObject, new BigDecimal(value));

			} else if (field.getType().equals(xmlGregorianCal)) {
				GregorianCalendar c = new GregorianCalendar();
				XMLGregorianCalendar dateXMLGreg = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
				field.set(innerObject, dateXMLGreg);

			} else if (field.getType().equals(list)) {
				List<String> myList = new ArrayList<>(Arrays.asList(value));
				log.debug("end object :{}", innerObject, " final value: {}", myList);
				field.set(innerObject, myList);

			} else if (field.getType().isEnum()) {
				field.set(innerObject, Enum.valueOf((Class<Enum>) field.getType(), value));

			} else {
				field.set(innerObject, value);
				log.debug("end object :{}", innerObject, " final value: {}", value);
			}
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException
				| DatatypeConfigurationException e) {
			log.error("Exception while setting values:" + e.getMessage(), e);
		}

	}

	private static String doField(String str) {
		if (!Util.isEmpty(str))
			return str.toLowerCase().charAt(0) + str.substring(1, str.length());
		return str;
	}

	private String getString(String str) {

		if (str.indexOf('{') != -1 && str.indexOf('}') != -1) {
			return str.replace("{", "").replace("}", "");
		}
		return str;
	}

	private String getSource(String name, boolean isClass) {
		String className = name.replace(".", "");
		if (isClass) {
			return TranslatorConstants.BINDING_CLASS_PAKAGE + className + ".";
		}
		return TranslatorConstants.BINDING_CLASS_PAKAGE + className;
	}

	// Unmarshalling process start
	@Override
	public void doUnmarshalling(ValueDTO value) throws ServiceException {

		String sourceName = "";
		String mappingProperty = "";
		Map<String, String> instConfig = value.getInstitution();
		if (Util.isNotNull(instConfig) && !instConfig.isEmpty()) {
			sourceName = instConfig.get(TranslatorConstants.SOURCEMESSAGETYPE);
			mappingProperty = instConfig.get(TranslatorConstants.MAPPINGPROPERTY);
		}
		Map<String, String> prop = translatorResources.getISO20022propertymapping(mappingProperty);
		Map<String, String> mappingClass = translatorResources.getISO20022XSDmapping(sourceName);
		if (Util.isNotNull(prop) && !prop.isEmpty() && Util.isNotNull(mappingClass) && !mappingClass.isEmpty()) {
			Long startTime = Util.getCurrentTime();
			processISO20022Unmarsharlling(value, prop, mappingClass, sourceName);

			log.info("Object completion Time " + (Util.getCurrentTime() - startTime));
		} else {
			log.error("Error occured while Unmarshalling");
			throw new ServiceException("", ResponseMessages.INAVALID_INVALID_REQUEST_PARSE);
		}

	}

	private void processISO20022Unmarsharlling(ValueDTO value, Map<String, String> prop,
			Map<String, String> mappingClass, String sourceName) throws ServiceException {

		log.debug("ISO20022 Unmarshalling");
		try {
			String xmlReq = value.getRequestObject().get(TranslatorConstants.REQUESTDATA) + "";
			StringReader reader = new StringReader(xmlReq);
			String mapClas = getSource(sourceName, true) + TranslatorConstants.ROOT_DOCUMENT;
			Class<?> clas = Class.forName(mapClas);
			JAXBContext jaxbContext = JAXBContext.newInstance(clas);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			JAXBElement<?> jaxbElement = jaxbUnmarshaller.unmarshal(new StreamSource(reader), clas);
			Object obj = jaxbElement.getValue();

			Map<String, Object> mapvalue = getMap(prop, mappingClass, obj, sourceName);
			value.setTransferObject(mapvalue);

		} catch (Exception e) {
			log.error("Error occured while process ISO20022 Unmarshalling", e.getMessage(), e);
			throw new ServiceException("", ResponseMessages.INAVALID_INVALID_REQUEST_PARSE);
		}

	}

	private Map<String, Object> getMap(Map<String, String> map, Map<String, String> mappingKeys, Object supperClass,
			String sourceName) {

		Map<String, Object> valueMap = new HashMap<>();
		Object childObject = null;
		for (Entry<String, String> mapEntry : map.entrySet()) {
			Object innerObject = supperClass;

			String value = mapEntry.getValue();
			if (!"{}".equals(value)) {
				if (!Util.isEmpty(value))
					value = value.substring(1, value.length() - 1);
				String[] mapingValue = value.split("\\.");
				StringBuilder oldParent = new StringBuilder();
				for (int i = 0; i < mapingValue.length; i++) {

					Class<?> clasName = null;

					oldParent.append(mapingValue[i]);

					String mapClas = mappingKeys.get(oldParent.toString());
					if (mapClas != null && mapClas.length() > 0
							&& (supperClass == null || (mapingValue.length - 1 != i))) {
						mapClas = getSource(sourceName, true) + mapClas;
						try {
							if (innerObject != null && !mapClas.endsWith(innerObject.getClass().getName())) {

								clasName = Class.forName(mapClas);
								Method m = innerObject.getClass().getDeclaredMethod("get" + mapingValue[i]);
								childObject = m.invoke(innerObject);
								if (childObject != null) {
									innerObject = getObjectRef(innerObject, childObject, m, clasName, null);
								}

							}

						} catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException
								| SecurityException | IllegalArgumentException | InvocationTargetException e) {
							log.error("getMap Element data:***********", mapingValue[i], ":", e.getMessage(), "Cause:",
									e.getCause());
						}
					} else if (Util.isNotNull(innerObject) && mapingValue[mapingValue.length - 1].equals(mapingValue[i])
							&& innerObject != null) {// condition added by Hari

						try {
							Method m = innerObject.getClass().getDeclaredMethod("get" + mapingValue[i]);
							childObject = m.invoke(innerObject);
							if (m.getReturnType().equals(String.class)) {
								valueMap.put(getString(value), childObject);
								log.info("getReturnType:***********" + getString(value) + ":" + childObject);

							} else {
								Object obj = getfieldsValue(innerObject, mapingValue[i], "");

								if (obj instanceof ArrayList)
									obj = ((ArrayList<?>) obj).stream().map(Object::toString)
											.collect(Collectors.joining(" "));

								valueMap.put(getString(value), obj);
								log.info("getfieldsValue:***********" + getString(value) + ":" + childObject);

							}

						} catch (SecurityException | IllegalAccessException | IllegalArgumentException
								| InvocationTargetException | NoSuchMethodException e) {
							log.error("Error getting Element data:" + e.getMessage());
						}
					}
					oldParent.append(".");
				}

			}
		}

		return valueMap;
	}

	private static Object getfieldsValue(Object innerObject, String clas, String value) {

		Object valueObj = null;
		try {

			Class<?> bigdeci = BigDecimal.class;
			Class<?> xmlGregorianCal = XMLGregorianCalendar.class;
			Class<?> list = java.util.List.class;

			Field field = innerObject.getClass().getDeclaredField(doField(clas));
			field.setAccessible(true);

			if (field.getType().equals(bigdeci) || field.getType().equals(xmlGregorianCal)
					|| field.getType().equals(list) || field.getType().isEnum()) {
				valueObj = field.get(innerObject);

			} else {
				field.set(innerObject, value);
				valueObj = field.get(innerObject);
			}
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			log.error("getfieldsValueElement data:" + e.getMessage(), e);
		}
		return valueObj;
	}

	private static Object getObjectRef(Object innerObject, Object childObject, Method m, Class<?> childName,
			String mapString) {

		Object child = null;
		try {
			if (childObject instanceof List) {

				@SuppressWarnings("unchecked")
				List<Object> tempList = (List<Object>) childObject;
				if ((CollectionUtils.isEmpty(tempList))) {
					child = childName.newInstance();
					tempList.add(child);

					m.invoke(innerObject);
				} else {
					child = tempList.get(0);
				}
			} else {
				if (childObject == null && childName != null) {
					child = childName.newInstance();
				} else if (mapString != null) {
					child = mapString;

				}
			}

		} catch (Exception e) {
			log.error("Error while getting object reference : " + e.getMessage(), e);
		}
		return child;
	}

}
