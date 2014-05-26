package org.ei.drishti.service.reporting;


import org.ei.drishti.common.util.IntegerUtil;
import org.ei.drishti.domain.MCTSReport;
import org.ei.drishti.domain.MCTSServiceCode;
import org.ei.drishti.repository.AllMCTSReports;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MCTSReporter {

    public static final int DEFAULT_MCTS_REPORT_DELAY = 10;

    private AllMCTSReports reports;
    private String mctsReportDelay;

    @Autowired
    public MCTSReporter(AllMCTSReports reports, @Value("#{drishti['mcts-report-delay-in-days']}") String mctsReportDelay) {
        this.reports = reports;
        this.mctsReportDelay = mctsReportDelay;
    }

    public void report(String entityId, String thayiCardNumber, String indicator,
                       String registrationDate, String serviceProvidedDate) {
        MCTSReport report = new MCTSReport(entityId,
                MCTSServiceCode.valueOf(indicator).messageFor(thayiCardNumber, LocalDate.parse(serviceProvidedDate)),
                registrationDate, serviceProvidedDate, getSendDate(LocalDate.parse(serviceProvidedDate)));
        reports.add(report);
    }

    private String getSendDate(LocalDate serviceProvidedDate) {
        int delay = IntegerUtil.tryParse(mctsReportDelay, DEFAULT_MCTS_REPORT_DELAY);
        return serviceProvidedDate.plusDays(delay).toString();
    }
}
