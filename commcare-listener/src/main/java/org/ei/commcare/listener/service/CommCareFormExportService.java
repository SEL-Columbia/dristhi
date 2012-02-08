package org.ei.commcare.listener.service;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import org.ei.commcare.listener.contract.CommcareFormDefinition;
import org.ei.commcare.listener.contract.CommcareFormDefinitions;
import org.ei.commcare.listener.domain.CommcareForm;
import org.ei.commcare.listener.util.CommcareHttpClient;
import org.ei.commcare.listener.util.CommcareHttpResponse;
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

    @Autowired
    public CommCareFormExportService(CommcareHttpClient httpClient, @Qualifier("commCareListenerProperties") Properties listenerProperties) {
        this.httpClient = httpClient;

        String exportDefinitionJsonPath = listenerProperties.getProperty(COMMCARE_EXPORT_DEFINITION_FILE);
        this.formDefinitions = (CommcareFormDefinitions) new MotechJsonReader().readFromFile(exportDefinitionJsonPath, CommcareFormDefinitions.class);
    }

    public List<CommcareForm> fetchForms() throws IOException {
        List<CommcareForm> formZips = new ArrayList<CommcareForm>();
        for (CommcareFormDefinition formDefinition : formDefinitions.definitions()) {
            CommcareHttpResponse responseFromCommCareHQ = httpClient.get(formDefinition.url(), formDefinitions.userName(), formDefinitions.password());

            CommCareExportedForms exportedFormData = new Gson().fromJson(responseFromCommCareHQ.content(), CommCareExportedForms.class);
            for (List<String> formData : exportedFormData.formContents()) {
                String contentSerialized = new Gson().toJson(new CommCareFormContent(exportedFormData.headers(), formData));
                formZips.add(new CommcareForm(formDefinition, contentSerialized));
            }
        }
        return formZips;
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

    private class CommCareFormContent {
        private final List<String> headers;
        private final List<String> values;

        public CommCareFormContent(List<String> headers, List<String> values) {
            this.headers = headers;
            this.values = values;
        }
    }
}
