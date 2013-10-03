package org.ei.drishti.integration;

import org.ei.drishti.service.reporting.RuleNotFoundException;
import org.ei.drishti.service.reporting.RulesFactory;
import org.ei.drishti.service.reporting.rules.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/applicationContext-drishti-web.xml")
public class RulesFactoryTest {

    @Autowired
    private RulesFactory rulesFactory;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test(expected = RuleNotFoundException.class)
    public void shouldThrowExceptionWhenRuleClassCannotBeFoundByName() throws Exception {
        String ruleName = "NonExistentRule";
        IRule rule = rulesFactory.ruleByName(ruleName);
    }

    @Test
    public void shouldLoadAgeIsLessThanOneYearRuleClassByName() throws Exception {
        String ruleName = "AgeIsLessThanOneYearRule";

        IRule rule = rulesFactory.ruleByName(ruleName);

        assertTrue(rule instanceof AgeIsLessThanOneYearRule);
    }

    @Test
    public void shouldLoadCurrentFPMethodIsCondomRuleClassByName() throws Exception {
        String ruleName = "CurrentFPMethodIsCondomRule";

        IRule rule = rulesFactory.ruleByName(ruleName);

        assertTrue(rule instanceof CurrentFPMethodIsCondomRule);
    }

    @Test
    public void shouldLoadRelocationIsPermanentRuleClassByName() throws Exception {
        String ruleName = "RelocationIsPermanentRule";

        IRule rule = rulesFactory.ruleByName(ruleName);

        assertTrue(rule instanceof RelocationIsPermanentRule);
    }

    @Test
    public void shouldLoadNewFPMethodIsCondomRuleClassByName() throws Exception {
        String ruleName = "NewFPMethodIsCondomRule";

        IRule rule = rulesFactory.ruleByName(ruleName);

        assertTrue(rule instanceof NewFPMethodIsCondomRule);
    }

    @Test
    public void shouldLoadMoreThanOneCondomSuppliedRuleRuleClassByName() throws Exception {
        String ruleName = "MoreThanZeroCondomsSuppliedRule";

        IRule rule = rulesFactory.ruleByName(ruleName);

        assertTrue(rule instanceof MoreThanZeroCondomsSuppliedRule);
    }


    @Test
    public void shouldLoadJsyBeneficiaryIsTrueRuleRuleClassByName() throws Exception {
        String ruleName = "IsJsyBeneficiaryRule";

        IRule rule = rulesFactory.ruleByName(ruleName);

        assertTrue(rule instanceof IsJsyBeneficiaryRule);
    }

    @Test
    public void shouldLoadThirdANCVisitHappenedOnTimeRuleClassByName() throws Exception {
        String ruleName = "ThirdANCVisitHappenedOnTimeRule";

        IRule rule = rulesFactory.ruleByName(ruleName);

        assertTrue(rule instanceof ThirdANCVisitHappenedOnTimeRule);
    }

    @Test
    public void shouldLoadServiceProvidedAtSub_CenterRuleClassByName() throws Exception {
        String ruleName = "ServiceProvidedAtSub_CenterRule";

        IRule rule = rulesFactory.ruleByName(ruleName);

        assertTrue(rule instanceof ServiceProvidedAtSub_CenterRule);
    }

    @Test
    public void shouldLoadIsPersonAnaemicRuleRuleClassByName() throws Exception {
        String ruleName = "IsPersonAnaemicRule";

        IRule rule = rulesFactory.ruleByName(ruleName);

        assertTrue(rule instanceof IsPersonAnaemicRule);
    }

    @Test
    public void shouldLoadDeliveryIsAttendedBySBATrainedPersonRuleClassByName() throws Exception {
        String ruleName = "DeliveryIsAttendedBySBATrainedPersonRule";

        IRule rule = rulesFactory.ruleByName(ruleName);

        assertTrue(rule instanceof DeliveryIsAttendedBySBATrainedPersonRule);
    }

    @Test
    public void shouldLoadDeliveryHappenedAtHomeRuleClassByName() throws Exception {
        String ruleName = "DeliveryHappenedAtHomeRule";

        IRule rule = rulesFactory.ruleByName(ruleName);

        assertTrue(rule instanceof DeliveryHappenedAtHomeRule);
    }

    @Test
    public void shouldLoadDeliveryIsAttendedByNonSBATrainedPersonRuleClassByName() throws Exception {
        String ruleName = "DeliveryIsAttendedByNonSBATrainedPersonRule";

        IRule rule = rulesFactory.ruleByName(ruleName);

        assertTrue(rule instanceof DeliveryIsAttendedByNonSBATrainedPersonRule);
    }

    @Test
    public void shouldLoadIsPersonAnaemicRuleClassByName() throws Exception {
        String ruleName = "IsPersonAnaemicRule";

        IRule rule = rulesFactory.ruleByName(ruleName);

        assertTrue(rule instanceof IsPersonAnaemicRule);
    }

    @Test
    public void shouldLoadPNCVisitHappenedLessThan24HoursAfterDeliveryRuleClassByName() throws Exception {
        String ruleName = "PNCVisitHappenedLessThan24HoursAfterDeliveryRule";

        IRule rule = rulesFactory.ruleByName(ruleName);

        assertTrue(rule instanceof PNCVisitHappenedLessThan24HoursAfterDeliveryRule);
    }

    @Test
    public void shouldLoadWomanIsDischargedWithin48HoursOfDeliveryRuleClassByName() throws Exception {
        String ruleName = "WomanIsDischargedWithin48HoursOfDeliveryRule";

        IRule rule = rulesFactory.ruleByName(ruleName);

        assertTrue(rule instanceof WomanIsDischargedWithin48HoursOfDeliveryRule);
    }
}
