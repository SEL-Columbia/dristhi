package org.ei.drishti.web.controller;

import static java.text.MessageFormat.format;

import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.ei.drishti.common.util.HttpAgent;
import org.ei.drishti.common.util.HttpResponse;
import org.ei.drishti.dto.form.FormSubmissionDTO;


@Controller
public class SMSController {
	
	private static Logger logger = LoggerFactory
			.getLogger(FormSubmissionController.class.toString());
	
		 private HttpAgent httpAgent;
	 private final String drishtiSMSURL;
	 public FormSubmissionDTO formSubmissionsDTO;

	 @Autowired
		public SMSController(@Value("#{drishti['drishti.sms.url']}") String drishtiSMSURL,
	            HttpAgent httpAgent
				) {
					
			this.drishtiSMSURL = drishtiSMSURL;
			this.httpAgent = httpAgent;
		}

	 		public void sendSMSEC() {
	        //HttpResponse response = new HttpResponse(false, null);
	        try {
	        	logger.info("sms controller invoked");
	        	String num= "+918121337675";
	            logger.info("trying to send sms");
	        	JSONArray obj1=new JSONArray();
	            obj1.put("tel:"+num);
	            logger.info("drishti sms url********"+drishtiSMSURL+"******* json obj value*****"+obj1);
	            httpAgent.get(drishtiSMSURL + "/?tel=%5B%22tel:"+num+"%22%5D&message=%22registration%20is%20successfull%22");
	            logger.info("drishti sms url********"+drishtiSMSURL+"******* json obj value*****"+obj1);

	        }
	        catch(Exception e){
	        	logger.error(format(
					"Form submissions processing failed with exception {0}",e));
	        }
	 		}
	        
	        public void sendSMSPOC() {
		        //HttpResponse response = new HttpResponse(false, null);
		        try {
		        	logger.info("sms controller invoked");
		        	String num= "+918121337675";
		            logger.info("trying to send sms");
		        	JSONArray obj1=new JSONArray();
		            obj1.put("tel:"+num);
		            logger.info("drishti sms url********"+drishtiSMSURL+"******* json obj value*****"+obj1);
		            httpAgent.get(drishtiSMSURL + "/?tel=%5B%22tel:"+num+"%22%5D&message=%22registration%20is%20successfull%22");
		            logger.info("drishti sms url********"+drishtiSMSURL+"******* json obj value*****"+obj1);

		        }
		        catch(Exception e){
		        	logger.error(format(
							"Form submissions processing failed with exception {0}",e));
		        }
			

	 }

	    	
	    }



