package org.ei.commcare.api.util;

import org.ei.commcare.api.contract.CommCareFormDefinitions;
import org.motechproject.dao.MotechJsonReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class CommCareListenerProperties {
    public static final String COMMCARE_EXPORT_DEFINITION_FILE = "commcare-export.definition.file";
    private final Properties listenerProperties;

    @Autowired
    public CommCareListenerProperties(@Qualifier("propertiesForCommCareListener") Properties listenerProperties) {
        this.listenerProperties = listenerProperties;
    }

    public CommCareFormDefinitions definitions() {
        String jsonPath = listenerProperties.getProperty(COMMCARE_EXPORT_DEFINITION_FILE);
        return (CommCareFormDefinitions) new MotechJsonReader().readFromFile(jsonPath, CommCareFormDefinitions.class);
    }
}
