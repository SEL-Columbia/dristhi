package org.opensrp.connector.dhis2;

/*import static org.opensrp.common.AllConstants.DHIS2.DHIS2_BASE_URL;
import static org.opensrp.common.AllConstants.DHIS2.DHIS2_PWD;
import static org.opensrp.common.AllConstants.DHIS2.DHIS2_USER;*/

import java.text.ParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensrp.api.domain.Client;
import org.opensrp.common.util.DateUtil;
import org.opensrp.form.domain.FormSubmission;
import org.springframework.stereotype.Service;

@Service
public class Dhis2TrackCaptureConnector extends DHIS2Service {

	
	public Dhis2TrackCaptureConnector() {
		
	}
	
	public Dhis2TrackCaptureConnector(String dhis2Url, String user, String password) {
	    super(dhis2Url, user, password);
	}
	
	/** 
	 * Extract Event from given form submission
	 * @param fs
	 * @return
	 * @throws ParseException
	 * @throws JSONException 
	 */
	public JSONObject getTrackCaptureFromFormSubmission() throws ParseException, JSONException {
		JSONObject client =	new JSONObject();
		JSONArray attributeArray =	new JSONArray();
		
		/**
		 * example data
		 * */
		JSONObject attrValueObj1 = new JSONObject();
		attrValueObj1.put("attribute", "pzuh7zrs9Xx");
		attrValueObj1.put("value",  "Amier Nabi");
		attributeArray.put(attrValueObj1);
		
		JSONObject attrValueObj2 = new JSONObject();
		attrValueObj2.put("attribute", "xDvyz0ezL4e");
		attrValueObj2.put("value",  "Female");
		attributeArray.put(attrValueObj2);
		

		JSONObject attrValueObj3 = new JSONObject();
		attrValueObj3.put("attribute", "dXzZ7mByZFr");
		attrValueObj3.put("value",  "Dhaka Bangladesh");
		attributeArray.put(attrValueObj3);
		
		/////////////////////
		JSONArray enrollments =	new JSONArray();
		JSONObject enrollmentsObj = new JSONObject();
		enrollmentsObj.put("orgUnit", "IDc0HEyjhvL");
		enrollmentsObj.put("program", "OprRhyWVIM6");
		enrollmentsObj.put("enrollmentDate", DateUtil.getTodayAsString());
		enrollmentsObj.put("incidentDate", DateUtil.getTodayAsString());
		enrollments.put(enrollmentsObj);
		
		client.put("attributes", attributeArray);
		client.put("trackedEntity", "MCPQUTHX1Ze");
		client.put("orgUnit", "IDc0HEyjhvL");
		//personObj.put("enrollments", enrollments);
		return client;
	}
	
	public JSONObject trackCaptureDataSendToDHIS2(JSONObject payloadJsonObj) throws JSONException {		
		JSONObject Jobj =  new JSONObject(Dhis2HttpUtils.post(DHIS2_BASE_URL.replaceAll("\\s+","")+"trackedEntityInstances","",payloadJsonObj.toString(),DHIS2_USER.replaceAll("\\s+",""), DHIS2_PWD.replaceAll("\\s+","")).body());
		JSONObject j = (JSONObject) Jobj.get("response");
		System.err.println(j.get("reference"));
		JSONObject enroll =	new JSONObject();
		enroll.put("trackedEntityInstance", j.get("reference"));
		enroll.put("program", "OprRhyWVIM6");
		enroll.put("orgUnit", "IDc0HEyjhvL");
		return new JSONObject(Dhis2HttpUtils.post(DHIS2_BASE_URL.replaceAll("\\s+","")+"enrollments","",enroll.toString(),DHIS2_USER.replaceAll("\\s+",""),DHIS2_PWD.replaceAll("\\s+","")).body());
		
		

	}

	
	
}