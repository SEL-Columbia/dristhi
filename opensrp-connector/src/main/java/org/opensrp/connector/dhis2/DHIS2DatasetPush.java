package org.opensrp.connector.dhis2;

import java.text.SimpleDateFormat;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.opensrp.api.domain.Location;
import org.opensrp.connector.openmrs.service.OpenmrsLocationService;
import org.opensrp.repository.AllEvents;
import org.opensrp.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;


enum DhisSchedulerConfig {
	dhis2_syncer_sync_report_by_date_updated,
	dhis2_syncer_sync_report_by_date_voided
}

public class DHIS2DatasetPush extends DHIS2Service {
	private static final String DATASET_ENDPOINT = "dataValueSets?dataElementIdScheme=code";
	private static final String SCHEDULER_DHIS2_DATA_PUSH_SUBJECT = "DHIS2 Report Pusher";

	@Autowired
	private AllEvents reportEvents;
	
	@Autowired
	private ConfigService config;

	@Autowired
	protected OpenmrsLocationService openmrsLocationService;

	protected Dhis2HttpUtils dhis2HttpUtils;
	
	public DHIS2DatasetPush() {
		dhis2HttpUtils = new Dhis2HttpUtils();
	}
	
	public DHIS2DatasetPush(String dhis2Url, String user, String password) {
		super(dhis2Url, user, password);
		dhis2HttpUtils = new Dhis2HttpUtils(dhis2Url, user, password);

		this.config.registerAppStateToken(DhisSchedulerConfig.dhis2_syncer_sync_report_by_date_updated, 0,
			"ScheduleTracker token to keep track of reports synced with DHIS2", true);

		this.config.registerAppStateToken(DhisSchedulerConfig.dhis2_syncer_sync_report_by_date_voided, 0,
			"DHIS2 report pusher token to keep track of new / updated reports synced with DHIS2", true);
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
	
	public JSONObject createDHIS2Dataset(JSONObject reportEvent) throws JSONException {
		final String DATASET_KEY = "dataSet";
		final String COMPLETE_DATA_KEY = "completeData";
		final String PERIOD_KEY = "period";
		final String ORG_UNIT_KEY = "orgUnit";
		final String DATA_VALUES_KEY = "dataValues";

		// prepare report data
		String reportId = this.getDHIS2ReportId(reportEvent.getString("reportType"));
		String openmrsLocationUuid = reportEvent.getString("locationId");

		DateTimeFormatter parseDate = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		DateTime completeDataDate = parseDate.parseDateTime(reportEvent.getString("dateCreated"));
		String formatedCompleteDataDate= new SimpleDateFormat("yyyy-MM-dd").format(completeDataDate.toDate());
		String periodDate = new SimpleDateFormat("yyyyMM").format(completeDataDate.toDate());

		// get openmrs location and retrieve dhis2 org unit Id
		Location openmrsLocation = openmrsLocationService.getLocation(openmrsLocationUuid);
		System.out.println("[OpenmrsLocation]: " + openmrsLocation);
		String dhis2OrgUnitId = (String) openmrsLocation.getAttribute("dhis_ou_id");

		// get indicator data
		JSONArray indicators = reportEvent.getJSONArray("indicators");

		// generate the dhis2Dataset here
		JSONObject dhis2Dataset = new JSONObject();
		dhis2Dataset.put(DATASET_KEY, reportId);
		dhis2Dataset.put(COMPLETE_DATA_KEY, formatedCompleteDataDate);
		dhis2Dataset.put(PERIOD_KEY, periodDate);
		// completed date and period
		dhis2Dataset.put(ORG_UNIT_KEY, dhis2OrgUnitId);

		JSONArray dataValues = new JSONArray();

		for(int i = 0; i < indicators.length(); i++) {
			JSONObject dataValue = new JSONObject();
			JSONObject indicator = indicators.getJSONObject(i);

			if(!indicator.get("dhis2_id").equals("unknown")) {
				dataValue.put("dataElement", indicator.get("dhis2_id"));
				dataValue.put("value", indicator.get("value"));

				dataValues.put(dataValue);
			}
		}

		dhis2Dataset.put(DATA_VALUES_KEY, dataValues);

		return dhis2Dataset;
	}
	
	@MotechListener(subjects = SCHEDULER_DHIS2_DATA_PUSH_SUBJECT)
	public void pushToDHIS2(MotechEvent event) {
		// retrieve all the report events
		
	}
	
}
