package org.ei.drishti.service.reporting;

import org.ei.drishti.service.reporting.rules.AgeIsLessThanOneYearRule;
import org.ei.drishti.service.reporting.rules.IRule;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class RulesFactoryTest {

    public IRulesFactory rulesFactory = new RulesFactory();

    @Test
    public void shouldLoadRuleClassByName() throws Exception{
        String ruleName = "AgeIsLessThanOneYearRule";

        IRule rule = rulesFactory.ruleByName(ruleName);
        assertTrue(rule instanceof AgeIsLessThanOneYearRule);
    }

    @Test(expected = ClassNotFoundException.class)
    public void shouldThrowClassNotFoundExceptionWhenRuleClassCannotBeFoundByName() throws Exception{
        String ruleName = "NonExistentRule";
        IRule rule = rulesFactory.ruleByName(ruleName);
    }
}
