package org.ei.drishti.web.handler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

import org.ei.drishti.common.util.HttpAgent;
import org.ei.drishti.dto.form.FormSubmissionDTO;
import org.ei.drishti.web.controller.SMSController;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ei.drishti.form.service.FormSubmissionService;

@Component
public class FormDataHandler {
	private static Logger logger = LoggerFactory
			.getLogger(FormDataHandler.class.toString());
	private FormSubmissionService formSubmissionService;
	private final String drishtiformdataURL;
	private SMSController smsController;
	private HttpAgent httpAgent;
String entityidEC = null;
String edd = null;
String phoneNumber = null;
String entityId = null;
String entityId1 = null;
String edd1 = null;
String edd2=null;
String s=null;

@Autowired
public FormDataHandler(@Value("#{drishti['drishti.form.data.url']}") String drishtiformdataURL,FormSubmissionService formSubmissionService,SMSController smsController,
		HttpAgent httpAgent){
	this.formSubmissionService=formSubmissionService;
	this.smsController=smsController;
	this.httpAgent = httpAgent;
	this.drishtiformdataURL=drishtiformdataURL;
}
    
public void formData(List<FormSubmissionDTO> formSubmissionsDTO) throws JSONException, ParseException{
	
	
	Iterator<FormSubmissionDTO> itr = formSubmissionsDTO.iterator();
	logger.info("***** form data received****");

	while (itr.hasNext()) {
		Object object = (Object) itr.next();
		String jsonstr = object.toString();

		JSONObject dataObject = new JSONObject(jsonstr);

		String visittype = dataObject.getString("formName");
		logger.info("value of formname " + visittype);
		
		if(visittype.equalsIgnoreCase("anc_registration_oa")
				|| visittype.equalsIgnoreCase("pnc_registration_oa")
				|| visittype.equalsIgnoreCase("ec_registration")){
			
			String entityId = dataObject.getString("entityId");
			logger.info("*****sms controller***");
			
			JSONArray fieldJsonArray = dataObject
					.getJSONObject("formInstance")
					.getJSONObject("form").getJSONArray("fields");
			
			for (int i = 0; i < fieldJsonArray.length(); i++) {

				JSONObject jsonObject = fieldJsonArray
						.getJSONObject(i);

				if ((jsonObject.has("name"))
						&& jsonObject.getString("name").equals("entityId")) {

					entityId1 = (jsonObject.has("value") && jsonObject
							.getString("value") != null) ? jsonObject
							.getString("value") : "";
							

				}
				if ((jsonObject.has("name"))
						&& jsonObject.getString("name").equals("edd")) {

					edd1 = (jsonObject.has("value") && jsonObject
							.getString("value") != null) ? jsonObject
							.getString("value") : "";
							logger.info("value of edd1"+edd1);
									
					
					
					
						 
					
					
							
				}
				if ((jsonObject.has("name"))
						&& jsonObject.getString("name").equals("phoneNumber")) {

					phoneNumber = (jsonObject.has("value") && jsonObject
							.getString("value") != null) ? jsonObject
							.getString("value") : "";
				}
			
			
				
			}
			
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			Date date=(Date)formatter.parse(edd1);
			logger.info("value of date"+edd2);
			
			String edd=formatter.format(date);

			logger.info("value of edd2"+edd);
			logger.info("^^^^ form data entityId: "+entityId+ "^^^^^^^ edd: "+edd+"^^^phonenumber: "+phoneNumber);
			httpAgent.get(drishtiformdataURL + "formdata?entityId=" + entityId+"&edd="+edd+"&phoneNumber="+phoneNumber);
			
			//smsController.sendSMSEC();
		}
	
		if (visittype.equalsIgnoreCase("anc_visit")
				|| visittype.equalsIgnoreCase("pnc_visit")
				|| visittype.equalsIgnoreCase("child_illness")) {

			JSONArray fieldsJsonArray = dataObject
					.getJSONObject("formInstance")
					.getJSONObject("form").getJSONArray("fields");
			
			

			String visitentityid = dataObject.getString("entityId");

			String anmid = dataObject.getString("anmId");
			

			for (int i = 0; i < fieldsJsonArray.length(); i++) {

				JSONObject jsonObject = fieldsJsonArray
						.getJSONObject(i);

				if ((jsonObject.has("name"))
						&& jsonObject.getString("name").equals("ecId")) {

					entityidEC = (jsonObject.has("value") && jsonObject
							.getString("value") != null) ? jsonObject
							.getString("value") : "";
				

				}

				if ((jsonObject.has("name"))
						&& jsonObject.getString("name").equals(
								"isConsultDoctor")) {

					String isCon = (jsonObject.has("value") && jsonObject
							.getString("value") != null) ? jsonObject
							.getString("value") : "";

					logger.info("res1+++++" + isCon);
					if (isCon.equalsIgnoreCase("yes")) {
						smsController.sendSMSPOC();

						logger.info(" invoking a service");
						logger.info("res2+++++" + isCon);
						logger.info("anmid+++++" + anmid);

						formSubmissionService.requestConsultationTest(
								visittype, visitentityid, entityidEC,
								anmid);

						logger.info("invoking a service method");
					}

				}
			}
		}

	}

}
}
