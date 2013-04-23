package org.ei.drishti.service.formSubmissionHandler;

import com.google.gson.reflect.TypeToken;
import org.motechproject.dao.MotechJsonReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ReportFieldsDefinition {
    private Map<String, List<String>> reportDefintions;

    @Autowired
    public ReportFieldsDefinition(@Value("#{drishti['report-fields-definition-path']}") String reportFieldsDefinitionPath) {
        reportDefintions = (Map<String, List<String>>) new MotechJsonReader().readFromFile(reportFieldsDefinitionPath, new TypeToken<Map<String, List<String>>>() {
        }.getType());
    }

    public List<String> get(String formName) {
        return reportDefintions.get(formName);
    }
}
