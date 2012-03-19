package org.ei.commcare.api.service;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import org.ei.commcare.api.contract.CommCareFormDefinition;
import org.ei.commcare.api.contract.CommCareFormDefinitions;
import org.ei.commcare.api.domain.CommCareFormContent;
import org.ei.commcare.api.domain.CommcareFormInstance;
import org.ei.commcare.api.repository.AllExportTokens;
import org.ei.commcare.api.util.CommCareHttpClient;
import org.ei.commcare.api.util.CommCareHttpResponse;
import org.ei.commcare.api.util.CommCareImportProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class CommCareFormImportService {
    private final CommCareHttpClient httpClient;
    private CommCareFormDefinitions formDefinitions;
    private AllExportTokens allExportTokens;
    private static Logger logger = LoggerFactory.getLogger(CommCareFormImportService.class.toString());

    @Autowired
    public CommCareFormImportService(AllExportTokens allExportTokens, CommCareHttpClient httpClient, CommCareImportProperties properties) {
        this.httpClient = httpClient;
        this.allExportTokens = allExportTokens;

        this.formDefinitions = properties.definitions();
    }

    public List<CommcareFormInstance> fetchForms() throws IOException {
        List<CommcareFormInstance> formInstances = processAllForms(fetchAllForms());
        logger.debug("Fetched " + formInstances.size() + " formInstances.");
        return formInstances;
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

    private List<CommcareFormInstance> processAllForms(List<CommCareFormWithResponse> careFormWithResponses) {
        List<CommcareFormInstance> formInstances = new ArrayList<CommcareFormInstance>();
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
                formInstances.add(new CommcareFormInstance(definition, new CommCareFormContent(exportedFormData.headers(), formData)));
            }
        }

        return formInstances;
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
        @SerializedName("#") private CommCareExportedHeadersAndContent content;
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
