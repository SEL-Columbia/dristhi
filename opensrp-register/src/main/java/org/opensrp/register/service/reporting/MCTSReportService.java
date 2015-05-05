package org.opensrp.register.service.reporting;

import org.opensrp.form.domain.FormSubmission;
import org.opensrp.util.SafeMap;
import org.opensrp.register.service.reporting.IMCTSReportDefinitionLoader;
import org.opensrp.service.reporting.IMCTSReportService;
import org.opensrp.service.reporting.IRulesFactory;
import org.opensrp.register.service.reporting.MCTSReportDefinition;
import org.opensrp.register.service.reporting.MCTSReportIndicator;
import org.opensrp.service.reporting.rules.IReferenceDataRepository;
import org.opensrp.service.reporting.rules.IRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;

import static org.apache.commons.lang.exception.ExceptionUtils.getFullStackTrace;
import static org.opensrp.common.AllConstants.ANCFormFields.REGISTRATION_DATE;
import static org.opensrp.common.AllConstants.ANCFormFields.THAYI_CARD_NUMBER;
import static org.opensrp.common.AllConstants.ChildRegistrationFormFields.THAYI_CARD;
import static org.opensrp.common.AllConstants.CommonFormFields.SUBMISSION_DATE_FIELD_NAME;

@Service
public class MCTSReportService implements IMCTSReportService{
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
            logger.info("Loading MCTS report definition.");
            reportDefinition = reportDefinitionLoader.load();
        }

        List<MCTSReportIndicator> reportIndicators = reportDefinition.getIndicatorsByFormName(submission.formName());

        for (MCTSReportIndicator reportIndicator : reportIndicators) {
            SafeMap reportFields = createReportFields(submission, reportIndicator);
            boolean didAllRulesSucceed = processRules(reportIndicator.reportingRules(), reportFields, reportIndicator.indicator());
            if (didAllRulesSucceed) {
                logger.info(MessageFormat.format("Sending MCTS Report for indicator: {0} for entity with id {1} ", reportIndicator, submission.entityId()));
                reportToMCTS(submission.entityId(), reportFields, reportIndicator, getServiceProvidedDate(submission, reportIndicator));
            }
        }
    }

    private void reportToMCTS(String entityId, SafeMap reportFields,
                              MCTSReportIndicator reportIndicator, String serviceProvidedDate) {
        reporter.report(
                entityId,
                reportFields.get(THAYI_CARD_NUMBER),
                reportIndicator.indicator(),
                reportFields.get(REGISTRATION_DATE), serviceProvidedDate);
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

        SafeMap referenceDataFields = referenceDataRepository.getReferenceData(submission, reportIndicator.referenceData());
        reportData.putAll(referenceDataFields);

        if (!reportData.has(THAYI_CARD_NUMBER))
            reportData.put(THAYI_CARD_NUMBER, referenceDataFields.get(THAYI_CARD));

        return reportData;
    }

    private String getServiceProvidedDate(FormSubmission submission, MCTSReportIndicator reportIndicator) {
        return reportIndicator.serviceProvidedDateField() == null
                ? submission.getField(SUBMISSION_DATE_FIELD_NAME)
                : submission.getField(reportIndicator.serviceProvidedDateField());
    }
}
