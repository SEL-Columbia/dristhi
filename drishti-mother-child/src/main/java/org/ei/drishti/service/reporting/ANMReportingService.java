package org.ei.drishti.service.reporting;

import ch.lambdaj.collection.LambdaList;
import ch.lambdaj.function.convert.Converter;
import com.google.gson.Gson;
import org.ei.drishti.common.domain.ANMIndicatorSummary;
import org.ei.drishti.common.domain.ANMReport;
import org.ei.drishti.common.domain.MonthSummary;
import org.ei.drishti.dto.ActionData;
import org.ei.drishti.dto.MonthSummaryDatum;
import org.ei.drishti.service.ActionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static ch.lambdaj.collection.LambdaCollections.with;

@Service
public class ANMReportingService {
    private ActionService actionService;

    @Autowired
    public ANMReportingService(ActionService actionService) {
        this.actionService = actionService;
    }

    public void processReports(List<ANMReport> reports) {
        actionService.deleteReportActions();
        for (ANMReport report : reports) {
            for (ANMIndicatorSummary summary : report.summaries()) {
                LambdaList<Object> monthSummaryData = with(summary.monthlySummaries()).convert(new Converter<MonthSummary, Object>() {
                    @Override
                    public MonthSummaryDatum convert(MonthSummary monthSummary) {
                        return convertToMonthSummaryDatum(monthSummary);
                    }
                });
                actionService.reportForIndicator(report.anmIdentifier(),
                        ActionData.reportForIndicator(summary.indicator(), summary.annualTarget(), new Gson().toJson(monthSummaryData)));
            }
        }
    }

    public MonthSummaryDatum convertToMonthSummaryDatum(MonthSummary monthSummary) {
        return new MonthSummaryDatum(monthSummary.month(), monthSummary.year(),
                monthSummary.currentProgress(), monthSummary.aggregatedProgress(), monthSummary.externalIDs());
    }
}
