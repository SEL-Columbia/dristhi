package org.opensrp.service.formSubmission;


import com.google.gson.Gson;
import org.joda.time.DateTime;
import org.junit.Test;
import org.opensrp.BaseIntegrationTest;
import org.opensrp.common.FormEntityConstants;
import org.opensrp.domain.Address;
import org.opensrp.domain.Client;
import org.opensrp.domain.Event;
import org.opensrp.domain.Obs;
import org.opensrp.form.domain.FormSubmission;
import org.opensrp.form.service.FormAttributeParser;
import org.opensrp.form.service.FormSubmissionMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class FormEntityConverterTest extends BaseIntegrationTest {

    @Autowired
    FormAttributeParser formAttributeParser;
    @Autowired
    private FormEntityConverter formEntityConverter;

    private Gson gson = new Gson();

    @Test
    public void test() throws Exception {
        FormSubmission fs = getFormSubmissionFor("new_household_registration", 1);
        System.out.println("************************form submission ************************");
        System.out.println(gson.toJson(fs));
        System.out.println("************************form submission ************************");
        System.out.println("************************ client ************************");
        Client client = formEntityConverter.getClientFromFormSubmission(fs);
        System.out.println(gson.toJson(client));
        System.out.println("************************ client ************************");
    }


    //TODO: test date approximation check
    @Test
    public void testCreateBaseClient() throws Exception {
        String addressString = "  {\n" +
                "      \"addressType\": \"usual_residence\",\n" +
                "      \"addressFields\": {\n" +
                "        \"landmark\": \"nothing\"\n" +
                "      },\n" +
                "      \"latitude\": \"34\",\n" +
                "      \"longitude\": \"34\",\n" +
                "      \"geopoint\": \"34 34 0 0\"\n" +
                "    }";
        Client expectedClient = new Client("a3f2abf4-2699-4761-819a-cea739224164");
        expectedClient.withFirstName("test")
                .withLastName(".")
                .withGender("male")
                .withBirthdate(new DateTime("1900-01-01").withTimeAtStartOfDay(), false)
                .withAddress(new Gson().fromJson(addressString, Address.class))
                .withIdentifier("JiVita HHID", "1234")
                .withIdentifier("GOB HHID", "1234");
        FormSubmission fsubmission = getFormSubmissionFor("new_household_registration", 1);
        FormSubmissionMap formSubmissionMap = formAttributeParser.createFormSubmissionMap(fsubmission);


        Client client = formEntityConverter.createBaseClient(formSubmissionMap);


        assertEquals(expectedClient.fullName(), client.fullName());
        assertEquals(expectedClient.getBirthdate(), client.getBirthdate());
        assertEquals(expectedClient.getBirthdateApprox(), client.getBirthdateApprox());
        assertEquals(expectedClient.getGender(), client.getGender());
        assertEquals(expectedClient.getIdentifier("JiVita HHID"), client.getIdentifier("JiVita HHID"));
        assertEquals(expectedClient.getIdentifier("GOB HHID"), client.getIdentifier("GOB HHID"));

    }

    @Test
    public void testValidOpenmrsForm() throws IOException, XPathExpressionException, SAXException, ParserConfigurationException {
        FormSubmission fsubmission = getFormSubmissionFor("new_household_registration", 1);
        FormSubmissionMap formSubmissionMap = formAttributeParser.createFormSubmissionMap(fsubmission);

        boolean expectedValue = formEntityConverter.isOpenmrsForm(formSubmissionMap);

        assertTrue(expectedValue);
    }

    @Test
    public void testInvalidOpenmrsForm() throws IOException, XPathExpressionException, SAXException, ParserConfigurationException {
        FormSubmission fsubmission = getFormSubmissionFor("new_household_registration", 1);
        FormSubmissionMap formSubmissionMap = formAttributeParser.createFormSubmissionMap(fsubmission);
        formSubmissionMap.formAttributes().remove("encounter_type");

        boolean expectedValue = formEntityConverter.isOpenmrsForm(formSubmissionMap);

        assertFalse(expectedValue);
    }

    @Test
    public void testCreateEvent() throws ParserConfigurationException, SAXException, XPathExpressionException, IOException, ParseException {
        FormSubmission fsubmission = getFormSubmissionFor("new_household_registration", 1);
        FormSubmissionMap formSubmissionMap = formAttributeParser.createFormSubmissionMap(fsubmission);
        Event expectedEvent = new Event()
                .withBaseEntityId(fsubmission.entityId())
                .withEventDate(new DateTime(FormEntityConstants.FORM_DATE.parse("2015-05-07")))
                .withEventType(formSubmissionMap.formAttributes().get("encounter_type"))
                .withLocationId("KUPTALA")
                .withProviderId(formSubmissionMap.providerId())
                .withEntityType(formSubmissionMap.bindType())
                .withFormSubmissionId(formSubmissionMap.instanceId());

        List<Object> values = new ArrayList<>();
        values.add("2015-05-07");

        Obs obs1 = new Obs("concept", "DateTime",
                "160753AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, values,
                null, "FWNHREGDATE");
        values.clear();
        values.add(2);
        Obs obs2 = new Obs("concept", "Integer",
                "5611AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, values,
                null, "FWNHHMBRNUM");
        expectedEvent.addObs(obs1);
        expectedEvent.addObs(obs2);

        Event event = formEntityConverter.getEventFromFormSubmission(fsubmission);

        assertEvents(expectedEvent, event);
    }

    @Test(expected = IllegalStateException.class)
    public void testExceptionWhileCreatingEventFromFromSubmission() {
        FormSubmission fsubmission = new FormSubmission();
        formEntityConverter.getEventFromFormSubmission(fsubmission);
    }

    @Test
    public void testGetFieldName() throws IOException, XPathExpressionException, SAXException, ParserConfigurationException {
        String expected = "FWHOHGENDER";
        FormSubmission fsubmission = getFormSubmissionFor("new_household_registration", 1);
        FormSubmissionMap formSubmissionMap = formAttributeParser.createFormSubmissionMap(fsubmission);
        String actual = formEntityConverter.getFieldName(FormEntityConstants.Person.gender, formSubmissionMap);

        assertEquals(expected, actual);
    }

    @Test
    public void testGetFieldNameInSubForm() throws IOException, XPathExpressionException, SAXException, ParserConfigurationException {
        String expected = "FWGENDER";
        FormSubmission fsubmission = getFormSubmissionFor("new_household_registration", 1);
        FormSubmissionMap formSubmissionMap = formAttributeParser.createFormSubmissionMap(fsubmission);
        String actual =
                formEntityConverter.getFieldName(FormEntityConstants.Person.gender, formSubmissionMap.subforms().get(0));

        assertEquals(expected, actual);
    }

    @Test
    public void testInvalidFieldNameInForm() throws IOException, XPathExpressionException, SAXException, ParserConfigurationException {
        FormSubmission fsubmission = getFormSubmissionFor("new_household_registration", 1);
        FormSubmissionMap formSubmissionMap = formAttributeParser.createFormSubmissionMap(fsubmission);
        String actual =
                formEntityConverter.getFieldName(FormEntityConstants.Encounter.encounter_date, formSubmissionMap.subforms().get(0));
        assertNull(actual);
    }

    @Test
    public void testGetFieldNameUsingEntity() throws IOException, XPathExpressionException, SAXException, ParserConfigurationException {
        String expected = "FWNHHMBRNUM";
        FormSubmission fsubmission = getFormSubmissionFor("new_household_registration", 1);
        FormSubmissionMap formSubmissionMap = formAttributeParser.createFormSubmissionMap(fsubmission);
        String actual = formEntityConverter.getFieldName("concept","5611AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, formSubmissionMap);

        assertEquals(expected, actual);
    }

    @Test
    public void testGetFieldNameUsingEnitityWithInvalidData() throws IOException, XPathExpressionException, SAXException, ParserConfigurationException {
        FormSubmission fsubmission = getFormSubmissionFor("new_household_registration", 1);
        FormSubmissionMap formSubmissionMap = formAttributeParser.createFormSubmissionMap(fsubmission);
        String actual = formEntityConverter.getFieldName("concept","5611AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, formSubmissionMap.subforms().get(0));
        assertNull(actual);
    }

    private void assertEvents(Event expected, Event actual) {
        assertEquals(expected.getBaseEntityId(), actual.getBaseEntityId());
        assertEquals(expected.getEntityType(), actual.getEntityType());
        assertEquals(expected.getDetails(), actual.getDetails());
        assertEquals(expected.getEventDate(), actual.getEventDate());
        assertEquals(expected.getFormSubmissionId(), actual.getFormSubmissionId());
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getIdentifiers(), expected.getIdentifiers());
        assertThat(actual.getObs(), is(actual.getObs()));
        assertEquals(expected.getLocationId(), actual.getLocationId());
        assertEquals(expected.getProviderId(), actual.getProviderId());
    }


}
