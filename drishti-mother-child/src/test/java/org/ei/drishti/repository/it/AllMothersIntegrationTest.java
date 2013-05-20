package org.ei.drishti.repository.it;

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

import static org.ei.drishti.util.EasyMap.create;
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
    private AllMothers mothers;

    @Before
    public void setUp() throws Exception {
        mothers.removeAll();
    }

    @Test
    public void shouldRegisterAMother() {
        HashMap<String, String> details = new HashMap<>();
        details.put("some_field", "some_value");
        Mother mother = new Mother("CASE-1", "EC-CASE-1", "THAAYI-CARD-1").withAnm("ANM ID 1").withLMP(DateUtil.tomorrow())
                .withLocation("bherya", "Sub Center", "PHC X").withDetails(details);

        mothers.register(mother);

        List<Mother> allTheMothers = mothers.getAll();
        assertThat(allTheMothers.size(), is(1));

        Mother motherFromDB = allTheMothers.get(0);
        assertThat(motherFromDB, hasSameFieldsAs(mother));
    }

    @Test
    public void shouldFindARegisteredMotherByCaseId() {
        String caseId = "CASE-1";
        Mother motherToRegister = new Mother(caseId, "EC-CASE-1", "THAAYI-CARD-1");
        mothers.register(motherToRegister);

        assertThat(mothers.findByCaseId(caseId), hasSameFieldsAs(motherToRegister));
        assertThat(mothers.findByCaseId("SOME OTHER ID"), is(nullValue()));
    }

    @Test
    public void shouldFindARegisteredMotherByThaayiCardNumber() {
        String thaayiCardNumber = "THAAYI-CARD-1";
        Mother motherToRegister = new Mother("CASE-1", "EC-CASE-1", thaayiCardNumber);
        mothers.register(motherToRegister);

        assertThat(mothers.findByThaayiCardNumber(thaayiCardNumber), hasSameFieldsAs(motherToRegister));
        assertThat(mothers.findByThaayiCardNumber("SOME OTHER ID"), is(nullValue()));
    }

    @Test
    public void shouldSayThatAMotherDoesNotExistWhenTheMotherIsNotInTheDB() {
        Mother motherToRegister = new Mother("CASE-1", "EC-CASE-1", "THAAYI-CARD-1");
        mothers.register(motherToRegister);

        assertTrue(mothers.motherExists("CASE-1"));
        assertFalse(mothers.motherExists("CASE-NOT-KNOWN"));
    }

    @Test
    public void shouldUpdateDetailsOfAnExistingMother() throws Exception {
        mothers.register(motherWithoutDetails().withDetails(create("Key 1", "Value 1").put("Key 2", "Value 2").map()));
        Mother updatedMother = mothers.updateDetails("CASE X", create("Key 2", "Value 2 NEW").put("Key 3", "Value 3").map());

        Map<String, String> expectedUpdatedDetails = create("Key 1", "Value 1").put("Key 2", "Value 2 NEW").put("Key 3", "Value 3").map();
        assertThat(mothers.findByCaseId("CASE X"), is(motherWithoutDetails().withDetails(expectedUpdatedDetails)));
        assertThat(updatedMother, is(motherWithoutDetails().withDetails(expectedUpdatedDetails)));
    }

    @Test
    public void shouldUpdateDeliveryOutcomeForAMother() {
        Mother motherToRegister = new Mother("CASE-1", "EC-CASE-1", "THAAYI-CARD-1");
        mothers.register(motherToRegister);

        mothers.updateDeliveryOutcomeFor("CASE-1", "2013-01-01");
        assertThat(mothers.findByCaseId("CASE-1"), is(motherToRegister.withDeliveryOutCome("2013-01-01")));
    }

    @Test
    public void shouldMarkMotherAsClosedWhenMotherClose() {
        Mother motherToRegister = motherWithoutDetails();
        mothers.register(motherToRegister);

        mothers.close("CASE X");

        assertThat(mothers.findByCaseId("CASE X"), is(motherToRegister.setIsClosed(true)));
    }

    private Mother motherWithoutDetails() {
        return new Mother("CASE X", "EC-CASE-1", "TC 1").withAnm("ANM X");
    }
}
