package org.ei.drishti.service.reporting;

import org.ei.drishti.domain.Location;
import org.ei.drishti.form.domain.FormSubmission;
import org.ei.drishti.service.reporting.rules.IRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.List;

import static org.apache.commons.lang.exception.ExceptionUtils.getFullStackTrace;

@Component
public class FormSubmissionReportService {
    private static Logger logger = LoggerFactory.getLogger(FormSubmissionReportService.class);

    private IRulesFactory rulesFactory;
    private ILocationLoader locationLoader;
    private IReporterFactory reporterFactory;
    private IReportDefinitionLoader reportDefinitionLoader;

    @Autowired
    public FormSubmissionReportService(ILocationLoader locationLoader, IRulesFactory rulesFactory, IReporterFactory reporterFactory, IReportDefinitionLoader reportDefinitionLoader) {
        this.locationLoader = locationLoader;
        this.rulesFactory = rulesFactory;
        this.reporterFactory = reporterFactory;
        this.reportDefinitionLoader = reportDefinitionLoader;
    }

    public void reportFor(FormSubmission submission) throws Exception {
        ReportDefinition reportDefinition = reportDefinitionLoader.reportDefintion();

        List<ReportIndicator> reportIndicators = reportDefinition.getIndicatorsByFormName(submission.formName());
        for (ReportIndicator reportIndicator : reportIndicators) {
            List<String> formFields = reportIndicator.formFields();
            ReferenceData referenceData = reportIndicator.referenceData();
            List<String> rules = reportIndicator.reportingRules();
            boolean didAllRulesSucceed = processRules(submission, rules, formFields, referenceData);
            if (didAllRulesSucceed) {
                Location location = locationLoader.loadLocationFor(reportIndicator.bindType(), submission.entityId());
                report(submission, reportIndicator.indicator(), reportIndicator.bindType(), location);
            }
        }
    }

    private void report(FormSubmission submission, String reportIndicator, String bindType, Location location) {
        IReporter reporter = reporterFactory.reporterFor(bindType);
        reporter.report(submission, reportIndicator, location);
    }

    private boolean processRules(FormSubmission submission, List<String> rules, List<String> formFields, ReferenceData referenceData) {
        boolean didRuleSucceed = true;
        try {
            for (String ruleName : rules) {
                IRule rule = rulesFactory.ruleByName(ruleName);
                didRuleSucceed = rule.apply(submission, formFields, referenceData);
                if (!didRuleSucceed) break;
            }
        } catch (Exception e) {
            logger.error(MessageFormat.format("Exception while applying rules. Message: {0}", e.getMessage()));
            logger.error(getFullStackTrace(e));
            e.printStackTrace();
        }
        return didRuleSucceed;
    }
}
