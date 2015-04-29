package org.opensrp.service.reporting.rules;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.opensrp.common.util.EasyMap.mapOf;

import org.junit.Before;
import org.junit.Test;
import org.opensrp.util.SafeMap;


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
