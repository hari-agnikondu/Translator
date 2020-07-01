package com.fss.translator.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fss.translator.constants.ResponseMessages;
import com.fss.translator.constants.TranslatorConstants;
import com.fss.translator.dto.ValueDTO;
import com.fss.translator.exception.ServiceException;
import com.fss.translator.resource.TranslatorResources;
import com.fss.translator.service.ISO8583TranslatorService;
import com.fss.translator.util.Util;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class ISO8583TranslatorServiceImpl implements ISO8583TranslatorService {

	@Autowired
	TranslatorResources translatorResources;

	@Value("${translator.config.path}")
	String translatorPath;

	@Override
	public void unpackService(ValueDTO dto) throws ServiceException {

		log.debug("Unpacking ISO 8583 message");
		Map<String, String> instmap = dto.getInstitution();
		String instKeyValue = instmap.get(TranslatorConstants.ISO8583PACKAGER_FILE) + "";
		try (InputStream is = new FileInputStream(getXMLPath(instKeyValue));) {
			String data = dto.getRequestObject().get(TranslatorConstants.REQUESTDATA) + "";
			data = getActualString(data, 12);
			GenericPackager packager = new GenericPackager(is);
			ISOMsg isoMsg = new ISOMsg();
			isoMsg.setPackager(packager);
			isoMsg.unpack(data.getBytes());
			Map<String, Object> mapIso = logISOMsg(isoMsg);
			dto.setTransferObject(mapIso);
			
		} catch (Exception e) {
			log.error("Exception while unpacking ISO message ", e);
			throw new ServiceException("", ResponseMessages.INAVALID_INVALID_REQUEST_PARSE);
		}
		
	}

	@Override
	public void packService(ValueDTO dto) throws ServiceException {
		
		log.debug("packing Iso 8583 message");
		Map<String, String> instmap = dto.getInstitution();
		String instKeyValue = instmap.get(TranslatorConstants.ISO8583PACKAGER_FILE) + "";
		String isheader = "";
		try (InputStream is = new FileInputStream(getXMLPath(instKeyValue));) {
			GenericPackager packager = new GenericPackager(is);
			ISOMsg isoMsg = new ISOMsg();
			isoMsg.setPackager(packager);
			Map<String, Object> transferObject = dto.getTransferObject();
			if (Util.isNotNull(transferObject) && Util.isNotNull(instmap)) {
				isheader = instmap.get(TranslatorConstants.ISOHEADER);
				String target = instmap.get(TranslatorConstants.TARGETRESPONSEFORMATOPT);
				Map<String, String> mapTarget = translatorResources.getISO20022propertymapping(target);
				String mappingProperty = instmap.get(TranslatorConstants.MAPPINGPROPERTY);
				Map<String, String> prop = translatorResources.getISO20022propertymapping(mappingProperty);
				getFormatedMap(transferObject, prop, mapTarget);
				isoMsg.setMTI(transferObject.get(TranslatorConstants.ZERO) + "");
				transferObject.entrySet().stream().forEach(map -> {
					String actValue = map.getValue() + "";
					String actKey = map.getKey();
					if (actKey.compareTo("0") > 0)
						try {
							isoMsg.set(actKey, !Util.isEmpty(actValue) ? actValue : "");
						} catch (ISOException e) {
							log.error("ISOException is: ", e.getMessage(),e);
						}

				});

			}
			String data = new String(isoMsg.pack());
			dto.setResponseData(isheader + data);

		} catch (Exception e) {
			log.error("Exception ", e.getMessage(),e);
			throw new ServiceException("", ResponseMessages.INAVALID_INVALID_REQUEST_PARSE);
		}
		
	}

	private Map<String, Object> logISOMsg(ISOMsg msg) {
		
		
		log.debug("----ISO MESSAGE-----");
		Map<String, Object> isoMap = new HashMap<>();
		for (int i = 0; i <= msg.getMaxField(); i++) {
			if (msg.hasField(i)) {
				log.debug("Field- {} : {}",i, msg.getString(i));
				Object object = msg.getString(i);
				isoMap.put(msg.getPackager().getFieldDescription(msg, i), object);
			}
		}
		log.debug("----ISO MESSAGE-----");
		
		return isoMap;
	}

	private String getActualString(String isoMsg, int position) {
		if (!Util.isEmpty(isoMsg) && isoMsg.length() > position)
			return isoMsg.substring(position);
		return isoMsg;
	}

	private void getFormatedMap(Map<String, Object> map, Map<String, String> prop, Map<String, String> isoMap) {
		
		Map<String, Object> actMap = new HashMap<>();
		for (Map.Entry<String, String> propMap : prop.entrySet()) {
			String key = propMap.getKey();
			String value = map.get(getString(propMap.getValue())) + "";
			String message = "";
			if (key.contains("|")) {
				String[] arr = key.split("\\|");
				for (int i = 0; i < arr.length; i++) {

					if (arr[i].contains("(") && arr[i].contains(")")) {
						String actvalue = arr[i].substring(arr[i].indexOf('(') + 1, arr[i].indexOf(')'));
						String[] arrValue = actvalue.split("\\,");
						if (arrValue.length > 1) {
							int start = Integer.parseInt(arrValue[0]);
							int end = Integer.parseInt(arrValue[1]);
							message = value.substring(start, start + end);

						} else {
							int start = Integer.parseInt(arrValue[0]);
							message = value.substring(start);
						}
					}
					String formatKey = arr[i].substring(0, arr[i].indexOf('('));
					if (formatKey.endsWith(TranslatorConstants.AMOUNT) && Util.isEmpty(message))
						message = "0";
					if (!map.containsKey(arr[i])) {
						String mapKey = isoMap.get(formatKey);
						if (mapKey != null && mapKey.compareTo("0") > 0)
							actMap.put(mapKey, !Util.isEmpty(message) ? message : "");
						else if (mapKey != null && mapKey.equals("0")) {
							actMap.put(mapKey, !Util.isEmpty(message) ? message : "");
						}
					}
				}

			} else {
				if (key.endsWith(TranslatorConstants.AMOUNT) && Util.isEmpty(value))
					value = "0";
				String mapKey = isoMap.get(key);
				if (mapKey != null && mapKey.compareTo(TranslatorConstants.ZERO) > 0)
					actMap.put(mapKey, !Util.isEmpty(value) ? value : "");
			}
		}
		map.clear();
		map.putAll(actMap);
		
	}

	private String getString(String str) {

		return (str.indexOf('{') != -1 && str.indexOf('}') != -1) ? str.replace("{", "").replace("}", "") : str;
	}

	private String getXMLPath(String fileName) {

		return translatorPath + TranslatorConstants.XML_PATH + File.separator + fileName;
	}
}
