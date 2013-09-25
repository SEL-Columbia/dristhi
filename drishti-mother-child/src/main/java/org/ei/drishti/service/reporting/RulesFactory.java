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
    private static final String JSY_BENEFICIARY_IS_TRUE = "JsyBeneficiaryIsTrueRule";
    private static final String THIRD_ANC_VISIT_HAPPENED_ON_TIME_RULE = "ThirdANCVisitHappenedOnTimeRule";
    private static final String SERVICE_PROVIDED_AT_SUB_CENTER_RULE = "ServiceProvidedAtSubCenterRule";

    Map<String, IRule> rules = new HashMap<>();

    //#TODO: Resolve dependency by name automatically using spring dependency resolver
    @Autowired
    public RulesFactory(AgeIsLessThanOneYearRule ageIsLessThanOneYearRule,
                        RelocationIsPermanentRule relocationIsPermanentRule,
                        CurrentFPMethodIsCondomRule currentFPMethodIsCondomRule,
                        NewFPMethodIsCondomRule newFPMethodIsCondomRule,
                        MoreThanZeroCondomsSuppliedRule moreThanZeroCondomsSuppliedRule,
                        JsyBeneficiaryIsTrueRule jsyBeneficiaryIsTrueRule,
                        ThirdANCVisitHappenedOnTimeRule thirdANCVisitHappenedOnTimeRule, ServiceProvidedAtSubCenterRule serviceProvidedAtSubCenterRule) {
        rules.put(AGE_LESS_THAN_ONE_YEAR, ageIsLessThanOneYearRule);
        rules.put(RELOCATION_PERMANENT, relocationIsPermanentRule);
        rules.put(CURRENT_FP_METHOD_IS_CONDOM, currentFPMethodIsCondomRule);
        rules.put(NEW_FP_METHOD_IS_CONDOM, newFPMethodIsCondomRule);
        rules.put(MORE_THAN_ZERO_CONDOMS_SUPPLIED, moreThanZeroCondomsSuppliedRule);
        rules.put(JSY_BENEFICIARY_IS_TRUE, jsyBeneficiaryIsTrueRule);
        rules.put(THIRD_ANC_VISIT_HAPPENED_ON_TIME_RULE, thirdANCVisitHappenedOnTimeRule);
        rules.put(SERVICE_PROVIDED_AT_SUB_CENTER_RULE, serviceProvidedAtSubCenterRule);
    }

    public IRule ruleByName(String ruleName) throws RuleNotFoundException {
        if (rules.containsKey(ruleName)) {
            return rules.get(ruleName);
        }
        throw new RuleNotFoundException(ruleName);
    }
}
