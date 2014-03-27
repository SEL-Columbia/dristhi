package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.springframework.stereotype.Component;

import static org.ei.drishti.common.AllConstants.ANCFormFields.REGISTRATION_DATE;
import static org.ei.drishti.common.AllConstants.CommonFormFields.REFERENCE_DATE;
import static org.joda.time.LocalDate.parse;

@Component
public class IsANCRegisteredWithinTwelveWeeksOfPregnancy implements IRule {
    public static final int NUMBER_OF_DAYS_IN_12_WEEKS = 84;

    @Override
    public boolean apply(SafeMap reportFields) {

        return !(parse(reportFields.get(REGISTRATION_DATE)).minusDays(NUMBER_OF_DAYS_IN_12_WEEKS)
                .isAfter(parse(reportFields.get(REFERENCE_DATE))));
    }
}
