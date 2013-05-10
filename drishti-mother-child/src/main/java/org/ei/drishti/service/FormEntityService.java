package org.ei.drishti.service;

import com.google.gson.Gson;
import org.ei.drishti.domain.FormExportToken;
import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.repository.AllFormExportTokens;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

import static java.lang.String.valueOf;
import static java.text.MessageFormat.format;
import static java.util.Collections.sort;
import static org.ei.drishti.common.AllConstants.Form.*;
import static org.ei.drishti.util.EasyMap.create;

@Service
public class FormEntityService {
    private static Logger logger = LoggerFactory.getLogger(FormEntityService.class.toString());
    private ZiggyService ziggyService;
    private AllFormExportTokens allFormExportTokens;

    @Autowired
    public FormEntityService(ZiggyService ziggyService, AllFormExportTokens allFormExportTokens) {
        this.ziggyService = ziggyService;
        this.allFormExportTokens = allFormExportTokens;
    }

    public void process(List<FormSubmission> formSubmissions) {
        sort(formSubmissions, serverVersionComparator());
        FormExportToken exportToken = allFormExportTokens.getAll().get(0);
        for (FormSubmission submission : formSubmissions) {
            String params = getParams(submission);
            logger.info(format("Invoking save for form with instance Id: {0} and for entity Id: {1}",
                    submission.instanceId(), submission.entityId()));
            ziggyService.saveForm(params, new Gson().toJson(submission.instance()));
            allFormExportTokens.update(exportToken.withVersion(submission.serverVersion()));
        }
    }

    private String getParams(FormSubmission formSubmission) {
        return new Gson().toJson(create(ANM_ID, formSubmission.anmId())
                .put(INSTANCE_ID, formSubmission.instanceId())
                .put(ENTITY_ID, formSubmission.entityId())
                .put(FORM_NAME, formSubmission.formName())
                .put(TIME_STAMP, valueOf(formSubmission.timestamp()))
                .put(SERVER_VERSION, valueOf(formSubmission.serverVersion()))
                .map());
    }

    private Comparator<FormSubmission> serverVersionComparator() {
        return new Comparator<FormSubmission>() {
            public int compare(FormSubmission firstSubmission, FormSubmission secondSubmission) {
                long firstTimestamp = firstSubmission.serverVersion();
                long secondTimestamp = secondSubmission.serverVersion();
                return firstTimestamp == secondTimestamp ? 0 : firstTimestamp < secondTimestamp ? -1 : 1;
            }
        };
    }
}
