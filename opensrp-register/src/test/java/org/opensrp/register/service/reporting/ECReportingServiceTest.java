package org.opensrp.register.service.reporting;

import org.opensrp.common.domain.Indicator;
import org.opensrp.common.domain.ReportDataDeleteRequest;
import org.opensrp.common.domain.ReportMonth;
import org.opensrp.common.domain.ReportingData;
import org.opensrp.register.domain.EligibleCouple;
import org.opensrp.domain.Location;
import org.opensrp.util.SafeMap;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.opensrp.common.AllConstants.CommonFormFields.SUBMISSION_DATE_FIELD_NAME;
import static org.opensrp.common.AllConstants.FamilyPlanningFormFields.FP_METHOD_CHANGE_DATE_FIELD_NAME;
import static org.opensrp.common.util.EasyMap.create;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import org.opensrp.register.repository.AllEligibleCouples;
import org.opensrp.register.service.reporting.ECReportingService;
import org.opensrp.service.reporting.ReportingService;

public class ECReportingServiceTest {
    @Mock
    private ReportingService reportingService;
    @Mock
    private AllEligibleCouples allEligibleCouples;
    @Mock
    private ReportMonth reportMonth;

    private ECReportingService service;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        service = new ECReportingService(reportingService, allEligibleCouples, reportMonth);
    }

    @Test
    public void shouldReportFPMethodChangeWhenECIsRegistered() throws Exception {
        when(allEligibleCouples.findByCaseId("EC CASE 1")).thenReturn(new EligibleCouple("EC CASE 1", "EC NUMBER 1").withANMIdentifier("ANM X").withLocation("bherya", "Sub Center", "PHC X"));
        when(reportMonth.areDatesBelongToSameReportingMonth(LocalDate.parse("2012-01-01"), LocalDate.parse("2012-01-01"))).thenReturn(true);
        SafeMap reportData = new SafeMap(create("id", "EC CASE 1")
                .put("currentMethod", "iud")
                .put("familyPlanningMethodChangeDate", "2012-01-01")
                .put("caste", "sc")
                .put("submissionDate", "2012-01-01")
                .map());
        service.registerEC(reportData);

        verify(reportingService).sendReportData(ReportingData.anmReportData("ANM X", "EC CASE 1", Indicator.FP_IUD, "2012-01-01"));
        verify(reportingService).sendReportData(ReportingData.serviceProvidedData("ANM X", "EC NUMBER 1", Indicator.FP_IUD, "2012-01-01", new Location("bherya", "Sub Center", "PHC X"), "EC CASE 1"));
        verifyNoMoreInteractions(reportingService);
    }

    @Test
    public void shouldReportOCPSTWhenECIsRegisteredAndCurrentMethodIsOCPAndCasteIsST() throws Exception {
        when(allEligibleCouples.findByCaseId("EC CASE 1")).thenReturn(new EligibleCouple("EC CASE 1", "EC NUMBER 1").withANMIdentifier("ANM X").withLocation("bherya", "Sub Center", "PHC X"));
        when(reportMonth.areDatesBelongToSameReportingMonth(LocalDate.parse("2012-01-01"), LocalDate.parse("2012-01-01"))).thenReturn(true);

        SafeMap reportData = new SafeMap(create("id", "EC CASE 1")
                .put("currentMethod", "ocp")
                .put("familyPlanningMethodChangeDate", "2012-01-01")
                .put("caste", "st")
                .put("submissionDate", "2012-01-01")
                .map());
        service.registerEC(reportData);

        verify(reportingService).sendReportData(ReportingData.anmReportData("ANM X", "EC CASE 1", Indicator.FP_OCP_ST, "2012-01-01"));
        verify(reportingService).sendReportData(ReportingData.serviceProvidedData("ANM X", "EC NUMBER 1", Indicator.FP_OCP_ST, "2012-01-01", new Location("bherya", "Sub Center", "PHC X"), "EC CASE 1"));
    }

    @Test
    public void shouldReportOCPSCWhenECIsRegisteredAndCurrentMethodIsOCPAndCasteIsSC() throws Exception {
        when(allEligibleCouples.findByCaseId("EC CASE 1")).thenReturn(new EligibleCouple("EC CASE 1", "EC NUMBER 1").withANMIdentifier("ANM X").withLocation("bherya", "Sub Center", "PHC X"));
        when(reportMonth.areDatesBelongToSameReportingMonth(LocalDate.parse("2012-01-01"), LocalDate.parse("2012-01-01"))).thenReturn(true);

        SafeMap reportData = new SafeMap(create("id", "EC CASE 1")
                .put("currentMethod", "ocp")
                .put("familyPlanningMethodChangeDate", "2012-01-01")
                .put("caste", "sc")
                .put("submissionDate", "2012-01-01")
                .map());
        service.registerEC(reportData);

        verify(reportingService).sendReportData(ReportingData.anmReportData("ANM X", "EC CASE 1", Indicator.FP_OCP_SC, "2012-01-01"));
        verify(reportingService).sendReportData(ReportingData.serviceProvidedData("ANM X", "EC NUMBER 1", Indicator.FP_OCP_SC, "2012-01-01", new Location("bherya", "Sub Center", "PHC X"), "EC CASE 1"));
    }

    @Test
    public void shouldReportOCPC_OthersWhenECIsRegisteredAndCurrentMethodIsOCPAndCasteIsOthers() throws Exception {
        when(allEligibleCouples.findByCaseId("EC CASE 1")).thenReturn(new EligibleCouple("EC CASE 1", "EC NUMBER 1").withANMIdentifier("ANM X").withLocation("bherya", "Sub Center", "PHC X"));
        when(reportMonth.areDatesBelongToSameReportingMonth(LocalDate.parse("2012-01-01"), LocalDate.parse("2012-01-01"))).thenReturn(true);

        SafeMap reportData = new SafeMap(create("id", "EC CASE 1")
                .put("currentMethod", "ocp")
                .put("familyPlanningMethodChangeDate", "2012-01-01")
                .put("caste", "c_others")
                .put("submissionDate", "2012-01-01")
                .map());
        service.registerEC(reportData);

        verify(reportingService).sendReportData(ReportingData.anmReportData("ANM X", "EC CASE 1", Indicator.FP_OCP_CASTE_OTHERS, "2012-01-01"));
        verify(reportingService).sendReportData(ReportingData.serviceProvidedData("ANM X", "EC NUMBER 1", Indicator.FP_OCP_CASTE_OTHERS, "2012-01-01", new Location("bherya", "Sub Center", "PHC X"), "EC CASE 1"));
    }

    @Test
    public void shouldNotReportOCPCasteIndicatorsWhenECIsRegisteredAndCasteIsNotSpecified() throws Exception {
        when(allEligibleCouples.findByCaseId("EC CASE 1")).thenReturn(new EligibleCouple("EC CASE 1", "EC NUMBER 1").withANMIdentifier("ANM X").withLocation("bherya", "Sub Center", "PHC X"));
        when(reportMonth.areDatesBelongToSameReportingMonth(LocalDate.parse("2012-01-01"), LocalDate.parse("2012-01-01"))).thenReturn(true);

        SafeMap reportData = new SafeMap(create("id", "EC CASE 1")
                .put("currentMethod", "ocp")
                .put("familyPlanningMethodChangeDate", "2012-01-01")
                .put("caste", "")
                .put("submissionDate", "2012-01-01")
                .map());
        service.registerEC(reportData);

        verify(reportingService).sendReportData(ReportingData.anmReportData("ANM X", "EC CASE 1", Indicator.FP_OCP, "2012-01-01"));
        verify(reportingService).sendReportData(ReportingData.serviceProvidedData("ANM X", "EC NUMBER 1", Indicator.FP_OCP, "2012-01-01", new Location("bherya", "Sub Center", "PHC X"), "EC CASE 1"));
        verifyNoMoreInteractions(reportingService);
    }

    @Test
    public void shouldReportFPMethodChange() throws Exception {
        when(allEligibleCouples.findByCaseId("EC CASE 1")).thenReturn(new EligibleCouple("EC CASE 1", "EC NUMBER 1").withANMIdentifier("ANM X").withLocation("bherya", "Sub Center", "PHC X"));
        when(reportMonth.areDatesBelongToSameReportingMonth(LocalDate.parse("2012-01-01"), LocalDate.parse("2012-01-01"))).thenReturn(true);

        SafeMap reportData = new SafeMap(create("id", "EC CASE 1")
                .put("currentMethod", "ocp")
                .put("newMethod", "iud")
                .put("familyPlanningMethodChangeDate", "2012-01-01")
                .put("method_still_the_same", "no")
                .put("submissionDate", "2012-01-01")
                .map());
        service.fpChange(reportData);

        verify(reportingService).sendReportData(ReportingData.anmReportData("ANM X", "EC CASE 1", Indicator.FP_IUD, "2012-01-01"));
        verify(reportingService).sendReportData(ReportingData.serviceProvidedData("ANM X", "EC NUMBER 1", Indicator.FP_IUD, "2012-01-01", new Location("bherya", "Sub Center", "PHC X"), "EC CASE 1"));
        verifyNoMoreInteractions(reportingService);
    }

    @Test
    public void shouldReportOCPC_OthersWhenFPMethodChangeAndCasteIsOthers() throws Exception {
        when(allEligibleCouples.findByCaseId("EC CASE 1")).thenReturn(new EligibleCouple("EC CASE 1", "EC NUMBER 1").withANMIdentifier("ANM X").withLocation("bherya", "Sub Center", "PHC X"));
        when(reportMonth.areDatesBelongToSameReportingMonth(LocalDate.parse("2012-01-01"), LocalDate.parse("2012-01-01"))).thenReturn(true);

        SafeMap reportData = new SafeMap(create("id", "EC CASE 1")
                .put("currentMethod", "iud")
                .put("newMethod", "ocp")
                .put("familyPlanningMethodChangeDate", "2012-01-01")
                .put("caste", "c_others")
                .put("submissionDate", "2012-01-01")
                .map());
        service.fpChange(reportData);

        verify(reportingService).sendReportData(ReportingData.anmReportData("ANM X", "EC CASE 1", Indicator.FP_OCP_CASTE_OTHERS, "2012-01-01"));
        verify(reportingService).sendReportData(ReportingData.serviceProvidedData("ANM X", "EC NUMBER 1", Indicator.FP_OCP_CASTE_OTHERS, "2012-01-01", new Location("bherya", "Sub Center", "PHC X"), "EC CASE 1"));
    }

    @Test
    public void shouldReportOCPSTWhenFPMethodChangeAndCasteIsST() throws Exception {
        when(allEligibleCouples.findByCaseId("EC CASE 1")).thenReturn(new EligibleCouple("EC CASE 1", "EC NUMBER 1").withANMIdentifier("ANM X").withLocation("bherya", "Sub Center", "PHC X"));
        when(reportMonth.areDatesBelongToSameReportingMonth(LocalDate.parse("2012-01-01"), LocalDate.parse("2012-01-01"))).thenReturn(true);

        SafeMap reportData = new SafeMap(create("id", "EC CASE 1")
                .put("currentMethod", "iud")
                .put("newMethod", "ocp")
                .put("familyPlanningMethodChangeDate", "2012-01-01")
                .put("caste", "st")
                .put("submissionDate", "2012-01-01")
                .map());
        service.fpChange(reportData);

        verify(reportingService).sendReportData(ReportingData.anmReportData("ANM X", "EC CASE 1", Indicator.FP_OCP_ST, "2012-01-01"));
        verify(reportingService).sendReportData(ReportingData.serviceProvidedData("ANM X", "EC NUMBER 1", Indicator.FP_OCP_ST, "2012-01-01", new Location("bherya", "Sub Center", "PHC X"), "EC CASE 1"));
    }

    @Test
    public void shouldReportOCPSCWhenFPMethodChangeAndCasteIsSC() throws Exception {
        when(allEligibleCouples.findByCaseId("EC CASE 1")).thenReturn(new EligibleCouple("EC CASE 1", "EC NUMBER 1").withANMIdentifier("ANM X").withLocation("bherya", "Sub Center", "PHC X"));
        when(reportMonth.areDatesBelongToSameReportingMonth(LocalDate.parse("2012-01-01"), LocalDate.parse("2012-01-01"))).thenReturn(true);

        SafeMap reportData = new SafeMap(create("id", "EC CASE 1")
                .put("currentMethod", "iud")
                .put("newMethod", "ocp")
                .put("familyPlanningMethodChangeDate", "2012-01-01")
                .put("caste", "sc")
                .put("submissionDate", "2012-01-01")
                .map());
        service.fpChange(reportData);

        verify(reportingService).sendReportData(ReportingData.anmReportData("ANM X", "EC CASE 1", Indicator.FP_OCP_SC, "2012-01-01"));
        verify(reportingService).sendReportData(ReportingData.serviceProvidedData("ANM X", "EC NUMBER 1", Indicator.FP_OCP_SC, "2012-01-01",
                new Location("bherya", "Sub Center", "PHC X"), "EC CASE 1"));
    }

    @Test
    public void shouldNotReportFPMethodChangeWhenNoIndicatorIsFoundForTheCurrentFPMethod() throws Exception {
        SafeMap reportData = new SafeMap(create("id", "EC CASE 1")
                .put("currentMethod", "none")
                .put("familyPlanningMethodChangeDate", "2012-01-01")
                .put("submissionDate", "2012-01-01")
                .map());

        service.registerEC(reportData);

        verifyZeroInteractions(reportingService);
    }

    @Test
    public void shouldReportFemaleSterilizationAPLWhenECIsRegisteredAndEconomicStatusIsAPL() throws Exception {
        when(allEligibleCouples.findByCaseId("EC CASE 1")).thenReturn(new EligibleCouple("EC CASE 1", "EC NUMBER 1").withANMIdentifier("ANM X").withLocation("bherya", "Sub Center", "PHC X"));
        when(reportMonth.areDatesBelongToSameReportingMonth(LocalDate.parse("2012-01-01"), LocalDate.parse("2012-01-01"))).thenReturn(true);

        SafeMap reportData = new SafeMap(create("id", "EC CASE 1")
                .put("currentMethod", "female_sterilization")
                .put("familyPlanningMethodChangeDate", "2012-01-01")
                .put("caste", "")
                .put("economicStatus", "apl")
                .put("submissionDate", "2012-01-01")
                .map());
        service.registerEC(reportData);

        verify(reportingService).sendReportData(ReportingData.anmReportData("ANM X", "EC CASE 1", Indicator.FP_FEMALE_STERILIZATION_APL, "2012-01-01"));
        verify(reportingService).sendReportData(ReportingData.serviceProvidedData("ANM X", "EC NUMBER 1", Indicator.FP_FEMALE_STERILIZATION_APL, "2012-01-01", new Location("bherya", "Sub Center", "PHC X"), "EC CASE 1"));
    }

    @Test
    public void shouldReportFemaleSterilizationBPLWhenECIsRegisteredAndEconomicStatusIsBPL() throws Exception {
        when(allEligibleCouples.findByCaseId("EC CASE 1")).thenReturn(new EligibleCouple("EC CASE 1", "EC NUMBER 1").withANMIdentifier("ANM X").withLocation("bherya", "Sub Center", "PHC X"));
        when(reportMonth.areDatesBelongToSameReportingMonth(LocalDate.parse("2012-01-01"), LocalDate.parse("2012-01-01"))).thenReturn(true);

        SafeMap reportData = new SafeMap(create("id", "EC CASE 1")
                .put("currentMethod", "female_sterilization")
                .put("familyPlanningMethodChangeDate", "2012-01-01")
                .put("caste", "")
                .put("economicStatus", "bpl")
                .put("submissionDate", "2012-01-01")
                .map());
        service.registerEC(reportData);

        verify(reportingService).sendReportData(ReportingData.anmReportData("ANM X", "EC CASE 1", Indicator.FP_FEMALE_STERILIZATION_BPL, "2012-01-01"));
        verify(reportingService).sendReportData(ReportingData.serviceProvidedData("ANM X", "EC NUMBER 1", Indicator.FP_FEMALE_STERILIZATION_BPL, "2012-01-01", new Location("bherya", "Sub Center", "PHC X"), "EC CASE 1"));
    }

    @Test
    public void shouldNotReportFemaleSterilizationWhenECIsRegisteredAndEconomicStatusIsNotSpecified() throws Exception {
        when(allEligibleCouples.findByCaseId("EC CASE 1")).thenReturn(new EligibleCouple("EC CASE 1", "EC NUMBER 1").withANMIdentifier("ANM X").withLocation("bherya", "Sub Center", "PHC X"));
        when(reportMonth.areDatesBelongToSameReportingMonth(LocalDate.parse("2012-01-01"), LocalDate.parse("2012-01-01"))).thenReturn(true);

        SafeMap reportData = new SafeMap(create("id", "EC CASE 1")
                .put("currentMethod", "female_sterilization")
                .put("familyPlanningMethodChangeDate", "2012-01-01")
                .put("caste", "")
                .put("economicStatus", "")
                .put("submissionDate", "2012-01-01")
                .map());
        service.registerEC(reportData);

        verify(reportingService).sendReportData(ReportingData.anmReportData("ANM X", "EC CASE 1", Indicator.FP_FEMALE_STERILIZATION, "2012-01-01"));
        verify(reportingService).sendReportData(ReportingData.serviceProvidedData("ANM X", "EC NUMBER 1", Indicator.FP_FEMALE_STERILIZATION, "2012-01-01", new Location("bherya", "Sub Center", "PHC X"), "EC CASE 1"));
        verifyNoMoreInteractions(reportingService);
    }

    @Test
    public void shouldReportFemaleSterilizationBPLWhenFPMethodIsChangedAndEconomicStatusIsBPL() throws Exception {
        when(allEligibleCouples.findByCaseId("EC CASE 1")).thenReturn(new EligibleCouple("EC CASE 1", "EC NUMBER 1").withANMIdentifier("ANM X").withLocation("bherya", "Sub Center", "PHC X"));
        when(reportMonth.areDatesBelongToSameReportingMonth(LocalDate.parse("2012-01-01"), LocalDate.parse("2012-01-01"))).thenReturn(true);

        SafeMap reportData = new SafeMap(create("id", "EC CASE 1")
                .put("currentMethod", "iud")
                .put("newMethod", "female_sterilization")
                .put("familyPlanningMethodChangeDate", "2012-01-01")
                .put("caste", "")
                .put("economicStatus", "bpl")
                .put("submissionDate", "2012-01-01")
                .map());
        service.fpChange(reportData);

        verify(reportingService).sendReportData(ReportingData.anmReportData("ANM X", "EC CASE 1", Indicator.FP_FEMALE_STERILIZATION_BPL, "2012-01-01"));
        verify(reportingService).sendReportData(ReportingData.serviceProvidedData("ANM X", "EC NUMBER 1", Indicator.FP_FEMALE_STERILIZATION_BPL, "2012-01-01", new Location("bherya", "Sub Center", "PHC X"), "EC CASE 1"));
    }

    @Test
    public void shouldReportFemaleSterilizationAPLWhenFPMethodIsChangedAndEconomicStatusIsAPL() throws Exception {
        when(allEligibleCouples.findByCaseId("EC CASE 1")).thenReturn(new EligibleCouple("EC CASE 1", "EC NUMBER 1").withANMIdentifier("ANM X").withLocation("bherya", "Sub Center", "PHC X"));
        when(reportMonth.areDatesBelongToSameReportingMonth(LocalDate.parse("2012-01-01"), LocalDate.parse("2012-01-01"))).thenReturn(true);

        SafeMap reportData = new SafeMap(create("id", "EC CASE 1")
                .put("currentMethod", "iud")
                .put("newMethod", "female_sterilization")
                .put("familyPlanningMethodChangeDate", "2012-01-01")
                .put("caste", "")
                .put("economicStatus", "apl")
                .put("submissionDate", "2012-01-01")
                .map());
        service.fpChange(reportData);

        verify(reportingService).sendReportData(ReportingData.anmReportData("ANM X", "EC CASE 1", Indicator.FP_FEMALE_STERILIZATION_APL, "2012-01-01"));
        verify(reportingService).sendReportData(ReportingData.serviceProvidedData("ANM X", "EC NUMBER 1", Indicator.FP_FEMALE_STERILIZATION_APL, "2012-01-01", new Location("bherya", "Sub Center", "PHC X"), "EC CASE 1"));
    }

    @Test
    public void shouldNotReportFemaleSterilizationEconomicStatusWhenFPMethodIsChangedAndEconomicStatusIsNotSpecified() throws Exception {
        when(allEligibleCouples.findByCaseId("EC CASE 1")).thenReturn(new EligibleCouple("EC CASE 1", "EC NUMBER 1").withANMIdentifier("ANM X").withLocation("bherya", "Sub Center", "PHC X"));
        when(reportMonth.areDatesBelongToSameReportingMonth(LocalDate.parse("2012-01-01"), LocalDate.parse("2012-01-01"))).thenReturn(true);

        SafeMap reportData = new SafeMap(create("id", "EC CASE 1")
                .put("currentMethod", "iud")
                .put("newMethod", "female_sterilization")
                .put("familyPlanningMethodChangeDate", "2012-01-01")
                .put("caste", "")
                .put("economicStatus", "")
                .put("submissionDate", "2012-01-01")
                .map());
        service.fpChange(reportData);

        verify(reportingService).sendReportData(ReportingData.anmReportData("ANM X", "EC CASE 1", Indicator.FP_FEMALE_STERILIZATION, "2012-01-01"));
        verify(reportingService).sendReportData(ReportingData.serviceProvidedData("ANM X", "EC NUMBER 1", Indicator.FP_FEMALE_STERILIZATION, "2012-01-01", new Location("bherya", "Sub Center", "PHC X"), "EC CASE 1"));
        verifyNoMoreInteractions(reportingService);
    }

    @Test
    public void shouldReportQuantityWhenProvided() throws Exception {
        EligibleCouple ec = new EligibleCouple("EC CASE 1", "EC NUMBER 1").withANMIdentifier("ANM X").withLocation("bherya", "Sub Center", "PHC X");
        when(allEligibleCouples.findByCaseId("EC CASE 1")).thenReturn(ec);
        when(reportMonth.areDatesBelongToSameReportingMonth(LocalDate.parse("2013-01-01"), LocalDate.parse("2013-01-01"))).thenReturn(true);

        SafeMap reportData = new SafeMap(
                create("id", "EC CASE 1")
                        .put("familyPlanningMethodChangeDate", "2013-01-01")
                        .put("quantity", "10")
                        .put("submissionDate", "2013-01-01")
                        .map());
        service.reportIndicator(reportData, ec, Indicator.CONDOM_QTY, reportData.get(FP_METHOD_CHANGE_DATE_FIELD_NAME), reportData.get(SUBMISSION_DATE_FIELD_NAME));

        verify(reportingService).sendReportData(ReportingData.anmReportData("ANM X", "EC CASE 1", Indicator.CONDOM_QTY, "2013-01-01").withQuantity("10"));
        verify(reportingService).sendReportData(ReportingData.serviceProvidedData("ANM X", "EC NUMBER 1", Indicator.CONDOM_QTY, "2013-01-01",
                new Location("bherya", "Sub Center", "PHC X"), "EC CASE 1").withQuantity("10"));
    }

    @Test
    public void shouldUseExternalIdIfECNumberIsNotAvailable() throws Exception {
        EligibleCouple ec = new EligibleCouple("EC CASE 1", null).withANMIdentifier("ANM X").withLocation("bherya", "Sub Center", "PHC X");
        when(allEligibleCouples.findByCaseId("EC CASE 1")).thenReturn(ec);
        when(reportMonth.areDatesBelongToSameReportingMonth(LocalDate.parse("2013-01-01"), LocalDate.parse("2013-01-01"))).thenReturn(true);

        SafeMap reportData = new SafeMap(
                create("id", "EC CASE 1")
                        .put("familyPlanningMethodChangeDate", "2013-01-01")
                        .put("externalId", "external id 1")
                        .put("submissionDate", "2013-01-01")
                        .map());
        service.reportIndicator(reportData, ec, Indicator.CONDOM_QTY, reportData.get(FP_METHOD_CHANGE_DATE_FIELD_NAME), reportData.get(SUBMISSION_DATE_FIELD_NAME));

        verify(reportingService).sendReportData(ReportingData.serviceProvidedData("ANM X", "external id 1", Indicator.CONDOM_QTY, "2013-01-01",
                new Location("bherya", "Sub Center", "PHC X"), "EC CASE 1"));
    }

    @Test
    public void shouldDeleteReports() throws Exception {
        service.deleteReportsForEC("entity id 1");

        verify(reportingService).deleteReportData(ReportDataDeleteRequest.serviceProvidedDataDeleteRequest("entity id 1"));
        verify(reportingService).deleteReportData(ReportDataDeleteRequest.anmReportDataDeleteRequest("entity id 1"));
    }

    @Test
    public void shouldNotReportIfServiceProvidedDateIsNotInCurrentReportingMonth() throws Exception {
        when(allEligibleCouples.findByCaseId("EC CASE 1")).thenReturn(new EligibleCouple("EC CASE 1", "EC NUMBER 1").withANMIdentifier("ANM X").withLocation("bherya", "Sub Center", "PHC X"));
        when(reportMonth.areDatesBelongToSameReportingMonth(LocalDate.parse("2013-01-01"), LocalDate.parse("2013-01-01"))).thenReturn(true);

        SafeMap reportData = new SafeMap(create("id", "EC CASE 1")
                .put("currentMethod", "iud")
                .put("newMethod", "female_sterilization")
                .put("familyPlanningMethodChangeDate", "2012-01-01")
                .put("caste", "")
                .put("economicStatus", "")
                .put("submissionDate", "2013-01-01")
                .map());

        service.reportIndicator(reportData, null, Indicator.CONDOM_QTY, "2012-01-01", reportData.get(SUBMISSION_DATE_FIELD_NAME));

        verifyZeroInteractions(reportingService);
    }
}
