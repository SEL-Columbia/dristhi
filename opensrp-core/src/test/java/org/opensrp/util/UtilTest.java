package org.opensrp.util;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.opensrp.form.domain.FormSubmission;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.*;

public class UtilTest extends TestResourceLoader {

    public String declaredFieldOne;
    public int declaredFieldTwo;

    @Test
    public void testGetStringFromJson() throws IOException {
        String formSubmission = "{\"_id\": \"1\",\n" +
                "   \"_rev\": \"2\"}";
        Map<String, String> map = Utils.getStringMapFromJSON(formSubmission);
        assertFalse(map.isEmpty());
        assertEquals(map.size(), 2);
        assertEquals(map.get("_id"), "1");
        assertEquals(map.get("_rev"), "2");
        assertNull(map.get("sdf"));
    }

    @Test(expected = Exception.class)
    public void testErrorForInvalidJsonInGetStringFromJson() {
        String formSubmission = "Invalid  JSON";
        Map<String, String> map = Utils.getStringMapFromJSON(formSubmission);
    }

    @Test
    public void testGetFieldsAsList() {
        List<String > fieldList = Utils.getFieldsAsList(UtilTest.class);
        assertEquals(2, fieldList.size());
        assertEquals("declaredFieldOne", fieldList.get(0));
        assertEquals("declaredFieldTwo", fieldList.get(1));
    }

    @Test
    public void testGetZiggyParam() throws IOException, JSONException {
        FormSubmission formSubmission = getFormSubmissionFor("new_household_registration", 1);
        String ziggyParams = Utils.getZiggyParams(formSubmission);
        JSONObject jsonObject = new JSONObject(ziggyParams);

        assertEquals(formSubmission.anmId(), jsonObject.get("anmId"));
        assertEquals(formSubmission.formName(), jsonObject.get("formName"));
        assertEquals(formSubmission.serverVersion(), Long.parseLong((String)jsonObject.get("serverVersion")));
        assertEquals(formSubmission.entityId(), jsonObject.get("entityId"));
        assertEquals(formSubmission.instanceId(), jsonObject.get("instanceId"));
        assertEquals(formSubmission.clientVersion(),  Long.parseLong((String)jsonObject.get("clientVersion")));
    }

    @Test
    public void testGetXlsToJson() throws IOException, JSONException {
        String path = getFullPath("sampleXLS/validXLS.xls");
        JSONArray jsonArray = Utils.getXlsToJson(path);
        JSONObject jsonObject = jsonArray.getJSONObject(0);
        assertEquals(2, jsonArray.length());
        assertEquals("f", jsonObject.get("first"));
        assertEquals("s", jsonObject.get("second"));
        assertEquals("t", jsonObject.get("third"));

    }

    @Test
    public void testGetXlsToJsonForEmptyXls() throws IOException, JSONException{
        String path = getFullPath("sampleXLS/emptyXLS.xls");
        JSONArray jsonArray = Utils.getXlsToJson(path);
        assertEquals(0, jsonArray.length());
    }

    @Test(expected = Exception.class)
    public void testGetXlsToJsonForInvalidXls() throws IOException, JSONException{
        String path = getFullPath("sampleXLS/invalidXLS.xls");
        JSONArray jsonArray = Utils.getXlsToJson(path);
    }

}
