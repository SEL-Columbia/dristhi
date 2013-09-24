package org.ei.drishti.service.reporting;

import com.google.gson.reflect.TypeToken;
import org.motechproject.dao.MotechJsonReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;

import static org.apache.commons.lang.exception.ExceptionUtils.getFullStackTrace;

@Component
public class ReportDefinitionLoader implements IReportDefinitionLoader {
    private static Logger logger = LoggerFactory.getLogger(ReportDefinitionLoader.class);
    private String reportDefinitionFileName;

    @Autowired
    public ReportDefinitionLoader(@Value("#{drishti['report-definition-path']}") String reportDefinitionFileName) {
        this.reportDefinitionFileName = reportDefinitionFileName;
    }

    @Override
    public ReportDefinition load() throws Exception {
        try {
            return (ReportDefinition) new MotechJsonReader().readFromFile(reportDefinitionFileName, new TypeToken<ReportDefinition>() {
            }.getType());
        } catch (Exception e) {
            logger.error("Currently Running on: " + System.getProperty("user.dir"));
            logger.error(MessageFormat.format("Error loading report definition. Message: {0}", e.getMessage()));
            logger.error(getFullStackTrace(e));
            throw e;
        }
    }
}
