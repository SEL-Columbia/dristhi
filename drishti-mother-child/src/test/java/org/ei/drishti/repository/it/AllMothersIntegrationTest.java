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

import static java.util.Arrays.asList;
import static org.ei.drishti.util.Matcher.hasSameFieldsAs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
        Mother mother = new Mother("CASE-1", "EC-CASE-1", "THAYI-CARD-1").withAnm("ANM ID 1").withLMP(DateUtil.tomorrow())
                .withLocation("bherya", "Sub Center", "PHC X").withDetails(details);

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


    private Mother motherWithoutDetails() {
        return new Mother("CASE X", "EC-CASE-1", "TC 1").withAnm("ANM X");
    }
}
