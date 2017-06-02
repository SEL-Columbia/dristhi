package org.opensrp.connector.dhis2;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.opensrp.api.domain.Location;
import org.opensrp.connector.openmrs.service.OpenmrsLocationService;
import org.opensrp.connector.openmrs.service.TestResourceLoader;

public class DHIS2DatasetPushTest extends TestResourceLoader {

	@Mock
	DHIS2DatasetPush mockDatasetPush;
	
	@Mock
	Dhis2HttpUtils dhis2HttpUtils;
	
	@Mock
	OpenmrsLocationService mockOpenmrsLocationService;

	String orgUnitId = "gGl6WgM3qzS";
	String hia2ReportId = "XQDrq0oQEyN";

	public DHIS2DatasetPushTest() throws IOException {
		super();
		initMocks(this);
	}
	
	@Before
	public void setUp() throws JSONException  {
		JSONObject apiResponse = new JSONObject();
		JSONObject pager = new JSONObject();

		pager.put("page", 1);
		pager.put("pageCount", 1);
		pager.put("total", 1);
		pager.put("pageSize", 50);
		
		JSONArray dataSets = new JSONArray();
		
		JSONObject hia2Report = new JSONObject();

		hia2Report.put("id", hia2ReportId);
		hia2Report.put("displayName", "HIA2");
		
		dataSets.put(hia2Report);
		
		apiResponse.put("pager", pager);
		apiResponse.put("dataSets", dataSets);
		
		when(dhis2HttpUtils.get(anyString(), anyString())).thenReturn(apiResponse);
		
		Location location = new Location();
		location.addAttribute("dhis_ou_id", orgUnitId);
		when(mockOpenmrsLocationService.getLocation(anyString())).thenReturn(location);
	}

	public JSONObject createHIA2ReportData(JSONArray indicators) throws JSONException {
		JSONObject reportData = new JSONObject();

		reportData.put("dateCreated", "2017-05-22T15:12:28.894+03:00");
		reportData.put("locationId", "9e4fc064-d8e7-4fcb-942e-cbcf6524fb24");
		reportData.put("reportType", "HIA2");
		reportData.put("formSubmissionId", "5f52c82f-ea29-469e-96d6-f95a6cc8fbe9");
		reportData.put("providerId", "biddemo");
		reportData.put("duration", 0);
		reportData.put("indicators", indicators);

		return reportData;
	}

	@Test
	public void testGetDHIS2ReportId() throws JSONException {
		// Expected DHIS2 API response

		dhis2DatasetPush.dhis2HttpUtils = dhis2HttpUtils;
		String dhis2ReportId = dhis2DatasetPush.getDHIS2ReportId("HIA2");

		assertEquals(hia2ReportId, dhis2ReportId);
	}

	@Test
	public void testCreateDHIS2Dataset() throws JSONException {
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
		
		JSONObject reportData = this.createHIA2ReportData(indicators);
		
		dhis2DatasetPush.dhis2HttpUtils = dhis2HttpUtils;
		dhis2DatasetPush.openmrsLocationService = this.mockOpenmrsLocationService;

		JSONObject dhis2DatasetToPush = dhis2DatasetPush.createDHIS2Dataset(reportData);

		// Dataset ID
		assertEquals(hia2ReportId, dhis2DatasetToPush.get("dataSet"));
		//completeData
		assertEquals("2017-05-22", dhis2DatasetToPush.get("completeData"));
		// period
		assertEquals("201705", dhis2DatasetToPush.get("period"));
		// orgUnit
		assertEquals(orgUnitId, dhis2DatasetToPush.get("orgUnit"));
		// dataValues
		JSONArray dataValues = dhis2DatasetToPush.getJSONArray("dataValues");
		assertEquals(2, dataValues.length());

		for(int i = 0; i < dataValues.length(); i++) {
			JSONObject dataValue = dataValues.getJSONObject(i);
			JSONObject indicator = indicators.getJSONObject(i);

			assertEquals(dataValue.get("value"), indicator.get("value"));
		}
	}
	
	@Test
	public void testUnknownDataElementsAreIgnored() throws JSONException {
		JSONArray indicators = new JSONArray();

		JSONObject chn1011 = new JSONObject();
		chn1011.put("name", "CHN1-011");
		chn1011.put("value", 100);
		chn1011.put("dhis2_id", "unknown");

		indicators.put(chn1011);

		JSONObject reportData = this.createHIA2ReportData(indicators);

		dhis2DatasetPush.dhis2HttpUtils = dhis2HttpUtils;
		dhis2DatasetPush.openmrsLocationService = this.mockOpenmrsLocationService;

		JSONObject dhis2DatasetToPush = dhis2DatasetPush.createDHIS2Dataset(reportData);

		// Dataset ID
		assertEquals(hia2ReportId, dhis2DatasetToPush.get("dataSet"));
		JSONArray dataValues = dhis2DatasetToPush.getJSONArray("dataValues");
		assertEquals(0, dataValues.length());
	}

}
