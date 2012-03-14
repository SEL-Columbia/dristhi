package org.ei.drishti.action;

import org.ei.drishti.scheduler.router.Action;
import org.motechproject.model.MotechEvent;
import org.springframework.beans.factory.annotation.Qualifier;

@Qualifier("failAction")
public class FailAction implements Action {
    @Override
    public void invoke(MotechEvent event) {
        throw new RuntimeException("Unsupported.");
    }
}
