package org.opensrp.connector.openmrs;

import static org.opensrp.connector.openmrs.OpenmrsConstants.OPENMRS_DATE;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import javax.management.InvalidAttributeValueException;

import org.ei.drishti.common.util.HttpResponse;
import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.form.service.FormSubmissionService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensrp.connector.helper.ExceptionUtil;
import org.opensrp.connector.helper.HttpUtil;
import org.opensrp.connector.openmrs.OpenmrsConstants.FormField;
import org.opensrp.connector.openmrs.OpenmrsConstants.PersonField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mysql.jdbc.StringUtils;

@Service
public class OpenmrsService {
    private static Logger logger = LoggerFactory.getLogger("OMRS_SERVICE");

    private static String openmrsOpenmrsUrl;
    private static String openmrsUsername;
    private static String openmrsPassword;
    private static String openmrsOpenSrpConnectorUrl;
    private static String openmrsOpenSrpConnectorContentParam;
    private static final String OPENMRS_SYNC_ATTR_NAME = "openmrsSynced";
    private static final String OPENMRS_CASEID_ATTR_NAME = "openmrsCaseId";

    private HttpUtil http;
    
    private FormSubmissionService formSubmissionService;

    @Autowired
    public OpenmrsService(FormSubmissionService formSubmissionService, 
    		@Value("#{drishti['openmrs.url']}") String openmrsOpenmrsUrl,
    		@Value("#{drishti['openmrs.username']}") String openmrsUsername,
    		@Value("#{drishti['openmrs.password']}") String openmrsPassword,
    		@Value("#{drishti['openmrs.opensrp-connector.url']}") String openmrsOpenSrpConnectorUrl,
    		@Value("#{drishti['openmrs.opensrp-connector.content-param']}") String openmrsOpenSrpConnectorContentParam, 
    		@Value("#{drishti['openmrs.location-list.url']}") String openmrsLocationListUrl) {
        OpenmrsService.openmrsOpenmrsUrl = openmrsOpenmrsUrl;
        OpenmrsService.openmrsUsername = openmrsUsername;
        OpenmrsService.openmrsPassword = openmrsPassword;
        OpenmrsService.openmrsOpenSrpConnectorUrl = openmrsOpenSrpConnectorUrl;
        OpenmrsService.openmrsOpenSrpConnectorContentParam = openmrsOpenSrpConnectorContentParam;
        
        this.formSubmissionService = formSubmissionService;
        this.http = new HttpUtil();
    }

    public String pushDataToOpenmrs(String formToPush, long serverVersion) throws JSONException, FileNotFoundException, IOException {
    	List<FormSubmission> fsl = formSubmissionService.getSubmissionByOpenmrsNotSynced(formToPush);
    	
		for (FormSubmission formSubmission : fsl) {
			try{
			JSONObject json = makeRequestContent(formSubmission);
			
			String appUrl = removeEndingSlash(openmrsOpenmrsUrl);
			String serviceUrl =  removeTrailingSlash(removeEndingSlash(openmrsOpenSrpConnectorUrl));
			
			logger.info("URL:"+appUrl+"/"+serviceUrl+"\nJSON::"+json.toString());

			HttpResponse response = http.post(appUrl+"/"+serviceUrl, "username="+openmrsUsername+"&password="+openmrsPassword, json.toString(), openmrsOpenSrpConnectorContentParam);
			
			logger.info("RESPONSE:"+response.body());
			
			JSONObject resp = new JSONObject(response.body());
			
			String openmrsCaseId = (String) formSubmission.getFormMetaData().get(OPENMRS_CASEID_ATTR_NAME);
			
			if(!response.isSuccess() || resp.has("ERROR")){
				String subject = "ERROR IN SUBMITTED "+formToPush +" FORM FOR OMRS-ID "+openmrsCaseId;
				String message = "FORM: "+formToPush+"\nOMRS-ID: "+openmrsCaseId+"\nFS-ID: "+formSubmission.entityId()+"\nOpenMRS RESPONSE:\n"+resp.getString("ERROR_MESSAGE");
				System.out.println("ERROR:"+message);
				//EmailEngine.getInstance().emailReportToAdminInSeparateThread(subject, message);
				// if error mark as synced and put error message to avoid spamming with errors
				formSubmission.getFormMetaData().put(OPENMRS_SYNC_ATTR_NAME, true);
				formSubmission.getFormMetaData().put("ERROR", resp.getString("ERROR_MESSAGE"));
				formSubmissionService.update(formSubmission);
			}
			else if(resp.has(openmrsCaseId) && resp.getString(openmrsCaseId).equalsIgnoreCase("success")){
				formSubmission.getFormMetaData().put(OPENMRS_SYNC_ATTR_NAME, true);
				formSubmissionService.update(formSubmission);
			}
			
			}
			catch(Exception e){
				e.printStackTrace();
				
				String subject = "ERROR POSTING "+formToPush +" FORM FOR FS-ID "+formSubmission.entityId();
				String message = "FORM: "+formToPush+"\nFS-ID: "+formSubmission.entityId()+"\nEXCEPTION:\n"+ExceptionUtil.getStackTrace(e);

//				EmailEngine.getInstance().emailReportToAdminInSeparateThread(subject, message);
				System.out.println("ERROR:"+message);

				logger.error(subject+ "\nEXCEPTION : " + ExceptionUtil.getStackTrace(e));
			}
		}
		return "DONE";
	}
    
    public JSONObject makeRequestContent(FormSubmission fs) throws JSONException, InvalidAttributeValueException{
    	JSONObject json = new JSONObject();
		
		JSONArray mainObj = new JSONArray();
		
		JSONObject pd = new JSONObject();

		//PATIENT/PERSON INFO
		JSONObject patient = new JSONObject();
		patient.put(PersonField.IDENTIFIER.OMR_FIELD(), fs.entityId());
		patient.put(PersonField.IDENTIFIER_TYPE.OMR_FIELD(), "TB_IDENTIFIER");
		String firstname = PersonField.FIRST_NAME.SRP_VALUE(fs);
		String lastname = PersonField.LAST_NAME.SRP_VALUE(fs);
		patient.put(PersonField.FIRST_NAME.OMR_FIELD(), StringUtils.isEmptyOrWhitespaceOnly(firstname)?".":firstname);
		patient.put(PersonField.LAST_NAME.OMR_FIELD(), StringUtils.isEmptyOrWhitespaceOnly(lastname)?".":lastname);
		patient.put(PersonField.GENDER.OMR_FIELD(), PersonField.GENDER.SRP_VALUE(fs));
		
		if(PersonField.BIRTHDATE.SRP_VALUE(fs) != null){
			patient.put(PersonField.BIRTHDATE.OMR_FIELD(), PersonField.BIRTHDATE.SRP_VALUE(fs));
			patient.put(PersonField.BIRTHDATE_IS_APPROX.OMR_FIELD(), false);
		}
		else if(PersonField.AGE.SRP_VALUE(fs) != null){
			Calendar bdc = Calendar.getInstance();
			bdc.add(Calendar.YEAR, -Integer.parseInt(PersonField.AGE.SRP_VALUE(fs)));
			patient.put(PersonField.BIRTHDATE.OMR_FIELD(), OPENMRS_DATE.format(bdc.getTime()));
		}
		
		patient.put(PersonField.DEATHDATE.OMR_FIELD(), PersonField.DEATHDATE.SRP_VALUE(fs));
		patient.put(FormField.FORM_CREATOR.OMR_FIELD(), "daemon");
		
		//ENCOUNTER/EVENT/FORM INFO
		JSONArray en = new JSONArray();
		
		if(fs.formName().toLowerCase().contains("tb_registration")){
			patient.put(PersonField.IS_NEW_PERSON.OMR_FIELD(), true);
			fs.getFormMetaData().put(OPENMRS_CASEID_ATTR_NAME, fs.entityId());
			en.put(createTBFollowupEncounterAndObs(fs));
		}
		else if(fs.formName().toLowerCase().contains("tb_followup")){
			patient.put(PersonField.IS_NEW_PERSON.OMR_FIELD(), false);
			fs.getFormMetaData().put(OPENMRS_CASEID_ATTR_NAME, fs.entityId());
			en.put(createTBFollowupEncounterAndObs(fs));
		}

		pd.put("patient", patient);

		pd.put("encounter", en);
		
		JSONObject j = new JSONObject();
		j.put("patientData", pd);
		
		mainObj.put(j);
					
		json.put("MainObj",mainObj);
		
		return json;
    }
    
    private int fillEncounterAndFormDetailsObs(FormSubmission fs, JSONObject encounter, JSONArray obarr, int indexObsStart) throws JSONException{
    	//Form details in OBS
    	encounter.put(FormField.ENCOUNTER_LOCATION.OMR_FIELD(), FormField.ENCOUNTER_LOCATION.SRP_VALUE(fs));
		encounter.put(FormField.ENCOUNTER_DATE.OMR_FIELD(), FormField.ENCOUNTER_DATE.SRP_VALUE(fs)+" 00:00:00");
		encounter.put(FormField.FORM_CREATOR.OMR_FIELD(), "daemon");

		return indexObsStart;
    }
    
    private JSONObject createTBRegistrationEncounterAndObs(FormSubmission fs) throws JSONException{
		JSONObject encounter = new JSONObject();
		encounter.put(FormField.TB_REG_FORM_TYPE.OMR_FIELD(), FormField.TB_REG_FORM_TYPE.DEFAULT_VALUE());
		//encounter.put(FormField.TB_REG_FORM_NAME.OMR_FIELD(), FormField.TB_REG_FORM_NAME.DEFAULT_VALUE());

		//Fill other encounter and Form details in the end so that observations`s data comes in the end....
		int i = 1;
		JSONArray obarr = new JSONArray();

		FormField.WEIGHT.createConceptObs(obarr, fs, i++, null);
		FormField.HEIGHT.createConceptObs(obarr, fs, i++, null);
		FormField.SMEAR.createCodedObsWith(obarr, fs, i++, null);
		FormField.PATIENT_TYPE.createCodedObsWith(obarr, fs, i++, null);
		FormField.RESISTANCE_TYPE.createCodedObsWith(obarr, fs, i++, null);
		FormField.RESISTANCE_DRUGS.createCodedObsWith(obarr, fs, i++, null);
		FormField.RISK_FACTORS.createCodedObsWith(obarr, fs, i++, null);
		
		// must get i so tht obs ids donot get repeated
		i = fillEncounterAndFormDetailsObs(fs, encounter, obarr, i);
		
		encounter.put("obs", obarr);
		
		return encounter;
	}
	private JSONObject createTBFollowupEncounterAndObs(FormSubmission fs) throws JSONException{
		JSONObject encounter = new JSONObject();
		encounter.put(FormField.TB_FUP_FORM_TYPE.OMR_FIELD(), FormField.TB_FUP_FORM_TYPE.DEFAULT_VALUE());
		//encounter.put(FormField.TB_FUP_FORM_NAME.OMR_FIELD(), FormField.TB_FUP_FORM_NAME.DEFAULT_VALUE());

		int i = 1;
		JSONArray obarr = new JSONArray();
		
		// pregnancy and delivery info
		FormField.WEIGHT.createConceptObs(obarr, fs, i++, null);
		
		FormField.BMI.createConceptObs(obarr, fs, i++, null);
		FormField.SMEAR.createCodedObsWith(obarr, fs, i++, null);
		FormField.RESISTANCE_TYPE.createCodedObsWith(obarr, fs, i++, null);

		// must get i so tht obs ids donot get repeated
		i = fillEncounterAndFormDetailsObs(fs, encounter, obarr, i);

		encounter.put("obs", obarr);
		
		return encounter;
	}
	
	private String removeEndingSlash(String str){
		return str.endsWith("/")?str.substring(0, str.lastIndexOf("/")):str;
	}
	private String removeTrailingSlash(String str){
		return str.startsWith("/")?str.substring(1):str;
	}
}
