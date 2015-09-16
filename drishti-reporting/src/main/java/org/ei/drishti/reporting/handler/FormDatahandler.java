package org.ei.drishti.reporting.handler;


import static ch.lambdaj.Lambda.collect;
import static ch.lambdaj.Lambda.on;

import java.util.List;

import org.ei.drishti.common.util.DateUtil;
import org.ei.drishti.reporting.controller.SMSController;
import org.ei.drishti.reporting.domain.ANCVisitDue;
import org.ei.drishti.reporting.domain.EcRegDetails;
import org.ei.drishti.reporting.domain.VisitConf;
import org.ei.drishti.reporting.repository.ANCVisitRepository;
import org.ei.drishti.reporting.service.ANMService;
import org.ei.drishti.reporting.service.VisitService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



@Component
public class FormDatahandler {
    private ANCVisitRepository ancVisitRepository;
    private SMSController smsController;
    private ANMService anmService;
    private EcRegDetails ecRegDetails;
    private VisitService visitService;
    private DateUtil dateUtil;
    private static Logger logger = LoggerFactory.getLogger((String)FormDatahandler.class.toString());
    String regNumber="";
    String wifeName="";
    String phoneNumber="";

    @Autowired
    public void FormDataHandler(ANCVisitRepository ancVisitRepository,DateUtil dateUtil, ANMService anmService,SMSController smsController,VisitService visitService) {
        this.ancVisitRepository = ancVisitRepository;
        this.smsController = smsController;
        this.anmService=anmService;
        this.visitService=visitService;
        this.dateUtil=dateUtil;
    }

    public void ecRegistration(JSONObject dataObject, String anmPhoneNumber) throws JSONException {
        String entityId = dataObject.getString("entityId");
        logger.info("ecregistration method");
        JSONArray fieldJsonArray = dataObject.getJSONObject("formInstance").getJSONObject("form").getJSONArray("fields");
        for (int i = 0; i < fieldJsonArray.length(); i++) {
            JSONObject jsonObject = fieldJsonArray.getJSONObject(i);
            
            if (jsonObject.has("name") &&
            		jsonObject.getString("name").equals("phoneNumber")){
            
            phoneNumber = jsonObject.has("value") && 
            			  jsonObject.getString("value") != null ? jsonObject
            					  .getString("value") : "";
        }
            if (jsonObject.has("name") &&
            		jsonObject.getString("name").equals("ecNumber")){
            
            	regNumber = jsonObject.has("value") && 
            			  jsonObject.getString("value") != null ? jsonObject
            					  .getString("value") : "";
        }
            if (jsonObject.has("name") &&
            		jsonObject.getString("name").equals("wifeName")){
            
            wifeName = jsonObject.has("value") && 
            			  jsonObject.getString("value") != null ? jsonObject
            					  .getString("value") : "";
        }
        
    }
        logger.info("invoke sms controller******" + phoneNumber);
        smsController.sendSMSEC(phoneNumber, regNumber, wifeName,"EC");
        ancVisitRepository.ecinsert(entityId, phoneNumber);
        logger.info("invoke sms eeeecontroller******");
    }

    public void fpRegistration() {
    }

    public void ancRegistration(JSONObject dataObject, String visittype, String anmNumber) throws JSONException {
        String edd = "";
        String ecNumber="";
         logger.info("anc_registration handler");     
        String entityId = dataObject.getString("entityId");
       // String ptphoneNumber=anmService.getPhoneNumber(entityId).toString();
        
//        List ancvisitdetails= anmService.getPhoneNumber(entityId);
//    	
//    	String ptphoneNumber = collect(ancvisitdetails, on(EcRegDetails.class).phonenumber()).get(0).toString();

        //logger.info("patient number from EC db: "+ptphoneNumber);
        JSONArray fieldJsonArray = dataObject.getJSONObject("formInstance").getJSONObject("form").getJSONArray("fields");
        Integer visitnumber = 1;
        for (int i = 0; i < fieldJsonArray.length(); ++i) {
            JSONObject jsonObject = fieldJsonArray.getJSONObject(i);
            if (jsonObject.has("name") && 
            		jsonObject.getString("name").equals("referenceDate")) {
            	
                edd = jsonObject.has("value") && jsonObject.
                		getString("value") != null ? jsonObject
                		.getString("value") : "";
                
                logger.info("reference date: " + edd);
            }
            if (jsonObject.has("name") && jsonObject.getString("name").equals("phoneNumber")) {;
            phoneNumber = jsonObject.has("value") && jsonObject
            		.getString("value") != null ? jsonObject
            		.getString("value") : "";
            }
            
            if (jsonObject.has("name") &&
            		jsonObject.getString("name").equals("ancNumber")){
            
            	regNumber = jsonObject.has("value") && 
            			  jsonObject.getString("value") != null ? jsonObject
            					  .getString("value") : "";
        }
            if (jsonObject.has("name") &&
            		jsonObject.getString("name").equals("ecNumber")){
            
            	ecNumber = jsonObject.has("value") && 
            			  jsonObject.getString("value") != null ? jsonObject
            					  .getString("value") : "";
        }
            if (jsonObject.has("name") &&
            		jsonObject.getString("name").equals("wifeName")){
            
            wifeName = jsonObject.has("value") && 
            			  jsonObject.getString("value") != null ? jsonObject
            					  .getString("value") : "";
        }
        }
        List visitconf= visitService.getVisitconf();
        String visittime=collect(visitconf, on(VisitConf.class).anc_visit1_from_week()).get(0).toString();
        logger.info("visit time interval:"+visittime);
        int visitti=Integer.parseInt(visittime)*7;
        String lmpdate=dateUtil.dateFormat(edd,visitti);
       if (visittype.equalsIgnoreCase("anc_registration")){
    	   List ancvisitdetails= anmService.getPhoneNumber(entityId);
       	
       	String ptphoneNumber = collect(ancvisitdetails, on(EcRegDetails.class).phonenumber()).get(0).toString();
//        	smsController.sendSMSEC(ptphoneNumber, regNumber, wifeName,"ANC");
       	ancVisitRepository.insert(entityId, ptphoneNumber, anmNumber, "anc_visit", visitnumber,edd,wifeName,lmpdate);
        }
        if (visittype.equalsIgnoreCase("anc_registration_oa")){
        	logger.info("trying to send sms");
        	smsController.sendSMSEC(phoneNumber, regNumber, wifeName,"ANC");
        	logger.info("sms sent done");
        	ancVisitRepository.insert(entityId, phoneNumber, anmNumber, "anc_visit", visitnumber,edd,wifeName,lmpdate);
        }
        
       //smsController.sendSMSEC(phoneNumber, regNumber, wifeName,"ANC");
        logger.info("^^ transfer data from controller to repository****");
        //ancVisitRepository.insert(entityId, phoneNumber, anmNumber, visittype, visitnumber, edd);
    }

    public void ancVisit(JSONObject dataObject, String visittype) throws JSONException {
    	String ecId="";
    	String newdate="";
    	String visittime="";
    	Integer visitno=null;
    	Integer visitti=null;
    	    	
    	JSONArray fieldJsonArray = dataObject
				.getJSONObject("formInstance")
				.getJSONObject("form").getJSONArray("fields");
							
		for (int i = 0; i < fieldJsonArray.length(); i++) {

			JSONObject jsonObject = fieldJsonArray
					.getJSONObject(i);
				if((jsonObject.has("name"))
						&& jsonObject.getString("name").equals("ecId")) {

					ecId = (jsonObject.has("value") && jsonObject
							.getString("value") != null) ? jsonObject
							.getString("value") : "";
				}
		}
		logger.info("entityid"+ecId);
		List ancvisitdetails= visitService.getVisitDue(ecId);
		List visitconf= visitService.getVisitconf();
		logger.info("ancvisitpastdetails^^^^"+ancvisitdetails);
		String datetime=collect(ancvisitdetails, on(ANCVisitDue.class).lmpdate()).get(0).toString();
		String visit=collect(ancvisitdetails, on(ANCVisitDue.class).visitno()).get(0).toString();
		int visitnum=Integer.parseInt(visit);
		if(visitnum==1){
			visittime=collect(visitconf, on(VisitConf.class).anc_visit2_from_week()).get(0).toString();
			visitti=Integer.parseInt(visittime)*7;
			newdate=dateUtil.dateFormat(datetime,visitti);
			visitno=2;
		}
		if(visitnum==2){
			visittime=collect(visitconf, on(VisitConf.class).anc_visit3_from_week()).get(0).toString();
			logger.info("visit time interval:"+visittime);
			visitti=Integer.parseInt(visittime)*7;
			newdate=dateUtil.dateFormat(datetime,visitti);
			
			visitno=3;
		}
		if(visitnum==3){
			visittime=collect(visitconf, on(VisitConf.class).anc_visit4_from_week()).get(0).toString();
			logger.info("visit time interval:"+visittime);
			visitti=Integer.parseInt(visittime)*7;
			logger.info("visit time converted interval:"+visitti);
			newdate=dateUtil.dateFormat(datetime,visitti);
			logger.info("visit time converted time:"+newdate);
			visitno=4;
		}
		if(visitnum==4){
			newdate="";
			logger.info("visit time converted time:"+newdate);
			visitno=5;
		}
		
		logger.info("new date"+newdate);
		String sid = collect(ancvisitdetails, on(ANCVisitDue.class).id()).get(0).toString();
		logger.info("id from db:"+sid);
		int id= Integer.parseInt(sid);
		logger.info("id from db:"+id);
		ancVisitRepository.ancUpdate(id,newdate,visitno);
    }

    public void pncRegistration(JSONObject dataObject, String visittype, String anmNumber) throws JSONException {
    	String edd = "";
        String ecNumber="";
        String ptnumber="";  
        String ecId="";
        String entityId = dataObject.getString("entityId");
        logger.info("pnc registration");
        
        JSONArray fieldJsonArray = dataObject.getJSONObject("formInstance").getJSONObject("form").getJSONArray("fields");
        
        for (int i = 0; i < fieldJsonArray.length(); ++i) {
            JSONObject jsonObject = fieldJsonArray.getJSONObject(i);
            if (jsonObject.has("name") && 
            		jsonObject.getString("name").equals("referenceDate")) {
            	
                edd = jsonObject.has("value") && jsonObject.
                		getString("value") != null ? jsonObject
                		.getString("value") : "";
                
                logger.info("reference date: " + edd);
            }
            if (jsonObject.has("name") && jsonObject.getString("name").equals("phoneNumber")) {;
            phoneNumber = jsonObject.has("value") && jsonObject
            		.getString("value") != null ? jsonObject
            		.getString("value") : "";
            }
            
            if (jsonObject.has("name") &&
            		jsonObject.getString("name").equals("pncNumber")){
            
            	regNumber = jsonObject.has("value") && 
            			  jsonObject.getString("value") != null ? jsonObject
            					  .getString("value") : "";
        }
            if (jsonObject.has("name") &&
            		jsonObject.getString("name").equals("ecNumber")){
            
            	ecNumber = jsonObject.has("value") && 
            			  jsonObject.getString("value") != null ? jsonObject
            					  .getString("value") : "";
        }
            if (jsonObject.has("name") &&
            		jsonObject.getString("name").equals("wifeName")){
            
            wifeName = jsonObject.has("value") && 
            			  jsonObject.getString("value") != null ? jsonObject
            					  .getString("value") : "";
        }
            if (jsonObject.has("name") &&
            		jsonObject.getString("name").equals("ecId")){
            
            	ecId = jsonObject.has("value") && 
            			  jsonObject.getString("value") != null ? jsonObject
            					  .getString("value") : "";
            
        }
        }
        if(visittype.equalsIgnoreCase("delivery_outcome")){
        	
        	List ancvisitdetails= visitService.getVisitDue(ecId);
        	
        	String womenName = collect(ancvisitdetails, on(ANCVisitDue.class).womenName()).get(0).toString();
        	
        	logger.info("women Name from db"+womenName);
        	String womphoneNumber=collect(ancvisitdetails, on(ANCVisitDue.class).patientnum()).get(0).toString();
            logger.info("wom phone number from db"+womphoneNumber);
           smsController.sendSMSPNC(womphoneNumber,regNumber,womenName,"PNC");
        }
        if(visittype.equalsIgnoreCase("pnc_registration_oa")){
            logger.info("phonenumber"+phoneNumber+"*** wife name"+wifeName+"***reg Number"+regNumber);  
       smsController.sendSMSEC(phoneNumber, regNumber, wifeName,"PNC");
        logger.info("^^ transfer data from controller to repository****");
        //ancVisitRepository.insert(entityId, phoneNumber, anmNumber, visittype, visitnumber, edd);
    }
    }

    public void pncVisit() {
    }

    public void childRegistration(JSONObject dataObject, String visittype) throws JSONException {
    	 String edd = "";
         String ecNumber="";
         String motherName="";
               
         String entityId = dataObject.getString("entityId");
         //String ptphoneNumber=anmService.getPhoneNumber(entityId).toString();
        // logger.info("patient number from db"+ptphoneNumber);
         JSONArray fieldJsonArray = dataObject.getJSONObject("formInstance").getJSONObject("form").getJSONArray("fields");
         Integer visitnumber = 1;
         for (int i = 0; i < fieldJsonArray.length(); ++i) {
             JSONObject jsonObject = fieldJsonArray.getJSONObject(i);
             
             if (jsonObject.has("name") && jsonObject.getString("name").equals("phoneNumber")) {;
             phoneNumber = jsonObject.has("value") && jsonObject
             		.getString("value") != null ? jsonObject
             		.getString("value") : "";
             }
                          
             if (jsonObject.has("name") &&
             		jsonObject.getString("name").equals("motherName")){
             
            motherName = jsonObject.has("value") && 
             			  jsonObject.getString("value") != null ? jsonObject
             					  .getString("value") : "";
         }
             if (jsonObject.has("name") &&
             		jsonObject.getString("name").equals("wifeName")){
             
             wifeName = jsonObject.has("value") && 
             			  jsonObject.getString("value") != null ? jsonObject
             					  .getString("value") : "";
         }
     }
         if (visittype.equalsIgnoreCase("child_registration_oa")){
         smsController.sendSMSChild(phoneNumber,motherName);}
         
         if (visittype.equalsIgnoreCase("child_registration_ec")){
        	 List ancvisitdetails= anmService.getPhoneNumber(entityId);
         	
         	String ptphoneNumber = collect(ancvisitdetails, on(EcRegDetails.class).phonenumber()).get(0).toString();
         	logger.info("phonenumber: "+ptphoneNumber);
             smsController.sendSMSChild(ptphoneNumber,wifeName);
             }
    }

    public void childIllness() {
    }
}