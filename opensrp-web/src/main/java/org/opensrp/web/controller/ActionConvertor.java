package org.opensrp.web.controller;

import org.opensrp.dto.Action;
import org.opensrp.dto.ActionData;

public class ActionConvertor {
    public static Action from(org.opensrp.domain.Action action){
        return new Action(action.caseId(), action.target(), action.actionType(), action.data(), String.valueOf(action.timestamp()), action.getIsActionActive(), action.details());
    }

    public static org.opensrp.domain.Action toAction(Action actionItem, String anmIdentifier) {
        return new org.opensrp.domain.Action(actionItem.caseID(), anmIdentifier, ActionData.from(actionItem.type(), actionItem.target(), actionItem.data(), actionItem.details()));
    }
}
