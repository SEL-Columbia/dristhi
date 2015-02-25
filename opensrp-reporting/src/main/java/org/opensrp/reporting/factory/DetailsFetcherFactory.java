package org.opensrp.reporting.factory;

import org.opensrp.reporting.controller.ANMDetailsFetcher;
import org.opensrp.reporting.controller.PHCUserDetailsFetcher;
import org.opensrp.reporting.controller.SCUserDetailsFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DetailsFetcherFactory {
    private static String PHC_USER_ROLE = "ROLE_PHC_USER";
    private static String SC_USER_ROLE = "ROLE_USER";

    private SCUserDetailsFetcher scUserDetailsFetcher;
    private PHCUserDetailsFetcher phcUserDetailsFetcher;

    @Autowired
    public DetailsFetcherFactory(SCUserDetailsFetcher scUserDetailsFetcher,
                                 PHCUserDetailsFetcher phcUserDetailsFetcher) {
        this.scUserDetailsFetcher = scUserDetailsFetcher;
        this.phcUserDetailsFetcher = phcUserDetailsFetcher;
    }

    public ANMDetailsFetcher detailsFetcher(List<String> roles) {
        if(roles.contains(PHC_USER_ROLE)) {
            return phcUserDetailsFetcher;
        }
        return scUserDetailsFetcher;
    }
}
