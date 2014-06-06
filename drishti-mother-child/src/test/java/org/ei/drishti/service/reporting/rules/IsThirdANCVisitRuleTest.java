package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.common.util.EasyMap;
import org.ei.drishti.util.SafeMap;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IsThirdANCVisitRuleTest {

    private IsThirdANCVisitRule rule;

    @Before
    public void setUp() throws Exception {
        rule = new IsThirdANCVisitRule();
    }

    @Test
    public void shouldPassWhenANCVisitIsTheThirdOne() throws Exception {
        Map<String, String> fields = EasyMap.create("ancVisitNumber", "3").map();

        assertTrue(rule.apply(new SafeMap(fields)));
    }

    @Test
    public void shouldFailWhenANCVisitIsNotTheThirdOne() throws Exception {
        Map<String, String> fields = EasyMap.create("ancVisitNumber", "4").map();

        assertFalse(rule.apply(new SafeMap(fields)));
    }
}
