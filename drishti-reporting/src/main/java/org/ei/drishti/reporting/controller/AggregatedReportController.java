package org.ei.drishti.reporting.controller;

import org.apache.commons.lang3.StringUtils;
import org.ei.drishti.dto.LocationDTO;
import org.ei.drishti.dto.aggregatorResponse.AggregatorResponseDTO;
import org.ei.drishti.dto.report.AggregatedReportsDTO;
import org.ei.drishti.reporting.domain.Location;
import org.ei.drishti.reporting.service.ANMService;
import org.ei.drishti.reporting.service.AggregateReportsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.ei.drishti.reporting.HttpHeaderFactory.allowOrigin;
import static org.springframework.http.HttpStatus.OK;

@Controller
public class AggregatedReportController {
    private final AggregateReportsService aggregateReportsService;
    private final String drishtiSiteUrl;
    private final ANMService anmService;

    @Autowired
    public AggregatedReportController(AggregateReportsService aggregateReportsService,
                                      @Value("#{drishti['drishti.site.url']}") String drishtiSiteUrl,
                                      ANMService anmService) {
        this.aggregateReportsService = aggregateReportsService;
        this.drishtiSiteUrl = drishtiSiteUrl;
        this.anmService = anmService;
    }

    @ResponseBody
    @RequestMapping(value = "/report/aggregated-reports", method = RequestMethod.GET)
    public ResponseEntity<AggregatedReportsDTO> get(@RequestParam("anm-id") String anmIdentifier, @RequestParam("month") int month, @RequestParam("year") int year) {
        Map<String, Integer> indicatorSummary = buildIndicatorSummary(anmIdentifier, month, year);
        LocationDTO locationDTO = getLocation(anmIdentifier);
        return new ResponseEntity<>(new AggregatedReportsDTO(indicatorSummary, locationDTO), allowOrigin(drishtiSiteUrl), OK);
    }

    private LocationDTO getLocation(String anmIdentifier) {
        Location location = anmService.getLocation(anmIdentifier);
        return new LocationDTO(StringUtils.capitalize(location.subCenter()), location.phcName(), location.taluka(), location.district(), location.state());
    }

    private Map<String, Integer> buildIndicatorSummary(String anmIdentifier, int month, int year) {
        List<AggregatorResponseDTO> aggregatorResponseDTO = aggregateReportsService.getAggregatedReports(anmIdentifier, month, year);
        Map<String, Integer> indicatorSummary = new HashMap<>();
        for (AggregatorResponseDTO responseDTO : aggregatorResponseDTO) {
            indicatorSummary.put(responseDTO.indicator(), responseDTO.count());
        }
        return indicatorSummary;
    }
}
