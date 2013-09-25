package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.junit.Before;
import org.junit.Test;

import static org.ei.drishti.util.EasyMap.mapOf;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class JsyBeneficiaryIsTrueRuleTest {

    private JsyBeneficiaryIsTrueRule rule;

    @Before
    public void setUp() throws Exception {
        rule = new JsyBeneficiaryIsTrueRule();
    }

    @Test
    public void shouldReturnFalseWhenJsyBeneficiaryIsSetToFalse() throws Exception {
        assertFalse(rule.apply(new SafeMap(mapOf("isJSYBeneficiary","no"))));


    }


    @Test
    public void shouldReturnTrueWhenJsyBeneficiaryIsSetToTrue() throws Exception {
        assertTrue(rule.apply(new SafeMap(mapOf("isJSYBeneficiary", "yes"))));
    }
}
