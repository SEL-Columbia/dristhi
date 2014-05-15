package org.ei.drishti.reporting;

import org.ei.drishti.common.domain.ReportDataDeleteRequest;
import org.ei.drishti.common.domain.ReportingData;

import java.text.MessageFormat;
import java.util.ArrayList;

public class DristhiEntityIdMissingException extends Exception {
    public DristhiEntityIdMissingException(ReportDataDeleteRequest request) {
        super(MessageFormat.format("Cannot delete {0} report for the report data, as the DristhiEntityId is missing.",
                request.type()));
    }
}
