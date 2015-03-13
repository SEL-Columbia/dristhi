package org.opensrp.register.service.reporting;

import ch.lambdaj.collection.LambdaList;
import ch.lambdaj.function.convert.Converter;
import com.google.gson.Gson;
import org.opensrp.common.domain.ANMIndicatorSummary;
import org.opensrp.common.domain.ANMReport;
import org.opensrp.common.domain.MonthSummary;
import org.opensrp.dto.ActionData;
import org.opensrp.dto.MonthSummaryDatum;
import org.opensrp.register.service.ActionService;
import org.opensrp.service.reporting.IProviderReporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static ch.lambdaj.collection.LambdaCollections.with;

@Service
public class ANMReportingService implements IProviderReporter{
    private ActionService actionService;
    private MotherReportingService motherReportingService;
    private ChildReportingService childReportingService;

    @Autowired
    public ANMReportingService(ActionService actionService, MotherReportingService motherReportingService, ChildReportingService childReportingService) {
        this.actionService = actionService;
        this.motherReportingService = motherReportingService;
        this.childReportingService = childReportingService;
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

    private MonthSummaryDatum convertToMonthSummaryDatum(MonthSummary monthSummary) {
        return new MonthSummaryDatum(monthSummary.month(), monthSummary.year(),
                monthSummary.currentProgress(), monthSummary.aggregatedProgress(), monthSummary.externalIDs());
    }

    public void reportFromEntityData() {
        childReportingService.reportInfantAndChildBalance();
    }
}
