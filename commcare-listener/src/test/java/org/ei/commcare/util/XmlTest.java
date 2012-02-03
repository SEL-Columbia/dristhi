package org.ei.commcare.util;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertTrue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class XmlTest {
    @Test
    public void shouldGetAnEmptyMapWhenThereAreNoFieldsSpecified() {
        HashMap<String, String> emptyMap = new HashMap<String, String>();

        Map<String,String> fieldsInXmlWeCareAbout = new Xml("<some_xml/>").getValuesOfFieldsSpecifiedByPath(emptyMap);

        assertTrue(fieldsInXmlWeCareAbout.isEmpty());
    }

    @Test
    public void shouldFindAFieldWhichIsDirectlyUnderTheRootOfTheXML() {
        HashMap<String, String> fieldWeNeed = new HashMap<String, String>();
        fieldWeNeed.put("abc", "/some_xml/x");

        Map<String,String> fieldsInXmlWeCareAbout = new Xml("<some_xml><x>Hello</x></some_xml>").getValuesOfFieldsSpecifiedByPath(fieldWeNeed);

        assertThat(fieldsInXmlWeCareAbout.size(), is(1));
        assertThat(fieldsInXmlWeCareAbout.get("abc"), is("Hello"));
    }
}
