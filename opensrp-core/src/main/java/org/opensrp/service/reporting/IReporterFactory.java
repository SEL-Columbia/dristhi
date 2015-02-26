package org.opensrp.service.reporting;

public interface IReporterFactory {
    public IReporter reporterFor(String entityType);
}
