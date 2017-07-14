package org.opensrp.connector.dhis2;

import java.text.SimpleDateFormat;
import java.util.List;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.opensrp.api.domain.Location;
import org.opensrp.connector.openmrs.service.OpenmrsLocationService;
import org.opensrp.domain.Report;
import org.opensrp.domain.AppStateToken;
import org.opensrp.domain.Hia2Indicator;
import org.opensrp.service.ConfigService;
import org.opensrp.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

enum DhisSchedulerConfig {
	dhis2_syncer_sync_report_by_date_updated, dhis2_syncer_sync_report_by_date_voided
}

public class DHIS2DatasetPush extends DHIS2Service {
	
	private static Logger logger = LoggerFactory.getLogger(DHIS2DatasetPush.class.toString());
	
	@Autowired
	protected ReportService reportService;
	
	@Autowired
	protected ConfigService config;
	
	@Autowired
	protected OpenmrsLocationService openmrsLocationService;
	
	protected Dhis2HttpUtils dhis2HttpUtils;
	
	public static final String SCHEDULER_DHIS2_DATA_PUSH_SUBJECT = "DHIS2 Report Pusher";
	
	public static final String DATASET_ENDPOINT = "dataValueSets?dataElementIdScheme=code";
	
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
		
		if (!response.has("dataSets")) {
			throw new JSONException("Required dataSets key is absent");
		}
		
		JSONArray dataSets = response.getJSONArray("dataSets");
		
		for (int i = 0; i < dataSets.length(); i++) {
			JSONObject dataSet = dataSets.getJSONObject(i);
			String datasetId = dataSet.getString("id");
			String datasetName = dataSet.getString("displayName");
			
			if (datasetName.equals(reportName)) {
				reportId = datasetId;
				break;
			}
		}
		return reportId;
	}
	
	public JSONObject createDHIS2Dataset(Report report) throws JSONException {
		final String DATASET_KEY = "dataSet";
		final String COMPLETE_DATA_KEY = "completeData";
		final String PERIOD_KEY = "period";
		final String ORG_UNIT_KEY = "orgUnit";
		final String DATA_VALUES_KEY = "dataValues";
		
		// prepare report data
		String reportId = this.getDHIS2ReportId(report.getReportType());
		String openmrsLocationUuid = report.getLocationId();
		
		DateTime completeDataDate = report.getReportDate();
		String formatedCompleteDataDate = new SimpleDateFormat("yyyy-MM-dd").format(completeDataDate.toDate());
		String periodDate = new SimpleDateFormat("yyyyMM").format(completeDataDate.toDate());
		
		// get openmrs location and retrieve dhis2 org unit Id
		Location openmrsLocation = openmrsLocationService.getLocation(openmrsLocationUuid);
		System.out.println("[OpenmrsLocation]: " + openmrsLocation);
		String dhis2OrgUnitId = (String) openmrsLocation.getAttribute("dhis_ou_id");
		
		// get indicator data
		List<Hia2Indicator> indicators = report.getHia2Indicators();
		
		// generate the dhis2Dataset here
		JSONObject dhis2Dataset = new JSONObject();
		dhis2Dataset.put(DATASET_KEY, reportId);
		dhis2Dataset.put(COMPLETE_DATA_KEY, formatedCompleteDataDate);
		dhis2Dataset.put(PERIOD_KEY, periodDate);
		// completed date and period
		dhis2Dataset.put(ORG_UNIT_KEY, dhis2OrgUnitId);
		
		JSONArray dataValues = new JSONArray();
		
		for (Hia2Indicator indicator : indicators) {
			JSONObject dataValue = new JSONObject();
			
			if (!indicator.getDhisId().equals("unknown")) {
				dataValue.put("dataElement", indicator.getDhisId());
				dataValue.put("value", indicator.getValue());
				
				dataValues.put(dataValue);
			}
		}
		
		dhis2Dataset.put(DATA_VALUES_KEY, dataValues);
		
		return dhis2Dataset;
	}
	
	@MotechListener(subjects = SCHEDULER_DHIS2_DATA_PUSH_SUBJECT)
	public void pushToDHIS2(MotechEvent event) {
		// retrieve all the reports
		logger.info("RUNNING " + event.getSubject() + " at " + DateTime.now());
		
		AppStateToken lastsync = config.getAppStateTokenByName(DhisSchedulerConfig.dhis2_syncer_sync_report_by_date_updated);
		Long start = lastsync == null || lastsync.getValue() == null ? 0 : lastsync.longValue();
		
		List<Report> reports = reportService.findByServerVersion(start);
		logger.info("Report list size " + reports.size() + " [start]" + start);
		
		// process all reports and sync them to DHIS2
		for (Report report : reports) {
			try {
				JSONObject dhis2DatasetToPush = this.createDHIS2Dataset(report);
				JSONObject response = dhis2HttpUtils.post(DATASET_ENDPOINT, "", dhis2DatasetToPush.toString());
				report.setStatus(response.getString("status"));
				reportService.updateReport(report);
				config.updateAppStateToken(DhisSchedulerConfig.dhis2_syncer_sync_report_by_date_updated,
				    report.getServerVersion());
			}
			catch (JSONException e) {
				logger.error("", e);
			}
			
		}
		logger.info("PUSH TO DHIS2 FINISHED AT " + DateTime.now());
	}
	
}
