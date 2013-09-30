package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.common.AllConstants;
import org.ei.drishti.common.util.IntegerUtil;
import org.ei.drishti.util.SafeMap;
import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;

import static org.ei.drishti.common.AllConstants.CommonFormFields.REFERENCE_DATE;

@Component
public class ThirdANCVisitHappenedOnTimeRule implements IRule {

    private static final int THIRD_ANC_VISIT = 3;
    private static final int START_RANGE_FOR_THIRD_ANC_VISIT_IN_WEEKS = 28;
    private static final int END_RANGE_FOR_THIRD_ANC_VISIT_IN_WEEKS = 34;

    @Override
    public boolean apply(SafeMap reportFields) {
        LocalDate lmp = LocalDate.parse(reportFields.get(REFERENCE_DATE));
        LocalDate ancVisitDate = LocalDate.parse(reportFields.get(AllConstants.ANCFormFields.ANC_VISIT_DATE_FIELD));
        int ancVisitNumber = IntegerUtil.tryParse(reportFields.get(AllConstants.ANCFormFields.ANC_VISIT_NUMBER_FIELD), 0);

        return (ancVisitNumber == THIRD_ANC_VISIT)
                && !(lmp.plusWeeks(START_RANGE_FOR_THIRD_ANC_VISIT_IN_WEEKS).isAfter(ancVisitDate)
                || lmp.plusWeeks(END_RANGE_FOR_THIRD_ANC_VISIT_IN_WEEKS).isBefore(ancVisitDate));
    }
}
