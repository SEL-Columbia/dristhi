package org.ei.drishti.repository.it;

import com.google.gson.Gson;
import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.repository.AllMothers;
import org.ei.drishti.repository.FormDataRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Map;

import static org.ei.drishti.common.util.EasyMap.create;
import static org.ei.drishti.common.util.EasyMap.mapOf;
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

    @Before
    public void setUp() throws Exception {
        eligibleCouples.removeAll();
        mothers.removeAll();
    }

    @Test
    public void shouldSaveNewEntityAsEligibleCouple() throws Exception {
        Map<String, String> fields =
                create("id", "entity id 1")
                        .put("husbandName", "raja")
                        .put("wifeName", "asha")
                        .put("ecNumber", "ec 123")
                        .put("village", "")
                        .put("phc", "phc")
                        .put("subCenter", "sub center")
                        .put("currentMethod", "ocp")
                        .put("isHighPriority", "no")
                        .put("isClosed", "false")
                        .put("isOutOfArea", "false")
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
                        .put("husbandName", "raja")
                        .put("wifeName", "asha")
                        .put("ecNumber", "ec 123")
                        .put("village", "")
                        .put("phc", "phc")
                        .put("subCenter", "sub center")
                        .put("currentMethod", "ocp")
                        .put("isHighPriority", "no")
                        .put("isClosed", "false")
                        .put("isOutOfArea", "false")
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
                        .put("husbandName", "raja")
                        .put("ecNumber", "ec 123")
                        .put("wifeName", "asha").put("village", "")
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
        eligibleCouples.add(oldEC);

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
                        .put("thayiCardNumber", "thayi1")
                        .put("ecCaseId", "ec 123")
                        .put("isHighPriority", "no")
                        .map();
        String fieldsJSON = new Gson().toJson(fields);
        Mother oldMother = new Mother("entity id 1", "ec 123", "thayi2");
        mothers.add(oldMother);

        String entityId = repository.saveEntity("mother", fieldsJSON);

        Mother savedMother = mothers.findByCaseId(entityId);
        Map<String, String> expectedDetails = mapOf("isHighPriority", "no");
        Mother expectedMother = new Mother("entity id 1", "ec 123", "thayi1").withDetails(expectedDetails);
        assertEquals(expectedMother, savedMother);
    }
}
