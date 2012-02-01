package org.ei.commcare;

import org.ei.commcare.domain.CommcareForm;
import org.ei.commcare.domain.CommcareFormDefinition;
import org.ei.commcare.domain.CommcareFormDefinitions;
import org.ei.commcare.util.Unzip;
import org.motechproject.dao.MotechJsonReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class CommCareListener {
    public static final String COMMCARE_EXPORT_DEFINITION_FILE = "commcare-export.definition.file";
    private final CommcareHttpClient httpClient;
    private CommcareFormDefinitions formDefinitions;

    public CommCareListener(CommcareHttpClient httpClient, Properties listenerProperties) {
        this.httpClient = httpClient;

        String exportDefinitionJsonPath = listenerProperties.getProperty(COMMCARE_EXPORT_DEFINITION_FILE);
        this.formDefinitions = (CommcareFormDefinitions) new MotechJsonReader().readFromFile(exportDefinitionJsonPath, CommcareFormDefinitions.class);
    }

    public List<CommcareForm> fetchForms() throws IOException {
        List<CommcareForm> formZips = new ArrayList<CommcareForm>();
        for (CommcareFormDefinition formDefinition : formDefinitions.definitions()) {
            byte[] zipContent = httpClient.get(formDefinition.url(), formDefinitions.userName(), formDefinitions.password());
            formZips.add(new CommcareForm(formDefinition, unzipForms(zipContent)));
        }
        return formZips;
    }

    private List<String> unzipForms(byte[] zipContent) throws IOException {
        return new Unzip().getFiles(zipContent);
    }

    public static void main(String[] args) throws IOException {
        CommCareListener obj = new CommCareListener(new CommcareHttpClient(), new Properties());
        CommcareFormDefinitions o = (CommcareFormDefinitions) new MotechJsonReader().readFromFile("/commcare-export.json", CommcareFormDefinitions.class);
        System.out.println(o.definitions());
        if (true) return;
        obj.fetchForms();
    }

}
