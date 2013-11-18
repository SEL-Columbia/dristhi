package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.SafeMap;
import org.junit.Before;
import org.junit.Test;

import static org.ei.drishti.common.util.EasyMap.mapOf;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IsJsyBeneficiaryRuleTest {

    private IsJsyBeneficiaryRule rule;

    @Before
    public void setUp() throws Exception {
        rule = new IsJsyBeneficiaryRule();
    }

    @Test
    public void shouldReturnFalseWhenJsyBeneficiaryIsSetToFalse() throws Exception {
        boolean isJsyBeneficiary = rule.apply(new SafeMap(mapOf("isJSYBeneficiary", "no")));
        assertFalse(isJsyBeneficiary);
    }


    @Test
    public void shouldReturnTrueWhenJsyBeneficiaryIsSetToTrue() throws Exception {
        boolean isJsyBeneficiary = rule.apply(new SafeMap(mapOf("isJSYBeneficiary", "yes")));
        assertTrue(isJsyBeneficiary);
    }
}
