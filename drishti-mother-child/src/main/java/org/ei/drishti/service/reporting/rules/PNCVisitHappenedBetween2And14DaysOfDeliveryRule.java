package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;

import java.util.Date;

import static org.ei.drishti.common.AllConstants.CommonFormFields.REFERENCE_DATE;
import static org.ei.drishti.common.AllConstants.PNCVisitFormFields.VISIT_DATE_FIELD_NAME;

@Component
public class PNCVisitHappenedBetween2And14DaysOfDeliveryRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        long NUMBER_OF_MILLISECONDS_IN_A_DAY = 86400000;

        Date pncVisitDate = LocalDate.parse(reportFields.get(VISIT_DATE_FIELD_NAME)).toDate();
        Date deliveryDate = LocalDate.parse(reportFields.get(REFERENCE_DATE)).toDate();
        long numberOfDays = (long) Math.floor((pncVisitDate.getTime() - deliveryDate.getTime()) / NUMBER_OF_MILLISECONDS_IN_A_DAY);
        return numberOfDays > 2 && numberOfDays <= 14;
    }
}
