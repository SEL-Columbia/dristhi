package org.opensrp.reporting.controller;

import org.opensrp.reporting.domain.SP_ANM;
import org.opensrp.reporting.service.ANMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public abstract class ANMDetailsFetcher {
    protected ANMService anmService;

    @Autowired
    protected ANMDetailsFetcher(ANMService anmService) {
        this.anmService = anmService;
    }

    public abstract List<SP_ANM> fetchDetails(String anmIdentifier);
}

