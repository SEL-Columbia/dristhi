package org.ei.drishti.repository;

import com.google.gson.Gson;
import org.ei.drishti.domain.FormSubmission;
import org.ektorp.CouchDbConnector;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Map;

import static org.ei.drishti.util.EasyMap.create;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class FormDataRepositoryTest {
    @Mock
    private AllFormSubmissions allFormSubmissions;
    @Mock
    private CouchDbConnector dbConnector;

    private FormDataRepository repository;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        repository = new FormDataRepository(allFormSubmissions, dbConnector);
    }

    @Test
    public void shouldSaveFormSubmission() throws Exception {
        Map<String, String> params = create("instanceId", "id 1")
                .put("formName", "form name")
                .put("anmId", "anm 1")
                .put("timeStamp", "0")
                .put("entityId", "entity id 1")
                .map();
        String paramsJSON = new Gson().toJson(params);
        FormSubmission submission = new FormSubmission("id 1", "form name", "anm 1", "0", "entity id 1", "data");

        repository.saveFormSubmission(paramsJSON, "data");

        verify(allFormSubmissions).add(submission);
    }
}
