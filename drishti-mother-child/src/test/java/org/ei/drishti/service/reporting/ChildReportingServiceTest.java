package org.ei.drishti.service.reporting;

import org.ei.drishti.common.domain.Indicator;
import org.ei.drishti.common.domain.ReportingData;
import org.ei.drishti.contract.ChildImmunizationUpdationRequest;
import org.ei.drishti.domain.Child;
import org.ei.drishti.domain.Location;
import org.ei.drishti.repository.AllChildren;
import org.ei.drishti.util.SafeMap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Arrays;

import static org.ei.drishti.common.domain.Indicator.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ChildReportingServiceTest {
    @Mock
    private ReportingService reportingService;
    @Mock
    private AllChildren allChildren;

    private ChildReportingService service;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        service = new ChildReportingService(reportingService, allChildren);
    }

    @Test
    public void shouldSendChildReportingData() throws Exception {
        SafeMap reportingData = new SafeMap();
        reportingData.put("anmIdentifier", "ANM X");
        reportingData.put("immunizationsProvidedDate", "2012-01-01");
        when(allChildren.findByCaseId("CASE X")).thenReturn(new Child("CASE X", "TC 1", "boo", Arrays.asList("bcg", "hepb1"), "female").withLocation("bherya", "Sub Center", "PHC X"));

        service.updateChildImmunization(new ChildImmunizationUpdationRequest("CASE X", "ANM X", "bcg hepb1 opv0", "2012-01-01"), reportingData);

        ReportingData expectedReportingData = ReportingData.serviceProvidedData("ANM X", "TC 1", OPV, "2012-01-01", new Location("bherya", "Sub Center", "PHC X"));
        verify(reportingService).sendReportData(expectedReportingData);
    }

    @Test
    public void shouldMakeAReportingCallForEachNewlyProvidedImmunization() throws Exception {
        SafeMap reportingData = new SafeMap();
        reportingData.put("anmIdentifier", "ANM X");
        reportingData.put("immunizationsProvidedDate", "2012-01-01");
        when(allChildren.findByCaseId("CASE X")).thenReturn(new Child("CASE X", "TC 1", "boo", Arrays.asList("dpt1", "dpt2"), "female").withLocation("bherya", "Sub Center", "PHC X"));

        service.updateChildImmunization(new ChildImmunizationUpdationRequest("CASE X", "ANM X", "dpt1 bcg dpt2 measles", "2012-01-01"), reportingData);

        verify(reportingService).sendReportData(ReportingData.serviceProvidedData("ANM X", "TC 1", BCG, "2012-01-01", new Location("bherya", "Sub Center", "PHC X")));
        verify(reportingService).sendReportData(ReportingData.serviceProvidedData("ANM X", "TC 1", MEASLES, "2012-01-01", new Location("bherya", "Sub Center", "PHC X")));
    }

    @Test
    public void shouldNotSendChildReportingDataWhenChildIsNotFound() throws Exception {
        SafeMap reportingData = new SafeMap();
        reportingData.put("anmIdentifier", "ANM X");
        when(allChildren.findByCaseId("CASE X")).thenReturn(null);

        service.updateChildImmunization(new ChildImmunizationUpdationRequest("CASE X", "ANM X", "bcg hep opv", "2012-01-01"), reportingData);

        verifyZeroInteractions(reportingService);
    }

    @Test
    public void shouldGetRidOfSequenceNumberFormImmunizationReportIndicator() throws Exception {
        assertIndicatorBasedOnImmunization("bcg", BCG);

        assertIndicatorBasedOnImmunization("dpt1", DPT);
        assertIndicatorBasedOnImmunization("dpt2", DPT);
        assertIndicatorBasedOnImmunization("dpt3", DPT);
        assertIndicatorBasedOnImmunization("dptbooster1", DPT);
        assertIndicatorBasedOnImmunization("dptbooster2", DPT);

        assertIndicatorBasedOnImmunization("hepB0", HEP);
        assertIndicatorBasedOnImmunization("hepb1", HEP);
        assertIndicatorBasedOnImmunization("hepb2", HEP);
        assertIndicatorBasedOnImmunization("hepb3", HEP);

        assertIndicatorBasedOnImmunization("opv0", OPV);
        assertIndicatorBasedOnImmunization("opv1", OPV);
        assertIndicatorBasedOnImmunization("opv2", OPV);
        assertIndicatorBasedOnImmunization("opvbooster", OPV);

        assertIndicatorBasedOnImmunization("measles", MEASLES);
        assertIndicatorBasedOnImmunization("MeaslesBooster", MEASLES);
    }

    private void assertIndicatorBasedOnImmunization(String immunizationProvided, Indicator expectedIndicator) {
        AllChildren children = mock(AllChildren.class);
        ReportingService fakeReportingService = mock(ReportingService.class);
        ChildReportingService childReportingService = new ChildReportingService(fakeReportingService, children);

        SafeMap reportingData = new SafeMap();
        reportingData.put("anmIdentifier", "ANM X");
        reportingData.put("immunizationsProvidedDate", "2012-01-01");
        when(children.findByCaseId("CASE X")).thenReturn(new Child("CASE X", "TC 1", "boo", new ArrayList<String>(), "female").withLocation("bherya", "Sub Center", "PHC X"));

        childReportingService.updateChildImmunization(new ChildImmunizationUpdationRequest("CASE X", "ANM X", immunizationProvided, "2012-01-01"), reportingData);

        verify(fakeReportingService).sendReportData(ReportingData.serviceProvidedData("ANM X", "TC 1", expectedIndicator, "2012-01-01", new Location("bherya", "Sub Center", "PHC X")));
        verifyNoMoreInteractions(fakeReportingService);
    }

    @Test
    public void shouldNotSendChildReportingDataWhenWrongImmunizationIsProvided() throws Exception {
        SafeMap reportingData = new SafeMap();
        reportingData.put("anmIdentifier", "ANM X");
        reportingData.put("immunizationsProvidedDate", "2012-01-01");
        when(allChildren.findByCaseId("CASE X")).thenReturn(new Child("CASE X", "TC 1", "boo", new ArrayList<String>(), "female").withLocation("bherya", "Sub Center", "PHC X"));

        service.updateChildImmunization(new ChildImmunizationUpdationRequest("CASE X", "ANM X", "NON_EXISTENT_IMMUNIZATION bcg", "2012-01-01"), reportingData);

        verify(reportingService).sendReportData(ReportingData.serviceProvidedData("ANM X", "TC 1", BCG, "2012-01-01", new Location("bherya", "Sub Center", "PHC X")));
        verifyZeroInteractions(reportingService);
    }

}
