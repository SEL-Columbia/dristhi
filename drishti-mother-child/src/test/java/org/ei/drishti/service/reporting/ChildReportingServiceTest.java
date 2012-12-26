package org.ei.drishti.service.reporting;

import org.ei.drishti.common.domain.Indicator;
import org.ei.drishti.common.domain.ReportingData;
import org.ei.drishti.common.util.DateUtil;
import org.ei.drishti.domain.Child;
import org.ei.drishti.domain.Location;
import org.ei.drishti.repository.AllChildren;
import org.ei.drishti.util.SafeMap;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;

import static java.util.Arrays.asList;
import static org.ei.drishti.common.domain.Indicator.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ChildReportingServiceTest {
    @Mock
    private ReportingService reportingService;
    @Mock
    private AllChildren allChildren;

    private ChildReportingService service;

    private static final Child CHILD = new Child("CASE X", "EC-CASE-1", "MOTHER-CASE-1", "TC 1", "boo", new ArrayList<String>(), "female")
            .withDateOfBirth("2012-01-01")
            .withLocation("bherya", "Sub Center", "PHC X")
            .withAnm("ANM X");

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        service = new ChildReportingService(reportingService, allChildren);
    }

    @Test
    public void shouldGetRidOfSequenceNumberFormImmunizationReportIndicator() throws Exception {
        assertIndicatorBasedOnImmunization("bcg", BCG);

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

    @Test
    public void shouldMakeAReportingCallForEachNewlyProvidedImmunization() throws Exception {
        SafeMap reportingData = reportDataForImmunization("dpt_1 bcg measles");
        when(allChildren.findByCaseId("CASE X")).thenReturn(new Child("CASE X", "EC-CASE-1", "MOTHER-CASE-1", "TC 1", "boo", asList("dpt_1", "dpt_2", "bcg", "measles"), "female")
                .withLocation("bherya", "Sub Center", "PHC X")
                .withAnm("ANM X"));

        service.immunizationProvided(reportingData, asList("dpt_1"));

        verify(reportingService).sendReportData(ReportingData.serviceProvidedData("ANM X", "TC 1", BCG, "2012-01-01", new Location("bherya", "Sub Center", "PHC X")));
        verify(reportingService).sendReportData(ReportingData.anmReportData("ANM X", "CASE X", BCG, "2012-01-01"));
        verify(reportingService).sendReportData(ReportingData.serviceProvidedData("ANM X", "TC 1", MEASLES, "2012-01-01", new Location("bherya", "Sub Center", "PHC X")));
        verify(reportingService).sendReportData(ReportingData.anmReportData("ANM X", "CASE X", MEASLES, "2012-01-01"));
        verifyNoMoreInteractions(reportingService);
    }

    @Test
    public void shouldNotSendChildReportingDataWhenWrongImmunizationIsProvided() throws Exception {
        SafeMap reportingData = new SafeMap();
        reportingData.put("anmIdentifier", "ANM X");
        reportingData.put("immunizationsProvidedDate", "2012-01-01");
        when(allChildren.findByCaseId("CASE X")).thenReturn(CHILD);

        service.immunizationProvided(reportDataForImmunization("NON_EXISTENT_IMMUNIZATION bcg"), new ArrayList<String>());

        verify(reportingService).sendReportData(ReportingData.serviceProvidedData("ANM X", "TC 1", BCG, "2012-01-01", new Location("bherya", "Sub Center", "PHC X")));
        verify(reportingService).sendReportData(ReportingData.anmReportData("ANM X", "CASE X", BCG, "2012-01-01"));
        verifyNoMoreInteractions(reportingService);
    }

    @Test
    public void shouldSendChildImmunizationDataWhenChildIsRegistered() throws Exception {
        when(allChildren.findByCaseId("CASE X")).thenReturn(new Child("CASE X", "EC-CASE-1", "MOTHER-CASE-1", "TC 1", "boo", asList("opv_0"), "female")
                .withLocation("bherya", "Sub Center", "PHC X")
                .withAnm("ANM X")
                .withDateOfBirth("2012-01-01"));

        SafeMap reportData = new SafeMap();
        reportData.put("caseId", "CASE X");
        reportData.put("childWeight", "5");
        reportData.put("bfPostBirth", "");
        service.registerChild(reportData);

        ReportingData serviceProvidedData = ReportingData.serviceProvidedData("ANM X", "TC 1", OPV, "2012-01-01", new Location("bherya", "Sub Center", "PHC X"));
        ReportingData anmReportData = ReportingData.anmReportData("ANM X", "CASE X", OPV, "2012-01-01");
        verify(reportingService).sendReportData(serviceProvidedData);
        verify(reportingService).sendReportData(anmReportData);
    }

    @Test
    public void shouldReportLowWeightDuringChildRegistration() throws Exception {
        when(allChildren.findByCaseId("CASE X")).thenReturn(CHILD);

        SafeMap reportData = new SafeMap();
        reportData.put("caseId", "CASE X");
        reportData.put("childWeight", "2.2");
        reportData.put("bfPostBirth", "");
        service.registerChild(reportData);

        ReportingData serviceProvidedData = ReportingData.serviceProvidedData("ANM X", "TC 1", LBW, "2012-01-01", new Location("bherya", "Sub Center", "PHC X"));
        ReportingData anmReportData = ReportingData.anmReportData("ANM X", "CASE X", LBW, "2012-01-01");
        verify(reportingService).sendReportData(serviceProvidedData);
        verify(reportingService).sendReportData(anmReportData);
    }

    @Test
    public void shouldReportIfBreastFeedingInitiatedDuringChildRegistration() throws Exception {
        when(allChildren.findByCaseId("CASE X")).thenReturn(CHILD);

        SafeMap reportData = new SafeMap();
        reportData.put("caseId", "CASE X");
        reportData.put("childWeight", "2.2");
        reportData.put("bfPostBirth", "yes");
        service.registerChild(reportData);

        ReportingData serviceProvidedData = ReportingData.serviceProvidedData("ANM X", "TC 1", BF_POST_BIRTH, "2012-01-01", new Location("bherya", "Sub Center", "PHC X"));
        ReportingData anmReportData = ReportingData.anmReportData("ANM X", "CASE X", BF_POST_BIRTH, "2012-01-01");
        verify(reportingService).sendReportData(serviceProvidedData);
        verify(reportingService).sendReportData(anmReportData);
    }

    @Test
    public void shouldReportIfWeightIsMeasuredDuringChildRegistration() throws Exception {
        when(allChildren.findByCaseId("CASE X")).thenReturn(CHILD);

        SafeMap reportData = new SafeMap();
        reportData.put("caseId", "CASE X");
        reportData.put("childWeight", "2.2");
        reportData.put("bfPostBirth", "yes");
        service.registerChild(reportData);

        ReportingData serviceProvidedData = ReportingData.serviceProvidedData("ANM X", "TC 1", WEIGHED_AT_BIRTH, "2012-01-01", new Location("bherya", "Sub Center", "PHC X"));
        ReportingData anmReportData = ReportingData.anmReportData("ANM X", "CASE X", WEIGHED_AT_BIRTH, "2012-01-01");
        verify(reportingService).sendReportData(serviceProvidedData);
        verify(reportingService).sendReportData(anmReportData);
    }

    @Test
    public void shouldNotReportIfWeightIsNotMeasuredDuringChildRegistration() throws Exception {
        when(allChildren.findByCaseId("CASE X")).thenReturn(CHILD);

        SafeMap reportData = new SafeMap();
        reportData.put("caseId", "CASE X");
        reportData.put("childWeight", "---");
        reportData.put("bfPostBirth", "");
        service.registerChild(reportData);

        ReportingData serviceProvidedData = ReportingData.serviceProvidedData("ANM X", "TC 1", WEIGHED_AT_BIRTH, "2012-01-01", new Location("bherya", "Sub Center", "PHC X"));
        ReportingData anmReportData = ReportingData.anmReportData("ANM X", "CASE X", WEIGHED_AT_BIRTH, "2012-01-01");
        verify(reportingService, times(0)).sendReportData(serviceProvidedData);
        verify(reportingService, times(0)).sendReportData(anmReportData);
    }

    @Test
    public void shouldNotReportBreastFeedingIfNotInitiatedDuringChildRegistration() throws Exception {
        when(allChildren.findByCaseId("CASE X")).thenReturn(CHILD);

        SafeMap reportData = new SafeMap();
        reportData.put("caseId", "CASE X");
        reportData.put("childWeight", "2.2");
        reportData.put("bfPostBirth", "");
        service.registerChild(reportData);

        ReportingData serviceProvidedData = ReportingData.serviceProvidedData("ANM X", "TC 1", BF_POST_BIRTH, "2012-01-01", new Location("bherya", "Sub Center", "PHC X"));
        ReportingData anmReportData = ReportingData.anmReportData("ANM X", "CASE X", BF_POST_BIRTH, "2012-01-01");
        verify(reportingService, times(0)).sendReportData(serviceProvidedData);
        verify(reportingService, times(0)).sendReportData(anmReportData);
    }

    @Test
    public void shouldNotReportNormalWeight() throws Exception {
        when(allChildren.findByCaseId("CASE X")).thenReturn(CHILD);

        SafeMap reportData = new SafeMap();
        reportData.put("caseId", "CASE X");
        reportData.put("childWeight", "2.5");
        reportData.put("bfPostBirth", "");
        service.registerChild(reportData);

        ReportingData serviceProvidedData = ReportingData.serviceProvidedData("ANM X", "TC 1", LBW, "2012-01-01", new Location("bherya", "Sub Center", "PHC X"));
        ReportingData anmReportData = ReportingData.anmReportData("ANM X", "CASE X", LBW, "2012-01-01");
        verify(reportingService, times(0)).sendReportData(serviceProvidedData);
        verify(reportingService, times(0)).sendReportData(anmReportData);
    }

    @Test
    public void shouldNotReportInvalidWeightValue() throws Exception {
        when(allChildren.findByCaseId("CASE X")).thenReturn(CHILD);

        SafeMap reportData = new SafeMap();
        reportData.put("caseId", "CASE X");
        reportData.put("childWeight", "");
        reportData.put("bfPostBirth", "");
        service.registerChild(reportData);

        ReportingData serviceProvidedData = ReportingData.serviceProvidedData("ANM X", "TC 1", LBW, "2012-01-01", new Location("bherya", "Sub Center", "PHC X"));
        ReportingData anmReportData = ReportingData.anmReportData("ANM X", "CASE X", LBW, "2012-01-01");
        verify(reportingService, times(0)).sendReportData(serviceProvidedData);
        verify(reportingService, times(0)).sendReportData(anmReportData);
    }

    @Test
    public void shouldReportCloseChildCaseWhenReasonIsDeath() {
        DateUtil.fakeIt(LocalDate.parse("2012-11-01"));
        when(allChildren.findByCaseId("CASE X")).thenReturn(CHILD);

        service.closeChild(reportDataForCloseChild("death_of_child"));

        ReportingData serviceProvidedData = ReportingData.serviceProvidedData("ANM X", "TC 1", CHILD_MORTALITY, "2012-03-05", new Location("bherya", "Sub Center", "PHC X"));
        ReportingData anmReportData = ReportingData.anmReportData("ANM X", "CASE X", CHILD_MORTALITY, "2012-03-05");
        verify(reportingService).sendReportData(serviceProvidedData);
        verify(reportingService).sendReportData(anmReportData);
    }

    @Test
    public void shouldNotReportCloseChildCaseWhenReasonIsNotDeath() {
        when(allChildren.findByCaseId("CASE X")).thenReturn(CHILD);

        service.closeChild(reportDataForCloseChild("child_over5"));

        verifyZeroInteractions(reportingService);
    }

    @Test
    public void shouldSendChildReportingDataWhenChildIsExactlyElevenMonthsOld() throws Exception {
        DateUtil.fakeIt(LocalDate.parse("2012-12-01"));
        when(allChildren.findByCaseId("CASE X")).thenReturn(CHILD);

        service.closeChild(reportDataForCloseChild("death_of_child"));

        ReportingData serviceProvidedData = ReportingData.serviceProvidedData("ANM X", "TC 1", CHILD_MORTALITY, "2012-03-05", new Location("bherya", "Sub Center", "PHC X"));
        ReportingData anmReportData = ReportingData.anmReportData("ANM X", "CASE X", CHILD_MORTALITY, "2012-03-05");
        verify(reportingService).sendReportData(serviceProvidedData);
        verify(reportingService).sendReportData(anmReportData);
    }

    @Test
    public void shouldSendChildReportingDataWhenChildIsYoungerThanElevenMonths() throws Exception {
        DateUtil.fakeIt(LocalDate.parse("2012-11-15"));
        when(allChildren.findByCaseId("CASE X")).thenReturn(CHILD);

        service.closeChild(reportDataForCloseChild("death_of_child"));

        ReportingData serviceProvidedData = ReportingData.serviceProvidedData("ANM X", "TC 1", CHILD_MORTALITY, "2012-03-05", new Location("bherya", "Sub Center", "PHC X"));
        ReportingData anmReportData = ReportingData.anmReportData("ANM X", "CASE X", CHILD_MORTALITY, "2012-03-05");
        verify(reportingService).sendReportData(serviceProvidedData);
        verify(reportingService).sendReportData(anmReportData);
    }

    @Test
    public void shouldNotSendChildReportingDataWhenChildIsOlderThanElevenMonths() throws Exception {
        DateUtil.fakeIt(LocalDate.parse("2012-12-31"));
        when(allChildren.findByCaseId("CASE X")).thenReturn(CHILD);

        service.closeChild(reportDataForCloseChild("death_of_child"));

        verifyZeroInteractions(reportingService);
    }

    @Test
    public void shouldNotReportCloseChildCaseWhenChildIsNotFound() {
        when(allChildren.findByCaseId("CASE X")).thenReturn(null);

        service.closeChild(reportDataForCloseChild("child_over5"));

        verifyZeroInteractions(reportingService);
    }

    private void assertIndicatorBasedOnImmunization(String immunizationProvided, Indicator expectedIndicator) {
        ReportingService fakeReportingService = mock(ReportingService.class);
        ChildReportingService childReportingService = new ChildReportingService(fakeReportingService, allChildren);
        SafeMap reportingData = reportDataForImmunization(immunizationProvided);
        when(allChildren.findByCaseId("CASE X")).thenReturn(CHILD);

        childReportingService.immunizationProvided(reportingData, new ArrayList<String>());

        verify(fakeReportingService).sendReportData(ReportingData.serviceProvidedData("ANM X", "TC 1", expectedIndicator, "2012-01-01", new Location("bherya", "Sub Center", "PHC X")));
        verify(fakeReportingService).sendReportData(ReportingData.anmReportData("ANM X", "CASE X", expectedIndicator, "2012-01-01"));
        verifyNoMoreInteractions(fakeReportingService);
    }

    private SafeMap reportDataForImmunization(String immunizationProvided) {
        SafeMap reportingData = new SafeMap();
        reportingData.put("caseId", "CASE X");
        reportingData.put("immunizationsProvided", immunizationProvided);
        reportingData.put("immunizationsProvidedDate", "2012-01-01");
        return reportingData;
    }

    private SafeMap reportDataForCloseChild(String closeReason) {
        SafeMap reportingData = new SafeMap();
        reportingData.put("caseId", "CASE X");
        reportingData.put("closeReason", closeReason);
        reportingData.put("submissionDate", "2012-03-05");
        return reportingData;
    }
}
