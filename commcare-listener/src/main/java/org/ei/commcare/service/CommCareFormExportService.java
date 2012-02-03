package org.ei.commcare.service;

import org.ei.commcare.CommcareHttpClient;
import org.ei.commcare.contract.CommcareFormDefinition;
import org.ei.commcare.contract.CommcareFormDefinitions;
import org.ei.commcare.domain.CommcareForm;
import org.ei.commcare.util.Zip;
import org.motechproject.dao.MotechJsonReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class CommCareFormExportService {
    public static final String COMMCARE_EXPORT_DEFINITION_FILE = "commcare-export.definition.file";
    private final CommcareHttpClient httpClient;
    private CommcareFormDefinitions formDefinitions;

    public CommCareFormExportService(CommcareHttpClient httpClient, Properties listenerProperties) {
        this.httpClient = httpClient;

        String exportDefinitionJsonPath = listenerProperties.getProperty(COMMCARE_EXPORT_DEFINITION_FILE);
        this.formDefinitions = (CommcareFormDefinitions) new MotechJsonReader().readFromFile(exportDefinitionJsonPath, CommcareFormDefinitions.class);
    }

    public List<CommcareForm> fetchForms() throws IOException {
        List<CommcareForm> formZips = new ArrayList<CommcareForm>();
        for (CommcareFormDefinition formDefinition : formDefinitions.definitions()) {
            byte[] zipContent = httpClient.get(formDefinition.url(), formDefinitions.userName(), formDefinitions.password());
            for (String formContent : unzipForms(zipContent)) {
                formZips.add(new CommcareForm(formDefinition, formContent));
            }
        }
        return formZips;
    }

    private List<String> unzipForms(byte[] zipContent) throws IOException {
        return new Zip().getFiles(zipContent);
    }
}
