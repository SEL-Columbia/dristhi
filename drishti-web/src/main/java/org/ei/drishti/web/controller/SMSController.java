package org.ei.drishti.web.controller;

import static ch.lambdaj.collection.LambdaCollections.with;
import static java.text.MessageFormat.format;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.json.JSONArray;
import org.motechproject.scheduler.gateway.OutboundEventGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.ei.drishti.common.util.HttpAgent;
import org.ei.drishti.common.util.HttpResponse;
import org.ei.drishti.dto.VillagesDTO;
import org.ei.drishti.form.service.FormSubmissionService;


public class SMSController {
	
	private static Logger logger = LoggerFactory
			.getLogger(FormSubmissionController.class.toString());
	private FormSubmissionService formSubmissionService;
	private OutboundEventGateway gateway;
	 private HttpAgent httpAgent;
	 private final String drishtiSMSURL;

	 @Autowired
		public SMSController(@Value("#{drishti['drishti.sms.url']}") String drishtiSMSURL,
	            HttpAgent httpAgent,
				FormSubmissionService formSubmissionService,
				OutboundEventGateway gateway) {
			this.formSubmissionService = formSubmissionService;
			this.gateway = gateway;
			this.drishtiSMSURL = drishtiSMSURL;
			this.httpAgent = httpAgent;
		}

	 		public void sendSMS() {
	        HttpResponse response = new HttpResponse(false, null);
	        try {
	        	String num= "8121337675";
	            
	        	JSONArray obj1=new JSONArray();
	            obj1.put("tel:"+num);
	            httpAgent.get(drishtiSMSURL + "?tel="+obj1.toString()+" &message=registration successfull");
	            

	        }
	        catch(Exception e){
	        	
	        }

			

	 }
}


