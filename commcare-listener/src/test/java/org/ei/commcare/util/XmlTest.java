package org.ei.commcare.util;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertTrue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class XmlTest {
    @Test
    public void shouldGetAnEmptyMapWhenThereAreNoFieldsSpecified() throws Exception {
        HashMap<String, String> emptyMap = new HashMap<String, String>();

        Map<String,String> fieldsInXmlWeCareAbout = new Xml("<some_xml/>").getValuesOfFieldsSpecifiedByPath(emptyMap);

        assertTrue(fieldsInXmlWeCareAbout.isEmpty());
    }

    @Test
    public void shouldFindAFieldWhichIsDirectlyUnderTheRootOfTheXML() throws Exception {
        HashMap<String, String> fieldWeNeed = new HashMap<String, String>();
        fieldWeNeed.put("Patient", "/data/Patient_Name");

        Xml xml = new Xml("<data><Patient_Name>Hello</Patient_Name></data>");
        Map<String,String> fieldsInXmlWeCareAbout = xml.getValuesOfFieldsSpecifiedByPath(fieldWeNeed);

        assertThat(fieldsInXmlWeCareAbout.size(), is(1));
        assertThat(fieldsInXmlWeCareAbout.get("Patient"), is("Hello"));
    }

    @Test
    public void shouldFindAFieldWhichIsMultipleLevelsBelowInTheXML() throws Exception {
        HashMap<String, String> fieldWeNeed = new HashMap<String, String>();
        fieldWeNeed.put("Beneficiary", "/data/Patient/Name");

        Xml xml = new Xml("<data><Patient><Name>Hello</Name></Patient></data>");
        Map<String,String> fieldsInXmlWeCareAbout = xml.getValuesOfFieldsSpecifiedByPath(fieldWeNeed);

        assertThat(fieldsInXmlWeCareAbout.size(), is(1));
        assertThat(fieldsInXmlWeCareAbout.get("Beneficiary"), is("Hello"));
    }

    @Test
    public void shouldGetMultipleFieldsAtDifferentLevelsInTheXML() throws Exception {
        HashMap<String, String> fieldWeNeed = new HashMap<String, String>();
        fieldWeNeed.put("Patient", "/data/Patient/Name");
        fieldWeNeed.put("PatientAge", "/data/Patient/Details/Age");

        Xml xml = new Xml("<data><Patient><Name>Abc</Name><Details><Age>23</Age></Details></Patient></data>");
        Map<String,String> fieldsInXmlWeCareAbout = xml.getValuesOfFieldsSpecifiedByPath(fieldWeNeed);

        assertThat(fieldsInXmlWeCareAbout.size(), is(2));
        assertThat(fieldsInXmlWeCareAbout.get("Patient"), is("Abc"));
        assertThat(fieldsInXmlWeCareAbout.get("PatientAge"), is("23"));
    }

    @Test
    public void shouldFindFieldsEvenIfThereAreNamespaces() throws Exception {
        HashMap<String, String> fieldWeNeed = new HashMap<String, String>();
        fieldWeNeed.put("Name", "/data/ChildName");
        fieldWeNeed.put("UserName", "/data/meta/username");

        String content = "<?xml version='1.0'?><data version=\"9\" xmlns=\"http://some.com/name/space\"><ChildName>Aditi</ChildName><meta><username>chandni</username></meta></data>";
        Xml xml = new Xml(content);
        Map<String, String> fieldsInXmlWeCareAbout = xml.getValuesOfFieldsSpecifiedByPath(fieldWeNeed);

        assertThat(fieldsInXmlWeCareAbout.size(), is(2));
        assertThat(fieldsInXmlWeCareAbout.get("Name"), is("Aditi"));
        assertThat(fieldsInXmlWeCareAbout.get("UserName"), is("chandni"));
    }
}
