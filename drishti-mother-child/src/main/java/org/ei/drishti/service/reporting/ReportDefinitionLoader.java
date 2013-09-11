package org.ei.drishti.service.reporting;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Component
public class ReportDefinitionLoader implements IReportDefinitionLoader {

    private String reportDefinitionFileName;

    @Autowired
    public ReportDefinitionLoader(@Value("#{drishti['report-definition-path']}") String reportDefinitionFileName) {
        this.reportDefinitionFileName = reportDefinitionFileName;
    }

    private String load(File file) throws IOException {
        FileInputStream inputStream = new FileInputStream(file);
        return IOUtils.toString(inputStream, "UTF-8");
    }

    @Override
    public ReportDefinition reportDefintion() throws Exception {
        File file = new File(reportDefinitionFileName);
        String indicatorDefinitionJSON = load(file);
        return new Gson().fromJson(indicatorDefinitionJSON, ReportDefinition.class);
    }
}
