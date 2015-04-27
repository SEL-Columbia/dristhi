package org.opensrp.service.reporting;

import static org.apache.commons.lang.exception.ExceptionUtils.getFullStackTrace;

import java.text.MessageFormat;

import org.motechproject.dao.MotechJsonReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Component
public class MCTSReportDefinitionLoader implements IMCTSReportDefinitionLoader {
    private static Logger logger = LoggerFactory.getLogger(MCTSReportDefinitionLoader.class);
    private String reportDefinitionFileName;

    @Autowired
    public MCTSReportDefinitionLoader(@Value("#{opensrp['mcts-report-definition-path']}") String reportDefinitionFileName) {
        this.reportDefinitionFileName = reportDefinitionFileName;
    }

    @Override
    public MCTSReportDefinition load() throws Exception {
        try {
            MCTSReportDefinition mctsReportDefinition = (MCTSReportDefinition) new MotechJsonReader()
                    .readFromFile(reportDefinitionFileName, new TypeToken<MCTSReportDefinition>() {
                    }.getType());
            logger.info("MCTS Report definition: " + new Gson().toJson(mctsReportDefinition));
            return mctsReportDefinition;
        } catch (Exception e) {
            logger.error("Currently Running on: " + System.getProperty("user.dir"));
            logger.error(MessageFormat.format("Error loading report definition. Message: {0}", e.getMessage()));
            logger.error(getFullStackTrace(e));
            throw e;
        }
    }
}
