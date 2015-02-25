package org.opensrp.repository;

import org.opensrp.common.AllConstants;
import org.opensrp.domain.MCTSReport;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;
import org.joda.time.LocalDate;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class AllMCTSReports extends MotechBaseRepository<MCTSReport> {

    @Autowired
    public AllMCTSReports(@Qualifier(AllConstants.DRISHTI_MCTS_DATABASE_CONNECTOR) CouchDbConnector db) {
        super(MCTSReport.class, db);
    }

    @GenerateView
    public MCTSReport findByCaseId(String caseId) {
        List<MCTSReport> reports = queryView("by_caseId", caseId);
        if (reports == null || reports.isEmpty()) {
            return null;
        }
        return reports.get(0);
    }

    @View(name = "all_mcts_reports_for_a_day",
            map = "function(doc) { if (doc.type === 'MCTSReport' && doc.reportSent === 'false') { emit(doc.sendDate); } }")
    public List<MCTSReport> allReportsToBeSentAsOf(String date) {
        String startDate = LocalDate.fromDateFields(new Date(0)).toString();
        return db.queryView(createQuery("all_mcts_reports_for_a_day")
                .startKey(startDate)
                .endKey(date)
                .includeDocs(true), MCTSReport.class);
    }

    public void markReportAsSent(MCTSReport report)  {
        this.update(report.markReportAsSent());
    }
}
