package org.ei.drishti.reporting.controller;

import org.ei.drishti.dto.aggregatorResponse.AggregatorResponseDTO;
import org.ei.drishti.dto.report.AggregatedReportsDTO;
import org.ei.drishti.reporting.service.AggregateReportsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AggregatedReportController {
    private final AggregateReportsService aggregateReportsService;

    @Autowired
    public AggregatedReportController(AggregateReportsService aggregateReportsService) {
        this.aggregateReportsService = aggregateReportsService;
    }

    @ResponseBody
    @RequestMapping(value = "/report/aggregated-reports", method = RequestMethod.GET)
    public AggregatedReportsDTO get(@RequestParam("anm-id") String anmIdentifier, @RequestParam("month") int month, @RequestParam("year") int year) {
        List<AggregatorResponseDTO> aggregatorResponseDTO = aggregateReportsService.getAggregatedReports(anmIdentifier, month, year);
        Map<String, Integer> indicatorSummary = new HashMap<>();
        for (AggregatorResponseDTO responseDTO : aggregatorResponseDTO) {
            indicatorSummary.put(responseDTO.indicator(), responseDTO.count());
        }
        return new AggregatedReportsDTO(indicatorSummary);
    }
}
