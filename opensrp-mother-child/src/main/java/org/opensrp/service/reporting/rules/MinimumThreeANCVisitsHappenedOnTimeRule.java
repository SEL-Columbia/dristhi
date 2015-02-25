package org.opensrp.service.reporting.rules;

import org.opensrp.common.AllConstants;
import org.opensrp.domain.Mother;
import org.opensrp.util.SafeMap;
import org.joda.time.LocalDate;
import org.opensrp.repository.AllMothers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.opensrp.common.AllConstants.CommonFormFields.ID;
import static org.opensrp.common.AllConstants.CommonFormFields.REFERENCE_DATE;

@Component
public class MinimumThreeANCVisitsHappenedOnTimeRule implements IRule {

    private static final int MINIMUM_NUMBER_OF_ANC_VISITS = 3;
    private static final int START_RANGE_FOR_THIRD_ANC_VISIT_IN_WEEKS = 28;
    private static final int END_RANGE_FOR_THIRD_ANC_VISIT_IN_WEEKS = 40;
    private AllMothers mothers;

    @Autowired
    public MinimumThreeANCVisitsHappenedOnTimeRule(AllMothers mothers) {
        this.mothers = mothers;
    }

    @Override
    public boolean apply(SafeMap reportFields) {
        LocalDate lmp = LocalDate.parse(reportFields.get(REFERENCE_DATE));
        LocalDate ancVisitDate = LocalDate.parse(reportFields.get(AllConstants.ANCFormFields.ANC_VISIT_DATE_FIELD));
        Mother mother = mothers.findByCaseId(reportFields.get(ID));
        int numberOfANCVisitsHappened = mother.ancVisits().size();

        return (numberOfANCVisitsHappened == MINIMUM_NUMBER_OF_ANC_VISITS)
                && !(lmp.plusWeeks(START_RANGE_FOR_THIRD_ANC_VISIT_IN_WEEKS).isAfter(ancVisitDate)
                || lmp.plusWeeks(END_RANGE_FOR_THIRD_ANC_VISIT_IN_WEEKS).isBefore(ancVisitDate));
    }
}
