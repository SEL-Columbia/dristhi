package org.ei.drishti.service.reporting;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.ei.drishti.form.domain.FormSubmission;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FormSubmissionReportService {
    public void reportFor(FormSubmission submission) throws IOException {
        File file = new File("/Users/Admin/work/projects/drishti/modilabs/drishti/drishti-mother-child/src/main/java/org/ei/drishti/service/reporting/report_definition.json");
        String indicatorDefinitionJSON = load(file);
        ReportDefinition reportDefinition = new Gson().fromJson(indicatorDefinitionJSON, ReportDefinition.class);

        //get formIndicators by name
        //iterate through indicators
        //get form field
        //get reference data

        //invoke the rules with the values
        //if rules pass load location and report
    }

    public String load(File file) throws IOException {
        FileInputStream inputStream = new FileInputStream(file);
        return IOUtils.toString(inputStream, "UTF-8");
    }

}
