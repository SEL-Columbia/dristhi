package org.ei.drishti.reporting.service;

import com.google.gson.Gson;
import org.ei.drishti.common.util.HttpAgent;
import org.ei.drishti.common.util.HttpResponse;
import org.ei.drishti.reporting.domain.ServiceProvidedReport;
import org.ei.drishti.reporting.repository.AllTokensRepository;
import org.ei.drishti.reporting.repository.ServicesProvidedRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.List;

@Service
public class AggregateReportsService {

    private static Logger logger = LoggerFactory.getLogger(AggregateReportsService.class);

    private String AggregatorDataSetUrl;
    private HttpAgent httpAgent;
    private AllTokensRepository tokenRepository;
    private ServicesProvidedRepository servicesProvidedRepository;

    protected AggregateReportsService() {
    }

    @Autowired
    public AggregateReportsService(@Value("#{drishti['aggregator.dataset.url']}") String AggregatorDataSetUrl,
                                   HttpAgent httpAgent, AllTokensRepository tokenRepository,
                                   ServicesProvidedRepository servicesProvidedRepository) {
        this.AggregatorDataSetUrl = AggregatorDataSetUrl;
        this.httpAgent = httpAgent;
        this.tokenRepository = tokenRepository;
        this.servicesProvidedRepository = servicesProvidedRepository;
    }

    @Transactional("service_provided")
    public void sendReportsToAggregator() {
        Integer token = tokenRepository.getAggregateReportsToken();
        logger.info(MessageFormat.format("Trying to aggregate reports. Token: {0}", token));
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
            String reportJson = gson.toJson(report);
            HttpResponse response = sendToAggregator(reportJson);
            if (!response.isSuccess()) {
                throw new RuntimeException(MessageFormat.format("Updating data to Aggregator with url {0} failed with error: {0}", AggregatorDataSetUrl, response.body()));
            }
            tokenRepository.saveAggregateReportsToken(report.id());
        }
    }

    private HttpResponse sendToAggregator(String reportJson) {
        return httpAgent.put(AggregatorDataSetUrl, "update=" + reportJson, MediaType.APPLICATION_JSON_VALUE);
    }
}
