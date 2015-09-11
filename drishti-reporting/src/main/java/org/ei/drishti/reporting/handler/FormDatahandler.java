package org.ei.drishti.reporting.handler;


import static ch.lambdaj.Lambda.collect;
import static ch.lambdaj.Lambda.on;

import java.util.List;

import org.ei.drishti.reporting.controller.SMSController;
import org.ei.drishti.reporting.domain.ANCVisitDue;
import org.ei.drishti.reporting.domain.EcRegDetails;
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
    private static Logger logger = LoggerFactory.getLogger((String)FormDatahandler.class.toString());
    String regNumber="";
    String wifeName="";
    String phoneNumber="";

    @Autowired
    public void FormDataHandler(ANCVisitRepository ancVisitRepository, ANMService anmService,SMSController smsController,VisitService visitService) {
        this.ancVisitRepository = ancVisitRepository;
        this.smsController = smsController;
        this.anmService=anmService;
        this.visitService=visitService;
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
              
        String entityId = dataObject.getString("entityId");
       // String ptphoneNumber=anmService.getPhoneNumber(entityId).toString();
        
        List ancvisitdetails= anmService.getPhoneNumber(entityId);
    	
    	String ptphoneNumber = collect(ancvisitdetails, on(EcRegDetails.class).phonenumber()).get(0).toString();

        logger.info("patient number from EC db: "+ptphoneNumber);
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
       if (visittype.equalsIgnoreCase("anc_registration")){
//        	smsController.sendSMSEC(ptphoneNumber, regNumber, wifeName,"ANC");
       	ancVisitRepository.insert(entityId, ptphoneNumber, anmNumber, visittype, visitnumber,edd,wifeName);
        }
        if (visittype.equalsIgnoreCase("anc_registration_oa")){
        	smsController.sendSMSEC(phoneNumber, regNumber, wifeName,"ANC");
        	logger.info("sms sent done");
        	ancVisitRepository.insert(entityId, phoneNumber, anmNumber, visittype, visitnumber,edd,wifeName);
        }
        
       //smsController.sendSMSEC(phoneNumber, regNumber, wifeName,"ANC");
        logger.info("^^ transfer data from controller to repository****");
        //ancVisitRepository.insert(entityId, phoneNumber, anmNumber, visittype, visitnumber, edd);
    }

    public void ancVisit() {
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
        	String womphoneNumber=collect(ancvisitdetails, on(ANCVisitDue.class).patientnum()).toString();
            logger.info("wom phone number from db"+womphoneNumber);
           smsController.sendSMSPNC(womphoneNumber, regNumber, womenName,"PNC");
        }
        if(visittype.equalsIgnoreCase("pnc_registration_oa"))
            logger.info("phonenumber"+phoneNumber+"*** wife name"+wifeName+"***reg Number"+regNumber);  
       smsController.sendSMSEC(phoneNumber, regNumber, wifeName,"PNC");
        logger.info("^^ transfer data from controller to repository****");
        //ancVisitRepository.insert(entityId, phoneNumber, anmNumber, visittype, visitnumber, edd);
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
         if (visittype.equalsIgnoreCase("child_registration")){
         smsController.sendSMSChild(phoneNumber,motherName);}
         if (visittype.equalsIgnoreCase("child_registration_ec")){
        	 List ancvisitdetails= anmService.getPhoneNumber(entityId);
         	
         	String ptphoneNumber = collect(ancvisitdetails, on(EcRegDetails.class).phonenumber()).get(0).toString();
             smsController.sendSMSChild(ptphoneNumber,wifeName);}
    }

    public void childIllness() {
    }
}