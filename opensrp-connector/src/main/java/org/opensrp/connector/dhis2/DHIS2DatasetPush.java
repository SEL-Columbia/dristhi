package org.opensrp.connector.dhis2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensrp.repository.AllEvents;
import org.springframework.beans.factory.annotation.Autowired;

public class DHIS2DatasetPush extends DHIS2Service {
	@Autowired
	private AllEvents reportEvents;
	
	protected Dhis2HttpUtils dhis2HttpUtils;
	
	public DHIS2DatasetPush() {
		dhis2HttpUtils = new Dhis2HttpUtils();
	}
	
	public DHIS2DatasetPush(String dhis2Url, String user, String password) {
		super(dhis2Url, user, password);
		dhis2HttpUtils = new Dhis2HttpUtils(dhis2Url, user, password);
	}
	
	public JSONObject createDHIS2Dataset(JSONObject reportEvents) throws JSONException {
		JSONObject dhis2Dataset = new JSONObject();
		
		// generate the dhis2Dataset here
		
		return dhis2Dataset;
	}
	
	public String getDHIS2ReportId(String reportName) throws JSONException {
		String reportId = "";
		JSONObject response = dhis2HttpUtils.get("dataSets.json", "");
		
		if(!response.has("dataSets")) {
			throw new JSONException("Required dataSets key is absent");
		}
		
		JSONArray dataSets = response.getJSONArray("dataSets");
		
		for(int i = 0; i < dataSets.length(); i++) {
			JSONObject dataSet = dataSets.getJSONObject(i);
			String datasetId = dataSet.getString("id");
			String datasetName = dataSet.getString("displayName");
			
			if(datasetName.equals(reportName)) {
				reportId = datasetId;
				break;
			}
		}
		return reportId;
	}
	
}
