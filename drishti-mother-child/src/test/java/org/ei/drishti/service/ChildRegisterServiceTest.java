package org.ei.drishti.service;

import org.ei.drishti.common.util.EasyMap;
import org.ei.drishti.domain.Child;
import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.domain.register.ChildRegister;
import org.ei.drishti.domain.register.ChildRegisterEntry;
import org.ei.drishti.repository.AllChildren;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.repository.AllMothers;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Map;

import static java.util.Arrays.asList;
import static org.ei.drishti.common.util.EasyMap.mapOf;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ChildRegisterServiceTest {

    private ChildRegisterService registerService;
    @Mock
    private AllChildren allChildren;
    @Mock
    private AllMothers allMothers;
    @Mock
    private AllEligibleCouples allEligibleCouples;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        registerService = new ChildRegisterService(allChildren, allMothers, allEligibleCouples);
    }

    @Test
    public void shouldGetChildRegisterForAGivenANM() throws Exception {
        Map<String, LocalDate> immunizations = EasyMap.create("bcg", LocalDate.parse("2013-01-01"))
                .put("opv_0", LocalDate.parse("2013-01-01"))
                .put("hepb_0", LocalDate.parse("2013-01-01"))
                .put("opv_1", LocalDate.parse("2013-01-01"))
                .put("pentavalent_1", LocalDate.parse("2013-01-01"))
                .put("opv_2", LocalDate.parse("2013-01-01"))
                .put("pentavalent_2", LocalDate.parse("2013-01-01"))
                .put("opv_3", LocalDate.parse("2013-01-01"))
                .put("pentavalent_3", LocalDate.parse("2013-01-01"))
                .put("measles", LocalDate.parse("2013-01-01"))
                .put("je", LocalDate.parse("2013-01-01"))
                .put("mmr", LocalDate.parse("2013-01-01"))
                .put("dptbooster_1", LocalDate.parse("2013-01-01"))
                .put("dptbooster_2", LocalDate.parse("2013-01-01"))
                .put("opvbooster", LocalDate.parse("2013-01-01"))
                .put("measlesbooster", LocalDate.parse("2013-01-01"))
                .put("je_2", LocalDate.parse("2013-01-01"))
                .map();
        Map<String, LocalDate> vitaminADoses = EasyMap.create("1", LocalDate.parse("2013-01-01"))
                .put("2", LocalDate.parse("2013-01-01"))
                .map();
        Child child = new Child("child id", "mother id", "bcg opv_0 hepb_0 opv_1 pentavalent_1 opv_2 pentavalent_2", "3.0", "male")
                .withImmunizations(immunizations)
                .withVitaminADoses(vitaminADoses)
                .withDateOfBirth("2013-01-01");
        Mother mother = new Mother("mother id", "ec id", "thayi card number 1");
        EligibleCouple eligibleCouple = new EligibleCouple("ec id", "123")
                .withCouple("name1", "name2")
                .withLocation("boregowdanakoppalu", "hosa_agrahara", "phc")
                .withDetails(
                        mapOf("womanDOB", "1989-01-01"));
        ChildRegister expectedRegister = new ChildRegister(asList(new ChildRegisterEntry()
                .withThayiCardNumber("thayi card number 1")
                .withWifeName("name1")
                .withHusbandName("name2")
                .withWifeDOB(LocalDate.parse("1989-01-01"))
                .withVillage("boregowdanakoppalu")
                .withSubCenter("hosa_agrahara")
                .withDOB(LocalDate.parse("2013-01-01"))
                .withImmunizations(immunizations)
                .withVitaminADoses(vitaminADoses)));
        when(allChildren.findAllOpenChildrenForANM("anm1")).thenReturn(asList(child));
        when(allMothers.findAll(asList("mother id"))).thenReturn(asList(mother));
        when(allEligibleCouples.findAll(asList("ec id"))).thenReturn(asList(eligibleCouple));

        ChildRegister register = registerService.getRegisterForANM("anm1");

        assertEquals(expectedRegister, register);
    }
}
