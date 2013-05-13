package org.ei.drishti.service.scheduling.fpMethodStrategy;

import org.ei.drishti.service.ActionService;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;

import static org.ei.drishti.common.AllConstants.FamilyPlanningCommCareFields.*;

public class FPMethodStrategyFactory {
    public static FPMethodStrategy create(ScheduleTrackingService scheduleTrackingService, ActionService actionService, String fpMethod) {
        if (DMPA_INJECTABLE_FP_METHOD_VALUE.equalsIgnoreCase(fpMethod)) {
            return new DMPAInjectableStrategy(scheduleTrackingService, actionService);
        }
        if (OCP_FP_METHOD_VALUE.equalsIgnoreCase(fpMethod)) {
            return new OCPStrategy(scheduleTrackingService, actionService);
        }
        if (CONDOM_FP_METHOD_VALUE.equalsIgnoreCase(fpMethod)) {
            return new CondomStrategy(scheduleTrackingService, actionService);
        }
        if (FEMALE_STERILIZATION_FP_METHOD_VALUE.equalsIgnoreCase(fpMethod)) {
            return new FemaleSterilizationStrategy(scheduleTrackingService, actionService);
        }
        return new DefaultFPMethodStrategy();
    }
}

