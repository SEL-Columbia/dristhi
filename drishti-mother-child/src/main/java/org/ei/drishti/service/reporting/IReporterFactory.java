package org.ei.drishti.service.reporting;

public interface IReporterFactory {
    public IReporter reporterFor(String entityType);
}
