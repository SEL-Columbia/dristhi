package org.ei.drishti.form.service;

import com.google.gson.Gson;
import org.ei.drishti.dto.form.FormSubmissionDTO;
import org.ei.drishti.form.domain.FormInstance;
import org.ei.drishti.form.domain.FormSubmission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;

import static java.lang.Long.parseLong;
import static java.lang.String.valueOf;

public class FormSubmissionConverter {
    private static Logger logger = LoggerFactory.getLogger(FormSubmissionConverter.class.toString());

    public static FormSubmissionDTO from(FormSubmission formSubmission) {
        return new FormSubmissionDTO(formSubmission.anmId(), formSubmission.instanceId(), formSubmission.entityId(), formSubmission.formName(),
                new Gson().toJson(formSubmission.instance()), valueOf(formSubmission.clientVersion()), formSubmission.formDataDefinitionVersion())
                .withServerVersion(valueOf(formSubmission.serverVersion()));
    }

    public static FormSubmission toFormSubmission(FormSubmissionDTO formSubmissionDTO) {
        try {
            FormInstance formInstance = new Gson().fromJson(formSubmissionDTO.instance(), FormInstance.class);
            return new FormSubmission(formSubmissionDTO.anmId(), formSubmissionDTO.instanceId(), formSubmissionDTO.formName(), formSubmissionDTO.entityId(),
                    formSubmissionDTO.formDataDefinitionVersion(), parseLong(formSubmissionDTO.clientVersion()), formInstance);
        } catch (Exception e) {
            logger.error(MessageFormat.format("Converting Form Submission :{0}, failed with error: {1}.", formSubmissionDTO, e));
            throw e;
        }
    }

    public static FormSubmission toFormSubmissionWithVersion(FormSubmissionDTO formSubmissionDTO) {
        return new FormSubmission(formSubmissionDTO.anmId(), formSubmissionDTO.instanceId(), formSubmissionDTO.formName(),
                formSubmissionDTO.entityId(), parseLong(formSubmissionDTO.clientVersion()), formSubmissionDTO.formDataDefinitionVersion(), new Gson().fromJson(formSubmissionDTO.instance(), FormInstance.class),
                parseLong(formSubmissionDTO.serverVersion()));
    }
}
