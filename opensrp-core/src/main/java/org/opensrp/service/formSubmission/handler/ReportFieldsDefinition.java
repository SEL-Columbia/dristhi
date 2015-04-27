package org.opensrp.service.formSubmission.handler;

import java.util.List;
import java.util.Map;

import org.motechproject.dao.MotechJsonReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.gson.reflect.TypeToken;

@Component
public class ReportFieldsDefinition {
    private Map<String, List<String>> reportDefinitions;

    @Autowired
    public ReportFieldsDefinition(@Value("#{opensrp['report-fields-definition-path']}") String reportFieldsDefinitionPath) {
        reportDefinitions = (Map<String, List<String>>) new MotechJsonReader().readFromFile(reportFieldsDefinitionPath, new TypeToken<Map<String, List<String>>>() {
        }.getType());
    }

    public List<String> get(String formName) {
        return reportDefinitions.get(formName);
    }
}
