package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.domain.Child;
import org.ei.drishti.repository.AllChildren;
import org.ei.drishti.util.SafeMap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class AgeIsLessThanOneYearRuleTest {

    private AgeIsLessThanOneYearRule ageIsLessThanOneYearRule;

    @Mock
    private AllChildren allChildren;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        ageIsLessThanOneYearRule = new AgeIsLessThanOneYearRule(allChildren);
    }

    @Test
    public void shouldReturnTrueIfAgeIsLessThanOneYear() {
        Child child = new Child("child id 1", "mother id 1", "opv", "2", "female").withDateOfBirth("2012-01-01");

        when(allChildren.findByCaseId("child id 1")).thenReturn(child);

        SafeMap safeMap = new SafeMap();
        safeMap.put("entityId", "child id 1");
        safeMap.put("submissionDate", "2012-03-01");
        boolean didRuleApply = ageIsLessThanOneYearRule.apply(safeMap);

        assertTrue(didRuleApply);
    }

    @Test
    public void shouldReturnFalseIfAgeIsNotLessThanOneYear() {
        Child child = new Child("child id 1", "mother id 1", "opv", "2", "female").withDateOfBirth("2011-01-01");

        when(allChildren.findByCaseId("child id 1")).thenReturn(child);

        SafeMap safeMap = new SafeMap();
        safeMap.put("entityId", "child id 1");
        safeMap.put("submissionDate", "2012-03-01");
        boolean didRuleApply = ageIsLessThanOneYearRule.apply(safeMap);

        assertFalse(didRuleApply);
    }
}
