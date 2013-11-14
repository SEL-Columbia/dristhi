package org.ei.drishti.reporting.repository.it;

import org.ei.drishti.reporting.domain.*;
import org.ei.drishti.reporting.repository.AllServicesProvidedRepository;
import org.ei.drishti.reporting.repository.cache.DatesCacheableRepository;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static ch.lambdaj.Lambda.*;
import static org.ei.drishti.reporting.domain.ServiceProviderType.ANM;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AllServicesProvidedIntegrationTest extends ServicesProvidedIntegrationTestBase {
    @Autowired
    private AllServicesProvidedRepository repository;

    @Autowired
    @Qualifier("serviceProvidedDatesRepository")
    private DatesCacheableRepository allDatesRepository;

    @Test
    @Transactional("service_provided")
    @Rollback
    public void shouldSaveAService() throws Exception {
        PHC phc = new PHC("bhe", "Bherya");
        template.save(phc);
        SP_ANM anm = new SP_ANM("ANM X", phc.id());
        template.save(anm);
        Dates dates = new Dates(LocalDate.now().toDate());
        Indicator indicator = new Indicator("ANC indicator");
        Location location = new Location("Bherya", "Sub Center", phc, "taluka", "mysore", "karnataka");
        List<ServiceProviderType> serviceProviderTypes = template.loadAll(ServiceProviderType.class);
        ServiceProviderType anmServiceProvider = selectUnique(serviceProviderTypes, having(on(ServiceProviderType.class).type(), equalTo(ANM.type())));
        ServiceProvider serviceProvider = new ServiceProvider(anm.id(), anmServiceProvider);
        template.save(location);
        template.save(indicator);
        template.save(serviceProvider);
        allDatesRepository.save(dates);

        repository.save(serviceProvider, "123", indicator, dates, location);

        List<ServiceProvided> servicesProvided = template.loadAll(ServiceProvided.class);
        assertTrue(servicesProvided.contains(new ServiceProvided(serviceProvider, "123", indicator, dates, location)));
    }

    @Test
    @Transactional("service_provided")
    @Rollback
    public void shouldUpdateIndicatorForReportingMonth() throws Exception {
        PHC phc = new PHC("bhe", "Bherya");
        template.save(phc);
        SP_ANM anm = new SP_ANM("ANM X", phc.id());
        template.save(anm);
        Indicator indicatorToDelete = new Indicator("INDICATOR 1");
        Indicator otherIndicator = new Indicator("INDICATOR 2");
        Location location = new Location("Bherya", "Sub Center", phc, "taluka", "mysore", "karnataka");
        Dates startDate = new Dates(LocalDate.parse("2013-01-26").toDate());
        Dates endDate = new Dates(LocalDate.parse("2013-02-25").toDate());
        List<ServiceProviderType> serviceProviderTypes = template.loadAll(ServiceProviderType.class);
        ServiceProviderType anmServiceProvider = selectUnique(serviceProviderTypes, having(on(ServiceProviderType.class).type(), equalTo(ANM.type())));
        ServiceProvider serviceProvider = new ServiceProvider(anm.id(), anmServiceProvider);
        template.save(location);
        template.save(indicatorToDelete);
        template.save(otherIndicator);
        template.save(serviceProvider);
        allDatesRepository.save(startDate);
        allDatesRepository.save(endDate);
        repository.save(serviceProvider, "123", indicatorToDelete, startDate, location);

        repository.delete("INDICATOR 1", "2013-01-26", "2013-02-02");

        List<ServiceProvided> servicesProvided = template.loadAll(ServiceProvided.class);
        assertFalse(
                servicesProvided.contains(
                        new ServiceProvided(serviceProvider, null, indicatorToDelete, startDate, location)));

    }

    @Test
    @Transactional("service_provided")
    @Rollback
    public void shouldFetchAllReportsWhenTokenIsZero() throws Exception {
        createServiceProvidedData();

        List<ServiceProvidedReport> reports = repository.getNewReports(0);

        assertTrue(
                reports.contains(
                        new ServiceProvidedReport(1, "ANM X", ANM.type(), "INDICATOR",
                                LocalDate.parse("2013-01-26").toDate(), "Bherya", "Sub Center", "bhe",
                                "taluka", "mysore", "karnataka")));
    }

    @Test
    @Transactional("service_provided")
    @Rollback
    public void shouldNotFetchNewReportsWhenTokenIsEqualToMostRecentReport() throws Exception {
        createServiceProvidedData();
        List<ServiceProvided> servicesProvided = template.loadAll(ServiceProvided.class);
        ServiceProvided mostRecentServiceProvided = servicesProvided.get(servicesProvided.size() - 1);
        Token token = new Token("aggregate-reports-token", mostRecentServiceProvided.id());
        template.save(token);

        List<ServiceProvidedReport> reports = repository.getNewReports(Integer.valueOf(token.value()));

        assertFalse(
                reports.contains(
                        new ServiceProvidedReport(1, "ANM X", ANM.type(), "INDICATOR",
                                LocalDate.parse("2013-01-26").toDate(), "Bherya", "Sub Center", "bhe",
                                "taluka", "mysore", "karnataka")));
    }

    @Test
    @Transactional("service_provided")
    @Rollback
    public void shouldNotFetchNewReportsWhenTokenValueIsHigherThanMostRecentReport() throws Exception {
        createServiceProvidedData();
        List<ServiceProvided> servicesProvided = template.loadAll(ServiceProvided.class);
        ServiceProvided mostRecentServiceProvided = servicesProvided.get(servicesProvided.size() - 1);
        Token token = new Token("aggregate-reports-token", mostRecentServiceProvided.id() + 1);
        template.save(token);

        List<ServiceProvidedReport> reports = repository.getNewReports(Integer.valueOf(token.value()));

        assertFalse(
                reports.contains(
                        new ServiceProvidedReport(1, "ANM X", ANM.type(), "INDICATOR",
                                LocalDate.parse("2013-01-26").toDate(), "Bherya", "Sub Center", "bhe",
                                "taluka", "mysore", "karnataka")));
    }

    private void createServiceProvidedData() {
        PHC phc = new PHC("bhe", "Bherya");
        template.save(phc);
        SP_ANM anm = new SP_ANM("ANM X", phc.id());
        template.save(anm);
        Indicator indicator = new Indicator("INDICATOR");
        Location location = new Location("Bherya", "Sub Center", phc, "taluka", "mysore", "karnataka");
        Dates date = new Dates(LocalDate.parse("2013-01-26").toDate());
        List<ServiceProviderType> serviceProviderTypes = template.loadAll(ServiceProviderType.class);
        ServiceProviderType anmServiceProvider = selectUnique(serviceProviderTypes, having(on(ServiceProviderType.class).type(), equalTo(ANM.type())));
        ServiceProvider serviceProvider = new ServiceProvider(anm.id(), anmServiceProvider);
        template.save(location);
        template.save(indicator);
        template.save(serviceProvider);
        allDatesRepository.save(date);
        repository.save(serviceProvider, "1", indicator, date, location);
    }
}
