package org.ei.drishti.reporting.listener;

import org.ei.drishti.reporting.controller.AggregateReportsService;
import org.ei.drishti.reporting.domain.ServiceProvidedReport;
import org.ei.drishti.reporting.repository.ServicesProvidedRepository;
import org.ei.drishti.reporting.repository.AllTokensRepository;
import org.ei.drishti.scheduler.AggregateReportsScheduler;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.List;

@Component
public class AggregateReportsEventListener {
    private static Logger logger = LoggerFactory.getLogger(AggregateReportsEventListener.class);
    private final ServicesProvidedRepository servicesProvidedRepository;
    private final AggregateReportsService aggregateReportsService;
    private final AllTokensRepository allTokensRepository;

    @Autowired
    public AggregateReportsEventListener(ServicesProvidedRepository servicesProvidedRepository,
                                         AggregateReportsService aggregateReportsService, AllTokensRepository allTokensRepository) {

        this.servicesProvidedRepository = servicesProvidedRepository;
        this.aggregateReportsService = aggregateReportsService;
        this.allTokensRepository = allTokensRepository;
    }

    @MotechListener(subjects = AggregateReportsScheduler.SUBJECT)
    public void aggregate(MotechEvent event) throws Exception {
        Integer token = allTokensRepository.getAggregateReportsToken();
        logger.info(MessageFormat.format("Trying to aggregate reports. Token: {0}", token));
        List<ServiceProvidedReport> reports = servicesProvidedRepository.getNewReports(token);
        if (reports.isEmpty()) {
            logger.info("No new reports to aggregate.");
            return;
        }
        logger.info(MessageFormat.format("Got reports to aggregate. Number of reports: {0}", reports.size()));
        aggregateReportsService.update(reports);
    }
}
