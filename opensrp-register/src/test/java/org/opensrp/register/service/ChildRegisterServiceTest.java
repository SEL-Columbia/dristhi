package org.opensrp.register.service;

import org.opensrp.common.util.EasyMap;
import org.opensrp.register.domain.Child;
import org.opensrp.register.domain.EligibleCouple;
import org.opensrp.register.domain.Mother;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Map;

import static java.util.Arrays.asList;
import static org.opensrp.common.util.EasyMap.mapOf;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import org.opensrp.register.ChildRegister;
import org.opensrp.register.ChildRegisterEntry;
import org.opensrp.register.repository.AllChildren;
import org.opensrp.register.repository.AllEligibleCouples;
import org.opensrp.register.repository.AllMothers;
import org.opensrp.register.service.ChildRegisterService;

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
        Map<String, String> immunizations = EasyMap.create("bcg", "2013-01-01")
                .put("opv_0", "2013-01-01")
                .put("hepb_0", "2013-01-01")
                .put("opv_1", "2013-01-01")
                .put("pentavalent_1", "2013-01-01")
                .put("opv_2", "2013-01-01")
                .put("pentavalent_2", "2013-01-01")
                .put("opv_3", "2013-01-01")
                .put("pentavalent_3", "2013-01-01")
                .put("measles", "2013-01-01")
                .put("je", "2013-01-01")
                .put("mmr", "2013-01-01")
                .put("dptbooster_1", "2013-01-01")
                .put("dptbooster_2", "2013-01-01")
                .put("opvbooster", "2013-01-01")
                .put("measlesbooster", "2013-01-01")
                .put("je_2", "2013-01-01")
                .map();
        Map<String, String> vitaminADoses = EasyMap.create("1", "2013-01-01")
                .put("2", "2013-01-01")
                .map();
        Child child = new Child("child id", "mother id", "bcg opv_0 hepb_0 opv_1 pentavalent_1 opv_2 pentavalent_2", "3.0", "male")
                .withImmunizations(immunizations)
                .withVitaminADoses(vitaminADoses)
                .withDateOfBirth("2013-01-01");
        Mother mother = new Mother("mother id", "ec id", "thayi card number 1");
        EligibleCouple eligibleCouple = new EligibleCouple("ec id", "123")
                .withCouple("name1", "name2")
                .withLocationId("boregowdanakoppalu")
                .withDetails(
                        mapOf("womanDOB", "1989-01-01"));
        ChildRegister expectedRegister = new ChildRegister(asList(new ChildRegisterEntry()
                .withThayiCardNumber("thayi card number 1")
                .withWifeName("name1")
                .withHusbandName("name2")
                .withWifeDOB("1989-01-01")
                .withDOB("2013-01-01")
                .withImmunizations(immunizations)
                .withVitaminADoses(vitaminADoses)));
        when(allChildren.findAllOpenChildrenForANM("anm1")).thenReturn(asList(child));
        when(allMothers.findAll(asList("mother id"))).thenReturn(asList(mother));
        when(allEligibleCouples.findAll(asList("ec id"))).thenReturn(asList(eligibleCouple));

        ChildRegister register = registerService.getRegisterForANM("anm1");

        assertEquals(expectedRegister, register);
    }
}
