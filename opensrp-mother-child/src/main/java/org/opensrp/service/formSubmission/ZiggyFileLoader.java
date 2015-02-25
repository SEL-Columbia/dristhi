package org.opensrp.service.formSubmission;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;

import static java.text.MessageFormat.format;

@Component
public class ZiggyFileLoader {
    private static Logger logger = LoggerFactory.getLogger(ZiggyFileLoader.class.toString());

    private String jsDirectoryName;
    private String formDirectoryName;

    @Autowired
    public ZiggyFileLoader(@Value("#{drishti['js.directory.name']}") String jsDirectoryName, @Value("#{drishti['form.directory.name']}") String formDirectoryName) {
        this.jsDirectoryName = jsDirectoryName;
        this.formDirectoryName = formDirectoryName;
    }

    public String getJSFiles() throws IOException, URISyntaxException {
        File jsFolder = new File(this.getClass().getResource(jsDirectoryName).toURI());
        File[] files = jsFolder.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().endsWith(".js");
            }
        });
        StringBuilder builder = new StringBuilder();
        for (File file : files) {
            builder.append(load(file));
        }
        return builder.toString();
    }

    public String loadAppData(String fileName) {
        try {
            File file = new File(this.getClass().getResource(formDirectoryName).getPath() + "/" + fileName);
            return load(file);
        } catch (IOException e) {
            logger.error(format("Error while loading app data file: {0}, with exception: {1}", fileName, e));
        }
        return null;
    }

    public String load(File file) throws IOException {
        FileInputStream inputStream = new FileInputStream(file);
        return IOUtils.toString(inputStream, "UTF-8");
    }
}
