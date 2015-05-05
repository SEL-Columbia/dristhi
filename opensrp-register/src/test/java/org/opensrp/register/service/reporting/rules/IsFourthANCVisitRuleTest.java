package org.opensrp.register.service.reporting.rules;

import org.opensrp.common.util.EasyMap;
import org.opensrp.util.SafeMap;
import org.junit.Before;
import org.junit.Test;
import org.opensrp.register.service.reporting.rules.IsFourthANCVisitRule;

import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class IsFourthANCVisitRuleTest {

    private IsFourthANCVisitRule rule;

    @Before
    public void setUp() throws Exception {
        rule = new IsFourthANCVisitRule();
    }

    @Test
    public void shouldPassWhenANCVisitIsTheFourthOne() throws Exception {
        Map<String, String> fields = EasyMap.create("ancVisitNumber", "4").map();

        assertTrue(rule.apply(new SafeMap(fields)));
    }

    @Test
    public void shouldFailWhenANCVisitIsNotTheFourthOne() throws Exception {
        Map<String, String> fields = EasyMap.create("ancVisitNumber", "1").map();

        assertFalse(rule.apply(new SafeMap(fields)));
    }
}
