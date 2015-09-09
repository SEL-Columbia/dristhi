package org.ei.drishti.reporting.controller;

import static java.text.MessageFormat.format;

import org.ei.drishti.common.util.HttpAgent;
import org.ei.drishti.dto.form.FormSubmissionDTO;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

@Controller
public class SMSController {

private static Logger logger = LoggerFactory
			.getLogger(SMSController.class.toString());
private HttpAgent httpAgent;
private final String drishtiSMSURL;
public FormSubmissionDTO formSubmissionsDTO;

	@Autowired
	public SMSController(@Value("#{drishti['drishti.sms.url']}") String drishtiSMSURL,HttpAgent httpAgent
				) {	
			this.drishtiSMSURL = drishtiSMSURL;
			this.httpAgent = httpAgent;
		}

	 public void sendSMSEC(String phoneNumber,String ecNumber, String wifeName) {
	        //HttpResponse response = new HttpResponse(false, null);
		 
	        try{
	        	logger.info("sms controller invoked");
	        	
	            logger.info("trying to send sms");
	        	JSONArray obj1=new JSONArray();
	            obj1.put("tel:"+phoneNumber);
	            String message="Dear%20"+wifeName+",%20you%20have%20been%20registered%20with%20EC%20number%20"+ecNumber+"%20";
	            logger.info("drishti sms url********"+drishtiSMSURL+"******* json obj value*****"+obj1);
	            httpAgent.get(drishtiSMSURL + "/?tel=%5B%22tel:+91"+phoneNumber+"%22%5D&message=%22"+message+"%22");
	            logger.info("drishti sms url********"+drishtiSMSURL+"******* success");     
	        }catch(Exception e){
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

