package org.ei.drishti.service.reporting;

import org.ei.drishti.service.reporting.rules.*;
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
    @Mock
    private CurrentFPMethodIsCondomRule currentFPMethodIsCondomRule;

    private IRulesFactory rulesFactory;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        rulesFactory = new RulesFactory(ageIsLessThanOneYearRule, relocationIsPermanentRule, currentFPMethodIsCondomRule);
    }

    @Test
    public void shouldLoadAgeIsLessThanOneYearRuleClassByName() throws Exception{
        String ruleName = "AgeIsLessThanOneYearRule";

        IRule rule = rulesFactory.ruleByName(ruleName);

        assertTrue(rule instanceof AgeIsLessThanOneYearRule);
    }

    @Test
    public void shouldLoadCurrentFPMethodIsCondomRuleClassByName() throws Exception{
        String ruleName = "CurrentFPMethodIsCondomRule";

        IRule rule = rulesFactory.ruleByName(ruleName);

        assertTrue(rule instanceof CurrentFPMethodIsCondomRule);
    }

    @Test
    public void shouldLoadRelocationIsPermanentRuleClassByName() throws Exception{
        String ruleName = "RelocationIsPermanentRule";

        IRule rule = rulesFactory.ruleByName(ruleName);

        assertTrue(rule instanceof RelocationIsPermanentRule);
    }

    @Test
    public void shouldReturnEmptyRuleWhenRuleClassCannotBeFoundByName() throws Exception{
        String ruleName = "NonExistentRule";
        IRule rule = rulesFactory.ruleByName(ruleName);

        assertTrue(rule instanceof EmptyRule);
    }
}
