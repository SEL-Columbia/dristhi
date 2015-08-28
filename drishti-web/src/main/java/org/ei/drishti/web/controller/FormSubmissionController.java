package org.ei.drishti.web.controller;

import static ch.lambdaj.collection.LambdaCollections.with;
import static java.text.MessageFormat.format;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.ei.drishti.common.util.HttpResponse;
import org.ei.drishti.dto.ANMVillagesDTO;
import org.ei.drishti.dto.VillagesDTO;
import org.ei.drishti.dto.form.FormSubmissionDTO;
import org.ei.drishti.event.FormSubmissionEvent;
import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.form.service.FormSubmissionConverter;
import org.ei.drishti.form.service.FormSubmissionService;
import org.json.JSONArray;
import org.json.JSONObject;
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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import ch.lambdaj.function.convert.Converter;

@Controller
public class FormSubmissionController {
	String isCon = null;
	private static Logger logger = LoggerFactory
			.getLogger(FormSubmissionController.class.toString());
	private FormSubmissionService formSubmissionService;
	private OutboundEventGateway gateway;
	 private HttpAgent httpAgent;
	 private final String drishtiANMVillagesURL;
	 private SMSController smsController;

	@Autowired
	public FormSubmissionController(@Value("#{drishti['drishti.anm.village.url']}") String drishtiANMVillagesURL,
            HttpAgent httpAgent,
            SMSController smsController,
			FormSubmissionService formSubmissionService,
			OutboundEventGateway gateway) {
		this.formSubmissionService = formSubmissionService;
		this.gateway = gateway;
		this.drishtiANMVillagesURL = drishtiANMVillagesURL;
		this.httpAgent = httpAgent;
		this.smsController = smsController;
	}

//	@RequestMapping(method = GET, value = "/form-submissions")
//	@ResponseBody
//	private List<FormSubmissionDTO> getNewSubmissionsForANM(
//			@RequestParam("anm-id") String anmIdentifier,
//			@RequestParam("timestamp") Long timeStamp,
//			@RequestParam(value = "batch-size", required = false) Integer batchSize) {
//		List<FormSubmission> newSubmissionsForANM = formSubmissionService
//				.getNewSubmissionsForANM(anmIdentifier, timeStamp, batchSize);
//
//		return with(newSubmissionsForANM).convert(
//				new Converter<FormSubmission, FormSubmissionDTO>()
//
//				{
//					@Override
//					public FormSubmissionDTO convert(FormSubmission submission) {
//						return FormSubmissionConverter.from(submission);
//					}
//				});
//	}
	
	@RequestMapping(method = GET, value = "/form-submissions")
	@ResponseBody
	private List<FormSubmissionDTO> getNewSubmissionsForANM(
			@RequestParam("anm-id") String anmIdentifier,
			@RequestParam("timestamp") Long timeStamp,
			@RequestParam(value = "batch-size", required = false) Integer batchSize) {
		logger.info("***** from controller&&&&&");
		HttpResponse response = new HttpResponse(false, null);
		List<FormSubmission> newSubmissionsForANM=null;
		List<FormSubmission> permnewSubmissionsForANM=new ArrayList<FormSubmission>();
		
		try{
			String anmID=anmIdentifier;
			logger.info("******anm id*** "+anmID);
			logger.info("****** village URL "+drishtiANMVillagesURL);
            response = httpAgent.get(drishtiANMVillagesURL + "?anm-id=" + anmID);
            logger.info("********villages response***"+response);
            
            ANMVillagesDTO anmvillagesDTOs = new Gson().fromJson(response.body(),
                    new TypeToken<ANMVillagesDTO>() {
                    }.getType());
            logger.info("Fetched Villages for the ANM"+anmvillagesDTOs);
            
          List<String> listvillages=anmvillagesDTOs.villages();
          logger.info("list of villages"+listvillages);
          Iterator<String> iterator = listvillages.iterator();
          while(iterator.hasNext()){
        	  String village=iterator.next();
        	  logger.info("one village from list*******  :"+village);
        	  
//        	  String village="kandukur";
  			logger.info("***form-submission***"+village);
  		newSubmissionsForANM = formSubmissionService
  				.getNewSubmissionsForANM(village, timeStamp, batchSize);
  		logger.info("***form-submission detailes fetched***"+newSubmissionsForANM);
  		permnewSubmissionsForANM.addAll(newSubmissionsForANM);
  		
          }
		}
		catch(Exception e){
			logger.error(MessageFormat.format("{0} occurred while fetching Village Details for anm. StackTrace: \n {1}", e));
		}
		
		logger.info("details of data from db********"+permnewSubmissionsForANM);	
			
		return with(permnewSubmissionsForANM).convert(
				new Converter<FormSubmission, FormSubmissionDTO>()

				{
					@Override
					public FormSubmissionDTO convert(FormSubmission submission) {
						return FormSubmissionConverter.from(submission);
					}
				});
	}

	@RequestMapping(method = GET, value = "/all-form-submissions")
	@ResponseBody
	private List<FormSubmissionDTO> getAllFormSubmissions(
			@RequestParam("timestamp") Long timeStamp,
			@RequestParam(value = "batch-size", required = false) Integer batchSize) {
		List<FormSubmission> allSubmissions = formSubmissionService
				.getAllSubmissions(timeStamp, batchSize);
		return with(allSubmissions).convert(
				new Converter<FormSubmission, FormSubmissionDTO>() {
					@Override
					public FormSubmissionDTO convert(FormSubmission submission) {
						return FormSubmissionConverter.from(submission);
					}
				});
	}

	@RequestMapping(headers = { "Accept=application/json" }, method = POST, value = "/form-submissions")
	public ResponseEntity<HttpStatus> submitForms(
			@RequestBody List<FormSubmissionDTO> formSubmissionsDTO) {
		String entityidEC = null;
		String village = null;
		String phnum = null;
		HttpResponse response = new HttpResponse(false, null);
		try {
			if (formSubmissionsDTO.isEmpty()) {
				return new ResponseEntity<>(BAD_REQUEST);
			}
			
			logger.info("*****"+formSubmissionsDTO.size() + " : -----------");

			Iterator<FormSubmissionDTO> itr = formSubmissionsDTO.iterator();

			while (itr.hasNext()) {
				Object object = (Object) itr.next();
				String jsonstr = object.toString();

				JSONObject dataObject = new JSONObject(jsonstr);

				String visittype = dataObject.getString("formName");
				logger.info("value of formname " + visittype);
				
				if(visittype.equalsIgnoreCase("anc_registration_oa")
						|| visittype.equalsIgnoreCase("pnc_registration_oa")
						|| visittype.equalsIgnoreCase("ec_registration")){
					 smsController.sendSMS();
				JSONArray fieldJsonArray = dataObject
						.getJSONObject("formInstance")
						.getJSONObject("form").getJSONArray("fields");
				
				for (int j = 0; j < fieldJsonArray.length(); j++) {

					JSONObject jsonObject = fieldJsonArray
							.getJSONObject(j);

													
						
					
					
					if ((jsonObject.has("name"))
							&& jsonObject.getString("name").equals("village")) {

						village = (jsonObject.has("value") && jsonObject
								.getString("value") != null) ? jsonObject
								.getString("value") : "";
						logger.info("*****village :"+village);
					}
				}
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

								logger.info(" invoking a service");
								logger.info("res2+++++" + isCon);
								logger.info("anmid+++++" + anmid);

								formSubmissionService.requestConsultationTest(
										visittype, visitentityid, entityidEC,
										anmid);

								logger.info("invoking a service method");
							}

						}
					}
				}

			}

			gateway.sendEventMessage(new FormSubmissionEvent(formSubmissionsDTO)
					.toEvent());
			logger.debug(format(
					"Added Form submissions to queue.\nSubmissions: {0}",
					formSubmissionsDTO));
		} catch (Exception e) {
			logger.error(format(
					"Form submissions processing failed with exception {0}.\nSubmissions: {1}",
					e, formSubmissionsDTO));
			return new ResponseEntity<>(INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(CREATED);
	}
}