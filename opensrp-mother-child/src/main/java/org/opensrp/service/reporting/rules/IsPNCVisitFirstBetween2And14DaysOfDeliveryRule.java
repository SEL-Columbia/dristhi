package org.opensrp.service.reporting.rules;

import org.opensrp.util.SafeMap;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;

import static org.opensrp.common.AllConstants.CommonFormFields.REFERENCE_DATE;
import static org.opensrp.common.AllConstants.PNCVisitFormFields.VISIT_DATES_FIELD_NAME;
import static org.opensrp.common.AllConstants.PNCVisitFormFields.VISIT_DATE_FIELD_NAME;

@Component
public class IsPNCVisitFirstBetween2And14DaysOfDeliveryRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        String pncVisitDate = reportFields.get(VISIT_DATE_FIELD_NAME);
        String pncVisitDates = reportFields.get(VISIT_DATES_FIELD_NAME);
        LocalDate dateOfBirth = LocalDate.parse(reportFields.get(REFERENCE_DATE));
        if (pncVisitDates != null) {
            String[] pncVisitDatesList = pncVisitDates.split(" ");
            for (String date : pncVisitDatesList) {
                if (LocalDate.parse(date).isBefore(dateOfBirth.plusDays(3))) {
                    continue;
                }
                return (date.equalsIgnoreCase(pncVisitDate));
            }
        }
        return false;
    }
}
