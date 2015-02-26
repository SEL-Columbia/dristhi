package org.opensrp.reporting.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.opensrp.common.domain.ReportMonth;
import org.opensrp.common.util.HttpAgent;
import org.opensrp.common.util.HttpResponse;
import org.joda.time.LocalDate;
import org.opensrp.dto.aggregatorResponse.AggregatorResponseDTO;
import org.opensrp.dto.report.ServiceProvidedReportDTO;
import org.opensrp.reporting.domain.ServiceProvidedReport;
import org.opensrp.reporting.repository.AllTokensRepository;
import org.opensrp.reporting.repository.ServicesProvidedRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import static java.text.MessageFormat.format;
import static java.util.Arrays.asList;
import static org.apache.commons.lang.exception.ExceptionUtils.getFullStackTrace;
import static org.opensrp.common.util.EasyMap.mapOf;

@Service
public class AggregateReportsService {

    private static Logger logger = LoggerFactory.getLogger(AggregateReportsService.class);
    private static final ReentrantLock lock = new ReentrantLock();
    private String aggregatorDataSetUrl;
    private String aggregatedDataSetUrl;
    private HttpAgent httpAgent;
    private AllTokensRepository tokenRepository;
    private ServicesProvidedRepository servicesProvidedRepository;
    private ReportMonth reportMonth;
    private int numberOfReportsSentInABatch;

    protected AggregateReportsService() {
    }

    @Autowired
    public AggregateReportsService(@Value("#{opensrp['aggregator.dataset.url']}") String aggregatorDataSetUrl,
                                   @Value("#{opensrp['aggregated.dataset.url']}") String aggregatedDataSetUrl,
                                   @Value("#{opensrp['number.of.reports.sent.in.a.batch']}") int numberOfReportsSentInABatch,
                                   HttpAgent httpAgent, AllTokensRepository tokenRepository,
                                   ServicesProvidedRepository servicesProvidedRepository, ReportMonth reportMonth) {
        this.aggregatorDataSetUrl = aggregatorDataSetUrl;
        this.aggregatedDataSetUrl = aggregatedDataSetUrl;
        this.numberOfReportsSentInABatch = numberOfReportsSentInABatch;
        this.httpAgent = httpAgent;
        this.tokenRepository = tokenRepository;
        this.servicesProvidedRepository = servicesProvidedRepository;
        this.reportMonth = reportMonth;
    }

    @Transactional("service_provided")
    public void sendReportsToAggregator() {
        if (!lock.tryLock()) {
            logger.warn("Not Aggregating reports. It is already in progress.");
            return;
        }
        try {
            Integer token = tokenRepository.getAggregateReportsToken();
            logger.info(format("Trying to aggregate reports. Report Token: {0}", token));
            List<ServiceProvidedReport> reports = servicesProvidedRepository.getNewReports(token, this.numberOfReportsSentInABatch);
            if (reports.isEmpty()) {
                logger.info("No new reports to aggregate.");
                return;
            }
            logger.info(format("Got reports to aggregate. Number of reports: {0}", reports.size()));
            update(reports);
        } catch (Exception e) {
            logger.error(MessageFormat.format("{0} occurred while trying to aggregate reports. Message: {1} with stack trace {2}",
                    e.toString(), e.getMessage(), getFullStackTrace(e)));
            throw e;
        } finally {
            lock.unlock();
        }
    }

    private void update(List<ServiceProvidedReport> reports) {
        String reportJson = new Gson().toJson(mapDomainToDTO(reports));
        HttpResponse response = sendToAggregator(reportJson);

        if (!response.isSuccess()) {
            throw new RuntimeException(format("Updating data to Aggregator with url {0} failed with error: {0}", aggregatorDataSetUrl, response.body()));
        }

        Integer id = reports.get(reports.size() - 1).id();
        tokenRepository.saveAggregateReportsToken(id);
        logger.info(format("Updated report token to: {0}", id));
    }

    private List<ServiceProvidedReportDTO> mapDomainToDTO(List<ServiceProvidedReport> reports) {
        List<ServiceProvidedReportDTO> serviceProvidedReportDTOs = new ArrayList<>();
        for (ServiceProvidedReport report : reports) {
            serviceProvidedReportDTOs.add(mapDomainToDTO(report));
        }
        return serviceProvidedReportDTOs;
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

    public List<AggregatorResponseDTO> getAggregatedReports(String anmIdentifier, int month, int year) {
        //http://bamboo.io/datasets/c67218ce415e4722a9f3b00882cd5a7b\?query\='{"anm_identifier": "demo1","nrhm_report_year":2013, "nrhm_report_month":10}'
        String queryParams = URLEncodedUtils.format(
                asList(whereClauseParam(anmIdentifier, month, year))
                , "UTF-8");
        String url = MessageFormat.format("{0}?{1}", aggregatedDataSetUrl, queryParams);
        HttpResponse response = httpAgent.get(url);
        return new Gson().fromJson(response.body(),
                new TypeToken<List<AggregatorResponseDTO>>() {
                }.getType());
    }

    private BasicNameValuePair whereClauseParam(String anmIdentifier, int month, int year) {
        return new BasicNameValuePair("query",
                "{\"anm_identifier\": \"" +
                        anmIdentifier + "\",\"nrhm_report_year\":" + year + ", \"nrhm_report_month\":" + month +
                        "}");
    }
}
