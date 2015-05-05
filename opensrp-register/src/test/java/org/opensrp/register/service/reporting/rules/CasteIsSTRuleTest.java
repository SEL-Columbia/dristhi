package org.opensrp.register.service.reporting.rules;

import org.opensrp.util.SafeMap;
import org.junit.Before;
import org.junit.Test;
import org.opensrp.register.service.reporting.rules.CasteIsSTRule;

import static org.opensrp.common.util.EasyMap.mapOf;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;



public class CasteIsSTRuleTest {

    private CasteIsSTRule rule;

    @Before
    public void setUp() {
        rule = new CasteIsSTRule();
    }

    @Test
    public void shouldReturnFalseWhenCasteIsNotST() {
        boolean didRuleSucceed = rule.apply(new SafeMap(mapOf("caste", "sc")));

        assertFalse(didRuleSucceed);

        didRuleSucceed = rule.apply(new SafeMap(mapOf("caste", "c_others")));

        assertFalse(didRuleSucceed);

        didRuleSucceed = rule.apply(new SafeMap(mapOf("caste", "")));

        assertFalse(didRuleSucceed);
    }

    @Test
    public void shouldReturnTrueWhenCasteIsSC() {
        boolean didRuleSucceed = rule.apply(new SafeMap(mapOf("caste", "st")));

        assertTrue(didRuleSucceed);
    }
}
