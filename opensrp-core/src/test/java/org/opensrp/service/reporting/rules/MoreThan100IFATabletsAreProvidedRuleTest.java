package org.opensrp.service.reporting.rules;

import org.opensrp.util.SafeMap;
import org.junit.Before;
import org.junit.Test;
import org.opensrp.service.reporting.rules.MoreThan100IFATabletsAreProvidedRule;

import java.util.Map;

import static org.opensrp.common.util.EasyMap.create;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;


public class MoreThan100IFATabletsAreProvidedRuleTest {

    private MoreThan100IFATabletsAreProvidedRule rule;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        rule = new MoreThan100IFATabletsAreProvidedRule();
    }

    @Test
    public void shouldReturnTrueWhenMoreThan100IFATabletsAreProvidedSoFar() throws Exception {
        Map<String, String> reportFields = create("totalNumberOfIFATabletsGiven", "100")
                .put("numberOfIFATabletsGiven", "60")
                .map();

        assertTrue(rule.apply(new SafeMap(reportFields)));
    }

    @Test
    public void shouldReturnFalseWhenLessThan100IFATabletsAreProvidedSoFar() throws Exception {
        Map<String, String> reportFields = create("totalNumberOfIFATabletsGiven", "99")
                .put("numberOfIFATabletsGiven", "60")
                .map();

        assertFalse(rule.apply(new SafeMap(reportFields)));
    }

    @Test
    public void shouldReturnFalseWhenMoreThan100IFATabletsAreProvidedSoFarButThisIsNotTheFirstTime() throws Exception {
        Map<String, String> reportFields = create("totalNumberOfIFATabletsGiven", "120")
                .put("numberOfIFATabletsGiven", "20")
                .map();

        assertFalse(rule.apply(new SafeMap(reportFields)));
    }
}
