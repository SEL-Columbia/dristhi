package org.ei.commcare.listener.service;

import org.ei.commcare.listener.util.CommcareHttpClient;
import org.ei.commcare.listener.contract.CommcareFormDefinition;
import org.ei.commcare.listener.contract.CommcareFormDefinitions;
import org.ei.commcare.listener.domain.CommcareForm;
import org.ei.commcare.listener.util.Zip;
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
