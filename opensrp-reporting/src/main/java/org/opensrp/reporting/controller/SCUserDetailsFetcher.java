package org.opensrp.reporting.controller;

import org.opensrp.reporting.domain.SP_ANM;
import org.opensrp.reporting.service.ANMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SCUserDetailsFetcher extends ANMDetailsFetcher {

    @Autowired
    public SCUserDetailsFetcher(ANMService anmService) {
        super(anmService);
    }

    @Override
    public List<SP_ANM> fetchDetails(String anmIdentifier) {
        return anmService.anmsInTheSameSC(anmIdentifier);
    }
}
