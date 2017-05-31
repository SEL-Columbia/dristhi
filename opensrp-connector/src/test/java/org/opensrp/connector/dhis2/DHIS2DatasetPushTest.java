package org.opensrp.connector.dhis2;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.opensrp.connector.openmrs.service.TestResourceLoader;

public class DHIS2DatasetPushTest extends TestResourceLoader {

	@Mock
	DHIS2DatasetPush mockDatasetPush;
	
	@Mock
	Dhis2HttpUtils dhis2HttpUtils;
	
	public DHIS2DatasetPushTest() throws IOException {
		super();
		initMocks(this);
	}
	
	@Test
	public void testGetDHIS2ReportId() throws JSONException {
		// Expected DHIS2 API response
		JSONObject apiResponse = new JSONObject();

		JSONObject pager = new JSONObject();
		pager.put("page", 1);
		pager.put("pageCount", 1);
		pager.put("total", 1);
		pager.put("pageSize", 50);
		
		JSONArray dataSets = new JSONArray();
		
		JSONObject hia2Report = new JSONObject();
		String hia2ReportId = "XQDrq0oQEyN";

		hia2Report.put("id", hia2ReportId);
		hia2Report.put("displayName", "HIA2");
		
		dataSets.put(hia2Report);
		
		apiResponse.put("pager", pager);
		apiResponse.put("dataSets", dataSets);
		
		when(dhis2HttpUtils.get(anyString(), anyString())).thenReturn(apiResponse);
		dhis2DatasetPush.dhis2HttpUtils = dhis2HttpUtils;
		
		String dhis2ReportId = dhis2DatasetPush.getDHIS2ReportId("HIA2");
		
		assertEquals(hia2ReportId, dhis2ReportId);
	}
	
	@Ignore
	public void testCreateDHIS2Dataset() throws JSONException {
		JSONObject reportData = new JSONObject();
		JSONArray indicators = new JSONArray();
		
		JSONObject chn1005 = new JSONObject();
		chn1005.put("name", "CHN1-005");
		chn1005.put("value", 100);
		chn1005.put("dhis2_id", "n0uHub5ubqH");
		
		JSONObject chn1010 = new JSONObject();
		chn1010.put("name", "CHN1-010");
		chn1010.put("value", 150);
		chn1010.put("dhis2_id", "IWwblgpMxiS");
		
		indicators.put(chn1005);
		indicators.put(chn1010);
		
		reportData.put("dateCreated", "2017-05-22T15:12:28.894+03:00");
		reportData.put("locationId", "826684c3-6d82-4b0b-a750-a5f7f98b8608");
		reportData.put("reportType", "HIA2");
		reportData.put("formSubmissionId", "5f52c82f-ea29-469e-96d6-f95a6cc8fbe9");
		reportData.put("providerId", "biddemo");
		reportData.put("duration", 0);
		reportData.put("indicators", indicators);
		
		JSONObject dhis2DatasetToPush = dhis2DatasetPush.createDHIS2Dataset(reportData);

		// Dataset ID
		assertEquals("", dhis2DatasetToPush.get("dataSet"));
		//completeData
		assertEquals("", dhis2DatasetToPush.get("completeData"));
		// period
		assertEquals("", dhis2DatasetToPush.get("period"));
		// orgUnit
		assertEquals("", dhis2DatasetToPush.get("orgUnit"));
		// dataValues
		JSONArray dataValues = dhis2DatasetToPush.getJSONArray("dataValues");
		assertEquals(2, dataValues.length());
	}
	
}
