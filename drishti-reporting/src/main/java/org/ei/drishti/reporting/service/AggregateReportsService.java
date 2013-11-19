package org.ei.drishti.reporting.service;

import com.google.gson.Gson;
import org.ei.drishti.common.util.HttpAgent;
import org.ei.drishti.common.util.HttpResponse;
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

import java.text.MessageFormat;
import java.util.List;

import static org.ei.drishti.common.util.EasyMap.mapOf;

@Service
public class AggregateReportsService {

    private static Logger logger = LoggerFactory.getLogger(AggregateReportsService.class);

    private String aggregatorDataSetUrl;
    private HttpAgent httpAgent;
    private AllTokensRepository tokenRepository;
    private ServicesProvidedRepository servicesProvidedRepository;

    protected AggregateReportsService() {
    }

    @Autowired
    public AggregateReportsService(@Value("#{drishti['aggregator.dataset.url']}") String AggregatorDataSetUrl,
                                   HttpAgent httpAgent, AllTokensRepository tokenRepository,
                                   ServicesProvidedRepository servicesProvidedRepository) {
        this.aggregatorDataSetUrl = AggregatorDataSetUrl;
        this.httpAgent = httpAgent;
        this.tokenRepository = tokenRepository;
        this.servicesProvidedRepository = servicesProvidedRepository;
    }

    @Transactional("service_provided")
    public void sendReportsToAggregator() {
        Integer token = tokenRepository.getAggregateReportsToken();
        logger.info(MessageFormat.format("Trying to aggregate reports. Report Token: {0}", token));
        List<ServiceProvidedReport> reports = servicesProvidedRepository.getNewReports(token);
        if (reports.isEmpty()) {
            logger.info("No new reports to aggregate.");
            return;
        }
        logger.info(MessageFormat.format("Got reports to aggregate. Number of reports: {0}", reports.size()));
        update(reports);
    }

    private void update(List<ServiceProvidedReport> reports) {
        Gson gson = new Gson();
        for (ServiceProvidedReport report : reports) {


            LocalDate reportingDate = LocalDate.parse(report.date());
            String reportJson = gson.toJson(mapDomainToDTO(report, reportingDate));

            HttpResponse response = sendToAggregator(reportJson);
            if (!response.isSuccess()) {
                throw new RuntimeException(MessageFormat.format("Updating data to Aggregator with url {0} failed with error: {0}", aggregatorDataSetUrl, response.body()));
            }
            tokenRepository.saveAggregateReportsToken(report.id());
            logger.info(MessageFormat.format("Updated report token to: {0}", report.id()));
        }
    }

    private ServiceProvidedReportDTO mapDomainToDTO(ServiceProvidedReport report, LocalDate reportingDate) {
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
                .withDay(reportingDate.getDayOfMonth())
                .withMonth(reportingDate.getMonthOfYear())
                .withYear(reportingDate.getYear());
    }

    private HttpResponse sendToAggregator(String reportJson) {
        logger.info(MessageFormat.format("Sending report data to Aggregator. URL: {0}, data: {1}",
                aggregatorDataSetUrl, "update=" + reportJson));
        return httpAgent.put(aggregatorDataSetUrl, mapOf("update", reportJson));
    }
}
