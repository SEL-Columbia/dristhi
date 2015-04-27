package org.opensrp.service.reporting.rules;

import static org.joda.time.LocalDate.parse;
import static org.opensrp.common.AllConstants.CommonFormFields.REFERENCE_DATE;
import static org.opensrp.common.AllConstants.CommonFormFields.SERVICE_PROVIDED_DATE;

import org.opensrp.util.SafeMap;
import org.springframework.stereotype.Component;

@Component
public class IsANCRegisteredWithinTwelveWeeksOfPregnancy implements IRule {
    public static final int NUMBER_OF_DAYS_IN_12_WEEKS = 84;

    @Override
    public boolean apply(SafeMap reportFields) {

        return !(parse(reportFields.get(SERVICE_PROVIDED_DATE)).minusDays(NUMBER_OF_DAYS_IN_12_WEEKS)
                .isAfter(parse(reportFields.get(REFERENCE_DATE))));
    }
}
