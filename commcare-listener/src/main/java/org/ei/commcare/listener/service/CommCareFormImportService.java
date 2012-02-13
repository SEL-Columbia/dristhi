package org.ei.commcare.listener.service;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import org.ei.commcare.listener.contract.CommCareFormDefinition;
import org.ei.commcare.listener.contract.CommCareFormDefinitions;
import org.ei.commcare.listener.domain.CommCareFormContent;
import org.ei.commcare.listener.domain.CommcareForm;
import org.ei.commcare.listener.repository.AllExportTokens;
import org.ei.commcare.listener.util.CommCareHttpClient;
import org.ei.commcare.listener.util.CommCareHttpResponse;
import org.ei.commcare.listener.util.CommCareListenerProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Service
public class CommCareFormImportService {
    private final CommCareHttpClient httpClient;
    private CommCareFormDefinitions formDefinitions;
    private AllExportTokens allExportTokens;
    private static Logger logger = Logger.getLogger(CommCareFormImportService.class.toString());

    @Autowired
    public CommCareFormImportService(AllExportTokens allExportTokens, CommCareHttpClient httpClient, CommCareListenerProperties properties) {
        this.httpClient = httpClient;
        this.allExportTokens = allExportTokens;

        this.formDefinitions = properties.definitions();
    }

    public List<CommcareForm> fetchForms() throws IOException {
        return processAllForms(fetchAllForms());
    }

    private List<CommCareFormWithResponse> fetchAllForms() throws IOException {
        List<CommCareFormWithResponse> formWithResponses = new ArrayList<CommCareFormWithResponse>();

        for (CommCareFormDefinition formDefinition : formDefinitions.definitions()) {
            String previousToken = allExportTokens.findByNameSpace(formDefinition.nameSpace()).value();
            CommCareHttpResponse responseFromCommCareHQ = httpClient.get(formDefinition.url(previousToken), formDefinitions.userName(), formDefinitions.password());

            if (responseFromCommCareHQ.hasValidExportToken()) {
                allExportTokens.updateToken(formDefinition.nameSpace(), responseFromCommCareHQ.tokenForNextExport());
                formWithResponses.add(new CommCareFormWithResponse(formDefinition, responseFromCommCareHQ));
            }
        }

        return formWithResponses;
    }

    private List<CommcareForm> processAllForms(List<CommCareFormWithResponse> careFormWithResponses) {
        List<CommcareForm> forms = new ArrayList<CommcareForm>();
        for (CommCareFormWithResponse formWithResponse : careFormWithResponses) {
            CommCareFormDefinition definition = formWithResponse.formDefinition;
            CommCareHttpResponse response = formWithResponse.response;

            CommCareExportedForms exportedFormData;
            try {
                exportedFormData = new Gson().fromJson(response.contentAsString(), CommCareExportedForms.class);
            } catch (JsonParseException e) {
                throw new RuntimeException(response.contentAsString() + e);
            }
            for (List<String> formData : exportedFormData.formContents()) {
                forms.add(new CommcareForm(definition, new CommCareFormContent(exportedFormData.headers(), formData)));
            }
        }

        return forms;
    }

    private class CommCareFormWithResponse {
        private final CommCareFormDefinition formDefinition;
        private final CommCareHttpResponse response;

        public CommCareFormWithResponse(CommCareFormDefinition formDefinition, CommCareHttpResponse response) {
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
