package org.ei.drishti.web.controller;

import com.google.gson.Gson;
import org.ei.drishti.common.util.HttpAgent;
import org.ei.drishti.common.util.HttpResponse;
import org.ei.drishti.dto.report.AggregatedReportsDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.MessageFormat;

import static org.ei.drishti.common.AllConstants.HTTP.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.OPTIONS;

@Controller
public class AuthenticatedAggregatedReportController {
    private static Logger logger = LoggerFactory.getLogger(AuthenticatedAggregatedReportController.class.toString());
    private final HttpAgent httpAgent;
    private final String reportServiceURL;
    private final String drishtiSiteUrl;

    @Autowired
    public AuthenticatedAggregatedReportController(HttpAgent httpAgent,
                                                   @Value("#{drishti['drishti.reporting.url']}") String url,
                                                   @Value("#{drishti['drishti.site.url']}") String drishtiSiteUrl) {
        this.httpAgent = httpAgent;
        this.reportServiceURL = url;
        this.drishtiSiteUrl = drishtiSiteUrl;
    }

    @RequestMapping(value = "/aggregated-reports", method = {GET, OPTIONS})
    @ResponseBody
    public ResponseEntity<AggregatedReportsDTO> get(@RequestParam("anm-id") String anmIdentifier, @RequestParam("month") int month, @RequestParam("year") int year) {
        String url = MessageFormat.format("{0}/aggregated-reports?anm-id={1}&month={2}&year={3}",
                reportServiceURL, anmIdentifier, String.valueOf(month), String.valueOf(year));
        HttpResponse httpResponse = httpAgent.get(url);
        if (!httpResponse.isSuccess()) {
            logger.error(MessageFormat.format("Fetching Aggregated Report for ANM {0}, reporting month {1}, and year {2} failed with error: {3}.",
                    anmIdentifier, month, year, httpResponse.body()));
            return null;
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add(ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, drishtiSiteUrl);
        AggregatedReportsDTO aggregatedReports = new Gson().fromJson(httpResponse.body(), AggregatedReportsDTO.class);
        return new ResponseEntity<>(aggregatedReports, headers, OK);
    }
}
