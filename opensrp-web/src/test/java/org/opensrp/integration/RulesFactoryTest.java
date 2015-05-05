package org.opensrp.integration;

import static org.junit.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensrp.register.service.reporting.RulesFactory;
import org.opensrp.register.service.reporting.rules.CurrentFPMethodIsCondomRule;
import org.opensrp.register.service.reporting.rules.DeliveryHappenedAtHomeRule;
import org.opensrp.register.service.reporting.rules.DeliveryIsAttendedByNonSBATrainedPersonRule;
import org.opensrp.register.service.reporting.rules.DeliveryIsAttendedBySBATrainedPersonRule;
import org.opensrp.register.service.reporting.rules.IsChildLessThanOneYearOldRule;
import org.opensrp.register.service.reporting.rules.IsJsyBeneficiaryRule;
import org.opensrp.register.service.reporting.rules.IsPersonAnaemicRule;
import org.opensrp.register.service.reporting.rules.MoreThanZeroCondomsSuppliedRule;
import org.opensrp.register.service.reporting.rules.NewFPMethodIsCondomRule;
import org.opensrp.register.service.reporting.rules.RelocationIsPermanentRule;
import org.opensrp.register.service.reporting.rules.ServiceProvidedAtSub_CenterRule;
import org.opensrp.register.service.reporting.rules.WomanIsDischargedWithin48HoursOfDeliveryRule;
import org.opensrp.service.reporting.RuleNotFoundException;
import org.opensrp.service.reporting.rules.IRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/applicationContext-opensrp-web.xml")
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
        String ruleName = "IsChildLessThanOneYearOldRule";

        IRule rule = rulesFactory.ruleByName(ruleName);

        assertTrue(rule instanceof IsChildLessThanOneYearOldRule);
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

  /*  @Test
    public void shouldLoadMinimumThreeANCVisitsHappenedOnTimeRuleClassByName() throws Exception {
        String ruleName = "MinimumThreeANCVisitsHappenedOnTimeRule";

        IRule rule = rulesFactory.ruleByName(ruleName);

        assertTrue(rule instanceof MinimumThreeANCVisitsHappenedOnTimeRule);
    }
*/
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
    public void shouldLoadWomanIsDischargedWithin48HoursOfDeliveryRuleClassByName() throws Exception {
        String ruleName = "WomanIsDischargedWithin48HoursOfDeliveryRule";

        IRule rule = rulesFactory.ruleByName(ruleName);

        assertTrue(rule instanceof WomanIsDischargedWithin48HoursOfDeliveryRule);
    }
}
