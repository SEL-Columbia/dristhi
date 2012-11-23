package org.ei.drishti.service.reporting;

import org.ei.drishti.common.domain.Indicator;
import org.ei.drishti.common.domain.ReportingData;
import org.ei.drishti.contract.ChildImmunizationUpdationRequest;
import org.ei.drishti.domain.Child;
import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.domain.Location;
import org.ei.drishti.repository.AllChildren;
import org.ei.drishti.repository.AllEligibleCouples;
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
    @Mock
    private AllEligibleCouples allECs;

    private ChildReportingService service;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        service = new ChildReportingService(reportingService, allChildren, allECs);
    }

    @Test
    public void shouldSendChildReportingData() throws Exception {
        SafeMap reportingData = new SafeMap();
        reportingData.put("anmIdentifier", "ANM X");
        reportingData.put("immunizationsProvidedDate", "2012-01-01");
        when(allChildren.findByCaseId("CASE X")).thenReturn(new Child("CASE X", "EC-CASE-1", "MOTHER-CASE-1", "TC 1", "boo", Arrays.asList("bcg", "hepb1"), "female"));
        when(allECs.findByCaseId("EC-CASE-1")).thenReturn(new EligibleCouple("EC-CASE-1", "EC 1").withLocation("bherya", "Sub Center", "PHC X"));

        service.updateChildImmunization(new ChildImmunizationUpdationRequest("CASE X", "ANM X", "bcg hepb_1 opv_0", "2012-01-01"), reportingData);

        ReportingData serviceProvidedData = ReportingData.serviceProvidedData("ANM X", "TC 1", OPV, "2012-01-01", new Location("bherya", "Sub Center", "PHC X"));
        ReportingData anmReportData = ReportingData.anmReportData("ANM X", "TC 1", OPV, "2012-01-01");
        verify(reportingService).sendReportData(serviceProvidedData);
        verify(reportingService).sendReportData(anmReportData);
    }

    @Test
    public void shouldMakeAReportingCallForEachNewlyProvidedImmunization() throws Exception {
        SafeMap reportingData = new SafeMap();
        reportingData.put("anmIdentifier", "ANM X");
        reportingData.put("immunizationsProvidedDate", "2012-01-01");
        when(allChildren.findByCaseId("CASE X")).thenReturn(new Child("CASE X", "EC-CASE-1", "MOTHER-CASE-1", "TC 1", "boo", Arrays.asList("dpt1", "dpt2"), "female"));
        when(allECs.findByCaseId("EC-CASE-1")).thenReturn(new EligibleCouple("EC-CASE-1", "EC 1").withLocation("bherya", "Sub Center", "PHC X"));

        service.updateChildImmunization(new ChildImmunizationUpdationRequest("CASE X", "ANM X", "dpt1 bcg dpt2 measles", "2012-01-01"), reportingData);

        verify(reportingService).sendReportData(ReportingData.serviceProvidedData("ANM X", "TC 1", BCG, "2012-01-01", new Location("bherya", "Sub Center", "PHC X")));
        verify(reportingService).sendReportData(ReportingData.anmReportData("ANM X", "TC 1", BCG, "2012-01-01"));
        verify(reportingService).sendReportData(ReportingData.serviceProvidedData("ANM X", "TC 1", MEASLES, "2012-01-01", new Location("bherya", "Sub Center", "PHC X")));
        verify(reportingService).sendReportData(ReportingData.anmReportData("ANM X", "TC 1", MEASLES, "2012-01-01"));
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
    public void shouldNotSendChildReportingDataWhenECIsNotFound() throws Exception {
        SafeMap reportingData = new SafeMap();
        reportingData.put("anmIdentifier", "ANM X");
        when(allChildren.findByCaseId("CASE X")).thenReturn(new Child("CASE X", "EC-CASE-1", "MOTHER-CASE-1", "TC 1", "bcg", Arrays.asList("dpt1", "dpt2"), "female"));
        when(allECs.findByCaseId("EC-CASE-1")).thenReturn(null);

        service.updateChildImmunization(new ChildImmunizationUpdationRequest("CASE X", "ANM X", "bcg hep opv", "2012-01-01"), reportingData);

        verifyZeroInteractions(reportingService);
    }

    @Test
    public void shouldGetRidOfSequenceNumberFormImmunizationReportIndicator() throws Exception {
        assertIndicatorBasedOnImmunization("bcg", BCG);

        assertIndicatorBasedOnImmunization("dpt_0", DPT);
        assertIndicatorBasedOnImmunization("dpt_1", DPT);
        assertIndicatorBasedOnImmunization("dpt_2", DPT);
        assertIndicatorBasedOnImmunization("dpt_3", DPT);
        assertIndicatorBasedOnImmunization("dptbooster_1", DPT);
        assertIndicatorBasedOnImmunization("dptbooster_2", DPT);

        assertIndicatorBasedOnImmunization("hepb_0", HEP);
        assertIndicatorBasedOnImmunization("hepb_1", HEP);
        assertIndicatorBasedOnImmunization("hepb_2", HEP);
        assertIndicatorBasedOnImmunization("hepb_3", HEP);

        assertIndicatorBasedOnImmunization("opv_0", OPV);
        assertIndicatorBasedOnImmunization("opv_1", OPV);
        assertIndicatorBasedOnImmunization("opv_2", OPV);
        assertIndicatorBasedOnImmunization("opv_3", OPV);
        assertIndicatorBasedOnImmunization("opvbooster", OPV);

        assertIndicatorBasedOnImmunization("measles", MEASLES);
        assertIndicatorBasedOnImmunization("measlesbooster", MEASLES);
    }

    private void assertIndicatorBasedOnImmunization(String immunizationProvided, Indicator expectedIndicator) {
        AllChildren children = mock(AllChildren.class);
        ReportingService fakeReportingService = mock(ReportingService.class);
        ChildReportingService childReportingService = new ChildReportingService(fakeReportingService, children, allECs);

        SafeMap reportingData = new SafeMap();
        reportingData.put("anmIdentifier", "ANM X");
        reportingData.put("immunizationsProvidedDate", "2012-01-01");
        when(children.findByCaseId("CASE X")).thenReturn(new Child("CASE X", "EC-CASE-1", "MOTHER-CASE-1", "TC 1", "boo", new ArrayList<String>(), "female"));
        when(allECs.findByCaseId("EC-CASE-1")).thenReturn(new EligibleCouple("EC-CASE-1", "EC 1").withLocation("bherya", "Sub Center", "PHC X"));

        childReportingService.updateChildImmunization(new ChildImmunizationUpdationRequest("CASE X", "ANM X", immunizationProvided, "2012-01-01"), reportingData);

        verify(fakeReportingService).sendReportData(ReportingData.serviceProvidedData("ANM X", "TC 1", expectedIndicator, "2012-01-01", new Location("bherya", "Sub Center", "PHC X")));
        verify(fakeReportingService).sendReportData(ReportingData.anmReportData("ANM X", "TC 1", expectedIndicator, "2012-01-01"));
        verifyNoMoreInteractions(fakeReportingService);
    }

    @Test
    public void shouldNotSendChildReportingDataWhenWrongImmunizationIsProvided() throws Exception {
        SafeMap reportingData = new SafeMap();
        reportingData.put("anmIdentifier", "ANM X");
        reportingData.put("immunizationsProvidedDate", "2012-01-01");
        when(allChildren.findByCaseId("CASE X")).thenReturn(new Child("CASE X", "EC-CASE-1", "MOTHER-CASE-1", "TC 1", "boo", new ArrayList<String>(), "female"));
        when(allECs.findByCaseId("EC-CASE-1")).thenReturn(new EligibleCouple("EC-CASE-1", "EC 1").withLocation("bherya", "Sub Center", "PHC X"));

        service.updateChildImmunization(new ChildImmunizationUpdationRequest("CASE X", "ANM X", "NON_EXISTENT_IMMUNIZATION bcg", "2012-01-01"), reportingData);

        verify(reportingService).sendReportData(ReportingData.serviceProvidedData("ANM X", "TC 1", BCG, "2012-01-01", new Location("bherya", "Sub Center", "PHC X")));
        verify(reportingService).sendReportData(ReportingData.anmReportData("ANM X", "TC 1", BCG, "2012-01-01"));
        verifyZeroInteractions(reportingService);
    }

}
