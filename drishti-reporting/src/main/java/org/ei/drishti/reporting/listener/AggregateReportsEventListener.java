package org.ei.drishti.reporting.listener;

import org.ei.drishti.reporting.controller.BambooService;
import org.ei.drishti.reporting.domain.ServiceProvidedReport;
import org.ei.drishti.reporting.repository.ServicesProvidedRepository;
import org.ei.drishti.reporting.repository.TokenRepository;
import org.ei.drishti.scheduler.AggregateReportsScheduler;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AggregateReportsEventListener {
    private static Logger logger = LoggerFactory.getLogger(AggregateReportsEventListener.class);
    private final ServicesProvidedRepository servicesProvidedRepository;
    private final BambooService bambooService;
    private final TokenRepository tokenRepository;

    @Autowired
    public AggregateReportsEventListener(ServicesProvidedRepository servicesProvidedRepository,
                                         BambooService bambooService, TokenRepository tokenRepository) {

        this.servicesProvidedRepository = servicesProvidedRepository;
        this.bambooService = bambooService;
        this.tokenRepository = tokenRepository;
    }

    @MotechListener(subjects = AggregateReportsScheduler.SUBJECT)
    public void aggregate(MotechEvent event) throws Exception {
        Integer token = tokenRepository.getAggregateReportsToken();
        List<ServiceProvidedReport> reports = servicesProvidedRepository.getNewerReportsAfter(token);
        if (reports.isEmpty()) {
            return;
        }
        bambooService.update(reports);
    }
}
