
package org.opensrp.connector.openmrs.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.domain.MilestoneFulfillment;
import org.opensrp.common.util.HttpResponse;
import org.opensrp.connector.HttpUtil;
import org.opensrp.connector.openmrs.constants.OpenmrsConstants;
import org.opensrp.scheduler.Action;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OpenmrsSchedulerService extends OpenmrsService{
	
	private static final String TRACK_URL = "ws/rest/v1/scheduletracker/track";
	private static final String TRACK_MILESTONE_URL = "ws/rest/v1/scheduletracker/trackmilestone";
	private static final String SCHEDULE_URL = "ws/rest/v1/scheduletracker/schedule";
	private static final String MILESTONE_URL = "ws/rest/v1/scheduletracker/milestone";
	
	private OpenmrsUserService userService;

	public OpenmrsUserService getUserService() {
		return userService;
	}

	public void setUserService(OpenmrsUserService userService) {
		this.userService = userService;
	}

	@Autowired
	public OpenmrsSchedulerService(OpenmrsUserService userService) {
		this.userService = userService;
	}

    public OpenmrsSchedulerService(String openmrsUrl, String user, String password) {
    	super(openmrsUrl, user, password);
    }
	
	public JSONObject createTrack(Enrollment e, List<Action> alertActions) throws JSONException, ParseException
	{
		JSONObject t = new JSONObject();
		t.put("beneficiary", e.getExternalId());
		t.put("beneficiaryRole", alertActions!=null&&alertActions.size()>0?alertActions.get(0).data().get("beneficiaryType"):null);
		t.put("schedule", e.getScheduleName());
		String hr = StringUtils.leftPad(e.getPreferredAlertTime().getHour().toString(),2,"0");
		String mn = StringUtils.leftPad(e.getPreferredAlertTime().getMinute().toString(),2,"0");
		t.put("preferredAlertTime", hr+":"+mn+":00");
		t.put("referenceDate", OPENMRS_DATE.format(e.getStartOfSchedule().toDate()));
		t.put("referenceDateType", "MANUAL");
		t.put("dateEnrolled", OPENMRS_DATE.format(e.getEnrolledOn().toDate()));
		
		/*DateTime earliestStart = e.getStartOfWindowForCurrentMilestone(WindowName.earliest);
        DateTime dueStart = e.getStartOfWindowForCurrentMilestone(WindowName.due);
        DateTime lateStart = e.getStartOfWindowForCurrentMilestone(WindowName.late);
        DateTime maxStart = e.getStartOfWindowForCurrentMilestone(WindowName.max);
		t.put("earlyStartDate", OPENMRS_DATE.format(earliestStart.toDate()));
		t.put("dueStartDate", OPENMRS_DATE.format(dueStart.toDate()));
		t.put("lateStartDate", OPENMRS_DATE.format(lateStart.toDate()));
		t.put("maxStartDate", OPENMRS_DATE.format(maxStart.toDate()));*/
		t.put("currentMilestone", e.getCurrentMilestoneName());
		t.put("status", e.getStatus().name());
		
		
		JSONObject to = new JSONObject(HttpUtil.post(getURL()+"/"+TRACK_URL, "", t.toString(), OPENMRS_USER, OPENMRS_PWD).body());
		String trackuuid = to.getString("uuid");
		
		JSONArray tmarr = new JSONArray();
		for (Action ac : alertActions) {
			if(ac.actionType().equalsIgnoreCase("createAlert")){
				Action close = getClosedAction(ac.data().get("visitCode"), alertActions);
				JSONObject pr = userService.getPersonByUser(ac.anmIdentifier());
				JSONObject tm = new JSONObject();
				tm.put("track", trackuuid);
				MilestoneFulfillment m = getMilestone(ac.data().get("visitCode"), e);
				tm.put("milestone", ac.data().get("visitCode"));
				tm.put("alertRecipient", pr.getString("uuid"));
				tm.put("alertRecipientRole", "PROVIDER");
				String fdate = m == null?null:OPENMRS_DATE.format(m.getFulfillmentDateTime().toDate());
				if(fdate == null){
					fdate = close==null?null:OPENMRS_DATE.format(new SimpleDateFormat("dd-MM-yyyy").parse(close.data().get("completionDate")));
				}
				tm.put("fulfillmentDate", fdate);
				tm.put("status", ac.data().get("alertStatus")+(close==null?"":"-completed"));
				//TODO tm.put("reasonClosed", ac.data().get(""));
				tm.put("alertStartDate", ac.data().get("startDate"));
				tm.put("alertExpiryDate", ac.data().get("expiryDate"));
				tm.put("isActive", ac.getIsActionActive());
				tm.put("actionType", "PROVIDER ALERT");
				
				JSONObject tmo = new JSONObject(HttpUtil.post(getURL()+"/"+TRACK_MILESTONE_URL, "", tm.toString(), OPENMRS_USER, OPENMRS_PWD).body());
			
				tmarr.put(tmo);
			}
		}
		
		to.put("trackMilestones", tmarr);
		return to;
	}
	
	public JSONObject updateTrack(Enrollment e, List<Action> alertActions) throws JSONException, ParseException
	{
		JSONObject t = new JSONObject();
		t.put("beneficiaryRole", alertActions!=null&&alertActions.size()>0?alertActions.get(0).data().get("beneficiaryType"):null);
		String hr = StringUtils.leftPad(e.getPreferredAlertTime().getHour().toString(),2,"0");
		String mn = StringUtils.leftPad(e.getPreferredAlertTime().getMinute().toString(),2,"0");
		t.put("preferredAlertTime", hr+":"+mn+":00");
		t.put("currentMilestone", e.getCurrentMilestoneName());
		t.put("status", e.getStatus().name());
		
		
		JSONObject to = new JSONObject(HttpUtil.post(getURL()+"/"+TRACK_URL+"/"+e.getMetadata().get(OpenmrsConstants.ENROLLMENT_TRACK_UUID), "", t.toString(), OPENMRS_USER, OPENMRS_PWD).body());
		String trackuuid = to.getString("uuid");
		
		JSONArray tmarr = new JSONArray();
		for (Action ac : alertActions) {
			if(ac.actionType().equalsIgnoreCase("createAlert")){
				String milestone = ac.data().get("visitCode");
				JSONArray j = new JSONObject(HttpUtil.get(getURL()+"/"+TRACK_MILESTONE_URL, "v=full&track="+trackuuid+"&milestone="+milestone, OPENMRS_USER, OPENMRS_PWD).body()).getJSONArray("results");
				JSONObject tm = new JSONObject();
				String uuid = j.length()>0?j.getJSONObject(0).getString("uuid"):"";
				Action close = getClosedAction(milestone, alertActions);
				MilestoneFulfillment m = getMilestone(milestone, e);
				String fdate = m == null?null:OPENMRS_DATE.format(m.getFulfillmentDateTime().toDate());
				if(fdate == null){
					fdate = close==null?null:OPENMRS_DATE.format(new SimpleDateFormat("dd-MM-yyyy").parse(close.data().get("completionDate")));
				}
				tm.put("fulfillmentDate", fdate);
				tm.put("status", ac.data().get("alertStatus")+(close==null?"":"-completed"));
				//TODO tm.put("reasonClosed", ac.data().get(""));
				tm.put("alertStartDate", ac.data().get("startDate"));
				tm.put("alertExpiryDate", ac.data().get("expiryDate"));
				tm.put("isActive", ac.getIsActionActive());
				tm.put("actionType", "PROVIDER ALERT");
				
				JSONObject tmo = new JSONObject(HttpUtil.post(getURL()+"/"+TRACK_MILESTONE_URL+"/"+uuid, "", tm.toString(), OPENMRS_USER, OPENMRS_PWD).body());
			
				tmarr.put(tmo);
			}
		}
		
		t.put("trackMilestones", tmarr);
		return t;
	}
	
	private Action getClosedAction(String milestone, List<Action> actions){
		for (Action a : actions) {
			if(a.data().get("visitCode") != null && a.data().get("visitCode").equalsIgnoreCase(milestone)
					&& a.actionType().equalsIgnoreCase("closeAlert")){
				return a;
			}
		}
		return null;
	}
	private MilestoneFulfillment getMilestone(String milestone, Enrollment e) {
		for (MilestoneFulfillment m : e.getFulfillments()) {
			if(m.getMilestoneName().equalsIgnoreCase(milestone)){
				return m;
			}
		}
		return null;
	}
}
