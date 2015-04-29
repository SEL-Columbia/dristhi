package org.opensrp.service.reporting.rules;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.opensrp.common.util.EasyMap;
import org.opensrp.util.SafeMap;


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
