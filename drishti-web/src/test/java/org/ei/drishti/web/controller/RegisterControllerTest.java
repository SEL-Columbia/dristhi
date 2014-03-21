package org.ei.drishti.web.controller;

import org.ei.drishti.common.util.EasyMap;
import org.ei.drishti.domain.register.*;
import org.ei.drishti.dto.register.*;
import org.ei.drishti.mapper.ANCRegisterMapper;
import org.ei.drishti.mapper.ChildRegisterMapper;
import org.ei.drishti.mapper.ECRegisterMapper;
import org.ei.drishti.service.ANCRegisterService;
import org.ei.drishti.service.ChildRegisterService;
import org.ei.drishti.service.ECRegisterService;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class RegisterControllerTest {

    @Mock
    private ANCRegisterService ancRegisterService;
    @Mock
    private ECRegisterService ecRegisterService;
    @Mock
    private ChildRegisterService childRegisterService;
    @Mock
    private ANCRegisterMapper ancRegisterMapper;
    @Mock
    private ECRegisterMapper ecRegisterMapper;
    @Mock
    private ChildRegisterMapper childRegisterMapper;
    private RegisterController controller;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        controller = new RegisterController(
                ancRegisterService, ecRegisterService, childRegisterService,
                ancRegisterMapper, ecRegisterMapper, childRegisterMapper, "http://dristhi_site_url");
    }

    @Test
    public void shouldGetECRegisterForGivenANM() throws Exception {
        ECRegister ecRegister = new ECRegister(asList(new ECRegisterEntry()
                .withECNumber("123")
                .withRegistrationDate("2013-01-01")
                .withWifeName("wifeName")
                .withHusbandName("husbandName")
                .withVillage("village")
                .withSubCenter("sc")
                .withPHC("phc")
                .withWifeAge("23")
                .withHusbandAge("32")
                .withHouseholdNumber("22")
                .withHouseholdAddress("HH Address")
                .withHeadOfHousehold("Head of HH")
                .withReligion("religion")
                .withCaste("caste")
                .withEconomicStatus("bpl")
                .withWifeEducationLevel("wife education level")
                .withHusbandEducationLevel("husband education level")
                .withGravida("2")
                .withParity("1")
                .withNumberOfLivingChildren("1")
                .withNumberOfLivingMaleChildren("0")
                .withNumberOfLivingFemaleChildren("1")
                .withNumberOfStillBirths("0")
                .withNumberOfAbortions("1")
                .withYoungestChildAge("1")
                .withCurrentFPMethod("ocp")
                .withCurrentFPMethodStartDate("2012-01-01")
                .withPregnancyStatus(false)
        ));
        ECRegisterDTO expectedECRegisterDTO = new ECRegisterDTO(asList(new ECRegisterEntryDTO()
                .withECNumber("123")
                .withRegistrationDate("2013-01-01")
                .withWifeName("wifeName")
                .withHusbandName("husbandName")
                .withVillage("village")
                .withSubCenter("sc")
                .withPHC("phc")
                .withWifeAge("23")
                .withHusbandAge("32")
                .withHouseholdNumber("22")
                .withHouseholdAddress("HH Address")
                .withHeadOfHousehold("Head of HH")
                .withReligion("religion")
                .withCaste("caste")
                .withEconomicStatus("bpl")
                .withWifeEducationLevel("wife education level")
                .withHusbandEducationLevel("husband education level")
                .withGravida("2")
                .withParity("1")
                .withNumberOfLivingChildren("1")
                .withNumberOfLivingMaleChildren("0")
                .withNumberOfLivingFemaleChildren("1")
                .withNumberOfStillBirths("0")
                .withNumberOfAbortions("1")
                .withYoungestChildAge("1")
                .withCurrentFPMethod("ocp")
                .withCurrentFPMethodStartDate("2012-01-01")
                .withPregnancyStatus("no")
        ));

        when(ecRegisterService.getRegisterForANM("anm1")).thenReturn(ecRegister);
        when(ecRegisterMapper.mapToDTO(ecRegister)).thenReturn(expectedECRegisterDTO);

        ResponseEntity<ECRegisterDTO> response = controller.getECRegister("anm1");

        assertEquals(expectedECRegisterDTO, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void shouldGetANCRegisterForGivenANM() throws Exception {
        ANCRegister ancRegister = new ANCRegister(asList(new ANCRegisterEntry()
                .withANCNumber("OA899")
                .withRegistrationDate("2014-01-01")
                .withECNumber("123")
                .withThayiCardNumber("thayi card number 1")
                .withAadharCardNumber("aadhar card number 1")
                .withWifeName("name1")
                .withHusbandName("name2")
                .withAddress("address1")
                .withWifeDOB(LocalDate.parse("1989-01-01"))
                .withPhoneNumber("phone 1")
                .withWifeEducationLevel("wife education level")
                .withHusbandEducationLevel("husband education level")
                .withCaste("sc")
                .withReligion("hindu")
                .withEconomicStatus("bpl")
                .withBPLCardNumber("bpl card number 1")
                .withJSYBeneficiary("yes")
                .withGravida("1")
                .withParity("2")
                .withNumberOfLivingChildren("3")
                .withNumberOfStillBirths("4")
                .withNumberOfAbortions("5")
                .withYoungestChildDOB("2012-01-01")
                .withLMP("2013-12-30")
                .withEDD("2014-09-01")
                .withHeight("150")
                .withBloodGroup("o -ve")
                .withIsHRP("yes")));
        ANCRegisterDTO expectedANCRegisterDTO = new ANCRegisterDTO(asList(new ANCRegisterEntryDTO()
                .withANCNumber("OA899")
                .withRegistrationDate("2014-01-01")
                .withECNumber("123")
                .withThayiCardNumber("thayi card number 1")
                .withAadharCardNumber("aadhar card number 1")
                .withWifeName("name1")
                .withHusbandName("name2")
                .withAddress("address1")
                .withWifeDOB(LocalDate.parse("1989-01-01"))
                .withPhoneNumber("phone 1")
                .withWifeEducationLevel("wife education level")
                .withHusbandEducationLevel("husband education level")
                .withCaste("sc")
                .withReligion("hindu")
                .withEconomicStatus("bpl")
                .withBPLCardNumber("bpl card number 1")
                .withJSYBeneficiary("yes")
                .withGravida("1")
                .withParity("2")
                .withNumberOfLivingChildren("3")
                .withNumberOfStillBirths("4")
                .withNumberOfAbortions("5")
                .withYoungestChildDOB("2012-01-01")
                .withLMP("2013-12-30")
                .withEDD("2014-09-01")
                .withHeight("150")
                .withBloodGroup("o -ve")
                .withIsHRP("yes")));
        when(ancRegisterService.getRegisterForANM("anm1")).thenReturn(ancRegister);
        when(ancRegisterMapper.mapToDTO(ancRegister)).thenReturn(expectedANCRegisterDTO);

        ResponseEntity<ANCRegisterDTO> response = controller.getANCRegister("anm1");

        assertEquals(expectedANCRegisterDTO, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void shouldGetChildRegisterForGivenANM() throws Exception {
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
        ChildRegister childRegister = new ChildRegister(asList(new ChildRegisterEntry()
                .withThayiCardNumber("thayi card number 1")
                .withWifeName("name1")
                .withHusbandName("name2")
                .withDOB(LocalDate.parse("2013-01-01"))
                .withVillage("village1")
                .withSubCenter("subCenter1")
                .withWifeDOB(LocalDate.parse("1989-01-01"))
                .withImmunizations(immunizations)
                .withVitaminADoses(vitaminADoses)));

        ChildRegisterDTO expectedChildRegisterDTO = new ChildRegisterDTO(asList(new ChildRegisterEntryDTO()
                .withThayiCardNumber("thayi card number 1")
                .withWifeName("name1")
                .withHusbandName("name2")
                .withDOB(LocalDate.parse("2013-01-01"))
                .withVillage("village1")
                .withSubCenter("subCenter1")
                .withWifeDOB(LocalDate.parse("1989-01-01"))
                .withImmunizations(immunizations)
                .withVitaminADoses(vitaminADoses)));

        when(childRegisterService.getRegisterForANM("anm1")).thenReturn(childRegister);
        when(childRegisterMapper.mapToDTO(childRegister)).thenReturn(expectedChildRegisterDTO);

        ResponseEntity<ChildRegisterDTO> response = controller.getChildRegister("anm1");

        assertEquals(expectedChildRegisterDTO, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
