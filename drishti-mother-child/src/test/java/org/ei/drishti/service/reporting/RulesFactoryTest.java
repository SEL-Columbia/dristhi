package org.ei.drishti.service.reporting;

import org.ei.drishti.service.reporting.rules.AgeIsLessThanOneYearRule;
import org.ei.drishti.service.reporting.rules.EmptyRule;
import org.ei.drishti.service.reporting.rules.IRule;
import org.ei.drishti.service.reporting.rules.RelocationIsPermanentRule;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

public class RulesFactoryTest {

    @Mock
    private AgeIsLessThanOneYearRule ageIsLessThanOneYearRule;
    @Mock
    private RelocationIsPermanentRule relocationIsPermanentRule;

    private IRulesFactory rulesFactory;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        rulesFactory = new RulesFactory(ageIsLessThanOneYearRule, relocationIsPermanentRule);
    }

    @Test
    public void shouldLoadRuleClassByName() throws Exception{
        String ruleName = "AgeIsLessThanOneYearRule";

        IRule rule = rulesFactory.ruleByName(ruleName);

        assertTrue(rule instanceof AgeIsLessThanOneYearRule);
    }

    @Test
    public void shouldReturnEmptyRuleWhenRuleClassCannotBeFoundByName() throws Exception{
        String ruleName = "NonExistentRule";
        IRule rule = rulesFactory.ruleByName(ruleName);

        assertTrue(rule instanceof EmptyRule);
    }
}
