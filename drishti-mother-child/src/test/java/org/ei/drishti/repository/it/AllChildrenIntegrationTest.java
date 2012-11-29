package org.ei.drishti.repository.it;

import org.ei.drishti.domain.Child;
import org.ei.drishti.repository.AllChildren;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.ei.drishti.util.EasyMap.create;
import static org.ei.drishti.util.Matcher.hasSameFieldsAs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
        Child child = new Child("CASE-1", "EC-CASE-1", "MOTHER-CASE-1", "THAAYI-CARD-1", "Child", Arrays.asList("bcg", "hep"), "male").withAnm("ANM ID 1").withDateOfBirth("2012-09-07");

        children.register(child);

        List<Child> allTheChildren = children.getAll();
        assertThat(allTheChildren.size(), is(1));

        Child childFromDB = allTheChildren.get(0);
        assertThat(childFromDB, hasSameFieldsAs(child));
    }

    @Test
    public void shouldFindChildByCaseId() {
        Child child = new Child("CASE-1", "EC-CASE-1", "MOTHER-CASE-1", "THAAYI-CARD-1", "Child", Arrays.asList("bcg", "hep"), "male").withAnm("ANM ID 1");
        children.register(child);

        Child childFromDB = children.findByCaseId("CASE-1");

        assertThat(childFromDB, hasSameFieldsAs(child));
    }

    @Test
    public void shouldCheckIfChildExists() {
        Child child = new Child("CASE-1", "EC-CASE-1", "MOTHER-CASE-1", "THAAYI-CARD-1", "Child", Arrays.asList("bcg", "hep"), "male").withAnm("ANM ID 1");
        children.register(child);

        assertTrue(children.childExists("CASE-1"));
        assertFalse(children.childExists("CASE-NON-EXISTENT"));
    }

    @Test
    public void shouldFindChildByMotherCaseId() {
        Child child = new Child("CASE-1", "EC-CASE-1", "MOTHER-CASE-1", "THAAYI-CARD-1", "Child", Arrays.asList("bcg", "hep"), "male").withAnm("ANM ID 1");
        children.register(child);

        Child childFromDB = children.findByMotherCaseId("MOTHER-CASE-1");

        assertThat(childFromDB, hasSameFieldsAs(child));
    }

    @Test
    public void shouldUpdateDetailsOfAnExistingChild() throws Exception {
        children.register(childWithoutDetails().withDetails(create("Key 1", "Value 1").put("Key 2", "Value 2").map()));
        Child updatedChild = children.updateDetails("CASE X", create("Key 2", "Value 2 NEW").put("Key 3", "Value 3").map());

        Map<String, String> expectedUpdatedDetails = create("Key 1", "Value 1").put("Key 2", "Value 2 NEW").put("Key 3", "Value 3").map();
        assertThat(children.findByCaseId("CASE X"), is(childWithoutDetails().withDetails(expectedUpdatedDetails)));
        assertThat(updatedChild, is(childWithoutDetails().withDetails(expectedUpdatedDetails)));
    }

    private Child childWithoutDetails() {
        return new Child("CASE X", "EC-CASE-1", "MOTHER-CASE-1", "THAAYI-CARD-1", "Child", Arrays.asList("bcg", "hep"), "male")
                .withAnm("ANM ID 1");
    }

}
