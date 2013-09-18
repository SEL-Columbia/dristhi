package org.ei.drishti.service.reporting;

import org.ei.drishti.common.domain.Indicator;
import org.ei.drishti.common.domain.ReportingData;
import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.domain.Location;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.util.SafeMap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.ei.drishti.util.EasyMap.create;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ECReportingServiceTest {
    @Mock
    private ReportingService reportingService;
    @Mock
    private AllEligibleCouples allEligibleCouples;

    private ECReportingService service;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        service = new ECReportingService(reportingService, allEligibleCouples);
    }

    @Test
    public void shouldReportFPMethodChangeWhenECIsRegistered() throws Exception {
        when(allEligibleCouples.findByCaseId("EC CASE 1")).thenReturn(new EligibleCouple("EC CASE 1", "EC NUMBER 1").withANMIdentifier("ANM X").withLocation("bherya", "Sub Center", "PHC X"));

        SafeMap reportData = new SafeMap(create("id", "EC CASE 1")
                .put("currentMethod", "iud")
                .put("familyPlanningMethodChangeDate", "2012-01-01")
                .put("caste", "sc")
                .map());
        service.registerEC(reportData);

        verify(reportingService).sendReportData(ReportingData.anmReportData("ANM X", "EC CASE 1", Indicator.FP_IUD, "2012-01-01"));
        verify(reportingService).sendReportData(ReportingData.serviceProvidedData("ANM X", "EC NUMBER 1", Indicator.FP_IUD, "2012-01-01", new Location("bherya", "Sub Center", "PHC X")));
        verifyNoMoreInteractions(reportingService);
    }

    @Test
    public void shouldReportOCPSTWhenECIsRegisteredAndCurrentMethodIsOCPAndCasteIsST() throws Exception {
        when(allEligibleCouples.findByCaseId("EC CASE 1")).thenReturn(new EligibleCouple("EC CASE 1", "EC NUMBER 1").withANMIdentifier("ANM X").withLocation("bherya", "Sub Center", "PHC X"));

        SafeMap reportData = new SafeMap(create("id", "EC CASE 1")
                .put("currentMethod", "ocp")
                .put("familyPlanningMethodChangeDate", "2012-01-01")
                .put("caste", "st")
                .map());
        service.registerEC(reportData);

        verify(reportingService).sendReportData(ReportingData.anmReportData("ANM X", "EC CASE 1", Indicator.FP_OCP_ST, "2012-01-01"));
        verify(reportingService).sendReportData(ReportingData.serviceProvidedData("ANM X", "EC NUMBER 1", Indicator.FP_OCP_ST, "2012-01-01", new Location("bherya", "Sub Center", "PHC X")));
    }

    @Test
    public void shouldReportOCPSCWhenECIsRegisteredAndCurrentMethodIsOCPAndCasteIsSC() throws Exception {
        when(allEligibleCouples.findByCaseId("EC CASE 1")).thenReturn(new EligibleCouple("EC CASE 1", "EC NUMBER 1").withANMIdentifier("ANM X").withLocation("bherya", "Sub Center", "PHC X"));

        SafeMap reportData = new SafeMap(create("id", "EC CASE 1")
                .put("currentMethod", "ocp")
                .put("familyPlanningMethodChangeDate", "2012-01-01")
                .put("caste", "sc")
                .map());
        service.registerEC(reportData);

        verify(reportingService).sendReportData(ReportingData.anmReportData("ANM X", "EC CASE 1", Indicator.FP_OCP_SC, "2012-01-01"));
        verify(reportingService).sendReportData(ReportingData.serviceProvidedData("ANM X", "EC NUMBER 1", Indicator.FP_OCP_SC, "2012-01-01", new Location("bherya", "Sub Center", "PHC X")));
    }

    @Test
    public void shouldReportOCPC_OthersWhenECIsRegisteredAndCurrentMethodIsOCPAndCasteIsOthers() throws Exception {
        when(allEligibleCouples.findByCaseId("EC CASE 1")).thenReturn(new EligibleCouple("EC CASE 1", "EC NUMBER 1").withANMIdentifier("ANM X").withLocation("bherya", "Sub Center", "PHC X"));

        SafeMap reportData = new SafeMap(create("id", "EC CASE 1")
                .put("currentMethod", "ocp")
                .put("familyPlanningMethodChangeDate", "2012-01-01")
                .put("caste", "c_others")
                .map());
        service.registerEC(reportData);

        verify(reportingService).sendReportData(ReportingData.anmReportData("ANM X", "EC CASE 1", Indicator.FP_OCP_CASTE_OTHERS, "2012-01-01"));
        verify(reportingService).sendReportData(ReportingData.serviceProvidedData("ANM X", "EC NUMBER 1", Indicator.FP_OCP_CASTE_OTHERS, "2012-01-01", new Location("bherya", "Sub Center", "PHC X")));
    }

    @Test
    public void shouldNotReportOCPCasteIndicatorsWhenECIsRegisteredAndCasteIsNotSpecified() throws Exception {
        when(allEligibleCouples.findByCaseId("EC CASE 1")).thenReturn(new EligibleCouple("EC CASE 1", "EC NUMBER 1").withANMIdentifier("ANM X").withLocation("bherya", "Sub Center", "PHC X"));

        SafeMap reportData = new SafeMap(create("id", "EC CASE 1")
                .put("currentMethod", "ocp")
                .put("familyPlanningMethodChangeDate", "2012-01-01")
                .put("caste", "")
                .map());
        service.registerEC(reportData);

        verify(reportingService).sendReportData(ReportingData.anmReportData("ANM X", "EC CASE 1", Indicator.FP_OCP, "2012-01-01"));
        verify(reportingService).sendReportData(ReportingData.serviceProvidedData("ANM X", "EC NUMBER 1", Indicator.FP_OCP, "2012-01-01", new Location("bherya", "Sub Center", "PHC X")));
        verifyNoMoreInteractions(reportingService);
    }

    @Test
    public void shouldReportFPMethodChange() throws Exception {
        when(allEligibleCouples.findByCaseId("EC CASE 1")).thenReturn(new EligibleCouple("EC CASE 1", "EC NUMBER 1").withANMIdentifier("ANM X").withLocation("bherya", "Sub Center", "PHC X"));

        SafeMap reportData = new SafeMap(create("id", "EC CASE 1")
                .put("currentMethod", "ocp")
                .put("newMethod", "iud")
                .put("familyPlanningMethodChangeDate", "2012-01-01")
                .put("method_still_the_same", "no")
                .map());
        service.fpChange(reportData);

        verify(reportingService).sendReportData(ReportingData.anmReportData("ANM X", "EC CASE 1", Indicator.FP_IUD, "2012-01-01"));
        verify(reportingService).sendReportData(ReportingData.serviceProvidedData("ANM X", "EC NUMBER 1", Indicator.FP_IUD, "2012-01-01", new Location("bherya", "Sub Center", "PHC X")));
        verifyNoMoreInteractions(reportingService);
    }

    @Test
    public void shouldReportOCPC_OthersWhenFPMethodChangeAndCasteIsOthers() throws Exception {
        when(allEligibleCouples.findByCaseId("EC CASE 1")).thenReturn(new EligibleCouple("EC CASE 1", "EC NUMBER 1").withANMIdentifier("ANM X").withLocation("bherya", "Sub Center", "PHC X"));

        SafeMap reportData = new SafeMap(create("id", "EC CASE 1")
                .put("currentMethod", "iud")
                .put("newMethod", "ocp")
                .put("familyPlanningMethodChangeDate", "2012-01-01")
                .put("caste", "c_others")
                .map());
        service.fpChange(reportData);

        verify(reportingService).sendReportData(ReportingData.anmReportData("ANM X", "EC CASE 1", Indicator.FP_OCP_CASTE_OTHERS, "2012-01-01"));
        verify(reportingService).sendReportData(ReportingData.serviceProvidedData("ANM X", "EC NUMBER 1", Indicator.FP_OCP_CASTE_OTHERS, "2012-01-01", new Location("bherya", "Sub Center", "PHC X")));
    }

    @Test
    public void shouldReportOCPSTWhenFPMethodChangeAndCasteIsST() throws Exception {
        when(allEligibleCouples.findByCaseId("EC CASE 1")).thenReturn(new EligibleCouple("EC CASE 1", "EC NUMBER 1").withANMIdentifier("ANM X").withLocation("bherya", "Sub Center", "PHC X"));

        SafeMap reportData = new SafeMap(create("id", "EC CASE 1")
                .put("currentMethod", "iud")
                .put("newMethod", "ocp")
                .put("familyPlanningMethodChangeDate", "2012-01-01")
                .put("caste", "st")
                .map());
        service.fpChange(reportData);

        verify(reportingService).sendReportData(ReportingData.anmReportData("ANM X", "EC CASE 1", Indicator.FP_OCP_ST, "2012-01-01"));
        verify(reportingService).sendReportData(ReportingData.serviceProvidedData("ANM X", "EC NUMBER 1", Indicator.FP_OCP_ST, "2012-01-01", new Location("bherya", "Sub Center", "PHC X")));
    }

    @Test
    public void shouldReportOCPSCWhenFPMethodChangeAndCasteIsSC() throws Exception {
        when(allEligibleCouples.findByCaseId("EC CASE 1")).thenReturn(new EligibleCouple("EC CASE 1", "EC NUMBER 1").withANMIdentifier("ANM X").withLocation("bherya", "Sub Center", "PHC X"));

        SafeMap reportData = new SafeMap(create("id", "EC CASE 1")
                .put("currentMethod", "iud")
                .put("newMethod", "ocp")
                .put("familyPlanningMethodChangeDate", "2012-01-01")
                .put("caste", "sc")
                .map());
        service.fpChange(reportData);

        verify(reportingService).sendReportData(ReportingData.anmReportData("ANM X", "EC CASE 1", Indicator.FP_OCP_SC, "2012-01-01"));
        verify(reportingService).sendReportData(ReportingData.serviceProvidedData("ANM X", "EC NUMBER 1", Indicator.FP_OCP_SC, "2012-01-01",
                new Location("bherya", "Sub Center", "PHC X")));
    }

    @Test
    public void shouldNotReportFPMethodChangeWhenNoIndicatorIsFoundForTheCurrentFPMethod() throws Exception {
        SafeMap reportData = new SafeMap(create("id", "EC CASE 1")
                .put("currentMethod", "none")
                .put("familyPlanningMethodChangeDate", "2012-01-01")
                .map());

        service.registerEC(reportData);

        verifyZeroInteractions(reportingService);
    }

    @Test
    public void shouldReportFemaleSterilizationAPLWhenECIsRegisteredAndEconomicStatusIsAPL() throws Exception {
        when(allEligibleCouples.findByCaseId("EC CASE 1")).thenReturn(new EligibleCouple("EC CASE 1", "EC NUMBER 1").withANMIdentifier("ANM X").withLocation("bherya", "Sub Center", "PHC X"));

        SafeMap reportData = new SafeMap(create("id", "EC CASE 1")
                .put("currentMethod", "female_sterilization")
                .put("familyPlanningMethodChangeDate", "2012-01-01")
                .put("caste", "")
                .put("economicStatus", "apl")
                .map());
        service.registerEC(reportData);

        verify(reportingService).sendReportData(ReportingData.anmReportData("ANM X", "EC CASE 1", Indicator.FP_FEMALE_STERILIZATION_APL, "2012-01-01"));
        verify(reportingService).sendReportData(ReportingData.serviceProvidedData("ANM X", "EC NUMBER 1", Indicator.FP_FEMALE_STERILIZATION_APL, "2012-01-01", new Location("bherya", "Sub Center", "PHC X")));
    }

    @Test
    public void shouldReportFemaleSterilizationBPLWhenECIsRegisteredAndEconomicStatusIsBPL() throws Exception {
        when(allEligibleCouples.findByCaseId("EC CASE 1")).thenReturn(new EligibleCouple("EC CASE 1", "EC NUMBER 1").withANMIdentifier("ANM X").withLocation("bherya", "Sub Center", "PHC X"));

        SafeMap reportData = new SafeMap(create("id", "EC CASE 1")
                .put("currentMethod", "female_sterilization")
                .put("familyPlanningMethodChangeDate", "2012-01-01")
                .put("caste", "")
                .put("economicStatus", "bpl")
                .map());
        service.registerEC(reportData);

        verify(reportingService).sendReportData(ReportingData.anmReportData("ANM X", "EC CASE 1", Indicator.FP_FEMALE_STERILIZATION_BPL, "2012-01-01"));
        verify(reportingService).sendReportData(ReportingData.serviceProvidedData("ANM X", "EC NUMBER 1", Indicator.FP_FEMALE_STERILIZATION_BPL, "2012-01-01", new Location("bherya", "Sub Center", "PHC X")));
    }

    @Test
    public void shouldNotReportFemaleSterilizationWhenECIsRegisteredAndEconomicStatusIsNotSpecified() throws Exception {
        when(allEligibleCouples.findByCaseId("EC CASE 1")).thenReturn(new EligibleCouple("EC CASE 1", "EC NUMBER 1").withANMIdentifier("ANM X").withLocation("bherya", "Sub Center", "PHC X"));

        SafeMap reportData = new SafeMap(create("id", "EC CASE 1")
                .put("currentMethod", "female_sterilization")
                .put("familyPlanningMethodChangeDate", "2012-01-01")
                .put("caste", "")
                .put("economicStatus", "")
                .map());
        service.registerEC(reportData);

        verify(reportingService).sendReportData(ReportingData.anmReportData("ANM X", "EC CASE 1", Indicator.FP_FEMALE_STERILIZATION, "2012-01-01"));
        verify(reportingService).sendReportData(ReportingData.serviceProvidedData("ANM X", "EC NUMBER 1", Indicator.FP_FEMALE_STERILIZATION, "2012-01-01", new Location("bherya", "Sub Center", "PHC X")));
        verifyNoMoreInteractions(reportingService);
    }

    @Test
    public void shouldReportFemaleSterilizationBPLWhenFPMethodIsChangedAndEconomicStatusIsBPL() throws Exception {
        when(allEligibleCouples.findByCaseId("EC CASE 1")).thenReturn(new EligibleCouple("EC CASE 1", "EC NUMBER 1").withANMIdentifier("ANM X").withLocation("bherya", "Sub Center", "PHC X"));

        SafeMap reportData = new SafeMap(create("id", "EC CASE 1")
                .put("currentMethod", "iud")
                .put("newMethod", "female_sterilization")
                .put("familyPlanningMethodChangeDate", "2012-01-01")
                .put("caste", "")
                .put("economicStatus", "bpl")
                .map());
        service.fpChange(reportData);

        verify(reportingService).sendReportData(ReportingData.anmReportData("ANM X", "EC CASE 1", Indicator.FP_FEMALE_STERILIZATION_BPL, "2012-01-01"));
        verify(reportingService).sendReportData(ReportingData.serviceProvidedData("ANM X", "EC NUMBER 1", Indicator.FP_FEMALE_STERILIZATION_BPL, "2012-01-01", new Location("bherya", "Sub Center", "PHC X")));
    }

    @Test
    public void shouldReportFemaleSterilizationAPLWhenFPMethodIsChangedAndEconomicStatusIsAPL() throws Exception {
        when(allEligibleCouples.findByCaseId("EC CASE 1")).thenReturn(new EligibleCouple("EC CASE 1", "EC NUMBER 1").withANMIdentifier("ANM X").withLocation("bherya", "Sub Center", "PHC X"));

        SafeMap reportData = new SafeMap(create("id", "EC CASE 1")
                .put("currentMethod", "iud")
                .put("newMethod", "female_sterilization")
                .put("familyPlanningMethodChangeDate", "2012-01-01")
                .put("caste", "")
                .put("economicStatus", "apl")
                .map());
        service.fpChange(reportData);

        verify(reportingService).sendReportData(ReportingData.anmReportData("ANM X", "EC CASE 1", Indicator.FP_FEMALE_STERILIZATION_APL, "2012-01-01"));
        verify(reportingService).sendReportData(ReportingData.serviceProvidedData("ANM X", "EC NUMBER 1", Indicator.FP_FEMALE_STERILIZATION_APL, "2012-01-01", new Location("bherya", "Sub Center", "PHC X")));
    }

    @Test
    public void shouldNotReportFemaleSterilizationEconomicStatusWhenFPMethodIsChangedAndEconomicStatusIsNotSpecified() throws Exception {
        when(allEligibleCouples.findByCaseId("EC CASE 1")).thenReturn(new EligibleCouple("EC CASE 1", "EC NUMBER 1").withANMIdentifier("ANM X").withLocation("bherya", "Sub Center", "PHC X"));

        SafeMap reportData = new SafeMap(create("id", "EC CASE 1")
                .put("currentMethod", "iud")
                .put("newMethod", "female_sterilization")
                .put("familyPlanningMethodChangeDate", "2012-01-01")
                .put("caste", "")
                .put("economicStatus", "")
                .map());
        service.fpChange(reportData);

        verify(reportingService).sendReportData(ReportingData.anmReportData("ANM X", "EC CASE 1", Indicator.FP_FEMALE_STERILIZATION, "2012-01-01"));
        verify(reportingService).sendReportData(ReportingData.serviceProvidedData("ANM X", "EC NUMBER 1", Indicator.FP_FEMALE_STERILIZATION, "2012-01-01", new Location("bherya", "Sub Center", "PHC X")));
        verifyNoMoreInteractions(reportingService);
    }

    @Test
    public void shouldReportQuantityWhenProvided() throws Exception {
        EligibleCouple ec = new EligibleCouple("EC CASE 1", "EC NUMBER 1").withANMIdentifier("ANM X").withLocation("bherya", "Sub Center", "PHC X");
        when(allEligibleCouples.findByCaseId("EC CASE 1")).thenReturn(ec);

        SafeMap reportData = new SafeMap(
                create("id", "EC CASE 1")
                        .put("familyPlanningMethodChangeDate", "2013-01-01")
                        .put("quantity", "10")
                        .map());
        service.reportIndicator(reportData, ec, Indicator.CONDOM_QTY);

        verify(reportingService).sendReportData(ReportingData.anmReportData("ANM X", "EC CASE 1", Indicator.CONDOM_QTY, "2013-01-01").withQuantity("10"));
        verify(reportingService).sendReportData(ReportingData.serviceProvidedData("ANM X", "EC NUMBER 1", Indicator.CONDOM_QTY, "2013-01-01",
                new Location("bherya", "Sub Center", "PHC X")).withQuantity("10"));
    }
}
