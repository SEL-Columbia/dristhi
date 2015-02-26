package org.opensrp.controller;

import org.opensrp.scheduler.router.Action;
import org.opensrp.scheduler.router.AlertRouter;
import org.opensrp.scheduler.router.Matcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import static org.opensrp.scheduler.DrishtiScheduleConstants.ChildScheduleConstants.*;
import static org.opensrp.scheduler.DrishtiScheduleConstants.ECSchedulesConstants.*;
import static org.opensrp.scheduler.DrishtiScheduleConstants.MotherScheduleConstants.*;
import static org.opensrp.scheduler.router.Matcher.*;
import static org.motechproject.scheduletracking.api.domain.WindowName.*;

@Component
public class AlertController {
    @Autowired
    public AlertController(AlertRouter router,
                           @Qualifier("ForceFulfillAction") Action forceFulfill,
                           @Qualifier("AlertCreationAction") Action alertCreation,
                           @Qualifier("AutoClosePNCAction") Action autoClosePNCAction) {
        router.addRoute(eq(SCHEDULE_ANC), any(), eq(max.toString()), forceFulfill);
        router.addRoute(eq(SCHEDULE_LAB), any(), eq(max.toString()), forceFulfill);
        router.addRoute(eq(SCHEDULE_AUTO_CLOSE_PNC), any(), any(), autoClosePNCAction);
        router.addRoute(motherSchedules(), any(), anyOf(earliest.toString(), due.toString(), late.toString()),
                alertCreation).addExtraData("beneficiaryType", "mother");
        router.addRoute(childSchedules(), any(), anyOf(earliest.toString(), due.toString(),
                late.toString(), max.toString()), alertCreation).addExtraData("beneficiaryType", "child");
        router.addRoute(ecSchedules(), any(), anyOf(earliest.toString(), due.toString(), late.toString()),
                alertCreation).addExtraData("beneficiaryType", "ec");
    }

    private Matcher childSchedules() {
        return anyOf(CHILD_SCHEDULE_BCG,

                CHILD_SCHEDULE_DPT_BOOSTER1,
                CHILD_SCHEDULE_DPT_BOOSTER2,

                CHILD_SCHEDULE_MEASLES,
                CHILD_SCHEDULE_MEASLES_BOOSTER,

                CHILD_SCHEDULE_OPV_0_AND_1,
                CHILD_SCHEDULE_OPV_2,
                CHILD_SCHEDULE_OPV_3,
                CHILD_SCHEDULE_OPV_BOOSTER,

                CHILD_SCHEDULE_PENTAVALENT_1,
                CHILD_SCHEDULE_PENTAVALENT_2,
                CHILD_SCHEDULE_PENTAVALENT_3
        );
    }

    private Matcher motherSchedules() {
        return anyOf(SCHEDULE_ANC, SCHEDULE_TT_1, SCHEDULE_TT_2, SCHEDULE_IFA_1, SCHEDULE_IFA_2, SCHEDULE_IFA_3,
                SCHEDULE_LAB, SCHEDULE_EDD, SCHEDULE_HB_TEST_1, SCHEDULE_HB_TEST_2, SCHEDULE_HB_FOLLOWUP_TEST,
                SCHEDULE_DELIVERY_PLAN);
    }

    private Matcher ecSchedules() {
        return anyOf(EC_SCHEDULE_DMPA_INJECTABLE_REFILL,
                EC_SCHEDULE_OCP_REFILL,
                EC_SCHEDULE_CONDOM_REFILL,
                EC_SCHEDULE_FEMALE_STERILIZATION_FOLLOWUP,
                EC_SCHEDULE_MALE_STERILIZATION_FOLLOWUP,
                EC_SCHEDULE_IUD_FOLLOWUP,
                EC_SCHEDULE_FP_FOLLOWUP,
                EC_SCHEDULE_FP_REFERRAL_FOLLOWUP_MILESTONE);
    }
}
