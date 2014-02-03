package org.ei.drishti.repository.it;

import org.ei.drishti.common.util.EasyMap;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.repository.AllMothers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.ei.drishti.common.util.EasyMap.create;
import static org.ei.drishti.common.util.EasyMap.mapOf;
import static org.ei.drishti.util.Matcher.hasSameFieldsAs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-applicationContext-drishti.xml")
public class AllMothersIntegrationTest {
    @Autowired
    private AllMothers allMothers;

    @Before
    public void setUp() throws Exception {
        allMothers.removeAll();
    }

    @Test
    public void shouldRegisterAMother() {
        HashMap<String, String> details = new HashMap<>();
        details.put("some_field", "some_value");
        Mother mother = new Mother("CASE-1", "EC-CASE-1", "THAYI-CARD-1")
                .withAnm("ANM ID 1")
                .withLMP(DateUtil.tomorrow())
                .withLocation("bherya", "Sub Center", "PHC X")
                .withANCVisits(asList(
                        create("ancVisitDate", "2014-02-14")
                                .put("weight", "70")
                                .put("bpSystolic", "150")
                                .put("bpDiastolic", "100")
                                .map(),
                        create("ancVisitDate", "2014-04-14")
                                .put("weight", "72")
                                .put("bpSystolic", "120")
                                .put("bpDiastolic", "80")
                                .map()))
                .withDetails(details);

        allMothers.add(mother);

        List<Mother> allTheMothers = allMothers.getAll();
        assertThat(allTheMothers.size(), is(1));

        Mother motherFromDB = allTheMothers.get(0);
        assertThat(motherFromDB, hasSameFieldsAs(mother));
    }

    @Test
    public void shouldFindARegisteredMotherByCaseId() {
        String caseId = "CASE-1";
        Mother motherToRegister = new Mother(caseId, "EC-CASE-1", "THAYI-CARD-1");
        allMothers.add(motherToRegister);

        assertThat(allMothers.findByCaseId(caseId), hasSameFieldsAs(motherToRegister));
        assertThat(allMothers.findByCaseId("SOME OTHER ID"), is(nullValue()));
    }

    @Test
    public void shouldSayThatAMotherDoesNotExistWhenTheMotherIsNotInTheDB() {
        Mother motherToRegister = new Mother("CASE-1", "EC-CASE-1", "THAYI-CARD-1");
        allMothers.add(motherToRegister);

        assertTrue(allMothers.exists("CASE-1"));
        assertFalse(allMothers.exists("CASE-NOT-KNOWN"));
    }

    @Test
    public void shouldMarkMotherAsClosedWhenMotherClose() {
        Mother motherToRegister = motherWithoutDetails();
        allMothers.add(motherToRegister);

        allMothers.close("CASE X");

        assertThat(allMothers.findByCaseId("CASE X"), is(motherToRegister.setIsClosed(true)));
    }

    @Test
    public void shouldFindOpenMothersByECCaseId() {
        Mother motherForEC1 = new Mother("mother id 1", "ec id 1", "thayi 1");
        Mother closedMotherForEC1 = new Mother("mother id 2", "ec id 1", "thayi 2").setIsClosed(true);
        Mother motherForEC2 = new Mother("mother id 3", "ec id 2", "thayi 2");
        Mother motherForEC3 = new Mother("mother id 4", "ec id 3", "thayi 3");
        allMothers.add(motherForEC1);
        allMothers.add(closedMotherForEC1);
        allMothers.add(motherForEC2);
        allMothers.add(motherForEC3);

        List<Mother> mothers = allMothers.findAllOpenMothersByECCaseId(asList("ec id 1", "ec id 2"));

        assertTrue(mothers.containsAll(asList(motherForEC1, motherForEC2)));
        assertFalse(mothers.contains(closedMotherForEC1));
        assertFalse(mothers.contains(motherForEC3));
    }

    @Test
    public void shouldFindOpenMothersForANM() {
        Mother anc = new Mother("mother id 1", "ec id 1", "thayi 1")
                .withAnm("demo1")
                .withDetails(EasyMap.mapOf("type", "ANC"));
        Mother closedMother = new Mother("mother id 2", "ec id 1", "thayi 2").withAnm("demo1").setIsClosed(true);
        Mother motherServicedByDifferentANM = new Mother("mother id 3", "ec id 2", "thayi 2").withAnm("demo2");
        Mother pnc = new Mother("mother id 4", "ec id 4", "thayi 4")
                .withAnm("demo1")
                .withDetails(EasyMap.mapOf("type", "PNC"));

        allMothers.add(anc);
        allMothers.add(closedMother);
        allMothers.add(motherServicedByDifferentANM);
        allMothers.add(pnc);

        List<Mother> mothers = allMothers.findAllOpenMothersForANM("demo1");

        assertTrue(mothers.contains(anc));
        assertFalse(mothers.contains(closedMother));
        assertFalse(mothers.contains(motherServicedByDifferentANM));
        assertFalse(mothers.contains(pnc));
    }

    @Test
    public void shouldFindANCCountForANM() {
        Mother anc = new Mother("mother id 1", "ec id 1", "thayi 1")
                .withAnm("demo1")
                .withDetails(EasyMap.mapOf("type", "ANC"));
        Mother anotherANC = new Mother("mother id 21", "ec id 21", "thayi 21")
                .withAnm("demo1")
                .withDetails(EasyMap.mapOf("type", "ANC"));
        Mother closedMother = new Mother("mother id 2", "ec id 1", "thayi 2").withAnm("demo1").setIsClosed(true);
        Mother motherServicedByDifferentANM = new Mother("mother id 3", "ec id 2", "thayi 2").withAnm("demo2").withDetails(EasyMap.mapOf("type", "ANC"));
        Mother pnc = new Mother("mother id 4", "ec id 4", "thayi 4")
                .withAnm("demo1")
                .withDetails(EasyMap.mapOf("type", "PNC"));

        allMothers.add(anc);
        allMothers.add(anotherANC);
        allMothers.add(closedMother);
        allMothers.add(motherServicedByDifferentANM);
        allMothers.add(pnc);

        Map<String, Integer> openMothersCount = allMothers.allOpenMotherCount(asList("demo1", "demo2"));

        assertEquals(2, openMothersCount.size());
        assertEquals(2, (long) openMothersCount.get("demo1"));
        assertEquals(1, (long) openMothersCount.get("demo2"));
    }

    @Test
    public void shouldFindOpenANCByECCaseId() {
        Mother motherForEC1 = new Mother("mother id 1", "ec id 1", "thayi 1").withDetails(mapOf("type", "ANC"));
        Mother closedMotherForEC1 = new Mother("mother id 2", "ec id 1", "thayi 2").setIsClosed(true);
        Mother motherForEC2 = new Mother("mother id 3", "ec id 2", "thayi 2");
        Mother pncMotherForEC1 = new Mother("mother id 3", "ec id 2", "thayi 2").withDetails(mapOf("type", "PNC"));

        allMothers.add(motherForEC1);
        allMothers.add(closedMotherForEC1);
        allMothers.add(motherForEC2);
        allMothers.add(pncMotherForEC1);

        List<Mother> mothers = allMothers.findAllOpenANCByECCaseId("ec id 1");

        assertTrue(mothers.containsAll(asList(motherForEC1)));
        assertFalse(mothers.contains(closedMotherForEC1));
        assertFalse(mothers.contains(motherForEC2));
        assertFalse(mothers.contains(closedMotherForEC1));
        assertFalse(mothers.contains(pncMotherForEC1));
    }

    @Test
    public void shouldFindPNCCountForANM() {
        Mother pnc = new Mother("mother id 1", "ec id 1", "thayi 1")
                .withAnm("demo1")
                .withDetails(EasyMap.mapOf("type", "PNC"));
        Mother anotherPNC = new Mother("mother id 21", "ec id 21", "thayi 21")
                .withAnm("demo1")
                .withDetails(EasyMap.mapOf("type", "PNC"));
        Mother closedMother = new Mother("mother id 2", "ec id 1", "thayi 2").withAnm("demo1")
                .withDetails(EasyMap.mapOf("type", "PNC")).setIsClosed(true);
        Mother motherServicedByDifferentANM = new Mother("mother id 3", "ec id 2", "thayi 2")
                .withDetails(EasyMap.mapOf("type", "PNC")).withAnm("demo2");
        Mother anc = new Mother("mother id 4", "ec id 4", "thayi 4")
                .withAnm("demo1")
                .withDetails(EasyMap.mapOf("type", "ANC"));

        allMothers.add(pnc);
        allMothers.add(anotherPNC);
        allMothers.add(closedMother);
        allMothers.add(motherServicedByDifferentANM);
        allMothers.add(anc);

        Map<String, Integer> openPNCCount = allMothers.allOpenPNCCount(asList("demo1", "demo2"));

        assertEquals(2, openPNCCount.size());
        assertEquals(2, (long) openPNCCount.get("demo1"));
        assertEquals(1, (long) openPNCCount.get("demo2"));
    }

    private Mother motherWithoutDetails() {
        return new Mother("CASE X", "EC-CASE-1", "TC 1").withAnm("ANM X");
    }
}
