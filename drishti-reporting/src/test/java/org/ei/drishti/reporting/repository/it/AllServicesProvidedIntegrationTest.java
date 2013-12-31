package org.ei.drishti.reporting.repository.it;

import org.ei.drishti.reporting.domain.*;
import org.ei.drishti.reporting.repository.AllServicesProvidedRepository;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static ch.lambdaj.Lambda.*;
import static org.ei.drishti.reporting.domain.ServiceProviderType.ANM;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

public class AllServicesProvidedIntegrationTest extends ServicesProvidedIntegrationTestBase {
    @Autowired
    private AllServicesProvidedRepository repository;

    @Test
    @Transactional("service_provided")
    @Rollback
    public void shouldSaveAService() throws Exception {
        PHC phc = new PHC("bhe", "Bherya");
        template.save(phc);
        SP_ANM anm = new SP_ANM("ANM X", "anmx name", "Sub Center 1", phc.id());
        template.save(anm);
        Date dates = LocalDate.now().toDate();
        Indicator indicator = new Indicator("ANC indicator");
        Location location = new Location("Bherya", "Sub Center", phc, "taluka", "mysore", "karnataka");
        List<ServiceProviderType> serviceProviderTypes = template.loadAll(ServiceProviderType.class);
        ServiceProviderType anmServiceProvider = selectUnique(serviceProviderTypes, having(on(ServiceProviderType.class).type(), equalTo(ANM.type())));
        ServiceProvider serviceProvider = new ServiceProvider(anm.id(), anmServiceProvider);
        template.save(location);
        template.save(indicator);
        template.save(serviceProvider);

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
        SP_ANM anm = new SP_ANM("ANM X", "anmx name", "Sub Center 1", phc.id());
        template.save(anm);
        Indicator indicatorToDelete = new Indicator("INDICATOR 1");
        Indicator otherIndicator = new Indicator("INDICATOR 2");
        Location location = new Location("Bherya", "Sub Center", phc, "taluka", "mysore", "karnataka");
        Date startDate = LocalDate.parse("2013-01-26").toDate();
        List<ServiceProviderType> serviceProviderTypes = template.loadAll(ServiceProviderType.class);
        ServiceProviderType anmServiceProvider = selectUnique(serviceProviderTypes, having(on(ServiceProviderType.class).type(), equalTo(ANM.type())));
        ServiceProvider serviceProvider = new ServiceProvider(anm.id(), anmServiceProvider);
        template.save(location);
        template.save(indicatorToDelete);
        template.save(otherIndicator);
        template.save(serviceProvider);
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

    @Test
    @Transactional("service_provided")
    @Rollback
    public void shouldFetchOnlyAFixedBatchSizeOfReportsFromTheGivenToken() throws Exception {
        createMultipleServiceProvidedData();

        List<ServiceProvidedReport> reports = repository.getNewReports(0, 1);

        assertEquals(1, reports.size());
        assertTrue(
                reports.contains(
                        new ServiceProvidedReport(1, "ANM X", ANM.type(), "INDICATOR",
                                LocalDate.parse("2013-01-26").toDate(), "Bherya", "Sub Center", "bhe",
                                "taluka", "mysore", "karnataka")));
    }

    private void createServiceProvidedData() {
        PHC phc = new PHC("bhe", "Bherya");
        template.save(phc);
        SP_ANM anm = new SP_ANM("ANM X", "anmx name", "Sub Center 1", phc.id());
        template.save(anm);
        Indicator indicator = new Indicator("INDICATOR");
        Location location = new Location("Bherya", "Sub Center", phc, "taluka", "mysore", "karnataka");
        Date date = LocalDate.parse("2013-01-26").toDate();
        List<ServiceProviderType> serviceProviderTypes = template.loadAll(ServiceProviderType.class);
        ServiceProviderType anmServiceProvider = selectUnique(serviceProviderTypes, having(on(ServiceProviderType.class).type(), equalTo(ANM.type())));
        ServiceProvider serviceProvider = new ServiceProvider(anm.id(), anmServiceProvider);
        template.save(location);
        template.save(indicator);
        template.save(serviceProvider);
        repository.save(serviceProvider, "1", indicator, date, location);
    }

    private void createMultipleServiceProvidedData() {
        PHC phc = new PHC("bhe", "Bherya");
        template.save(phc);
        SP_ANM anm = new SP_ANM("ANM X", "anmx name", "Sub Center 1", phc.id());
        template.save(anm);
        SP_ANM anm1 = new SP_ANM("ANM Y", "anmy name", "Sub Center 1", phc.id());
        template.save(anm1);
        Indicator indicator = new Indicator("INDICATOR");
        Location location = new Location("Bherya", "Sub Center", phc, "taluka", "mysore", "karnataka");
        Date date = LocalDate.parse("2013-01-26").toDate();
        List<ServiceProviderType> serviceProviderTypes = template.loadAll(ServiceProviderType.class);
        ServiceProviderType anmServiceProvider = selectUnique(serviceProviderTypes, having(on(ServiceProviderType.class).type(), equalTo(ANM.type())));
        ServiceProvider serviceProvider = new ServiceProvider(anm.id(), anmServiceProvider);
        ServiceProvider anotherServiceProvider = new ServiceProvider(anm1.id(), anmServiceProvider);
        template.save(location);
        template.save(indicator);
        template.save(serviceProvider);
        template.save(anotherServiceProvider);

        repository.save(serviceProvider, "1", indicator, date, location);
        repository.save(anotherServiceProvider, "2", indicator, date, location);
    }
}
