package org.ei.drishti.service.reporting;

import org.ei.drishti.common.AllConstants;
import org.ei.drishti.domain.Location;
import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.service.reporting.rules.IReferenceDataRepository;
import org.ei.drishti.service.reporting.rules.IRule;
import org.ei.drishti.util.SafeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.List;

import static org.apache.commons.lang.exception.ExceptionUtils.getFullStackTrace;
import static org.ei.drishti.common.AllConstants.CommonFormFields.SERVICE_PROVIDED_DATE;
import static org.ei.drishti.common.AllConstants.CommonFormFields.SUBMISSION_DATE_FIELD_NAME;

@Component
public class FormSubmissionReportService {
    private static Logger logger = LoggerFactory.getLogger(FormSubmissionReportService.class);

    private IRulesFactory rulesFactory;
    private ILocationLoader locationLoader;
    private IReporterFactory reporterFactory;
    private IReferenceDataRepository referenceDataRepository;
    private IReportDefinitionLoader reportDefinitionLoader;
    private ReportDefinition reportDefinition;

    @Autowired
    public FormSubmissionReportService(ILocationLoader locationLoader, IRulesFactory rulesFactory, IReporterFactory reporterFactory, IReferenceDataRepository referenceDataRepository, IReportDefinitionLoader reportDefinitionLoader) {
        this.locationLoader = locationLoader;
        this.rulesFactory = rulesFactory;
        this.reporterFactory = reporterFactory;
        this.referenceDataRepository = referenceDataRepository;
        this.reportDefinitionLoader = reportDefinitionLoader;
    }

    public void reportFor(FormSubmission submission) throws Exception {
        if (reportDefinition == null) {
            logger.info("Loading report definition.");
            reportDefinition = reportDefinitionLoader.load();
        }

        List<ReportIndicator> reportIndicators = reportDefinition.getIndicatorsByFormName(submission.formName());

        for (ReportIndicator reportIndicator : reportIndicators) {
            SafeMap reportFields = createReportFields(submission, reportIndicator);
            boolean didAllRulesSucceed = processRules(reportIndicator.reportingRules(), reportFields, reportIndicator.indicator());
            if (didAllRulesSucceed) {
                String entityId = reportIndicator.reportEntityIdField() == null
                        ? submission.entityId()
                        : submission.getField(reportIndicator.reportEntityIdField());

                Location location = locationLoader.loadLocationFor(reportIndicator.reportEntityType(), entityId);
                report(submission, reportIndicator, location);
            }
        }
    }

    private boolean processRules(List<String> rules, SafeMap reportFields, String indicator) {
        try {
            for (String ruleName : rules) {
                IRule rule = rulesFactory.ruleByName(ruleName);
                if (!rule.apply(reportFields)) {
                    return false;
                }
            }
        } catch (Exception e) {
            logger.error(MessageFormat.format("Exception while applying rules. Indicator: {0}. Message: {1}",
                    indicator, e.getMessage()));
            logger.error(getFullStackTrace(e));
            return false;
        }
        return true;
    }

    private SafeMap createReportFields(FormSubmission submission, ReportIndicator reportIndicator) {
        SafeMap reportFields = new SafeMap(submission.getFields(reportIndicator.formFields()));
        reportFields.put(SERVICE_PROVIDED_DATE, getServiceProvidedDate(submission, reportIndicator));
        if (reportIndicator.referenceData() == null) {
            return reportFields;
        }
        SafeMap referenceDataFields = referenceDataRepository.getReferenceData(submission, reportIndicator.referenceData());
        reportFields.putAll(referenceDataFields);

        return reportFields;
    }

    private String getServiceProvidedDate(FormSubmission submission, ReportIndicator reportIndicator) {
        return reportIndicator.serviceProvidedDateField() == null
                ? submission.getField(SUBMISSION_DATE_FIELD_NAME)
                : submission.getField(reportIndicator.serviceProvidedDateField());
    }

    private void report(FormSubmission submission, ReportIndicator reportIndicator, Location location) {
        IReporter reporter = reporterFactory.reporterFor(reportIndicator.reportEntityType());
        SafeMap reportData = createReportData(submission, reportIndicator.formFields(), reportIndicator.quantityField());
        String serviceProvidedDate = getServiceProvidedDate(submission, reportIndicator);
        String reportEntityId = reportIndicator.reportEntityIdField() == null
                ? submission.entityId()
                : submission.getField(reportIndicator.reportEntityIdField());
        reporter.report(reportEntityId, reportIndicator.indicator(), location, serviceProvidedDate, reportData);
    }

    private SafeMap createReportData(FormSubmission submission, List<String> formFields, String quantityField) {
        SafeMap safeMap = new SafeMap(submission.getFields(formFields));
        safeMap.put(SUBMISSION_DATE_FIELD_NAME, submission.getField(SUBMISSION_DATE_FIELD_NAME));
        if (quantityField != null) {
            safeMap.put(AllConstants.ReportDataParameters.QUANTITY, submission.getField(quantityField));
        }
        return safeMap;
    }
}
