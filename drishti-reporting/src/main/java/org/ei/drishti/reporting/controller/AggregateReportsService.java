package org.ei.drishti.reporting.controller;

import com.google.gson.Gson;
import org.ei.drishti.common.util.HttpAgent;
import org.ei.drishti.common.util.HttpResponse;
import org.ei.drishti.reporting.domain.ServiceProvidedReport;
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

    @Autowired
    public AggregateReportsService(@Value("#{drishti['aggregator.dataset.url']}") String AggregatorDataSetUrl, HttpAgent httpAgent) {
        this.AggregatorDataSetUrl = AggregatorDataSetUrl;
        this.httpAgent = httpAgent;
    }

    public void update(List reports) throws Exception {
        Gson gson = new Gson();
        for (Object report : reports) {
            ServiceProvidedReport serviceProvidedReport = (ServiceProvidedReport) report;
            String reportJson = gson.toJson(serviceProvidedReport);
            postToAggregator(reportJson);
        }
    }

    private void postToAggregator(String reportJson) throws Exception {
        HttpResponse response = httpAgent.put(AggregatorDataSetUrl, "update=" + reportJson, MediaType.APPLICATION_JSON_VALUE);
        if (!response.isSuccess()) {
            throw new Exception(MessageFormat.format("Updating data to Aggregator with url {0} failed with error: {0}", AggregatorDataSetUrl, response.body()));
        }
    }
}
