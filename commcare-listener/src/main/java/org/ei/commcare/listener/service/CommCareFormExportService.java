package org.ei.commcare.listener.service;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import org.ei.commcare.listener.contract.CommcareFormDefinition;
import org.ei.commcare.listener.contract.CommcareFormDefinitions;
import org.ei.commcare.listener.dao.AllExportTokens;
import org.ei.commcare.listener.domain.CommcareForm;
import org.ei.commcare.listener.util.CommCareHttpResponse;
import org.ei.commcare.listener.util.CommCareFormContent;
import org.ei.commcare.listener.util.CommcareHttpClient;
import org.motechproject.dao.MotechJsonReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Service
public class CommCareFormExportService {
    public static final String COMMCARE_EXPORT_DEFINITION_FILE = "commcare-export.definition.file";
    private final CommcareHttpClient httpClient;
    private CommcareFormDefinitions formDefinitions;
    private AllExportTokens allExportTokens;

    @Autowired
    public CommCareFormExportService(CommcareHttpClient httpClient, @Qualifier("commCareListenerProperties") Properties listenerProperties, AllExportTokens allExportTokens) {
        this.httpClient = httpClient;
        this.allExportTokens = allExportTokens;

        String exportDefinitionJsonPath = listenerProperties.getProperty(COMMCARE_EXPORT_DEFINITION_FILE);
        this.formDefinitions = (CommcareFormDefinitions) new MotechJsonReader().readFromFile(exportDefinitionJsonPath, CommcareFormDefinitions.class);
    }

    public List<CommcareForm> fetchForms() throws IOException {
        return processAllForms(fetchAllForms());
    }

    private List<CommCareFormWithResponse> fetchAllForms() throws IOException {
        List<CommCareFormWithResponse> formWithResponses = new ArrayList<CommCareFormWithResponse>();

        for (CommcareFormDefinition formDefinition : formDefinitions.definitions()) {
            String previousToken = allExportTokens.findByNamespace(formDefinition.nameSpace());
            CommCareHttpResponse responseFromCommCareHQ = httpClient.get(formDefinition.url(previousToken), formDefinitions.userName(), formDefinitions.password());

            if (responseFromCommCareHQ.isValid()) {
                allExportTokens.updateToken(formDefinition.nameSpace(), responseFromCommCareHQ.tokenForNextExport());
                formWithResponses.add(new CommCareFormWithResponse(formDefinition, responseFromCommCareHQ));
            }
        }

        return formWithResponses;
    }

    private List<CommcareForm> processAllForms(List<CommCareFormWithResponse> careFormWithResponses) {
        List<CommcareForm> formZips = new ArrayList<CommcareForm>();
        for (CommCareFormWithResponse formWithResponse : careFormWithResponses) {
            CommcareFormDefinition definition = formWithResponse.formDefinition;
            CommCareHttpResponse response = formWithResponse.response;

            CommCareExportedForms exportedFormData = null;
            try {
                exportedFormData = new Gson().fromJson(response.content(), CommCareExportedForms.class);
            } catch (JsonParseException e) {
                throw new RuntimeException(response.content() + e);
            }
            for (List<String> formData : exportedFormData.formContents()) {
                formZips.add(new CommcareForm(definition, new CommCareFormContent(exportedFormData.headers(), formData)));
            }
        }

        return formZips;
    }

    private class CommCareFormWithResponse {
        private final CommcareFormDefinition formDefinition;
        private final CommCareHttpResponse response;

        public CommCareFormWithResponse(CommcareFormDefinition formDefinition, CommCareHttpResponse response) {
            this.formDefinition = formDefinition;
            this.response = response;
        }
    }

    private static class CommCareExportedForms {
        @SerializedName("#.#") private CommCareExportedHeadersAndContent content;
        private static class CommCareExportedHeadersAndContent {
            private List<String> headers;
            private List<List<String>> rows;

        }

        public List<String> headers() {
            return content.headers;
        }
        public List<List<String>> formContents() {
            return content.rows;
        }

    }
}
