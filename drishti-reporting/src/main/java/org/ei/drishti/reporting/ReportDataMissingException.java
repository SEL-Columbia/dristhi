package org.ei.drishti.reporting;

import org.ei.drishti.common.domain.ReportingData;

import java.text.MessageFormat;
import java.util.ArrayList;

public class ReportDataMissingException extends Exception {
    public ReportDataMissingException(ReportingData reportingData, ArrayList missingData) {
        super(MessageFormat.format("Cannot report for the report data: {0}, as the following report data is missing: {1}",
                reportingData, missingData));
    }
}
