package org.ei.drishti.reporting.service;

import com.google.gson.Gson;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.ei.drishti.common.domain.ReportMonth;
import org.ei.drishti.common.util.HttpAgent;
import org.ei.drishti.common.util.HttpResponse;
import org.ei.drishti.dto.aggregatorResponse.AggregatorResponseDTO;
import org.ei.drishti.dto.report.ServiceProvidedReportDTO;
import org.ei.drishti.reporting.domain.ServiceProvidedReport;
import org.ei.drishti.reporting.repository.AllTokensRepository;
import org.ei.drishti.reporting.repository.ServicesProvidedRepository;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.text.MessageFormat.format;
import static java.util.Arrays.asList;
import static org.ei.drishti.common.util.EasyMap.mapOf;

@Service
public class AggregateReportsService {

    private static Logger logger = LoggerFactory.getLogger(AggregateReportsService.class);

    private String aggregatorDataSetUrl;
    private String aggregatedDataSetUrl;
    private HttpAgent httpAgent;
    private AllTokensRepository tokenRepository;
    private ServicesProvidedRepository servicesProvidedRepository;
    private ReportMonth reportMonth;

    protected AggregateReportsService() {
    }

    @Autowired
    public AggregateReportsService(@Value("#{drishti['aggregator.dataset.url']}") String aggregatorDataSetUrl,
                                   @Value("#{drishti['aggregated.dataset.url']}") String aggregatedDataSetUrl,
                                   HttpAgent httpAgent, AllTokensRepository tokenRepository,
                                   ServicesProvidedRepository servicesProvidedRepository, ReportMonth reportMonth) {
        this.aggregatorDataSetUrl = aggregatorDataSetUrl;
        this.aggregatedDataSetUrl = aggregatedDataSetUrl;
        this.httpAgent = httpAgent;
        this.tokenRepository = tokenRepository;
        this.servicesProvidedRepository = servicesProvidedRepository;
        this.reportMonth = reportMonth;
    }

    @Transactional("service_provided")
    public void sendReportsToAggregator() {
        Integer token = tokenRepository.getAggregateReportsToken();
        logger.info(format("Trying to aggregate reports. Report Token: {0}", token));
        List<ServiceProvidedReport> reports = servicesProvidedRepository.getNewReports(token);
        if (reports.isEmpty()) {
            logger.info("No new reports to aggregate.");
            return;
        }
        logger.info(format("Got reports to aggregate. Number of reports: {0}", reports.size()));
        update(reports);
    }

    private void update(List<ServiceProvidedReport> reports) {
        Gson gson = new Gson();
        for (ServiceProvidedReport report : reports) {

            String reportJson = gson.toJson(mapDomainToDTO(report));

            HttpResponse response = sendToAggregator(reportJson);
            if (!response.isSuccess()) {
                throw new RuntimeException(format("Updating data to Aggregator with url {0} failed with error: {0}", aggregatorDataSetUrl, response.body()));
            }
            tokenRepository.saveAggregateReportsToken(report.id());
            logger.info(format("Updated report token to: {0}", report.id()));
        }
    }

    private ServiceProvidedReportDTO mapDomainToDTO(ServiceProvidedReport report) {
        return new ServiceProvidedReportDTO(report.id(),
                report.anmIdentifier(),
                report.type(),
                report.indicator(),
                LocalDate.parse(report.date()),
                report.village(),
                report.subCenter(),
                report.phc(),
                report.taluka(),
                report.district(),
                report.state())
                .withNRHMReportMonth(reportMonth.reportingMonth(LocalDate.parse(report.date())))
                .withNRHMReportYear(reportMonth.reportingYear(LocalDate.parse(report.date())));
    }

    private HttpResponse sendToAggregator(String reportJson) {
        logger.info(format("Sending report data to Aggregator. URL: {0}, data: {1}",
                aggregatorDataSetUrl, "update=" + reportJson));
        return httpAgent.put(aggregatorDataSetUrl, mapOf("update", reportJson));
    }

    public AggregatorResponseDTO getAggregatedReports(String anmIdentifier, int month, int year) {
        //http://bamboo.io/datasets/2ed48f2461854baca56c0e2e236bdb36/summary\?select\='{"indicator":1}'\&query='{"anm_identifier": "demo1","nrhm_report_year":2013, "nrhm_report_month":2}'
        String queryParams = URLEncodedUtils.format(
                asList(selectParam(), whereClauseParam(anmIdentifier, month, year))
                , "UTF-8");
        String url = aggregatedDataSetUrl + "/summary?" + queryParams;
        HttpResponse response = httpAgent.get(url);
        AggregatorResponseDTO aggregatorResponse = new Gson().fromJson(response.body(), AggregatorResponseDTO.class);
        if (aggregatorResponse == null || aggregatorResponse.indicator() == null) {
            return AggregatorResponseDTO.empty();
        }
        return aggregatorResponse;
    }

    private BasicNameValuePair whereClauseParam(String anmIdentifier, int month, int year) {
        return new BasicNameValuePair("query",
                "{\"anm_identifier\": \"" +
                        anmIdentifier + "\",\"nrhm_report_year\":" + year + ", \"nrhm_report_month\":" + month +
                        "}");
    }

    private BasicNameValuePair selectParam() {
        return new BasicNameValuePair("select", "{\"indicator\":1}");
    }
}
