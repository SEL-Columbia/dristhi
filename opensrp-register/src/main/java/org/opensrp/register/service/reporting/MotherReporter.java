package org.opensrp.register.service.reporting;

import org.opensrp.common.domain.Indicator;
import org.opensrp.common.domain.Location;
import org.opensrp.register.domain.Mother;
import org.opensrp.util.SafeMap;
import org.opensrp.register.repository.AllMothers;
import org.opensrp.service.reporting.IReporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.opensrp.common.AllConstants.CommonFormFields.SUBMISSION_DATE_FIELD_NAME;

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
