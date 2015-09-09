package org.ei.drishti.reporting.handler;


import org.ei.drishti.reporting.controller.SMSController;
import org.ei.drishti.reporting.repository.ANCVisitRepository;
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
    private static Logger logger = LoggerFactory.getLogger((String)FormDatahandler.class.toString());

    @Autowired
    public void FormDataHandler(ANCVisitRepository ancVisitRepository, SMSController smsController) {
        this.ancVisitRepository = ancVisitRepository;
        this.smsController = smsController;
    }

    public void ecRegistration(JSONObject dataObject, String anmPhoneNumber) throws JSONException {
        String entityId = dataObject.getString("entityId");
        String phoneNumber = "";
        String ecNumber = "";
        String wifeName = "";
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
            
            ecNumber = jsonObject.has("value") && 
            			  jsonObject.getString("value") != null ? jsonObject
            					  .getString("value") : "";
        }
            if (jsonObject.has("name") &&
            		jsonObject.getString("name").equals("wifename")){
            
            wifeName = jsonObject.has("value") && 
            			  jsonObject.getString("value") != null ? jsonObject
            					  .getString("value") : "";
        }
        
    }
        logger.info("invoke sms controller******" + phoneNumber);
        this.smsController.sendSMSEC(phoneNumber, ecNumber, wifeName);
        logger.info("invoke sms eeeecontroller******");
    }

    public void fpRegistration() {
    }

    public void ancRegistration(JSONObject dataObject, String visittype, String anmNumber) throws JSONException {
        String edd = "";
        String phoneNumber = "";
        String entityId = dataObject.getString("entityId");
        JSONArray fieldJsonArray = dataObject.getJSONObject("formInstance").getJSONObject("form").getJSONArray("fields");
        Integer visitnumber = 1;
        for (int i = 0; i < fieldJsonArray.length(); ++i) {
            JSONObject jsonObject = fieldJsonArray.getJSONObject(i);
            if (jsonObject.has("name") && jsonObject.getString("name").equals("referenceDate")) {
                edd = jsonObject.has("value") && jsonObject.getString("value") != null ? jsonObject.getString("value") : "";
                logger.info("reference date: " + edd);
            }
            if (!jsonObject.has("name") || !jsonObject.getString("name").equals("phoneNumber")) continue;
            phoneNumber = jsonObject.has("value") && jsonObject.getString("value") != null ? jsonObject.getString("value") : "";
        }
        logger.info("^^ transfer data from conteroller to repository****");
       // smsController.sendSMSEC(phoneNumber);
        ancVisitRepository.insert(entityId, phoneNumber, anmNumber, visittype, visitnumber, edd);
    }

    public void ancVisit() {
    }

    public void pncRegistration() {
    }

    public void pncVisit() {
    }

    public void childRegistration() {
    }

    public void childIllness() {
    }
}