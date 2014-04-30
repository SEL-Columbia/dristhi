package org.ei.drishti.service.reporting;

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
import static org.ei.drishti.common.AllConstants.ANCFormFields.REGISTRATION_DATE;
import static org.ei.drishti.common.AllConstants.ANCFormFields.THAYI_CARD_NUMBER;
import static org.ei.drishti.common.AllConstants.CommonFormFields.SUBMISSION_DATE_FIELD_NAME;

@Component
public class MCTSReportService {
    public static final String SUBJECT = "SEND-MCTS-REPORT";
    private static Logger logger = LoggerFactory.getLogger(MCTSReportService.class);

    private IRulesFactory rulesFactory;
    private IReferenceDataRepository referenceDataRepository;
    private IMCTSReportDefinitionLoader reportDefinitionLoader;
    private MCTSReporter reporter;
    private MCTSReportDefinition reportDefinition;

    @Autowired
    public MCTSReportService(IRulesFactory rulesFactory,
                             IReferenceDataRepository referenceDataRepository,
                             IMCTSReportDefinitionLoader reportDefinitionLoader, MCTSReporter reporter) {
        this.rulesFactory = rulesFactory;
        this.referenceDataRepository = referenceDataRepository;
        this.reportDefinitionLoader = reportDefinitionLoader;
        this.reporter = reporter;
    }

    public void reportFor(FormSubmission submission) throws Exception {
        if (reportDefinition == null) {
            logger.info("Loading report definition.");
            reportDefinition = reportDefinitionLoader.load();
        }

        List<MCTSReportIndicator> reportIndicators = reportDefinition.getIndicatorsByFormName(submission.formName());

        for (MCTSReportIndicator reportIndicator : reportIndicators) {
            SafeMap reportFields = createReportFields(submission, reportIndicator);
            SafeMap referenceDataFields = referenceDataRepository.getReferenceData(submission, reportIndicator.referenceData());
            boolean didAllRulesSucceed = processRules(reportIndicator.reportingRules(), reportFields, reportIndicator.indicator());
            if (didAllRulesSucceed) {
                reportToMCTS(submission.entityId(), referenceDataFields, reportIndicator, getServiceProvidedDate(submission, reportIndicator));
            }
        }
    }

    private void reportToMCTS(String entityId, SafeMap referenceDataFields,
                              MCTSReportIndicator reportIndicator, String serviceProvidedDate) {
        reporter.report(
                entityId,
                referenceDataFields.get(THAYI_CARD_NUMBER),
                reportIndicator.indicator(),
                referenceDataFields.get(REGISTRATION_DATE), serviceProvidedDate);
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

    private SafeMap createReportFields(FormSubmission submission, MCTSReportIndicator reportIndicator) {
        SafeMap reportData = new SafeMap(submission.getFields(reportIndicator.formFields()));
        reportData.put(SUBMISSION_DATE_FIELD_NAME, submission.getField(SUBMISSION_DATE_FIELD_NAME));
        return reportData;
    }

    private String getServiceProvidedDate(FormSubmission submission, MCTSReportIndicator reportIndicator) {
        return reportIndicator.serviceProvidedDateField() == null
                ? submission.getField(SUBMISSION_DATE_FIELD_NAME)
                : submission.getField(reportIndicator.serviceProvidedDateField());
    }
}
