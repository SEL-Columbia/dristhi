package org.opensrp.service.reporting.rules;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.opensrp.common.util.EasyMap.mapOf;

import org.junit.Before;
import org.junit.Test;
import org.opensrp.util.SafeMap;



public class CasteIsOthersRuleTest {

    private CasteIsOthersRule rule;

    @Before
    public void setUp() {
        rule = new CasteIsOthersRule();
    }

    @Test
    public void shouldReturnFalseWhenCasteIsNotOthers() {
        boolean didRuleSucceed = rule.apply(new SafeMap(mapOf("caste", "sc")));

        assertFalse(didRuleSucceed);

        didRuleSucceed = rule.apply(new SafeMap(mapOf("caste", "st")));

        assertFalse(didRuleSucceed);

        didRuleSucceed = rule.apply(new SafeMap(mapOf("caste", "")));

        assertFalse(didRuleSucceed);
    }

    @Test
    public void shouldReturnTrueWhenCasteIsOthers() {
        boolean didRuleSucceed = rule.apply(new SafeMap(mapOf("caste", "c_others")));

        assertTrue(didRuleSucceed);
    }
}
