package org.ei.drishti.reporting.repository;

import org.ei.drishti.reporting.domain.ServiceProvider;
import org.ei.drishti.reporting.domain.ServiceProviderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import static org.ei.drishti.reporting.domain.ServiceProvider.FIND_BY_ANM_IDENTIFIER;
import static org.ei.drishti.reporting.domain.ServiceProvider.FIND_BY_PHC_IDENTIFIER;
import static org.ei.drishti.reporting.domain.ServiceProviderType.ANM;

@Repository
public class AllServiceProvidersRepository {
    private DataAccessTemplate dataAccessTemplate;

    protected AllServiceProvidersRepository() {
    }

    @Autowired
    public AllServiceProvidersRepository(@Qualifier("serviceProvidedDataAccessTemplate") DataAccessTemplate dataAccessTemplate) {
        this.dataAccessTemplate = dataAccessTemplate;
    }

    public ServiceProvider fetchBy(String serviceProviderIdentifier, ServiceProviderType type) {
        if (ANM.equals(type)) {
            return (ServiceProvider) dataAccessTemplate.getUniqueResult(FIND_BY_ANM_IDENTIFIER,
                    new String[]{"anmIdentifier"}, new Object[]{serviceProviderIdentifier});
        }
        return (ServiceProvider) dataAccessTemplate.getUniqueResult(FIND_BY_PHC_IDENTIFIER,
                new String[]{"phcIdentifier"}, new Object[]{serviceProviderIdentifier});
    }
}
