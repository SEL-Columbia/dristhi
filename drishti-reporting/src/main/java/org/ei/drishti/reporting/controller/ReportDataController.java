package org.ei.drishti.reporting.controller;

import com.google.gson.Gson;
import org.ei.drishti.common.domain.ANMReport;
import org.ei.drishti.common.domain.ReportDataUpdateRequest;
import org.ei.drishti.common.domain.ReportingData;
import org.ei.drishti.common.domain.ServiceProvidedReportDTO;
import org.ei.drishti.reporting.ReportDataMissingException;
import org.ei.drishti.reporting.domain.ANMReportData;
import org.ei.drishti.reporting.domain.ServiceProvided;
import org.ei.drishti.reporting.repository.ANMReportsRepository;
import org.ei.drishti.reporting.repository.ServicesProvidedRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import static org.ei.drishti.common.AllConstants.ReportDataParameters;

@Controller
public class ReportDataController {
    private ServicesProvidedRepository servicesProvidedRepository;
    private ANMReportsRepository anmReportsRepository;
    private static final Logger logger = LoggerFactory.getLogger(ReportDataController.class);

    @Autowired
    public ReportDataController(ServicesProvidedRepository servicesProvidedRepository, ANMReportsRepository anmReportsRepository) {
        this.servicesProvidedRepository = servicesProvidedRepository;
        this.anmReportsRepository = anmReportsRepository;
    }

    @RequestMapping(value = "/report/submit", method = RequestMethod.POST)
    @ResponseBody
    public String submit(@RequestBody ReportingData reportingData) throws ReportDataMissingException {
        logger.info("Reporting on: " + reportingData);
        if (ReportDataParameters.SERVICE_PROVIDED_DATA_TYPE.equals(reportingData.type())) {
            throwExceptionIfMandatoryDataIsNotPresentForServiceProvidedReport(reportingData);
            servicesProvidedRepository.save(
                    reportingData.get(ReportDataParameters.ANM_IDENTIFIER),
                    reportingData.get(ReportDataParameters.SERVICE_PROVIDER_TYPE),
                    reportingData.get(ReportDataParameters.EXTERNAL_ID),
                    reportingData.get(ReportDataParameters.INDICATOR),
                    reportingData.get(ReportDataParameters.SERVICE_PROVIDED_DATE),
                    reportingData.get(ReportDataParameters.VILLAGE),
                    reportingData.get(ReportDataParameters.SUB_CENTER),
                    reportingData.get(ReportDataParameters.PHC),
                    reportingData.get(ReportDataParameters.QUANTITY));
        } else if (ReportDataParameters.ANM_REPORT_DATA_TYPE.equals(reportingData.type())) {
            throwExceptionIfMandatoryDataIsNotPresentForANMReport(reportingData);
            anmReportsRepository.save(
                    reportingData.get(ReportDataParameters.ANM_IDENTIFIER),
                    reportingData.get(ReportDataParameters.EXTERNAL_ID),
                    reportingData.get(ReportDataParameters.INDICATOR),
                    reportingData.get(ReportDataParameters.SERVICE_PROVIDED_DATE),
                    reportingData.get(ReportDataParameters.QUANTITY));
        }
        return "Success.";
    }

    @RequestMapping(value = "/report/month", method = RequestMethod.GET)
    @ResponseBody
    public String reportForCurrentReportingMonth(@RequestParam("type") String type, @RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate, @RequestParam(value = "anmId", defaultValue = "", required = false) String anmId) throws ReportDataMissingException {
        logger.info("Reporting on: " + type);
        String reportJson = "";
        if (ReportDataParameters.SERVICE_PROVIDED_DATA_TYPE.equals(type)) {
            List result = servicesProvidedRepository.getReportsFor(startDate, endDate);
            List<ServiceProvidedReportDTO> serviceProvidedReportDTOs = buildServiceProvidedReportDTO(result);
            reportJson = new Gson().toJson(serviceProvidedReportDTOs);
        } else if (ReportDataParameters.ANM_REPORT_DATA_TYPE.equals(type)) {
            List result = anmReportsRepository.getReportsFor(anmId, startDate, endDate);
            List<ANMReportDTO> anmReportDTOs = buildAnmReportsReportDTO(result);
            reportJson = new Gson().toJson(anmReportDTOs);
        }
        return reportJson;
    }

    private List<ANMReportDTO> buildAnmReportsReportDTO(List result) {
        List<ANMReportDTO> anmReportDTOList = new ArrayList<>();
        for (Object object : result) {
            ANMReportData anmReport = (ANMReportData) object;
            anmReportDTOList.add(new ANMReportDTO(
                    anmReport.id(),
                    anmReport.indicator().indicator(),
                    anmReport.anmIdentifier(),
                    anmReport.date().date().toString()));
        }
        return anmReportDTOList;
    }

    private List<ServiceProvidedReportDTO> buildServiceProvidedReportDTO(List result) {
        List<ServiceProvidedReportDTO> serviceProvidedReportDTOs = new ArrayList<>();
        for (Object object : result) {
            ServiceProvided serviceProvided = (ServiceProvided) object;
            serviceProvidedReportDTOs.add(new ServiceProvidedReportDTO(
                    serviceProvided.id(),
                    serviceProvided.serviceProviderType(),
                    serviceProvided.indicator(),
                    serviceProvided.date(),
                    serviceProvided.location().village(),
                    serviceProvided.location().subCenter(),
                    serviceProvided.location().phc()));
        }
        return serviceProvidedReportDTOs;
    }


    @RequestMapping(headers = {"Accept=application/json"}, value = "/report/update", method = RequestMethod.POST)
    @ResponseBody
    public String updateReports(@RequestBody ReportDataUpdateRequest request) throws ReportDataMissingException {
        logger.info(MessageFormat.format("Flushing reports for reporting month {0} to {1} for indicator {2} for service type {3}",
                request.startDate(), request.endDate(), request.indicator(), request.type()));
        List<ReportingData> reportingData = request.reportingData();
        if (ReportDataParameters.SERVICE_PROVIDED_DATA_TYPE.equals(request.type())) {
            throwExceptionIfMandatoryDataIsNotPresentForServiceProvidedReport(reportingData);
            servicesProvidedRepository.update(request);
        } else if (ReportDataParameters.ANM_REPORT_DATA_TYPE.equals(request.type())) {
            throwExceptionIfMandatoryDataIsNotPresentForANMReport(reportingData);
            anmReportsRepository.update(request);
        }
        return "Success.";
    }

    private void throwExceptionIfMandatoryDataIsNotPresentForANMReport(ReportingData reportingData) throws ReportDataMissingException {
        ArrayList missingData = reportingData.getMissingReportDataForANMReport();
        if (!missingData.isEmpty()) {
            throw new ReportDataMissingException(reportingData, missingData);
        }
    }

    private void throwExceptionIfMandatoryDataIsNotPresentForServiceProvidedReport(ReportingData reportingData) throws ReportDataMissingException {
        ArrayList missingData = reportingData.getMissingReportDataForServiceProvided();
        if (!missingData.isEmpty()) {
            throw new ReportDataMissingException(reportingData, missingData);
        }
    }

    private void throwExceptionIfMandatoryDataIsNotPresentForANMReport(List<ReportingData> reportingData) throws ReportDataMissingException {
        for (ReportingData data : reportingData) {
            throwExceptionIfMandatoryDataIsNotPresentForANMReport(data);
        }
    }

    private void throwExceptionIfMandatoryDataIsNotPresentForServiceProvidedReport(List<ReportingData> reportingData) throws ReportDataMissingException {
        for (ReportingData data : reportingData) {
            throwExceptionIfMandatoryDataIsNotPresentForServiceProvidedReport(data);
        }
    }

    @RequestMapping(value = "/report/fetchForAllANMs", method = RequestMethod.GET)
    @ResponseBody
    public List<ANMReport> getAllANMsIndicatorSummaries() {
        return anmReportsRepository.fetchAllANMsReport();
    }
}

