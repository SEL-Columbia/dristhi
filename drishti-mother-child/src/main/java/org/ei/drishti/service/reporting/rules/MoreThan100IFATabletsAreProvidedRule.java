package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.common.AllConstants;
import org.ei.drishti.common.util.IntegerUtil;
import org.ei.drishti.util.SafeMap;
import org.springframework.stereotype.Component;

@Component
public class MoreThan100IFATabletsAreProvidedRule implements IRule {

    @Override
    public boolean apply(SafeMap reportFields) {
        int totalNumberOfIFATabletsGiven =
                IntegerUtil.tryParse(reportFields.get(AllConstants.IFAFields.TOTAL_NUMBER_OF_IFA_TABLETS_GIVEN), 0);
        int numberOfIFATabletsGivenThisTime =
                IntegerUtil.tryParse(reportFields.get(AllConstants.IFAFields.NUMBER_OF_IFA_TABLETS_GIVEN), 0);

        return totalNumberOfIFATabletsGiven >= 100
                && totalNumberOfIFATabletsGiven - numberOfIFATabletsGivenThisTime < 100;
    }
}
