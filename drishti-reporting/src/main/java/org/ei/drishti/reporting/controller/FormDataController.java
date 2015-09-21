package org.ei.drishti.reporting.controller;


import java.util.Iterator;
import java.util.List;

import org.ei.drishti.common.util.DateUtil;
import org.ei.drishti.dto.form.FormSubmissionDTO;
import org.ei.drishti.reporting.domain.ANCVisitDue;
import org.ei.drishti.reporting.handler.FormDatahandler;
import org.ei.drishti.reporting.repository.ANCVisitRepository;
import org.ei.drishti.reporting.service.ANMService;
import org.ei.drishti.reporting.service.VisitService;
import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import static ch.lambdaj.Lambda.collect;
import static ch.lambdaj.Lambda.on;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class FormDataController {
	
private ANCVisitRepository ancVisitRepository;
private VisitService visitService;
private ANMService anmService;
private FormDatahandler formDataHandler;
private SMSController smsController;
private DateUtil dateUtil;

private static Logger logger = LoggerFactory
.getLogger(FormDataController.class.toString());

String phoneNumber="";
String ecId="";

	@Autowired
	public FormDataController(ANCVisitRepository ancVisitRepository,VisitService visitService, 
			ANMService anmService,FormDatahandler formDataHandler,SMSController smsController){
	this.ancVisitRepository=ancVisitRepository;
	this.visitService=visitService;
	this.anmService=anmService;
	this.formDataHandler=formDataHandler;
	this.smsController=smsController;
	}
		
	@RequestMapping(headers = { "Accept=application/json" }, method = POST, value = "/formdata")
	    public void formData(
	    		@RequestBody List<FormSubmissionDTO> formSubmissionsDTO) throws JSONException {
			logger.info("^^ form data into reporting controller****");		
			
			//logger.info("formdto details"+formSubmissionsDTO);
			Iterator<FormSubmissionDTO> itr = formSubmissionsDTO.iterator();
			String visitno="";
			String date="";
			String newDate="";
			String anmphoneNumber="";
			
			while (itr.hasNext()) {
				Object object = (Object) itr.next();
				String jsonstr = object.toString();
 
				JSONObject dataObject = new JSONObject(jsonstr);
				String visittype = dataObject.getString("formName");
				logger.info("value of formname " + visittype);
				String user_id=dataObject.getString("anmId");
				anmphoneNumber=anmService.getanmPhoneNumber(user_id).get(0).toString();
				logger.info("value of anmphonenumber from db:"+anmphoneNumber);
				
				if (visittype.equalsIgnoreCase("ec_registration"))
						{
					logger.info("visit type"+visittype);
					formDataHandler.ecRegistration(dataObject,anmphoneNumber);
					
						}
				if (visittype.equalsIgnoreCase("ec_edit"))
						{
					logger.info("visit type"+visittype);
					
					formDataHandler.ecEdit(dataObject,anmphoneNumber);
			
						}
				if (visittype.equalsIgnoreCase("anc_reg_edit_oa"))
						{
					logger.info("visit type"+visittype);
			
					formDataHandler.ancEdit(dataObject,anmphoneNumber);
	
						}
				if (visittype.equalsIgnoreCase("child_registration_ec")
						|| visittype.equalsIgnoreCase("child_registration_oa"))
						{
					logger.info("visit type"+visittype);
					
					formDataHandler.childRegistration(dataObject,visittype);
			
						}
				
				if (visittype.equalsIgnoreCase("anc_registration")
						|| visittype.equalsIgnoreCase("anc_registration_oa"))
						{
					
					logger.info("visit type"+visittype);
					
					
					formDataHandler.ancRegistration(dataObject,visittype,anmphoneNumber);
					
						}
				if (visittype.equalsIgnoreCase("delivery_outcome")
						|| visittype.equalsIgnoreCase("pnc_registration_oa"))
						{
					
					logger.info("visit type"+visittype);
					String phoneNumber=anmService.getanmPhoneNumber(user_id).toString();
					formDataHandler.pncRegistration(dataObject,visittype,phoneNumber);
					
						}
//				if (visittype.equalsIgnoreCase("child_registration_oa")
//						)
//						{
//					
//					logger.info("visit type"+visittype);
//					//String phoneNumber=anmService.getanmPhoneNumber(user_id).toString();
//					//logger.info("value of anmphonenumber from db:"+visitnum);
//					formDataHandler.childRegistration(dataObject,visittype);
//					
//						}
				
				if(visittype.equalsIgnoreCase("anc_visit")){
					formDataHandler.ancVisit(dataObject,visittype,anmphoneNumber);
					
					}
			}
	}
}

