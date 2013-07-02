package org.ei.drishti.service;

import org.ei.drishti.contract.ChildCloseRequest;
import org.ei.drishti.contract.ChildImmunizationUpdationRequest;
import org.ei.drishti.domain.Child;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.repository.AllChildren;
import org.ei.drishti.repository.AllMothers;
import org.ei.drishti.service.reporting.ChildReportingService;
import org.ei.drishti.service.scheduling.ChildSchedulesService;
import org.ei.drishti.util.SafeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static org.ei.drishti.common.AllConstants.ANCFormFields.REFERENCE_DATE;
import static org.ei.drishti.common.AllConstants.ChildBirthCommCareFields.BF_POSTBIRTH_FIELD_NAME;
import static org.ei.drishti.common.AllConstants.DeliveryOutcomeFields.DID_BREAST_FEEDING_START;
import static org.ei.drishti.common.AllConstants.Form.ID;
import static org.ei.drishti.common.AllConstants.Report.REPORT_EXTRA_DATA_KEY_NAME;

@Service
public class ChildService {
    private static Logger logger = LoggerFactory.getLogger(ChildService.class.toString());
    private ChildSchedulesService childSchedulesService;
    private AllMothers allMothers;
    private AllChildren allChildren;
    private ChildReportingService childReportingService;
    private ActionService actionService;

    @Autowired
    public ChildService(ChildSchedulesService childSchedulesService,
                        AllMothers allMothers,
                        AllChildren allChildren,
                        ChildReportingService childReportingService, ActionService actionService) {
        this.childSchedulesService = childSchedulesService;
        this.allMothers = allMothers;
        this.allChildren = allChildren;
        this.childReportingService = childReportingService;
        this.actionService = actionService;
    }

    public void registerChildren(FormSubmission submission) {
        Mother mother = allMothers.findByCaseId(submission.entityId());
        if (mother == null) {
            logger.warn("Failed to handle children registration as there is no mother registered with id: " + submission.entityId());
            return;
        }

        List<Child> children = allChildren.findByMotherId(submission.entityId());

        for (Child child : children) {
            child = child.withAnm(submission.anmId()).withDateOfBirth(submission.getField(REFERENCE_DATE)).withThayiCard(mother.thayiCardNo());
            allChildren.update(child);

            SafeMap reportingData = new SafeMap();
            reportingData.put(ID, child.caseId());
            reportingData.put(BF_POSTBIRTH_FIELD_NAME, submission.getField(DID_BREAST_FEEDING_START));
            childReportingService.registerChild(reportingData);

            childSchedulesService.enrollChild(child);
        }
    }

    @Deprecated
    public void updateChildImmunization(ChildImmunizationUpdationRequest updationRequest, Map<String, Map<String, String>> extraData) {
        if (!allChildren.childExists(updationRequest.caseId())) {
            logger.warn("Found immunization update without registered child for case ID: " + updationRequest.caseId());
            return;
        }

        List<String> previousImmunizations = allChildren.findByCaseId(updationRequest.caseId()).immunizationsProvided();

        Child updatedChild = allChildren.update(updationRequest.caseId(), extraData.get("details"));
        actionService.updateImmunizations(updationRequest.caseId(), updationRequest.anmIdentifier(), updatedChild.details(), updationRequest.immunizationsProvided(),
                updationRequest.immunizationsProvidedDate(), updationRequest.vitaminADose());

        childReportingService.immunizationProvided(new SafeMap(extraData.get(REPORT_EXTRA_DATA_KEY_NAME)), previousImmunizations);

        childSchedulesService.updateEnrollments(updationRequest);
        closeAlertsForProvidedImmunizations(updationRequest);
    }

    private void closeAlertsForProvidedImmunizations(ChildImmunizationUpdationRequest updationRequest) {
        for (String immunization : updationRequest.immunizationsProvidedList()) {
            actionService.markAlertAsClosed(updationRequest.caseId(), updationRequest.anmIdentifier(), immunization, updationRequest.immunizationsProvidedDate().toString());
        }
    }

    @Deprecated
    public void closeChildCase(ChildCloseRequest childCloseRequest, Map<String, Map<String, String>> extraData) {
        if (!allChildren.childExists(childCloseRequest.caseId())) {
            logger.warn("Found close child request without registered child for case ID: " + childCloseRequest.caseId());
            return;
        }

        allChildren.close(childCloseRequest.caseId());
        actionService.closeChild(childCloseRequest.caseId(), childCloseRequest.anmIdentifier());
        childReportingService.closeChild(new SafeMap(extraData.get(REPORT_EXTRA_DATA_KEY_NAME)));
        childSchedulesService.unenrollChild(childCloseRequest.caseId());
    }
}
