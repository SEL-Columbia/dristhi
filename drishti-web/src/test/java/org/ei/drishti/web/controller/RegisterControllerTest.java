package org.ei.drishti.web.controller;

import org.ei.drishti.domain.register.ANCRegister;
import org.ei.drishti.domain.register.ANCRegisterEntry;
import org.ei.drishti.dto.register.ANCRegisterDTO;
import org.ei.drishti.dto.register.ANCRegisterEntryDTO;
import org.ei.drishti.service.RegisterService;
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
    private RegisterService registerService;
    private RegisterController controller;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        controller = new RegisterController(registerService, "http://dristhi_site_url");
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
        when(registerService.getANCRegister("anm1")).thenReturn(ancRegister);

        ResponseEntity<ANCRegisterDTO> response = controller.getANCRegister("anm1");

        assertEquals(expectedANCRegisterDTO, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getHeaders().containsKey("Access-Control-Allow-Origin"));
        assertEquals("http://dristhi_site_url", response.getHeaders().getFirst("Access-Control-Allow-Origin"));
    }
}
