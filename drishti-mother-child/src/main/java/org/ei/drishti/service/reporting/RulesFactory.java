package org.ei.drishti.service.reporting;

import org.ei.drishti.service.reporting.rules.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class RulesFactory implements IRulesFactory {
    private static final String AGE_LESS_THAN_ONE_YEAR = "AgeIsLessThanOneYearRule";
    private static final String RELOCATION_PERMANENT = "RelocationIsPermanentRule";
    private static final String CURRENT_FP_METHOD_IS_CONDOM = "CurrentFPMethodIsCondomRule";
    private static final String NEW_FP_METHOD_IS_CONDOM = "NewFPMethodIsCondomRule";
    private static final String MORE_THAN_ZERO_CONDOMS_SUPPLIED = "MoreThanZeroCondomsSuppliedRule";
    private static final String JSY_BENEFICIARY_IS_TRUE = "IsJsyBeneficiaryRule";
    private static final String THIRD_ANC_VISIT_HAPPENED_ON_TIME_RULE = "ThirdANCVisitHappenedOnTimeRule";
    private static final String SERVICE_PROVIDED_AT_SUB_CENTER_RULE = "ServiceProvidedAtSubCenterRule";
    private static final String DELIVERY_IS_ATTENDED_BY_SBA_TRAINED_PERSON_RULE = "DeliveryIsAttendedBySBATrainedPersonRule";
    private static final String DELIVERY_HAPPENED_AT_HOME_RULE = "DeliveryHappenedAtHomeRule";
    private static final String DELIVERY_IS_ATTENDED_BY_NON_SBA_TRAINED_PERSON_RULE = "DeliveryIsAttendedByNonSBATrainedPersonRule";
    private static final String IS_PERSON_ANAEMIC_RULE = "IsPersonAnaemicRule";
    private static final String PNC_VISIT_HAPPENED_LESS_THAN_24_HOURS_AFTER_DELIVERY_RULE = "PNCVisitHappenedLessThan24HoursAfterDeliveryRule";
    private static final String DELIVERY_HAPPENED_AT_SUB_CENTER_RULE = "DeliveryHappenedAtSubCenterRule";
    private static final String WOMAN_IS_DISCHARGED_WITHIN_48_HOURS_OF_DELIVERY_RULE = "WomanIsDischargedWithin48HoursOfDeliveryRule";
    private static final String IUD_REMOVED_AT_HOME_AT_HOME_OR_SUB_CENTER_RULE = "IUDRemovedAtHomeAtHomeOrSubCenterRule";

    Map<String, IRule> rules = new HashMap<>();

    //#TODO: Resolve dependency by name automatically using spring dependency resolver
    @Autowired
    public RulesFactory(AgeIsLessThanOneYearRule ageIsLessThanOneYearRule,
                        RelocationIsPermanentRule relocationIsPermanentRule,
                        CurrentFPMethodIsCondomRule currentFPMethodIsCondomRule,
                        NewFPMethodIsCondomRule newFPMethodIsCondomRule,
                        MoreThanZeroCondomsSuppliedRule moreThanZeroCondomsSuppliedRule,
                        IsJsyBeneficiaryRule isJsyBeneficiaryRule,
                        ThirdANCVisitHappenedOnTimeRule thirdANCVisitHappenedOnTimeRule,
                        ServiceProvidedAtSubCenterRule serviceProvidedAtSubCenterRule,
                        DeliveryIsAttendedBySBATrainedPersonRule deliveryIsAttendedBySBATrainedPerson,
                        DeliveryHappenedAtHomeRule deliveryHappenedAtHomeRule,
                        DeliveryIsAttendedByNonSBATrainedPersonRule deliveryIsAttendedByNonSBATrainedPersonRule,
                        IsPersonAnaemicRule isPersonAnaemicRule,
                        PNCVisitHappenedLessThan24HoursAfterDeliveryRule pncVisitHappenedLessThan24HoursAfterDeliveryRule,
                        DeliveryHappenedAtSubCenterRule deliveryHappenedAtSubCenterRule,
                        WomanIsDischargedWithin48HoursOfDeliveryRule womanIsDischargedWithin48HoursOfDeliveryRule,
                        IUDRemovedAtHomeAtHomeOrSubCenterRule iudRemovedAtHomeAtHomeOrSubCenterRule) {
        rules.put(AGE_LESS_THAN_ONE_YEAR, ageIsLessThanOneYearRule);
        rules.put(RELOCATION_PERMANENT, relocationIsPermanentRule);
        rules.put(CURRENT_FP_METHOD_IS_CONDOM, currentFPMethodIsCondomRule);
        rules.put(NEW_FP_METHOD_IS_CONDOM, newFPMethodIsCondomRule);
        rules.put(MORE_THAN_ZERO_CONDOMS_SUPPLIED, moreThanZeroCondomsSuppliedRule);
        rules.put(JSY_BENEFICIARY_IS_TRUE, isJsyBeneficiaryRule);
        rules.put(THIRD_ANC_VISIT_HAPPENED_ON_TIME_RULE, thirdANCVisitHappenedOnTimeRule);
        rules.put(SERVICE_PROVIDED_AT_SUB_CENTER_RULE, serviceProvidedAtSubCenterRule);
        rules.put(DELIVERY_IS_ATTENDED_BY_SBA_TRAINED_PERSON_RULE, deliveryIsAttendedBySBATrainedPerson);
        rules.put(DELIVERY_HAPPENED_AT_HOME_RULE, deliveryHappenedAtHomeRule);
        rules.put(DELIVERY_IS_ATTENDED_BY_NON_SBA_TRAINED_PERSON_RULE, deliveryIsAttendedByNonSBATrainedPersonRule);
        rules.put(IS_PERSON_ANAEMIC_RULE, isPersonAnaemicRule);
        rules.put(PNC_VISIT_HAPPENED_LESS_THAN_24_HOURS_AFTER_DELIVERY_RULE, pncVisitHappenedLessThan24HoursAfterDeliveryRule);
        rules.put(DELIVERY_HAPPENED_AT_SUB_CENTER_RULE, deliveryHappenedAtSubCenterRule);
        rules.put(WOMAN_IS_DISCHARGED_WITHIN_48_HOURS_OF_DELIVERY_RULE, womanIsDischargedWithin48HoursOfDeliveryRule);
        rules.put(IUD_REMOVED_AT_HOME_AT_HOME_OR_SUB_CENTER_RULE, iudRemovedAtHomeAtHomeOrSubCenterRule);
    }

    public IRule ruleByName(String ruleName) throws RuleNotFoundException {
        if (rules.containsKey(ruleName)) {
            return rules.get(ruleName);
        }
        throw new RuleNotFoundException(ruleName);
    }
}
