package org.ei.drishti.web.controller;

import org.ei.drishti.domain.register.ANCRegister;
import org.ei.drishti.domain.register.ANCRegisterEntry;
import org.ei.drishti.domain.register.ECRegister;
import org.ei.drishti.domain.register.ECRegisterEntry;
import org.ei.drishti.dto.register.ANCRegisterDTO;
import org.ei.drishti.dto.register.ANCRegisterEntryDTO;
import org.ei.drishti.dto.register.ECRegisterDTO;
import org.ei.drishti.dto.register.ECRegisterEntryDTO;
import org.ei.drishti.mapper.ANCRegisterMapper;
import org.ei.drishti.mapper.ECRegisterMapper;
import org.ei.drishti.service.ANCRegisterService;
import org.ei.drishti.service.ECRegisterService;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class RegisterControllerTest {

    @Mock
    private ANCRegisterService ancRegisterService;
    @Mock
    private ECRegisterService ecRegisterService;
    @Mock
    private ANCRegisterMapper ancRegisterMapper;
    @Mock
    private ECRegisterMapper ecRegisterMapper;
    private RegisterController controller;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        controller = new RegisterController(ancRegisterService, ecRegisterService, ancRegisterMapper, ecRegisterMapper, "http://dristhi_site_url");
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
        assertTrue(response.getHeaders().containsKey("Access-Control-Allow-Origin"));
        assertEquals("http://dristhi_site_url", response.getHeaders().getFirst("Access-Control-Allow-Origin"));
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
        assertTrue(response.getHeaders().containsKey("Access-Control-Allow-Origin"));
        assertEquals("http://dristhi_site_url", response.getHeaders().getFirst("Access-Control-Allow-Origin"));
    }
}
