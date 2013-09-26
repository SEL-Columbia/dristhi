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
    @Mock
    private NewFPMethodIsCondomRule newFPMethodIsCondomRule;
    @Mock
    private MoreThanZeroCondomsSuppliedRule moreThanZeroCondomsSuppliedRule;
    @Mock
    private IsJsyBeneficiaryRule isJsyBeneficiaryRule;
    @Mock
    private ThirdANCVisitHappenedOnTimeRule thirdANCVisitHappenedOnTimeRule;
    @Mock
    private ServiceProvidedAtSubCenterRule serviceProvidedAtSubCenterRule;
    @Mock
    private DeliveryHappenedAtHomeRule deliveryHappenedAtHomeRule;
    @Mock
    private DeliveryIsAttendedBySBATrainedPersonRule deliveryIsAttendedBySBATrainedPersonRule;
    @Mock
    private DeliveryIsAttendedByNonSBATrainedPersonRule deliveryIsAttendedByNonSBATrainedPersonRule;
    @Mock
    private IsPersonAnaemicRule isPersonAnaemicRule;
    @Mock
    private PNCVisitHappenedLessThan24HoursAfterDeliveryRule pncVisitHappenedLessThan24HoursAfterDeliveryRule;
    @Mock
    private DeliveryHappenedAtSubCenterRule deliveryHappenedAtSubCenterRule;
    @Mock
    private WomanIsDischargedWithin48HoursOfDeliveryRule womanIsDischargedWithin48HoursOfDeliveryRule;

    private IRulesFactory rulesFactory;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        rulesFactory = new RulesFactory(ageIsLessThanOneYearRule,
                relocationIsPermanentRule,
                currentFPMethodIsCondomRule,
                newFPMethodIsCondomRule,
                moreThanZeroCondomsSuppliedRule,
                isJsyBeneficiaryRule,
                thirdANCVisitHappenedOnTimeRule,
                serviceProvidedAtSubCenterRule,
                deliveryIsAttendedBySBATrainedPersonRule,
                deliveryHappenedAtHomeRule,
                deliveryIsAttendedByNonSBATrainedPersonRule,
                isPersonAnaemicRule,
                pncVisitHappenedLessThan24HoursAfterDeliveryRule,
                deliveryHappenedAtSubCenterRule,
                womanIsDischargedWithin48HoursOfDeliveryRule,
                null, null, null);
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
    public void shouldLoadServiceProvidedAtSubCenterRuleClassByName() throws Exception {
        String ruleName = "ServiceProvidedAtSubCenterRule";

        IRule rule = rulesFactory.ruleByName(ruleName);

        assertTrue(rule instanceof ServiceProvidedAtSubCenterRule);
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
    public void shouldLoadDeliveryHappenedAtSubCenterRuleClassByName() throws Exception {
        String ruleName = "DeliveryHappenedAtSubCenterRule";

        IRule rule = rulesFactory.ruleByName(ruleName);

        assertTrue(rule instanceof DeliveryHappenedAtSubCenterRule);
    }

    @Test
    public void shouldLoadWomanIsDischargedWithin48HoursOfDeliveryRuleClassByName() throws Exception {
        String ruleName = "WomanIsDischargedWithin48HoursOfDeliveryRule";

        IRule rule = rulesFactory.ruleByName(ruleName);

        assertTrue(rule instanceof WomanIsDischargedWithin48HoursOfDeliveryRule);
    }
}
