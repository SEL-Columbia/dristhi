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
import java.util.Map;

import static org.apache.commons.lang.exception.ExceptionUtils.getFullStackTrace;
import static org.ei.drishti.common.AllConstants.CommonFormFields.SUBMISSION_DATE_FIELD_NAME;

@Component
public class FormSubmissionReportService {
    private static Logger logger = LoggerFactory.getLogger(FormSubmissionReportService.class);

    private IRulesFactory rulesFactory;
    private ILocationLoader locationLoader;
    private IReporterFactory reporterFactory;
    private IReferenceDataRepository referenceDataRepository;
    private IReportDefinitionLoader reportDefinitionLoader;

    @Autowired
    public FormSubmissionReportService(ILocationLoader locationLoader, IRulesFactory rulesFactory, IReporterFactory reporterFactory, IReferenceDataRepository referenceDataRepository, IReportDefinitionLoader reportDefinitionLoader) {
        this.locationLoader = locationLoader;
        this.rulesFactory = rulesFactory;
        this.reporterFactory = reporterFactory;
        this.referenceDataRepository = referenceDataRepository;
        this.reportDefinitionLoader = reportDefinitionLoader;
    }

    public void reportFor(FormSubmission submission) throws Exception {
        //#TODO: Cache the report definition, it need not be loaded for every form submission
        ReportDefinition reportDefinition = reportDefinitionLoader.load();
        List<ReportIndicator> reportIndicators = reportDefinition.getIndicatorsByFormName(submission.formName());

        for (ReportIndicator reportIndicator : reportIndicators) {
            SafeMap reportFields = createReportFields(submission, reportIndicator.formFields(), reportIndicator.referenceData());
            boolean didAllRulesSucceed = processRules(reportIndicator.reportingRules(), reportFields);
            if (didAllRulesSucceed) {
                String entityId = reportIndicator.reportEntityIdField() == null
                        ? submission.entityId()
                        : submission.getField(reportIndicator.reportEntityIdField());

                Location location = locationLoader.loadLocationFor(reportIndicator.reportEntityType(), entityId);
                report(submission, reportIndicator, location);
            }
        }
    }

    private boolean processRules(List<String> rules, SafeMap reportFields) {
        try {
            for (String ruleName : rules) {
                IRule rule = rulesFactory.ruleByName(ruleName);
                if (!rule.apply(reportFields)) {
                    return false;
                }
            }
        } catch (Exception e) {
            logger.error(MessageFormat.format("Exception while applying rules. Message: {0}", e.getMessage()));
            logger.error(getFullStackTrace(e));
            return false;
        }
        return true;
    }

    private SafeMap createReportFields(FormSubmission submission, List<String> formFields, ReferenceData referenceDataDefinition) {
        Map<String, String> formFieldsMap = submission.getFields(formFields);
        if (referenceDataDefinition == null) {
            return new SafeMap(formFieldsMap);
        }
        return referenceDataRepository
                .getReferenceData(submission, referenceDataDefinition)
                .concatenate(formFieldsMap);
    }

    private void report(FormSubmission submission, ReportIndicator reportIndicator, Location location) {
        IReporter reporter = reporterFactory.reporterFor(reportIndicator.reportEntityType());
        SafeMap reportData = createReportData(submission, reportIndicator.formFields(), reportIndicator.quantityField());
        String serviceProvidedDate = reportIndicator.serviceProvidedDateField() == null
                ? submission.getField(SUBMISSION_DATE_FIELD_NAME)
                : submission.getField(reportIndicator.serviceProvidedDateField());
        String reportEntityId = reportIndicator.reportEntityIdField() == null
                ? submission.entityId()
                : submission.getField(reportIndicator.reportEntityIdField());
        reportData.put("serviceProvidedDate", serviceProvidedDate);
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
