package org.ei.drishti.reporting.controller;

import com.google.gson.Gson;
import org.ei.drishti.common.util.HttpAgent;
import org.ei.drishti.common.util.HttpResponse;
import org.ei.drishti.reporting.domain.ServiceProvidedReport;
import org.ei.drishti.reporting.repository.AllTokensRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.List;

@Component
public class AggregateReportsService {

    private String AggregatorDataSetUrl;
    private HttpAgent httpAgent;
    private AllTokensRepository tokenRepository;

    @Autowired
    public AggregateReportsService(@Value("#{drishti['aggregator.dataset.url']}") String AggregatorDataSetUrl,
                                   HttpAgent httpAgent, AllTokensRepository tokenRepository) {
        this.AggregatorDataSetUrl = AggregatorDataSetUrl;
        this.httpAgent = httpAgent;
        this.tokenRepository = tokenRepository;
    }

    public void update(List<ServiceProvidedReport> reports) {
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
