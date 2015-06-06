package org.ei.drishti.web.controller;

import ch.lambdaj.function.convert.Converter;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static ch.lambdaj.collection.LambdaCollections.with;
import static java.text.MessageFormat.format;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
public class FormSubmissionController {
	private static Logger logger = LoggerFactory
			.getLogger(FormSubmissionController.class.toString());
	private FormSubmissionService formSubmissionService;
	private OutboundEventGateway gateway;

	@Autowired
	public FormSubmissionController(
			FormSubmissionService formSubmissionService,
			OutboundEventGateway gateway) {
		this.formSubmissionService = formSubmissionService;
		this.gateway = gateway;
	}

	@RequestMapping(method = GET, value = "/form-submissions")
	@ResponseBody
	private List<FormSubmissionDTO> getNewSubmissionsForANM(
			@RequestParam("anm-id") String anmIdentifier,
			@RequestParam("timestamp") Long timeStamp,
			@RequestParam(value = "batch-size", required = false) Integer batchSize) {
		List<FormSubmission> newSubmissionsForANM = formSubmissionService
				.getNewSubmissionsForANM(anmIdentifier, timeStamp, batchSize);
		// logger.info("Hello 1++++++++++++++++++++++++++++++++++++++"+newSubmissionsForANM.size()+"---------------");
		// FormSubmission formSubmission = newSubmissionsForANM.get(0);
		// logger.info(formSubmission.entityId()+"Hello 2++++++++++++++++++++++++++++++++++++++"+formSubmission.formName()
		// +"***********"+formSubmission.getField("isConsultDoctor"));

		return with(newSubmissionsForANM).convert(
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
		try {
			if (formSubmissionsDTO.isEmpty()) {
				return new ResponseEntity<>(BAD_REQUEST);
			}

			logger.info(formSubmissionsDTO.size() + "-----------");

			for (Iterator iterator = formSubmissionsDTO.iterator(); iterator
					.hasNext();) {
				FormSubmissionDTO formSubmission2 = (FormSubmissionDTO) iterator
						.next();

				JSONObject json = new JSONObject(formSubmission2.instance());
				logger.info("++++++++++++++++" + json.getString("formName"));
				JSONObject formInstanceJson = json
						.getJSONObject("formInstance");
				JSONArray infoArray = formInstanceJson.getJSONObject("form")
						.getJSONArray("fields");
				String result = infoArray.toString();
				logger.info("String ++++++++++++++++" + result);
				for (int i = 0; i < infoArray.length(); i++) {

					JSONObject jsonObject = infoArray.getJSONObject(i);
					if (jsonObject.getString("name").equals("wifeName")) {
						logger.info("Wife Name+++++++++"
								+ jsonObject.getString("value"));
					}

					/*
					 * if (formSubmission2.formName().equals("anc_visit") &&
					 * formSubmission2
					 * .getField("isConsultDoctor").equals("yes")) {
					 */
					// logger.info("form name++++++" +
					// formSubmissionsDTO.formName());
					// String formName = formSubmissionsDTO.formName();
					// String entityId = formSubmissionsDTO.entityId();
					// String anmid = formSubmissionsDTO.anmId();
					// String entityEcId = formSubmissionsDTO.getField("ecId");
					// //formSubmissionService.insertDatas(entityId,entityEcId,anmid,formName);
					// }
					// logger.info("started print into table");
					// // List<FormSubmission> newSubmissionsForAN =
					// formSubmissionService
					// // .insertData(formSubmission2);
					//
					// }
					// }
					//
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