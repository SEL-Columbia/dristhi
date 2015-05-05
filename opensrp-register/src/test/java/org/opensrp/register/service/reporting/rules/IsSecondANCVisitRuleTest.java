package org.opensrp.register.service.reporting.rules;

import org.opensrp.common.util.EasyMap;
import org.opensrp.util.SafeMap;
import org.junit.Before;
import org.junit.Test;
import org.opensrp.register.service.reporting.rules.IsSecondANCVisitRule;

import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class IsSecondANCVisitRuleTest {

    private IsSecondANCVisitRule rule;

    @Before
    public void setUp() throws Exception {
        rule = new IsSecondANCVisitRule();
    }

    @Test
    public void shouldPassWhenANCVisitIsTheSecondOne() throws Exception {
        Map<String, String> fields = EasyMap.create("ancVisitNumber", "2").map();

        assertTrue(rule.apply(new SafeMap(fields)));
    }

    @Test
    public void shouldFailWhenANCVisitIsNotTheSecondOne() throws Exception {
        Map<String, String> fields = EasyMap.create("ancVisitNumber", "1").map();

        assertFalse(rule.apply(new SafeMap(fields)));
    }
}
