package org.ei.commcare.api.contract;

import org.junit.Test;
import org.motechproject.dao.MotechJsonReader;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CommCareFormDefinitionsTest {
    @Test
    public void shouldBeAbleToReadFormDefinitionsFromJSON() {
        CommCareFormDefinitions definitions = (CommCareFormDefinitions) new MotechJsonReader().readFromFile(
                "/test-data/commcare-export.json", CommCareFormDefinitions.class);

        assertThat(definitions.userName(), is("someUser@gmail.com"));
        assertThat(definitions.password(), is("somePassword"));

        assertThat(definitions.definitions().size(), is(1));

        CommCareFormDefinition definition = definitions.definitions().get(0);

        Map<String,String> mappings = definition.mappings();
        assertThat(mappings.size(), is(2));

        assertThat(mappings.get("form|path|to|field"), is("FieldInOutput"));
        assertThat(mappings.get("form|path|to|another|field"), is("AnotherFieldInOutput"));

        assertThat(definition.name(), is("Registration"));
        assertThat(definition.url(""), is("https://www.commcarehq.org/a/abhilasha/reports/export/?export_tag=%22http://openrosa.org/formdesigner/4FBE07FF-2434-40B3-B151-D2EBE2F4FB4F%22&format=json&previous_export="));
    }
}
