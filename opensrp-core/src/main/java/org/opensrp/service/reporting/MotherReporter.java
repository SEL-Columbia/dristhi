package org.opensrp.service.reporting;

import static org.opensrp.common.AllConstants.CommonFormFields.SUBMISSION_DATE_FIELD_NAME;

import org.opensrp.common.domain.Indicator;
import org.opensrp.domain.Location;
import org.opensrp.domain.Mother;
import org.opensrp.repository.AllMothers;
import org.opensrp.util.SafeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MotherReporter implements IReporter {

    private MotherReportingService motherReportingService;
    private AllMothers allMothers;

    @Autowired
    public MotherReporter(MotherReportingService motherReportingService, AllMothers allMothers) {
        this.motherReportingService = motherReportingService;
        this.allMothers = allMothers;
    }

    @Override
    public void report(String entityId, String reportIndicator, Location location, String serviceProvidedDate, SafeMap reportData) {
        Mother mother = allMothers.findByCaseId(entityId);
        motherReportingService.reportToBoth(mother, Indicator.from(reportIndicator), serviceProvidedDate, reportData.get(SUBMISSION_DATE_FIELD_NAME), location);
    }

}
