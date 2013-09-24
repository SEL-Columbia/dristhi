package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.MockitoAnnotations.initMocks;

public class AgeIsLessThanOneYearRuleTest {

    private AgeIsLessThanOneYearRule ageIsLessThanOneYearRule;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        ageIsLessThanOneYearRule = new AgeIsLessThanOneYearRule();
    }

    @Test
    public void shouldReturnTrueIfAgeIsLessThanOneYear() {
        SafeMap safeMap = new SafeMap();
        safeMap.put("entityId", "child id 1");
        safeMap.put("submissionDate", "2012-03-01");
        safeMap.put("dateOfBirth", "2012-01-01");

        boolean didRuleApply = ageIsLessThanOneYearRule.apply(safeMap);

        assertTrue(didRuleApply);
    }

    @Test
    public void shouldReturnFalseIfAgeIsNotLessThanOneYear() {
        SafeMap safeMap = new SafeMap();
        safeMap.put("entityId", "child id 1");
        safeMap.put("submissionDate", "2012-03-01");
        safeMap.put("dateOfBirth", "2011-01-01");

        boolean didRuleApply = ageIsLessThanOneYearRule.apply(safeMap);

        assertFalse(didRuleApply);
    }
}
