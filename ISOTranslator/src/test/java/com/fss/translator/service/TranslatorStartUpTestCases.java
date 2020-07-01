package com.fss.translator.service;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fss.translator.constants.TranslatorConstants;
import com.fss.translator.exception.ServiceException;
import com.fss.translator.resource.TranslatorResources;
import com.fss.translator.service.impl.TranslatorServiceImpl;
import com.fss.translator.util.Response;
import com.fss.translator.util.Util;
import com.fss.translator.validator.TranslatorValidator;

@RunWith(MockitoJUnitRunner.class)
//@SpringBootTest
public class TranslatorStartUpTestCases {
	
	//private MockMvc mockMvc;
	@Mock
	TranslatorResources translatorResources;
	
	@InjectMocks
	TranslatorServiceImpl translatorService;
	
	@Mock
	TranslatorValidator translatorValidatorImpl;
	
	@Mock
	TranslatorTargetformat translatorTargetformat;

	@Before
	public void setup(){
		/*try {
			translatorService=new TranslatorServiceImpl();

			TranslatorValidatorImpl translatorValidatorImpl=new TranslatorValidatorImpl();
			
			Field field = translatorService.getClass().getDeclaredField("translatorValidatorImpl");
			field.setAccessible(true);
			field.set(translatorService, translatorValidatorImpl);
			TranslatorTargetformatImpl translatorTargetformat=new TranslatorTargetformatImpl();
			Field field2 = translatorService.getClass().getDeclaredField("translatorTargetformat");
			field2.setAccessible(true);
			
				field2.set(translatorService, translatorTargetformat);
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			MockitoAnnotations.initMocks(this);
		/*	mockMvc = MockMvcBuilders.standaloneSetup(translatorService).build();*/
		

		


	}
	@Test
	public void dotranslatorServiceTest() throws ServiceException{
		
		String requestbody=getBodyMap();
		Map<String,String> headerMap=getHeaderMap();
			//Map<String,String> validationMap=getRequestConfigElements();
			//validationMap.remove(TranslatorConstants.CORRELATIONID);
			//Mockito.when(translatorResources.getRequestConfigElements()).thenReturn(validationMap);
			/*Mockito.when(translatorResources.getInstitutionData(Mockito.anyString())).thenReturn(getInstConfig());
			Mockito.when(translatorValidatorImpl.validateRequest(Mockito.any())).thenReturn();*/
			
			
			ResponseEntity<Response> responseEntity=translatorService.requestProcessTranslate(requestbody, headerMap);
			assertEquals(200, responseEntity.getStatusCodeValue());
			
			
		}
		
	/*	@Test
		public void doTranslatorValidator(){
			
			String requestbody=getBodyMap();
			Map<String,String> headerMap=getHeaderMap();
			try {
				ValueDTO dto=new ValueDTO();
				dto.setRequestObject(getHeaderMap());
				Map<String,String> validationMap=getRequestConfigElements();
				validationMap.remove(TranslatorConstants.CORRELATIONID);
				Mockito.when(translatorResources.getRequestConfigElements()).thenReturn(validationMap);
				Mockito.when(translatorResources.getRequestConfigElements()).thenReturn(validationMap);
				Mockito.when(translatorResources.getRequestConfigElements()).thenReturn(validationMap);
				ResponseEntity<Object> responseEntity=translatorValidatorImpl.requestProcesTranslate(requestbody, headerMap);
				assertEquals(HttpStatus.OK, responseEntity.getStatusCodeValue());
				
			} catch (ServiceException e) {
				
				
			}
	}*/
	
	
	
	private String getBodyMap(){
		String reuestBody="";
		Map<String,Object> map=new HashMap<>();
		map.put(TranslatorConstants.SOURCEMESSAGETYPE, TranslatorConstants.TARGET_RESPONSE_ISO8583);
		map.put(TranslatorConstants.APIKEY, "E02CFF12E96C834AC216BA6F6187F26141C6A4DD024FA7047588009FD611C686");
		map.put(TranslatorConstants.REQUESTDATA, "ISO0160000109200B452006B2067A00C0300000260001FC710201000000000100050000000002000701000000000002019052020190520000110291091407914070832323232109999999999111111111111110888888888800946146246301147474747474020484848484848484848488267860094614624630109140791407555502072727272727272727272112222222222233333333333084444444414ORIGINATORNAME017ORIGINATORADDRESS15BENIFICIARYNAME018BENIFICIARYADDRESS016120REFERENCEINFO024121REMITTANCEINFORMATION022122REGULATORYREPORTING008126PRCDE022127RETURNEDPAYMENTFPID1234567890ABCDEF");
		map.put(TranslatorConstants.SRCAPPID, "123");
		try {
			reuestBody=Util.mapToJson(map);
		} catch (JsonProcessingException e) {
		}
		System.out.println("reuestBody:"+reuestBody);
		return reuestBody;
	}
	
	private Map<String,String> getHeaderMap(){
		Map<String,String> map=new HashMap<>();
		map.put(TranslatorConstants.CONTENT_TYPE, TranslatorConstants.CONTENT_RESPONSE_ISJSON);
		map.put(TranslatorConstants.CORRELATIONID, "123456");
		map.put(TranslatorConstants.IPADDRESS, "10.44.112.102");
		
		
		return map;
	}
	@SuppressWarnings("unused")
	private Map<String,String> getInstConfig(){
		Map<String,String> map=new HashMap<>();
		map.put("ISO8583Packager.file", "D://Translator//Build//FPS_ISO8583.xml");
		map.put(TranslatorConstants.TARGET_RESPONSE_FORMAT, "ISO20022");
		map.put(TranslatorConstants.TARGETRESPONSEFORMATOPT, "pain.001.001.09");
		map.put(TranslatorConstants.REQUESTDATAFORMATOPT, "BASE64");
		
		return map;
	}
	@SuppressWarnings("unused")
	private Map<String,String> getRequestConfigElements(){
		Map<String,String> map=new HashMap<>();
		map.put(TranslatorConstants.SOURCEMESSAGETYPE, TranslatorConstants.IS_MANDATORY);
		map.put(TranslatorConstants.APIKEY, TranslatorConstants.IS_OPTIONAL);
		map.put(TranslatorConstants.REQUESTDATA, TranslatorConstants.IS_MANDATORY);
		map.put(TranslatorConstants.CONTENT_TYPE, TranslatorConstants.IS_MANDATORY);
		map.put(TranslatorConstants.CORRELATIONID, TranslatorConstants.IS_MANDATORY);
		map.put(TranslatorConstants.IPADDRESS, TranslatorConstants.IS_MANDATORY);
		
		return map;
	}
}
