package org.ei.drishti.repository.it;

import org.ei.drishti.domain.Child;
import org.ei.drishti.repository.AllChildren;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static java.util.Arrays.asList;
import static org.ei.drishti.util.EasyMap.mapOf;
import static org.ei.drishti.util.Matcher.hasSameFieldsAs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-applicationContext-drishti.xml")
public class AllChildrenIntegrationTest {
    @Autowired
    private AllChildren allChildren;

    @Before
    public void setUp() throws Exception {
        allChildren.removeAll();
    }

    @Test
    public void shouldRegisterAChild() {
        Child child = new Child("CASE-1", "MOTHER-CASE-1", "bcg", "3", "male").withAnm("ANM ID 1").withDateOfBirth("2012-09-07").withDetails(mapOf("immunizationDate", "2012-01-01"));

        allChildren.add(child);

        List<Child> allTheChildren = allChildren.getAll();
        assertThat(allTheChildren.size(), is(1));

        Child childFromDB = allTheChildren.get(0);
        assertThat(childFromDB, hasSameFieldsAs(child));
    }

    @Test
    public void shouldFindChildByCaseId() {
        Child child = new Child("CASE-1", "MOTHER-CASE-1", "bcg", "3", "male").withAnm("ANM ID 1");
        allChildren.add(child);

        Child childFromDB = allChildren.findByCaseId("CASE-1");

        assertThat(childFromDB, hasSameFieldsAs(child));
    }

    @Test
    public void shouldCheckIfChildExists() {
        Child child = new Child("CASE-1", "MOTHER-CASE-1", "bcg", "3", "male").withAnm("ANM ID 1");
        allChildren.add(child);

        assertTrue(allChildren.childExists("CASE-1"));
        assertFalse(allChildren.childExists("CASE-NON-EXISTENT"));
    }

    @Test
    public void shouldFindChildrenByMotherCaseId() {
        Child firstChild = new Child("CASE-1", "MOTHER-CASE-1", "bcg", "3", "male").withAnm("ANM ID 1");
        Child secondChild = new Child("CASE-2", "MOTHER-CASE-1", "bcg", "3", "male").withAnm("ANM ID 1");
        Child thirdChild = new Child("CASE-3", "MOTHER-CASE-1", "bcg", "3", "male").withAnm("ANM ID 1");
        Child orphan = new Child("CASE-4", "MOTHER-CASE-2", "bcg", "3", "male").withAnm("ANM ID 1");
        allChildren.add(firstChild);
        allChildren.add(secondChild);
        allChildren.add(thirdChild);
        allChildren.add(orphan);

        List<Child> childrenFromDB = allChildren.findByMotherId("MOTHER-CASE-1");

        assertEquals(asList(firstChild, secondChild, thirdChild), childrenFromDB);
    }

    @Test
    public void shouldMarkChildAsClosedOnClose() {
        Child child = childWithoutDetails();
        allChildren.add(child);

        allChildren.close(child.caseId());

        assertThat(allChildren.findByCaseId("CASE X"), is(child.setIsClosed(true)));
    }

    @Test
    public void shouldRemoveChild() {
        Child child = new Child("CASE-1", "MOTHER-CASE-1", "bcg", "3", "male").withAnm("ANM ID 1").withDateOfBirth("2012-09-07");
        allChildren.add(child);

        allChildren.remove(child);

        assertTrue(allChildren.getAll().isEmpty());
    }

    @Test
    public void shouldFindAllChildrenLessThanOneYearOldAsOfDate() {
        Child moreThanOneYearOld = new Child("CASE-1", "MOTHER-CASE-1", "bcg", "3", "male").withAnm("ANM ID 1").withDateOfBirth("2012-11-27");
        Child oneDayLessThanOneYearOld = new Child("CASE-2", "MOTHER-CASE-1", "bcg", "3", "male").withAnm("ANM ID 1").withDateOfBirth("2012-12-27");
        Child exactlyOneYearOld = new Child("CASE-3", "MOTHER-CASE-1", "bcg", "3", "male").withAnm("ANM ID 1").withDateOfBirth("2012-12-26");
        Child bornOnSameDay = new Child("CASE-4", "MOTHER-CASE-1", "bcg", "3", "male").withAnm("ANM ID 1").withDateOfBirth("2013-12-26");
        Child bornOneDayBefore = new Child("CASE-5", "MOTHER-CASE-1", "bcg", "3", "male").withAnm("ANM ID 1").withDateOfBirth("2013-12-25");
        Child fewMonthsOld = new Child("CASE-6", "MOTHER-CASE-1", "bcg", "3", "male").withAnm("ANM ID 1").withDateOfBirth("2013-06-06");
        Child closed = new Child("CASE-7", "MOTHER-CASE-2", "bcg", "3", "male").withAnm("ANM ID 1").setIsClosed(true).withDateOfBirth("2013-06-06");
        allChildren.add(moreThanOneYearOld);
        allChildren.add(oneDayLessThanOneYearOld);
        allChildren.add(exactlyOneYearOld);
        allChildren.add(bornOnSameDay);
        allChildren.add(bornOneDayBefore);
        allChildren.add(fewMonthsOld);
        allChildren.add(closed);

        List<Child> childrenFromDB = allChildren.
                findAllChildrenLessThanOneYearOldAsOfDate(LocalDate.parse("2013-12-26"));

        assertEquals(4, childrenFromDB.size());
        assertTrue(childrenFromDB.containsAll(asList(exactlyOneYearOld, oneDayLessThanOneYearOld, fewMonthsOld, bornOneDayBefore)));
    }

    @Test
    public void shouldFindAllChildrenTurnedOneYearOldAsOfDate() {
        Child turningOneYearOldOnFirstDayOfReportingMonth = new Child("CASE-1", "MOTHER-CASE-1", "bcg", "3", "male").withAnm("ANM ID 1").withDateOfBirth("2012-12-26");
        Child turningOneYearOldOnLastDayOfReportingMonth = new Child("CASE-2", "MOTHER-CASE-1", "bcg", "3", "male").withAnm("ANM ID 1").withDateOfBirth("2013-01-25");
        Child turningOneYearOldInTheReportingMonth = new Child("CASE-3", "MOTHER-CASE-1", "bcg", "3", "male").withAnm("ANM ID 1").withDateOfBirth("2012-12-26");
        Child turningOneYearOldOnReportingDay = new Child("CASE-4", "MOTHER-CASE-1", "bcg", "3", "male").withAnm("ANM ID 1").withDateOfBirth("2012-12-26");
        Child turningOneYearOldBeforeReportingMonth = new Child("CASE-5", "MOTHER-CASE-1", "bcg", "3", "male").withAnm("ANM ID 1").withDateOfBirth("2012-11-26");
        Child turningOneYearOldAfterReportingMonth = new Child("CASE-6", "MOTHER-CASE-1", "bcg", "3", "male").withAnm("ANM ID 1").withDateOfBirth("2013-02-02");
        Child closed = new Child("CASE-7", "MOTHER-CASE-2", "bcg", "3", "male").withAnm("ANM ID 1").setIsClosed(true).withDateOfBirth("2012-06-06");
        allChildren.add(turningOneYearOldOnFirstDayOfReportingMonth);
        allChildren.add(turningOneYearOldOnLastDayOfReportingMonth);
        allChildren.add(turningOneYearOldInTheReportingMonth);
        allChildren.add(turningOneYearOldOnReportingDay);
        allChildren.add(turningOneYearOldBeforeReportingMonth);
        allChildren.add(turningOneYearOldAfterReportingMonth);
        allChildren.add(closed);

        List<Child> childrenFromDB = allChildren.
                findAllChildrenWhoTurnedOneYearOld(LocalDate.parse("2013-12-26"));

        assertTrue(childrenFromDB.containsAll(asList(turningOneYearOldOnFirstDayOfReportingMonth, turningOneYearOldOnLastDayOfReportingMonth,
                turningOneYearOldInTheReportingMonth, turningOneYearOldOnReportingDay)));
        assertEquals(4, childrenFromDB.size());
    }

    @Test
    public void shouldFindOpenChildrenByMotherCaseId() {
        Child firstChild = new Child("CASE-1", "MOTHER-CASE-1", "bcg", "3", "male").withAnm("ANM ID 1");
        Child secondChild = new Child("CASE-2", "MOTHER-CASE-1", "bcg", "3", "male").withAnm("ANM ID 1");
        Child closedChild = new Child("CASE-5", "MOTHER-CASE-1", "bcg", "3", "male").withAnm("ANM ID 1").setIsClosed(true);
        Child thirdChild = new Child("CASE-3", "MOTHER-CASE-2", "bcg", "3", "male").withAnm("ANM ID 2");
        Child fourthChild = new Child("CASE-4", "MOTHER-CASE-3", "bcg", "3", "male").withAnm("ANM ID 2");
        allChildren.add(firstChild);
        allChildren.add(secondChild);
        allChildren.add(thirdChild);
        allChildren.add(closedChild);
        allChildren.add(fourthChild);

        List<Child> children = allChildren.findAllOpenChildrenByMotherId(asList("MOTHER-CASE-1", "MOTHER-CASE-2"));

        assertTrue(children.containsAll(asList(firstChild, secondChild, thirdChild)));
        assertFalse(children.contains(closedChild));
        assertFalse(children.contains(fourthChild));
    }

    @Test
    public void shouldFindAllChildrenLessThanFiveYearOldAsOfDate() {
        LocalDate referenceDate = LocalDate.parse("2013-12-26");
        Child moreThanFiveYearOld = new Child("CASE-1", "MOTHER-CASE-1", "bcg", "3", "male").withAnm("ANM ID 1")
                .withDateOfBirth(referenceDate.minusYears(5).minusDays(1).toString());
        Child exactlyFiveYearOld = new Child("CASE-3", "MOTHER-CASE-1", "bcg", "3", "male").withAnm("ANM ID 1")
                .withDateOfBirth(referenceDate.minusYears(5).toString());
        Child oneDayLessThanFiveYearOld = new Child("CASE-2", "MOTHER-CASE-1", "bcg", "3", "male").withAnm("ANM ID 1")
                .withDateOfBirth(referenceDate.minusYears(5).plusDays(1).toString());
        Child bornOnSameDay = new Child("CASE-4", "MOTHER-CASE-1", "bcg", "3", "male").withAnm("ANM ID 1")
                .withDateOfBirth(referenceDate.toString());
        Child bornOneDayBefore = new Child("CASE-5", "MOTHER-CASE-1", "bcg", "3", "male").withAnm("ANM ID 1")
                .withDateOfBirth(referenceDate.minusDays(1).toString());
        Child fewYearsOld = new Child("CASE-6", "MOTHER-CASE-1", "bcg", "3", "male").withAnm("ANM ID 1")
                .withDateOfBirth(referenceDate.minusYears(2).toString());
        Child closed = new Child("CASE-7", "MOTHER-CASE-2", "bcg", "3", "male").withAnm("ANM ID 1").setIsClosed(true)
                .withDateOfBirth(referenceDate.toString());
        allChildren.add(moreThanFiveYearOld);
        allChildren.add(exactlyFiveYearOld);
        allChildren.add(oneDayLessThanFiveYearOld);
        allChildren.add(fewYearsOld);
        allChildren.add(bornOneDayBefore);
        allChildren.add(bornOnSameDay);
        allChildren.add(closed);

        List<Child> childrenFromDB = allChildren.
                findAllChildrenLessThanFiveYearOldAsOfDate(referenceDate);

        assertEquals(5, childrenFromDB.size());
        assertTrue(childrenFromDB.containsAll(asList(exactlyFiveYearOld, oneDayLessThanFiveYearOld, bornOnSameDay, fewYearsOld, bornOneDayBefore)));
    }

    private Child childWithoutDetails() {
        return new Child("CASE X", "MOTHER-CASE-1", "bcg", "3", "male")
                .withAnm("ANM ID 1");
    }
}
