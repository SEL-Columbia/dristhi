package org.ei.drishti.service.reporting.rules;

import junit.framework.Assert;
import org.ei.drishti.util.SafeMap;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;
import static org.ei.drishti.util.EasyMap.create;
import static org.ei.drishti.util.EasyMap.mapOf;
import static org.junit.Assert.assertFalse;

public class IsAllImmunizationsGivenEitherWithMeaslesOrMMRRuleTest {
    private IsAllImmunizationsGivenEitherWithMeaslesOrMMRRule rule;

    @Before
    public void setUp() throws Exception {
        rule = new IsAllImmunizationsGivenEitherWithMeaslesOrMMRRule();
    }

    @Test
    public void shouldReturnTrueIfAllImmunizationsWereGiven() {
        SafeMap safeMap = new SafeMap(
                mapOf("immunizationsGiven", "bcg opv_0 hepb_0 opv_1 pentavalent_1 opv_2 pentavalent_2 opv_3 pentavalent_3 measles"));

        boolean didRuleApply = rule.apply(safeMap);
        assertTrue(didRuleApply);

        safeMap = new SafeMap(
                mapOf("immunizationsGiven", "bcg opv_0 hepb_0 opv_1 pentavalent_1 opv_2 pentavalent_2 opv_3 pentavalent_3 mmr"));

        didRuleApply = rule.apply(safeMap);
        assertTrue(didRuleApply);

        didRuleApply = rule.apply(new SafeMap(
                create("immunizationsGiven", "bcg opv_0 hepb_0 opv_1 pentavalent_1 opv_2 pentavalent_2 opv_3 pentavalent_3 measles")
                        .put("previousImmunizations", "bcg opv_0 hepb_0 opv_1 pentavalent_1 opv_2 pentavalent_2 opv_3 pentavalent_3")
                        .map()));
        Assert.assertTrue(didRuleApply);

        didRuleApply = rule.apply(new SafeMap(
                create("immunizationsGiven", "bcg opv_0 hepb_0 opv_1 pentavalent_1 opv_2 pentavalent_2 opv_3 pentavalent_3 mmr")
                        .put("previousImmunizations", "bcg opv_0 hepb_0 opv_1 pentavalent_1 opv_2 pentavalent_2 opv_3 pentavalent_3")
                        .map()));
        Assert.assertTrue(didRuleApply);

        didRuleApply = rule.apply(new SafeMap(
                create("immunizationsGiven", "bcg opv_0 opv_1 pentavalent_1 opv_2 pentavalent_2 opv_3 pentavalent_3 measles")
                        .put("previousImmunizations", "")
                        .map()));
        Assert.assertTrue(didRuleApply);

        didRuleApply = rule.apply(new SafeMap(
                create("immunizationsGiven", "bcg opv_0 opv_1 pentavalent_1 opv_2 pentavalent_2 opv_3 pentavalent_3 mmr")
                        .put("previousImmunizations", "")
                        .map()));
        Assert.assertTrue(didRuleApply);

        didRuleApply = rule.apply(new SafeMap(
                create("immunizationsGiven", "bcg opv_0 opv_1 pentavalent_1 opv_2 pentavalent_2 opv_3 pentavalent_3 measles")
                        .put("previousImmunizations", null)
                        .map()));
        Assert.assertTrue(didRuleApply);

        didRuleApply = rule.apply(new SafeMap(
                create("immunizationsGiven", "bcg opv_0 opv_1 pentavalent_1 opv_2 pentavalent_2 opv_3 pentavalent_3 mmr")
                        .put("previousImmunizations", null)
                        .map()));
        Assert.assertTrue(didRuleApply);
    }

    @Test
    public void shouldReturnFalseIfAllImmunizationsWereNotGiven() {
        boolean didRuleApply = rule.apply(new SafeMap(mapOf("immunizationsGiven", "")));
        assertFalse(didRuleApply);

        didRuleApply = rule.apply(new SafeMap().put("immunizationsGiven", null));
        assertFalse(didRuleApply);

        didRuleApply = rule.apply(
                new SafeMap(
                        mapOf("immunizationsGiven", "bcg opv_0 opv_1 pentavalent_1 opv_2 pentavalent_2 opv_3 pentavalent_3")));
        assertFalse(didRuleApply);

        didRuleApply = rule.apply(
                new SafeMap(
                        create("immunizationsGiven", "bcg opv_0 opv_1 pentavalent_1 opv_2 pentavalent_2 pentavalent_3 measles")
                                .put("previousImmunizations", "measles").map()));
        Assert.assertFalse(didRuleApply);

        didRuleApply = rule.apply(
                new SafeMap(
                        create("immunizationsGiven", "bcg opv_0 opv_1 pentavalent_1 opv_2 pentavalent_2 pentavalent_3 mmr")
                                .put("previousImmunizations", "mmr").map()));
        Assert.assertFalse(didRuleApply);
    }
}
