package org.ei.drishti.repository.it;

import org.ei.drishti.domain.Child;
import org.ei.drishti.repository.AllChildren;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.ei.drishti.common.AllConstants.ChildImmunizationCommCareFields.IMMUNIZATIONS_PROVIDED_COMMCARE_FIELD_NAME;
import static org.ei.drishti.util.EasyMap.create;
import static org.ei.drishti.util.Matcher.hasSameFieldsAs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-applicationContext-drishti.xml")
public class AllChildrenIntegrationTest {
    @Autowired
    private AllChildren children;

    @Before
    public void setUp() throws Exception {
        children.removeAll();
    }

    @Test
    public void shouldRegisterAChild() {
        Child child = new Child("CASE-1", "MOTHER-CASE-1", "THAYI-CARD-1", "Child", asList("bcg", "hep"), "male").withAnm("ANM ID 1").withDateOfBirth("2012-09-07");

        children.add(child);

        List<Child> allTheChildren = children.getAll();
        assertThat(allTheChildren.size(), is(1));

        Child childFromDB = allTheChildren.get(0);
        assertThat(childFromDB, hasSameFieldsAs(child));
    }

    @Test
    public void shouldFindChildByCaseId() {
        Child child = new Child("CASE-1", "MOTHER-CASE-1", "THAYI-CARD-1", "Child", asList("bcg", "hep"), "male").withAnm("ANM ID 1");
        children.add(child);

        Child childFromDB = children.findByCaseId("CASE-1");

        assertThat(childFromDB, hasSameFieldsAs(child));
    }

    @Test
    public void shouldCheckIfChildExists() {
        Child child = new Child("CASE-1", "MOTHER-CASE-1", "THAYI-CARD-1", "Child", asList("bcg", "hep"), "male").withAnm("ANM ID 1");
        children.add(child);

        assertTrue(children.childExists("CASE-1"));
        assertFalse(children.childExists("CASE-NON-EXISTENT"));
    }

    @Test
    public void shouldFindChildrenByMotherCaseId() {
        Child firstChild = new Child("CASE-1", "MOTHER-CASE-1", "THAYI-CARD-1", "Child", asList("bcg", "hep"), "male").withAnm("ANM ID 1");
        Child secondChild = new Child("CASE-2", "MOTHER-CASE-1", "THAYI-CARD-1", "Child", asList("bcg", "hep"), "male").withAnm("ANM ID 1");
        Child thirdChild = new Child("CASE-3", "MOTHER-CASE-1", "THAYI-CARD-1", "Child", asList("bcg", "hep"), "male").withAnm("ANM ID 1");
        Child orphan = new Child("CASE-4", "MOTHER-CASE-2", "THAYI-CARD-1", "Child", asList("bcg", "hep"), "male").withAnm("ANM ID 1");
        children.add(firstChild);
        children.add(secondChild);
        children.add(thirdChild);
        children.add(orphan);

        List<Child> childrenFromDB = children.findByMotherId("MOTHER-CASE-1");

        assertEquals(asList(firstChild, secondChild, thirdChild), childrenFromDB);
    }

    @Test
    public void shouldMarkChildAsClosedOnClose() {
        Child child = childWithoutDetails();
        children.add(child);

        children.close(child.caseId());

        assertThat(children.findByCaseId("CASE X"), is(child.setIsClosed(true)));
    }

    @Test
    public void shouldUpdateDetailsOfAnExistingChild() throws Exception {
        children.add(childWithoutDetails().withDetails(create("Key 1", "Value 1").put("Key 2", "Value 2").map()));
        Child updatedChild = children.update("CASE X", create("Key 2", "Value 2 NEW").put("Key 3", "Value 3").map());

        Map<String, String> expectedUpdatedDetails = create("Key 1", "Value 1").put("Key 2", "Value 2 NEW").put("Key 3", "Value 3").map();
        assertThat(children.findByCaseId("CASE X"), is(childWithoutDetails().withDetails(expectedUpdatedDetails)));
        assertThat(updatedChild, is(childWithoutDetails().withDetails(expectedUpdatedDetails)));
    }

    @Test
    public void shouldUpdateImmunizationDetailsOfAnExistingChildIfImmunizationProvided() throws Exception {
        children.add(childWithoutDetails().withDetails(create("Key 1", "Value 1").put("Key 2", "Value 2").map()));
        Map<String, String> expectedUpdatedDetails = create("Key 1", "Value 1").put("Key 2", "Value 2 NEW").put("Key 3", "Value 3").put(IMMUNIZATIONS_PROVIDED_COMMCARE_FIELD_NAME, "bcg hep dpt_1 measles").map();

        Child updatedChild = children.update("CASE X", create("Key 2", "Value 2 NEW").put("Key 3", "Value 3").put(IMMUNIZATIONS_PROVIDED_COMMCARE_FIELD_NAME, "dpt_1 hep measles").map());

        Child exptectedChild = new Child("CASE X", "MOTHER-CASE-1", "THAYI-CARD-1", "Child", asList("bcg", "hep", "dpt_1", "measles"), "male")
                .withAnm("ANM ID 1").withDetails(expectedUpdatedDetails);
        assertThat(children.findByCaseId("CASE X"), is(exptectedChild));
        assertThat(updatedChild, is(exptectedChild));
    }

    private Child childWithoutDetails() {
        return new Child("CASE X", "MOTHER-CASE-1", "THAYI-CARD-1", "Child", asList("bcg", "hep"), "male")
                .withAnm("ANM ID 1");
    }
}
