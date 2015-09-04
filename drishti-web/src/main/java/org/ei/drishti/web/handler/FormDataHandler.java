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

import org.ei.drishti.common.util.DateUtil;
import org.ei.drishti.common.util.HttpAgent;
import org.ei.drishti.common.util.HttpResponse;
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
	private DateUtil dateUtil;
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
		HttpAgent httpAgent,DateUtil dateUtil){
	this.formSubmissionService=formSubmissionService;
	this.smsController=smsController;
	this.httpAgent = httpAgent;
	this.drishtiformdataURL=drishtiformdataURL;
	this.dateUtil=dateUtil;
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
				|| visittype.equalsIgnoreCase("anc_registration")
				){
			
			registration(dataObject);
						//smsController.sendSMSEC();
		}

		if(visittype.equalsIgnoreCase("child_registration")
				|| visittype.equalsIgnoreCase("ec_registration")
				|| visittype.equalsIgnoreCase("a_registration")){
						
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
						//smsController.sendSMSPOC();

						logger.info(" invoking a service");
						logger.info("res2+++++" + isCon);
						logger.info("anmid+++++" + anmid);

						formSubmissionService.requestConsultationTest(
								visittype, visitentityid, entityidEC,
								anmid);

						logger.info("invoking a service method");
						visit(dataObject);
					}

				}
			}
		}

	}

}

public void registration(JSONObject dataObject) throws JSONException{
	String entityId = dataObject.getString("entityId");
	HttpResponse response = new HttpResponse(false, null);
	Integer visitno=1;
	JSONArray fieldJsonArray = dataObject
			.getJSONObject("formInstance")
			.getJSONObject("form").getJSONArray("fields");
	
	for (int i = 0; i < fieldJsonArray.length(); i++) {

		JSONObject jsonObject = fieldJsonArray
				.getJSONObject(i);

				if ((jsonObject.has("name"))
				&& jsonObject.getString("name").equals("referenceDate")) {

			edd = (jsonObject.has("value") && jsonObject
					.getString("value") != null) ? jsonObject
					.getString("value") : "";
							
			logger.info("reference date: "+edd);
					//edd=dateUtil.dateFormat(edd1);
			
		}
		if ((jsonObject.has("name"))
				&& jsonObject.getString("name").equals("phoneNumber")) {

			phoneNumber = (jsonObject.has("value") && jsonObject
					.getString("value") != null) ? jsonObject
					.getString("value") : "";
		}
			
	}
	logger.info("^^^^ form data entityId: "+entityId+ "^^^^^^^ edd: "+edd+"^^^phonenumber: "+phoneNumber);
	response=httpAgent.get(drishtiformdataURL + "formdata?entityId=" + entityId+"&edd="+edd+"&phoneNumber="+phoneNumber+"visitnumber"+visitno);
	response.toString();
	logger.info("response from http"+response);
 }
	public void visit(JSONObject dataObject) throws JSONException{
		String visitno="";
		Integer visitnum = null;
		String entityId = dataObject.getString("entityId");
		
	
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
					&& jsonObject.getString("name").equals("ancVisitNumber")) {

				visitno = (jsonObject.has("value") && jsonObject
						.getString("value") != null) ? jsonObject
						.getString("value") : "";
				
						visitnum=Integer.parseInt(visitno);
								
				logger.info("reference date: "+edd);
						//edd=dateUtil.dateFormat(edd1);
				
			}
			phoneNumber="845123658";
			edd="2015-09-06";
		
		
}
		logger.info("^^^^ form data entityId: "+entityId+ "^^^^^^^ edd: "+edd+"^^^phonenumber: "+phoneNumber+"visit number***"+visitnum);
		httpAgent.get(drishtiformdataURL + "formdata?entityId=" + entityId+"&edd="+edd+"&phoneNumber="+phoneNumber+"visitnumber="+visitnum);
	}
}

