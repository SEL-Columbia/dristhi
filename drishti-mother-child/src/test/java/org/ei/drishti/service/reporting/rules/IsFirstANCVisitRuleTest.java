package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.common.util.EasyMap;
import org.ei.drishti.util.SafeMap;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IsFirstANCVisitRuleTest {

    private IsFirstANCVisitRule rule;

    @Before
    public void setUp() throws Exception {
        rule = new IsFirstANCVisitRule();
    }

    @Test
    public void shouldPassWhenANCVisitIsTheFirstOne() throws Exception {
        Map<String, String> fields = EasyMap.create("ancVisitNumber", "1").map();

        assertTrue(rule.apply(new SafeMap(fields)));
    }

    @Test
    public void shouldFailWhenANCVisitIsNotTheFirstOne() throws Exception {
        Map<String, String> fields = EasyMap.create("ancVisitNumber", "2").map();

        assertFalse(rule.apply(new SafeMap(fields)));
    }
}
