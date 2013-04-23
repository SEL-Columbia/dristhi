package org.ei.drishti.service;

import com.google.gson.Gson;
import org.ei.drishti.domain.form.FormSubmission;
import org.ei.drishti.repository.AllFormSubmissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

import static java.text.MessageFormat.format;
import static java.util.Collections.sort;
import static org.ei.drishti.common.AllConstants.Form.*;
import static org.ei.drishti.util.EasyMap.create;

@Service
public class FormSubmissionService {
    private static Logger logger = LoggerFactory.getLogger(FormSubmissionService.class.toString());
    private ZiggyService ziggyService;
    private AllFormSubmissions allFormSubmissions;

    @Autowired
    public FormSubmissionService(ZiggyService ziggyService, AllFormSubmissions allFormSubmissions) {
        this.ziggyService = ziggyService;
        this.allFormSubmissions = allFormSubmissions;
    }

    public void processSubmissions(List<FormSubmission> formSubmissions) {
        sort(formSubmissions, timeStampComparator());
        for (FormSubmission submission : formSubmissions) {
            if (allFormSubmissions.exists(submission.instanceId())) {
                logger.warn(format("Received form submission that already exists. Skipping. Submission: {0}", submission));
                continue;
            }
            String params = getParams(submission);
            logger.info(format("Invoking save form for with params: {0} and instance: {1}", params, submission.instance()));
            ziggyService.saveForm(params, new Gson().toJson(submission.instance()));
        }
    }

    private String getParams(FormSubmission formSubmission) {
        return new Gson().toJson(create(ANM_ID, formSubmission.anmId())
                .put(INSTANCE_ID, formSubmission.instanceId())
                .put(ENTITY_ID, formSubmission.entityId())
                .put(FORM_NAME, formSubmission.formName())
                .put(TIME_STAMP, String.valueOf(formSubmission.timestamp()))
                .map());
    }

    private Comparator<FormSubmission> timeStampComparator() {
        return new Comparator<FormSubmission>() {
            public int compare(FormSubmission firstSubmission, FormSubmission secondSubmission) {
                long firstTimestamp = firstSubmission.timestamp();
                long secondTimestamp = secondSubmission.timestamp();
                return firstTimestamp == secondTimestamp ? 0 : firstTimestamp < secondTimestamp ? -1 : 1;
            }
        };
    }

    public List<FormSubmission> getNewSubmissionsForANM(String anmIdentifier, Long timeStamp) {
        return allFormSubmissions.findByANMIDAndTimeStamp(anmIdentifier, timeStamp);
    }
}

