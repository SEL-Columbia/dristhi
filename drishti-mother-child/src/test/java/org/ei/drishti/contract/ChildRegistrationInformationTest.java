package org.ei.drishti.contract;

import org.junit.Test;
import org.motechproject.util.DateUtil;

import java.util.Collections;
import java.util.Date;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ChildRegistrationInformationTest {
    private static final String BCG_NO = "bcg_no";
    private static final String BCG_YES = "BCG_yes";

    private static final String OPV0_YES = "opv0_yes";
    private static final String OPV0_NO = "opv0_no";

    private static final String HEPB1_NO = "hepb1_no";
    private static final String HEPB1_YES = "hepb1_yes";

    @Test
    public void shouldBeAbleToFindMissingVaccinationsDuringEnrollment() {
        ChildRegistrationInformation information;

        information = new ChildRegistrationInformation("Theresa", "9845700000", BCG_NO, OPV0_YES, HEPB1_YES);
        assertThat(information.missingVaccinations(), is(asList("BCG")));

        information = new ChildRegistrationInformation("Theresa", "9845700000", BCG_YES, OPV0_NO, HEPB1_NO);
        assertThat(information.missingVaccinations(), is(asList("OPV 0", "Hepatitis B")));

        information = new ChildRegistrationInformation("Theresa", "9845700000", BCG_NO, OPV0_NO, HEPB1_NO);
        assertThat(information.missingVaccinations(), is(asList("BCG", "OPV 0", "Hepatitis B")));

        information = new ChildRegistrationInformation("Theresa", "9845700000", BCG_YES, OPV0_YES, HEPB1_YES);
        assertThat(information.missingVaccinations(), is(Collections.<String>emptyList()));
    }

    @Test
    public void shouldBeAbleToGetMotherAndANMDetails() {
        ChildRegistrationInformation information = new ChildRegistrationInformation("Theresa", "9845700000", BCG_NO, OPV0_YES, HEPB1_YES);
        assertThat(information.mother(), is("Theresa"));
        assertThat(information.anmPhoneNumber(), is("9845700000"));
    }

    private Date today() {
        return DateUtil.today().toDate();
    }
}
