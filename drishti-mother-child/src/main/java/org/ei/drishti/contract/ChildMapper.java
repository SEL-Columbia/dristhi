package org.ei.drishti.contract;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.ei.drishti.common.AllConstants.ChildBirthCommCareFields.BIRTH_WEIGHT_COMMCARE_FIELD_NAME;
import static org.ei.drishti.common.AllConstants.ChildBirthCommCareFields.BLOOD_GROUP_COMMCARE_FIELD_NAME;
import static org.ei.drishti.common.AllConstants.DETAILS_EXTRA_DATA_KEY_NAME;
import static org.ei.drishti.util.EasyMap.create;
import static org.ei.drishti.util.EasyMap.mapOf;

@Component
public class ChildMapper {
    public List<ChildInformation> mapDeliveryOutcomeInformationToChildren(AnteNatalCareOutcomeInformation outcomeInformation, Map<String, Map<String, String>> extraData) {
        List<ChildInformation> childInformationList = new ArrayList<>();

        for (int i = 0; i < outcomeInformation.numberOfChildrenBorn(); i++) {
            String childNumber = "" + i;
            Map<String, Map<String, String>> childData = mapOf(DETAILS_EXTRA_DATA_KEY_NAME, create(BIRTH_WEIGHT_COMMCARE_FIELD_NAME, outcomeInformation.childWeight(childNumber))
                    .put(BLOOD_GROUP_COMMCARE_FIELD_NAME, outcomeInformation.childBloodGroup(childNumber))
                    .putAll(extraData.get(DETAILS_EXTRA_DATA_KEY_NAME))
                    .map());
            childInformationList.add(new ChildInformation(outcomeInformation.caseId(childNumber), outcomeInformation.motherCaseId(), outcomeInformation.anmIdentifier(), outcomeInformation.childName(childNumber),
                    outcomeInformation.gender(childNumber), outcomeInformation.deliveryOutcomeDate(), outcomeInformation.immunizationsProvided(childNumber),
                    outcomeInformation.childWeight(childNumber), outcomeInformation.bfPostBirth(), childData));
        }

        return childInformationList;
    }
}
