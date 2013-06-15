package org.ei.drishti.service.formSubmission.handler;

import com.google.gson.reflect.TypeToken;
import org.motechproject.dao.MotechJsonReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ReportFieldsDefinition {
    private Map<String, List<String>> reportDefinitions;

    @Autowired
    public ReportFieldsDefinition(@Value("#{drishti['report-fields-definition-path']}") String reportFieldsDefinitionPath) {
        reportDefinitions = (Map<String, List<String>>) new MotechJsonReader().readFromFile(reportFieldsDefinitionPath, new TypeToken<Map<String, List<String>>>() {
        }.getType());
    }

    public List<String> get(String formName) {
        return reportDefinitions.get(formName);
    }
}
