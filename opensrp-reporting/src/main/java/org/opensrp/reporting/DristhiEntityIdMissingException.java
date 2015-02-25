package org.opensrp.reporting;

import org.opensrp.common.domain.ReportDataDeleteRequest;
import org.opensrp.common.domain.ReportingData;

import java.text.MessageFormat;
import java.util.ArrayList;

public class DristhiEntityIdMissingException extends Exception {
    public DristhiEntityIdMissingException(ReportDataDeleteRequest request) {
        super(MessageFormat.format("Cannot delete {0} report for the report data, as the DristhiEntityId is missing.",
                request.type()));
    }
}
