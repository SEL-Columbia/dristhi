package org.ei.drishti.repository.it;

import com.google.gson.Gson;
import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.domain.form.FormInstance;
import org.ei.drishti.domain.form.FormSubmission;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.repository.AllFormSubmissions;
import org.ei.drishti.repository.AllMothers;
import org.ei.drishti.repository.FormDataRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;

import static org.ei.drishti.util.EasyMap.create;
import static org.ei.drishti.util.EasyMap.mapOf;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-applicationContext-drishti.xml")
public class FormDataRepositoryIntegrationTest {
    @Autowired
    private FormDataRepository repository;

    @Autowired
    private AllEligibleCouples eligibleCouples;

    @Autowired
    private AllMothers mothers;

    @Autowired
    private AllFormSubmissions formSubmissions;

    @Before
    public void setUp() throws Exception {
        eligibleCouples.removeAll();
        mothers.removeAll();
        formSubmissions.removeAll();
    }

    @Test
    public void shouldSaveFormSubmission() throws Exception {
        Map<String, String> params = create("instanceId", "id 1")
                .put("formName", "form name")
                .put("anmId", "anm 1")
                .put("timeStamp", "0")
                .put("entityId", "entity id 1")
                .put("serverVersion", "1")
                .map();
        String formInstanceJSON = "{form:{bind_type: 'ec'}}";
        String paramsJSON = new Gson().toJson(params);
        FormSubmission submission = new FormSubmission("anm 1", "id 1", "form name", "entity id 1", new Gson().fromJson(formInstanceJSON, FormInstance.class), 0L).withServerVersion(1L);

        String instanceId = repository.saveFormSubmission(paramsJSON, formInstanceJSON);

        assertEquals(submission, formSubmissions.getAll().get(0));
        assertEquals("id 1", instanceId);
    }

    @Test
    public void shouldSaveNewEntityAsEligibleCouple() throws Exception {
        Map<String, String> fields =
                create("id", "entity id 1")
                        .put("husband", "raja")
                        .put("ecNumber", "ec 123")
                        .put("wife", "asha")
                        .put("village", "")
                        .put("phc", "phc")
                        .put("subCenter", "sub center")
                        .put("currentMethod", "ocp")
                        .put("isHighPriority", "no")
                        .map();
        String fieldsJSON = new Gson().toJson(fields);

        String entityId = repository.saveEntity("eligible_couple", fieldsJSON);

        assertEquals(entityId, "entity id 1");
        EligibleCouple savedEC = eligibleCouples.findByCaseId(entityId);
        Map<String, String> expectedDetails = create("currentMethod", "ocp")
                .put("isHighPriority", "no")
                .map();
        EligibleCouple expectedEligibleCouple = new EligibleCouple("entity id 1", "ec 123")
                .withCouple("asha", "raja").withLocation("", "sub center", "phc").withDetails(expectedDetails);
        assertEquals(expectedEligibleCouple, savedEC);
    }

    @Test
    public void shouldSaveNewEntityWhenThereIsAnDifferentExistingEntityOfSameType() throws Exception {
        EligibleCouple existingEligibleCouple = new EligibleCouple("entity id 0", "ec 123").withDetails(mapOf("key", "value"));
        eligibleCouples.add(existingEligibleCouple);
        Map<String, String> fields =
                create("id", "entity id 1")
                        .put("husband", "raja")
                        .put("ecNumber", "ec 123")
                        .put("wife", "asha")
                        .put("village", "")
                        .put("phc", "phc")
                        .put("subCenter", "sub center")
                        .put("currentMethod", "ocp")
                        .put("isHighPriority", "no")
                        .map();
        String fieldsJSON = new Gson().toJson(fields);

        String entityId = repository.saveEntity("eligible_couple", fieldsJSON);

        assertEquals(entityId, "entity id 1");
        EligibleCouple savedEC = eligibleCouples.findByCaseId(entityId);
        EligibleCouple existingEC = eligibleCouples.findByCaseId("entity id 0");
        Map<String, String> expectedDetails = create("currentMethod", "ocp")
                .put("isHighPriority", "no")
                .map();
        EligibleCouple expectedEligibleCouple = new EligibleCouple("entity id 1", "ec 123")
                .withCouple("asha", "raja").withLocation("", "sub center", "phc").withDetails(expectedDetails);
        assertEquals(expectedEligibleCouple, savedEC);
        assertEquals(existingEligibleCouple, existingEC);
    }

    @Test
    public void shouldUpdateExistingEntityAsEligibleCouple() throws Exception {
        Map<String, String> fields =
                create("id", "entity id 1")
                        .put("husband", "raja")
                        .put("ecNumber", "ec 123")
                        .put("wife", "asha").put("village", "")
                        .put("currentMethod", "ocp")
                        .put("isHighPriority", "no")
                        .map();
        String fieldsJSON = new Gson().toJson(fields);
        Map<String, String> oldDetails = create("currentMethod", "condom")
                .put("isHighPriority", "yes")
                .put("bloodGroup", "o-ve")
                .map();
        EligibleCouple oldEC = new EligibleCouple("entity id 1", "ec 123")
                .withCouple("old wife name", "old husband name").withLocation("old village", "sub center", "phc").withDetails(oldDetails);
        eligibleCouples.register(oldEC);

        String entityId = repository.saveEntity("eligible_couple", fieldsJSON);

        assertEquals(entityId, "entity id 1");
        EligibleCouple savedEC = eligibleCouples.findByCaseId(entityId);
        Map<String, String> expectedDetails = create("currentMethod", "ocp")
                .put("isHighPriority", "no")
                .put("bloodGroup", "o-ve")
                .map();
        EligibleCouple expectedEligibleCouple = new EligibleCouple("entity id 1", "ec 123")
                .withCouple("asha", "raja").withLocation("", "sub center", "phc").withDetails(expectedDetails);
        assertEquals(expectedEligibleCouple, savedEC);
    }

    @Test
    public void shouldUpdateMotherEntity() throws Exception {
        Map<String, String> fields =
                create("id", "entity id 1")
                        .put("thaayiCardNumber", "thaayi1")
                        .put("name", "ashaNew")
                        .put("ecCaseId", "ec 123")
                        .put("isHighPriority", "no")
                        .map();
        String fieldsJSON = new Gson().toJson(fields);
        Mother oldMother = new Mother("entity id 1", "ec 123", "thaayi2", "asha");
        mothers.add(oldMother);

        String entityId = repository.saveEntity("mother", fieldsJSON);

        Mother savedMother = mothers.findByCaseId(entityId);
        Map<String, String> expectedDetails = mapOf("isHighPriority", "no");
        Mother expectedMother = new Mother("entity id 1", "ec 123", "thaayi1", "ashaNew").withDetails(expectedDetails);
        assertEquals(expectedMother, savedMother);
    }
}
