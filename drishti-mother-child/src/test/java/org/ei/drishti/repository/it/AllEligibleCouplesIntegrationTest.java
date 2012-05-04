package org.ei.drishti.repository.it;

import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.repository.AllEligibleCouples;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-applicationContext-drishti.xml")
public class AllEligibleCouplesIntegrationTest {
    @Autowired
    private AllEligibleCouples eligibleCouples;

    @Before
    public void setUp() throws Exception {
        eligibleCouples.removeAll();
    }

    @Test
    public void shouldRegisterEligibleCouple() throws Exception {
        EligibleCouple couple = new EligibleCouple("CASE X", "EC Number 1").withCouple("Wife 1", "Husband 1").withANMIdentifier("ANM X");

        eligibleCouples.register(couple);

        List<EligibleCouple> allCouplesInDB = eligibleCouples.getAll();
        assertThat(allCouplesInDB, is(asList(couple)));
        assertThat(allCouplesInDB.get(0).wife(), is("Wife 1"));
    }

    @Test
    public void shouldRemoveEligibleCoupleOnClose() throws Exception {
        EligibleCouple couple1 = new EligibleCouple("CASE X", "EC Number 1").withCouple("Wife 1", "Husband 1").withANMIdentifier("ANM X");
        EligibleCouple couple2 = new EligibleCouple("CASE Y", "EC Number 2").withCouple("Wife 2", "Husband 2").withANMIdentifier("ANM X");
        eligibleCouples.register(couple1);
        eligibleCouples.register(couple2);
        assertThat(eligibleCouples.getAll(), is(asList(couple1, couple2)));

        eligibleCouples.close("CASE X");

        assertThat(eligibleCouples.getAll(), is(asList(couple2)));
    }

    @Test
    public void shouldNotTryAndRemoveANonExistingCouple() {
        EligibleCouple couple = new EligibleCouple("CASE X", "EC Number 1").withCouple("Wife 1", "Husband 1").withANMIdentifier("ANM X");
        eligibleCouples.register(couple);
        assertThat(eligibleCouples.getAll(), is(asList(couple)));

        eligibleCouples.close("THIS CASE DOES NOT EXIST");

        assertThat(eligibleCouples.getAll(), is(asList(couple)));
    }
}
