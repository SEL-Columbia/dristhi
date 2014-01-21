package org.ei.drishti.repository.it;

import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.repository.AllMothers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.ei.drishti.common.util.EasyMap.create;
import static org.ei.drishti.common.util.EasyMap.mapOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-applicationContext-drishti.xml")
public class AllEligibleCouplesIntegrationTest {
    @Autowired
    private AllEligibleCouples eligibleCouples;
    @Autowired
    private AllMothers allMothers;

    @Before
    public void setUp() throws Exception {
        eligibleCouples.removeAll();
        allMothers.removeAll();
    }

    @Test
    public void shouldRegisterEligibleCouple() throws Exception {
        EligibleCouple couple = new EligibleCouple("CASE X", "EC Number 1").withCouple("Wife 1", "Husband 1").withANMIdentifier("ANM X").withDetails(create("Key 1", "Value 1").put("Key 2", "Value 2").map());

        eligibleCouples.add(couple);

        List<EligibleCouple> allCouplesInDB = eligibleCouples.getAll();
        assertThat(allCouplesInDB, is(asList(couple)));
        assertThat(allCouplesInDB.get(0).wifeName(), is("Wife 1"));
    }

    @Test
    public void shouldMarkAsCloseBothECAndMotherWhenECIsClosed() throws Exception {
        EligibleCouple couple1 = new EligibleCouple("CASE X", "EC Number 1").withCouple("Wife 1", "Husband 1").withANMIdentifier("ANM X");
        EligibleCouple couple2 = new EligibleCouple("CASE Y", "EC Number 2").withCouple("Wife 2", "Husband 2").withANMIdentifier("ANM X");
        EligibleCouple couple3 = new EligibleCouple("CASE Z", "EC Number 3");
        Mother mother1 = new Mother("CASE A", "CASE X", "Thayi 1").withAnm("ANM X").setIsClosed(false);
        Mother mother2 = new Mother("CASE B", "CASE Y", "Thayi 2").withAnm("ANM X").setIsClosed(false);
        eligibleCouples.add(couple1);
        eligibleCouples.add(couple2);
        eligibleCouples.add(couple3);
        allMothers.add(mother1);
        allMothers.add(mother2);
        assertThat(eligibleCouples.getAll(), is(asList(couple1, couple2, couple3)));
        assertThat(allMothers.getAll(), is(asList(mother1, mother2)));

        eligibleCouples.close("CASE X");
        eligibleCouples.close("CASE Z");

        assertThat(eligibleCouples.getAll(), is(asList(couple1.setIsClosed(true), couple2, couple3.setIsClosed(true))));
        assertThat(allMothers.getAll(), is(asList(mother1.setIsClosed(true), mother2)));
    }

    @Test
    public void shouldNotTryAndCloseANonExistingCouple() {
        EligibleCouple couple = new EligibleCouple("CASE X", "EC Number 1").withCouple("Wife 1", "Husband 1").withANMIdentifier("ANM X");
        eligibleCouples.add(couple);
        assertThat(eligibleCouples.getAll(), is(asList(couple)));

        eligibleCouples.close("THIS CASE DOES NOT EXIST");

        assertThat(eligibleCouples.getAll(), is(asList(couple)));
    }

    @Test
    public void shouldFindAllOutOfAreaCouples() throws Exception {
        EligibleCouple closedOutOfArea = new EligibleCouple("CASE X", "EC Number 1").setIsClosed(true);
        EligibleCouple inArea = new EligibleCouple("CASE Y", "EC Number 2");
        EligibleCouple outOfArea = new EligibleCouple("CASE Z", "EC Number 3").asOutOfArea();
        EligibleCouple anotherOutOfArea = new EligibleCouple("CASE A", "EC Number 3").asOutOfArea();
        eligibleCouples.add(closedOutOfArea);
        eligibleCouples.add(inArea);
        eligibleCouples.add(outOfArea);
        eligibleCouples.add(anotherOutOfArea);

        List<EligibleCouple> outOfAreaCouples = eligibleCouples.findAllOutOfAreaCouples();

        assertTrue(outOfAreaCouples.containsAll(asList(outOfArea, anotherOutOfArea)));
        assertFalse(outOfAreaCouples.contains(inArea));
        assertFalse(outOfAreaCouples.contains(closedOutOfArea));
    }

    @Test
    public void shouldFindAllBPLCouples() throws Exception {
        EligibleCouple coupleWithBplStatus = new EligibleCouple("CASE X", "EC Number 1").withDetails(mapOf("economicStatus", "bpl"));
        EligibleCouple coupleWithAplStatus = new EligibleCouple("CASE Y", "EC Number 2").withDetails(mapOf("economicStatus", "apl"));
        EligibleCouple coupleWithNoEconomicStatus = new EligibleCouple("CASE Z", "EC Number 3");
        eligibleCouples.add(coupleWithBplStatus);
        eligibleCouples.add(coupleWithAplStatus);
        eligibleCouples.add(coupleWithNoEconomicStatus);

        List<EligibleCouple> bplCouples = eligibleCouples.findAllBPLCouples();

        assertTrue(bplCouples.containsAll(asList(coupleWithBplStatus)));
        assertEquals(1, bplCouples.size());
    }

    @Test
    public void shouldFindAllECsByCaseId() throws Exception {
        EligibleCouple couple1 = new EligibleCouple("case 1", "EC Number 1");
        EligibleCouple couple2 = new EligibleCouple("case 2", "EC Number 2");
        EligibleCouple couple3 = new EligibleCouple("case 3", "EC Number 3");
        eligibleCouples.add(couple1);
        eligibleCouples.add(couple2);
        eligibleCouples.add(couple3);

        List<EligibleCouple> couples = eligibleCouples.findAll(asList("case 1", "case 2"));

        assertEquals(asList(couple1, couple2), couples);
    }

    @Test
    public void shouldFindCountOfAllOpenEligibleCouplesForANM() throws Exception {
        EligibleCouple closedOutOfArea = new EligibleCouple("CASE X", "EC Number 1").withANMIdentifier("demo1").setIsClosed(true);
        EligibleCouple inArea = new EligibleCouple("CASE Y", "EC Number 2").withANMIdentifier("demo1");
        EligibleCouple anotherInArea = new EligibleCouple("CASE Y", "EC Number 2").withANMIdentifier("demo1");
        EligibleCouple ecForAnotherANM = new EligibleCouple("CASE Z", "EC Number 3").withANMIdentifier("demo2");
        EligibleCouple anotherOutOfArea = new EligibleCouple("CASE A", "EC Number 3").withANMIdentifier("demo1").asOutOfArea();
        eligibleCouples.add(closedOutOfArea);
        eligibleCouples.add(inArea);
        eligibleCouples.add(anotherInArea);
        eligibleCouples.add(ecForAnotherANM);
        eligibleCouples.add(anotherOutOfArea);

        Map<String, Integer> allOpenECs = eligibleCouples.allOpenECs(asList("demo1", "demo2"));

        assertEquals(2, allOpenECs.size());
        assertEquals(2, (long) allOpenECs.get("demo1"));
        assertEquals(1, (long) allOpenECs.get("demo2"));
    }

    @Test
    public void shouldFindCountOfAllOpenEligibleCouplesWithFPMethodForANM() throws Exception {
        EligibleCouple closedOutOfArea = new EligibleCouple("CASE X", "EC Number 1").withANMIdentifier("demo1").setIsClosed(true);
        EligibleCouple inAreaWithFPMethodAsNone = new EligibleCouple("CASE Y", "EC Number 2")
                .withANMIdentifier("demo1").withDetails(mapOf("currentMethod", "none"));
        EligibleCouple inAreaWithFPMethodWithoutFPMethod = new EligibleCouple("CASE Y", "EC Number 2")
                .withANMIdentifier("demo1");
        EligibleCouple inAreaWithValidFPMethod = new EligibleCouple("CASE Y", "EC Number 2").withANMIdentifier("demo1")
                .withDetails(mapOf("currentMethod", "ocp"));
        EligibleCouple anotherInAreaECWithoutFPMethod = new EligibleCouple("CASE Y", "EC Number 2").withANMIdentifier("demo1");
        EligibleCouple ecForAnotherANMWithFPMethod = new EligibleCouple("CASE Z", "EC Number 3").withANMIdentifier("demo2")
                .withDetails(mapOf("currentMethod", "ocp"));
        EligibleCouple ecForAnotherANMWithoutFPMethod = new EligibleCouple("CASE Z", "EC Number 3").withANMIdentifier("demo2");
        EligibleCouple outOfArea = new EligibleCouple("CASE Z", "EC Number 3").withANMIdentifier("demo2");
        EligibleCouple anotherOutOfArea = new EligibleCouple("CASE A", "EC Number 3").withANMIdentifier("demo1").asOutOfArea();

        eligibleCouples.add(closedOutOfArea);
        eligibleCouples.add(inAreaWithFPMethodAsNone);
        eligibleCouples.add(inAreaWithFPMethodWithoutFPMethod);
        eligibleCouples.add(anotherInAreaECWithoutFPMethod);
        eligibleCouples.add(inAreaWithValidFPMethod);
        eligibleCouples.add(ecForAnotherANMWithFPMethod);
        eligibleCouples.add(ecForAnotherANMWithoutFPMethod);
        eligibleCouples.add(outOfArea);
        eligibleCouples.add(anotherOutOfArea);

        Map<String, Integer> allOpenFP = eligibleCouples.fpCountForANM(asList("demo1", "demo2"));

        assertEquals(2, allOpenFP.size());
        assertEquals(1, (long) allOpenFP.get("demo1"));
        assertEquals(1, (long) allOpenFP.get("demo2"));
    }
}
