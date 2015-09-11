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
	public void sendSMSPNC(String phoneNumber,String ecNumber, String wifeName, String registrationType){
		
		try{
        	logger.info("sms controller invoked");
        	
            logger.info("trying to send sms");
        	JSONArray obj1=new JSONArray();
            obj1.put("tel:"+phoneNumber);
            
            String message="Dear%20"+wifeName+",%20you%20and%20your%child%have%20been%20registered%20with%20"+registrationType+"%20number%20"+ecNumber+"%20";
            logger.info("drishti sms url********"+drishtiSMSURL+"******* json obj value*****"+obj1);
            httpAgent.get(drishtiSMSURL + "/?tel=%5B%22tel:"+phoneNumber+"%22%5D&message=%22"+message+"%22");
            logger.info("drishti sms url********"+drishtiSMSURL+"******* success");     
        }catch(Exception e){
        	logger.error(format(
					"Form submissions processing failed with exception {0}",e));
        }
	}
	 public void sendSMSEC(String phoneNumber,String ecNumber, String wifeName, String registrationType) {
	        //HttpResponse response = new HttpResponse(false, null);
		 
	        try{
	        	logger.info("sms controller invoked");
	        	
	            logger.info("trying to send sms");
	        	JSONArray obj1=new JSONArray();
	            obj1.put("tel:"+phoneNumber);
	            
	            String message="Dear%20"+wifeName+",%20you%20have%20been%20registered%20with%20"+registrationType+"%20number%20"+ecNumber+"%20";
	            logger.info("drishti sms url********"+drishtiSMSURL+"******* json obj value*****"+obj1);
	            httpAgent.get(drishtiSMSURL + "/?tel=%5B%22tel:"+phoneNumber+"%22%5D&message=%22"+message+"%22");
	            logger.info("drishti sms url********"+drishtiSMSURL+"******* success");     
	        }catch(Exception e){
	        	logger.error(format(
						"Form submissions processing failed with exception {0}",e));
	        }
	 		}
	        
	        public void sendSMSChild(String phoneNumber, String motherName) {
		        //HttpResponse response = new HttpResponse(false, null);
		        try {
		        	logger.info("sms controller invoked");
		        	
		            logger.info("trying to send sms");
		        	JSONArray obj1=new JSONArray();
		            obj1.put("tel:"+phoneNumber);
		            
		            String message="Your%20Child%20have%20been%20registered%20with%20Mother%20Name"+motherName+"%20";
		            logger.info("drishti sms url********"+drishtiSMSURL+"******* json obj value*****"+obj1);
		            httpAgent.get(drishtiSMSURL + "/?tel=%5B%22tel:"+phoneNumber+"%22%5D&message=%22"+message+"%22");
		        }
		        catch(Exception e){
		        	logger.error(format(
							"Form submissions processing failed with exception {0}",e));
		        }
			

	 }

	    	
}

