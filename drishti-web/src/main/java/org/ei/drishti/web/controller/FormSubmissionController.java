package org.ei.drishti.web.controller;

import static ch.lambdaj.collection.LambdaCollections.with;
import static ch.lambdaj.collection.LambdaCollections.with;
import ch.lambdaj.function.convert.Converter;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.MessageFormat;
import static java.text.MessageFormat.format;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.http.util.EntityUtils;
import org.ei.drishti.domain.Multimedia;
import org.ei.drishti.dto.form.MultimediaDTO;

import org.ei.drishti.service.MultimediaService;
import org.ei.drishti.common.util.HttpAgent;
import org.ei.drishti.common.util.HttpResponse;
import org.ei.drishti.dto.ANMVillagesDTO;
import org.ei.drishti.dto.VillagesDTO;
import org.ei.drishti.dto.form.FormSubmissionDTO;
import org.ei.drishti.event.FormSubmissionEvent;
import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.form.service.FormSubmissionConverter;
import org.ei.drishti.form.service.FormSubmissionService;
import org.ei.drishti.web.handler.FormsubmissionHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.motechproject.scheduler.gateway.OutboundEventGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

@Controller
public class FormSubmissionController {

    String isCon = null;
    private static Logger logger = LoggerFactory
            .getLogger(FormSubmissionController.class.toString());
    private FormSubmissionService formSubmissionService;
    private OutboundEventGateway gateway;
    private HttpAgent httpAgent;
    private final String drishtiANMVillagesURL;
    private FormsubmissionHandler drishtiform;

    @Autowired
    private MultimediaService multimediaService;

    @Autowired
    public FormSubmissionController(@Value("#{drishti['drishti.anm.village.url']}") String drishtiANMVillagesURL,
            HttpAgent httpAgent,
            FormsubmissionHandler drishtiform,
            FormSubmissionService formSubmissionService,
            OutboundEventGateway gateway) {
        this.formSubmissionService = formSubmissionService;
        this.gateway = gateway;
        this.drishtiANMVillagesURL = drishtiANMVillagesURL;
        this.httpAgent = httpAgent;
        this.drishtiform = drishtiform;

    }

    @RequestMapping(method = GET, value = "/form-submissions")
    @ResponseBody
    private List<FormSubmissionDTO> getNewSubmissionsForANM(
            @RequestParam("anm-id") String anmID,
            @RequestParam("timestamp") Long timeStamp,
            @RequestParam(value = "batch-size", required = false) Integer batchSize) {
        logger.info("***** from controller&&&&&");
        HttpResponse response = new HttpResponse(false, null);
        String village = "";

        List<FormSubmission> newSubmissionsForANM = null;
        List<FormSubmission> permnewSubmissionsForANM = new ArrayList<>();

        try {

            logger.info("******anm id*** " + anmID);
            logger.info("****** village URL " + drishtiANMVillagesURL);
            response = httpAgent.get(drishtiANMVillagesURL + "?anm-id=" + anmID);
            logger.info("********villages response***" + response);

            ANMVillagesDTO anmvillagesDTOs = new Gson().fromJson(response.body(),
                    new TypeToken<ANMVillagesDTO>() {
                    }.getType());
            logger.info("Fetched Villages for the ANM" + anmvillagesDTOs);
            String strvillages = anmvillagesDTOs.villages();

            String[] villageanm = strvillages.split(",");
            logger.info("anmvillages" + villageanm);

            logger.info("list of villages" + villageanm);
            for (int i = 0; i < villageanm.length; i++) {

                village = villageanm[i];
                logger.info("one village from list*******  :" + village);
                logger.info("***form-submission***" + village);
                newSubmissionsForANM = formSubmissionService
                        .getNewSubmissionsForANM(village, timeStamp, batchSize);
                logger.info("***form-submission detailes fetched***" + newSubmissionsForANM);
                permnewSubmissionsForANM.addAll(newSubmissionsForANM);
            }
        } catch (JsonSyntaxException e) {
            logger.error(MessageFormat.format("{0} occurred while fetching Village Details for anm. StackTrace: \n {1}", e));
        }

        logger.info("details of data from db********" + permnewSubmissionsForANM);

        return with(permnewSubmissionsForANM).convert(
                new Converter<FormSubmission, FormSubmissionDTO>() {
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

    @RequestMapping(headers = {"Accept=application/json"}, method = POST, value = "/form-submissions")
    public ResponseEntity<HttpStatus> submitForms(
            @RequestBody List<FormSubmissionDTO> formSubmissionsDTO) {

        try {
            if (formSubmissionsDTO.isEmpty()) {
                return new ResponseEntity<>(BAD_REQUEST);
            }
            logger.info("*****" + formSubmissionsDTO.size() + " : -----------");

            logger.info("** transfer data to handler*******");
            drishtiform.formData(formSubmissionsDTO);

            gateway.sendEventMessage(new FormSubmissionEvent(formSubmissionsDTO)
                    .toEvent());
            logger.debug(format(
                    "Added Form submissions to queue.\nSubmissions: {0}",
                    formSubmissionsDTO));
        } catch (JSONException | ParseException e) {
            logger.error(format(
                    "Form submissions processing failed with exception {0}.\nSubmissions: {1}",
                    e, formSubmissionsDTO));
            return new ResponseEntity<>(INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(CREATED);
    }

    @RequestMapping(headers = {"Accept=application/json"}, method = GET, value = "/multimedia-file")
    @ResponseBody
    public List<MultimediaDTO> getFiles(@RequestParam("anm-id") String providerId) {
        logger.info("Get Multimedia file");

        List<Multimedia> allMultimedias = multimediaService.getMultimediaFiles(providerId);

        return with(allMultimedias).convert(new Converter<Multimedia, MultimediaDTO>() {
            @Override
            public MultimediaDTO convert(Multimedia md) {
                return new MultimediaDTO(md.getCaseId(), md.getProviderId(), md.getContentType(), md.getFilePath(), md.getFileCategory());
            }
        });
    }

    @RequestMapping(headers = {"Accept=multipart/form-data"}, method = RequestMethod.POST, value = "/multimedia-file")
    public ResponseEntity<String> uploadFiles(@RequestParam("anm-id") String providerId, @RequestParam("entity-id") String entityId, @RequestParam("content-type") String contentType, @RequestParam("file-category") String fileCategory, @RequestParam("file") MultipartFile file) {
        logger.info("post request method");
        MultimediaDTO multimediaDTO = new MultimediaDTO(entityId, providerId, contentType, null, fileCategory);

        String status = multimediaService.saveMultimediaFile(multimediaDTO, file);
        String[] status1=status.split(":");
        
        if((status1[0]).equalsIgnoreCase("success")){
            return new ResponseEntity<>(new Gson().toJson(status1[1]), HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>(new Gson().toJson("fail"), HttpStatus.OK);
        }
    }
}
