package org.ei.commcare.listener.contract;

import org.junit.Test;
import org.motechproject.dao.MotechJsonReader;

import java.util.HashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CommcareFormDefinitionsTest {
    @Test
    public void shouldBeAbleToReadFormDefinitionsFromJSON() {
        CommcareFormDefinitions definitions = (CommcareFormDefinitions) new MotechJsonReader().readFromFile(
                "/commcare-export.json", CommcareFormDefinitions.class);

        assertThat(definitions.userName(), is("someUser@gmail.com"));
        assertThat(definitions.password(), is("somePassword"));

        assertThat(definitions.definitions().size(), is(1));

        CommcareFormDefinition definition = definitions.definitions().get(0);

        HashMap<String,String> mappings = definition.mappings();
        assertThat(mappings.size(), is(2));
        assertThat(mappings.get("FieldInOutput"), is("/path/in/xml/to/it"));
        assertThat(mappings.get("AnotherFieldInOutput"), is("/path/in/xml/to/that"));

        assertThat(definition.name(), is("Registration"));
        assertThat(definition.url(), is("https://www.commcarehq.org/a/abhilasha/reports/export/?export_tag=%22http://openrosa.org/formdesigner/4FBE07FF-2434-40B3-B151-D2EBE2F4FB4F%22&format=raw"));
    }
}
