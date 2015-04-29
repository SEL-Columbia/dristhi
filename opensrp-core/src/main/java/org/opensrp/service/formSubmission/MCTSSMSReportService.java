package org.opensrp.service.formSubmission;

import static java.text.MessageFormat.format;
import static org.apache.commons.lang.exception.ExceptionUtils.getFullStackTrace;

import java.util.List;

import org.opensrp.domain.MCTSReport;
import org.opensrp.repository.AllMCTSReports;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MCTSSMSReportService {
    private static Logger logger = LoggerFactory.getLogger(MCTSSMSReportService.class.toString());
    private AllMCTSReports mctsReportRepository;

    @Autowired
    public MCTSSMSReportService(AllMCTSReports mctsReportRepository) {
        this.mctsReportRepository = mctsReportRepository;
    }

    public List<MCTSReport> fetch(String date) throws Exception {
        try {
            return mctsReportRepository.allReportsToBeSentAsOf(date);
        } catch (Exception e) {
            logger.error(format("Fetching MCTS Reports for Date: {0} failed with exception : {1}",
                    date, getFullStackTrace(e)));
            throw e;
        }
    }

    public void markReportAsSent(MCTSReport report) {
        mctsReportRepository.markReportAsSent(report);
    }
}

