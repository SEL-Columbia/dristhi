package org.ei.drishti.scheduler;

import org.joda.time.LocalTime;

public class DrishtiScheduleConstants {
    public static final LocalTime PREFERED_TIME_FOR_SCHEDULES = new LocalTime(14,00);

    public static class ChildScheduleConstants{
        public static final String CHILD_SCHEDULE_BCG = "BCG";
        public static final String CHILD_SCHEDULE_BCG_MILESTONE = "bcg";

        public static final String CHILD_SCHEDULE_DPT = "DPT";
        public static final String CHILD_SCHEDULE_DPT_MILESTONE_0 = "dpt_0";
        public static final String CHILD_SCHEDULE_DPT_MILESTONE_1 = "dpt_1";
        public static final String CHILD_SCHEDULE_DPT_MILESTONE_2 = "dpt_2";
        public static final String CHILD_SCHEDULE_DPT_MILESTONE_3 = "dpt_3";


        public static final String CHILD_SCHEDULE_DPT1 = "DPT 1";
        public static final String CHILD_SCHEDULE_DPT2 = "DPT 2";
        public static final String CHILD_SCHEDULE_DPT3 = "DPT 3";
        public static final String CHILD_SCHEDULE_DPT_BOOSTER1 = "dptbooster1";
        public static final String CHILD_SCHEDULE_DPT_BOOSTER2 = "dptbooster2";

        public static final String CHILD_SCHEDULE_HEPATITIS = "Hepatitis";
        public static final String CHILD_SCHEDULE_HEPATITIS_MILESTONE_1 = "hepb_1";
        public static final String CHILD_SCHEDULE_HEPATITIS_MILESTONE_2 = "hepb_2";
        public static final String CHILD_SCHEDULE_HEPATITIS_MILESTONE_3 = "hepb_3";
        public static final String CHILD_SCHEDULE_HEPATITIS_MILESTONE_4 = "hepb_4";


        public static final String CHILD_SCHEDULE_MEASLES = "Measles Vaccination";
        public static final String CHILD_SCHEDULE_MEASLES_MILESTONE = "measles";

        public static final String CHILD_SCHEDULE_MEASLES_BOOSTER = "Measles Booster";
        public static final String CHILD_SCHEDULE_MEASLES_BOOSTER_MILESTONE = "measlesbooster";

        public static final String CHILD_SCHEDULE_OPV = "OPV";
        public static final String CHILD_SCHEDULE_OPV_MILESTONE_0 = "opv_0";
        public static final String CHILD_SCHEDULE_OPV_MILESTONE_1 = "opv_1";
        public static final String CHILD_SCHEDULE_OPV_MILESTONE_2 = "opv_2";
        public static final String CHILD_SCHEDULE_OPV_MILESTONE_3 = "opv_3";


    }

    public static class MotherScheduleConstants{
        public static final String SCHEDULE_ANC = "Ante Natal Care - Normal";
        public static final String SCHEDULE_EDD = "Expected Date Of Delivery";
        public static final String SCHEDULE_IFA = "Iron Folic Acid Supplements";
        public static final String SCHEDULE_LAB = "Lab Reminders";
        public static final String SCHEDULE_TT = "Tetatnus Toxoid Vaccination";

    }


}
