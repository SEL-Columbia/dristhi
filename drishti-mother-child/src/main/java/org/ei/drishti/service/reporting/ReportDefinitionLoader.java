package org.ei.drishti.service.reporting;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.apache.commons.lang.exception.ExceptionUtils.getFullStackTrace;

@Component
public class ReportDefinitionLoader implements IReportDefinitionLoader {
    private static Logger logger = LoggerFactory.getLogger(ReportDefinitionLoader.class);
    private String reportDefinitionFileName;

    @Autowired
    public ReportDefinitionLoader(@Value("#{drishti['report-definition-path']}") String reportDefinitionFileName) {
        this.reportDefinitionFileName = reportDefinitionFileName;
    }

    private String load(File file) throws IOException {
        try {
            FileInputStream inputStream = new FileInputStream(file);
            return IOUtils.toString(inputStream, "UTF-8");
        } catch (Exception e) {
            logger.error(String.format("Error reading report definition file. Message: {0}", e.getMessage()));
            logger.error(getFullStackTrace(e));
            throw e;
        }
    }

    @Override
    public ReportDefinition reportDefintion() throws Exception {
        try {
            File file = new File(reportDefinitionFileName);
            String indicatorDefinitionJSON = null;
            indicatorDefinitionJSON = load(file);
            return new Gson().fromJson(indicatorDefinitionJSON, ReportDefinition.class);
        } catch (Exception e) {
            logger.error(String.format("Error loading report definition. Message: {0}", e.getMessage()));
            logger.error(getFullStackTrace(e));
            throw e;
        }
    }
}
