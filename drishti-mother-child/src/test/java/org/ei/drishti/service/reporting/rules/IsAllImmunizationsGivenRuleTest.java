package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.ei.drishti.common.util.EasyMap.create;
import static org.ei.drishti.common.util.EasyMap.mapOf;

public class IsAllImmunizationsGivenRuleTest {
    private IsAllImmunizationsGivenRule rule;

    @Before
    public void setUp() throws Exception {
        rule = new IsAllImmunizationsGivenRule();
    }

    @Test
    public void shouldReturnTrueIfAllImmunizationsWereGiven() {
        SafeMap safeMap = new SafeMap(
                mapOf("immunizationsGiven", "bcg opv_0 hepb_0 opv_1 pentavalent_1 opv_2 pentavalent_2 opv_3 pentavalent_3 measles"));

        boolean didRuleApply = rule.apply(safeMap);
        assertTrue(didRuleApply);

        didRuleApply = rule.apply(new SafeMap(
                create("immunizationsGiven", "bcg pentavalent_3 mmr")
                        .put("previousImmunizations", "opv_0 hepb_0 opv_1 pentavalent_1 opv_2 pentavalent_2 opv_3")
                        .map()));
        assertTrue(didRuleApply);

        didRuleApply = rule.apply(new SafeMap(
                create("immunizationsGiven", "measles")
                        .put("previousImmunizations", "")
                        .map()));
        assertTrue(didRuleApply);

        didRuleApply = rule.apply(new SafeMap(
                create("immunizationsGiven", "measles")
                        .put("previousImmunizations", null)
                        .map()));
        assertTrue(didRuleApply);
    }

    @Test
    public void shouldReturnFalseIfAllImmunizationsWereNotGiven() {
        SafeMap safeMap = new SafeMap(
                mapOf("immunizationsGiven", "bcg opv_0 hepb_0 opv_1 pentavalent_1 opv_2 pentavalent_2 opv_3 pentavalent_3"));

        boolean didRuleApply = rule.apply(safeMap);
        assertFalse(didRuleApply);

        didRuleApply = rule.apply(new SafeMap(
                create("immunizationsGiven", "mmr")
                        .put("previousImmunizations", "measles")
                        .map()));
        assertFalse(didRuleApply);

        didRuleApply = rule.apply(new SafeMap(
                create("immunizationsGiven", "bcg")
                        .put("previousImmunizations", "")
                        .map()));
        assertFalse(didRuleApply);

        didRuleApply = rule.apply(new SafeMap(
                create("immunizationsGiven", "bcg")
                        .put("previousImmunizations", null)
                        .map()));
        assertFalse(didRuleApply);
    }
}
