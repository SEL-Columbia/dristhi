package org.ei.drishti.form.service;

import ch.lambdaj.function.convert.Converter;
import org.ei.drishti.common.util.DateUtil;
import org.ei.drishti.dto.form.FormSubmissionDTO;
import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.form.repository.AllFormSubmissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

import static ch.lambdaj.collection.LambdaCollections.with;
import static java.text.MessageFormat.format;
import static java.util.Collections.sort;

@Service
public class FormSubmissionService {
    private static Logger logger = LoggerFactory.getLogger(FormSubmissionService.class.toString());
    private AllFormSubmissions allFormSubmissions;

    @Autowired
    public FormSubmissionService(AllFormSubmissions allFormSubmissions) {
        this.allFormSubmissions = allFormSubmissions;
    }

    public List<FormSubmissionDTO> fetch(long formFetchToken) {
        return with(allFormSubmissions.findByServerVersion(formFetchToken)).convert(new Converter<FormSubmission, FormSubmissionDTO>() {
            @Override
            public FormSubmissionDTO convert(FormSubmission submission) {
                return FormSubmissionConvertor.from(submission);
            }
        });
    }

    public List<FormSubmission> getNewSubmissionsForANM(String anmIdentifier, Long version) {
        return allFormSubmissions.findByANMIDAndServerVersion(anmIdentifier, version);
    }

    public void submit(List<FormSubmissionDTO> formSubmissionsDTO) {
        List<FormSubmission> formSubmissions = with(formSubmissionsDTO).convert(new Converter<FormSubmissionDTO, FormSubmission>() {
            @Override
            public FormSubmission convert(FormSubmissionDTO submission) {
                return FormSubmissionConvertor.toFormSubmission(submission);
            }
        });

        sort(formSubmissions, timeStampComparator());
        for (FormSubmission submission : formSubmissions) {
            if (allFormSubmissions.exists(submission.instanceId())) {
                logger.warn(format("Received form submission that already exists. Skipping. Submission: {0}", submission));
                continue;
            }
            logger.info(format("Saving form with instance Id: {0} and for entity Id: {1}",
                    submission.instanceId(), submission.entityId()));
            submission.setServerVersion(DateUtil.millis());
            allFormSubmissions.add(submission);
        }
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
}
