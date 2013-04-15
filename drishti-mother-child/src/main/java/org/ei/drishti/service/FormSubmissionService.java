package org.ei.drishti.service;

import com.google.gson.Gson;
import org.ei.drishti.dto.FormSubmission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

import static java.text.MessageFormat.format;
import static java.util.Collections.sort;
import static org.ei.drishti.common.AllConstants.Form.*;
import static org.ei.drishti.common.AllConstants.Form.TIME_STAMP;
import static org.ei.drishti.util.EasyMap.create;

@Service
public class FormSubmissionService {
    private static Logger logger = LoggerFactory.getLogger(FormSubmissionService.class.toString());
    private DFLService dflService;

    @Autowired
    public FormSubmissionService(DFLService dflService) {
        this.dflService = dflService;
    }

    public void processSubmissions(List<FormSubmission> formSubmissions) {
        sort(formSubmissions, timeStampComparator());
        for (FormSubmission submission : formSubmissions) {
            String params = getParams(submission);
            logger.info(format("Invoking save form for with params: {0} and instance: {1}", params, submission.instance()));
            dflService.saveForm(params, submission.instance());
        }
    }

    private String getParams(FormSubmission formSubmission) {
        return new Gson().toJson(create(ANM_ID, formSubmission.anmId())
                .put(INSTANCE_ID, formSubmission.instanceId())
                .put(ENTITY_ID, formSubmission.entityId())
                .put(FORM_NAME, formSubmission.formName())
                .put(TIME_STAMP, String.valueOf(formSubmission.timeStamp())));
    }

    private Comparator<FormSubmission> timeStampComparator() {
        return new Comparator<FormSubmission>() {
            public int compare(FormSubmission firstSubmission, FormSubmission secondSubmission) {
                return firstSubmission.timeStamp() == secondSubmission.timeStamp() ? 0 :
                        firstSubmission.timeStamp() < secondSubmission.timeStamp() ? -1 : 1;
            }
        };
    }
}
