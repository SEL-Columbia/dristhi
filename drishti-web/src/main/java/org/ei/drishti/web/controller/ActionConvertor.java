package org.ei.drishti.web.controller;

import org.ei.drishti.domain.Action;
import org.ei.drishti.dto.ActionData;
import org.ei.drishti.dto.ActionItem;

public class ActionConvertor {
    public static ActionItem from(Action action){
        return new ActionItem(action.anmIdentifier(), action.caseID(), action.data(), action.timestamp(), action.target(), action.actionType());
    }

    public static Action toAction(ActionItem actionItem) {
        return new Action(actionItem.caseID(), actionItem.anmIdentifier(), ActionData.from(actionItem.actionType(), actionItem.actionTarget(), actionItem.data()));
    }
}
