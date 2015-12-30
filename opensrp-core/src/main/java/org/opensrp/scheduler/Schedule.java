package org.opensrp.scheduler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONException;
import org.json.JSONObject;

import ch.maxant.rules.CompileException;
import ch.maxant.rules.DuplicateNameException;
import ch.maxant.rules.Engine;
import ch.maxant.rules.NoMatchingRuleFoundException;
import ch.maxant.rules.ParseException;
import ch.maxant.rules.Rule;

import com.mysql.jdbc.StringUtils;

public class Schedule {

	public enum ActionType {
		enroll,
		unenroll,
		fulfill
	}
	private ActionType action;
	private List<String> forms;
	private String schedule;
	private String milestone;
	private List<String> triggerDateFields;
	private String entityType;
	private String passLogic;
	
	Schedule() {

	}
	
	Schedule(String json) throws JSONException{
		this(new JSONObject(json));
	}
	
	Schedule(JSONObject json) throws JSONException{
		this(ActionType.valueOf(json.getString("action").toLowerCase()), 
		json.getString("form").split(","), json.getString("schedule"), json.getString("milestone"), 
		json.getString("triggerDateField").split(","), json.getString("entityType"), 
		json.has("passLogic")?json.getString("passLogic"):null);
	}
	
	Schedule(ActionType action, String[] forms, String schedule, String milestone, 
			String[] triggerDateFields, String entityType, String passLogic) {
		this.action = action;
		this.forms = new ArrayList<>();
		for (String f : forms) {
			this.forms.add(f.trim());
		}
		this.schedule = schedule.trim();
		this.milestone = milestone.trim();
		this.triggerDateFields = new ArrayList<>();
		for (String tf : triggerDateFields) {
			this.triggerDateFields.add(tf.trim());
		}
		this.entityType = entityType.trim();
		this.passLogic = passLogic;
	}
	
	public boolean hasForm(String form) {
		for (String f : forms) {
			if(f.trim().equalsIgnoreCase(form.trim())){
				return true;
			}
		}
		return false;
	}
	
	public boolean applicableForEntity(String entity) {
		return entityType.equalsIgnoreCase(entity.trim());
	}
	
	public boolean haspassLogic() {
		return !StringUtils.isEmptyOrWhitespaceOnly(passLogic);
	}
	
	public boolean passesValidations(Map<String, String> flvl) {
		if(!haspassLogic()){
			return true;
		}
		
		String xpr = passLogic;
		for (Entry<String, String> kv : flvl.entrySet()) {
			if(kv.getValue() == null){
				flvl.put(kv.getKey(), "");
			}
			xpr = xpr.replace("${fs."+kv.getKey()+"}", "input."+kv.getKey());
		}
		
		Rule r1 = new Rule("R1", xpr, "true", 4, "dynamic.rules");
		Rule r2 = new Rule("R2", "!#R1", "false", 4, "dynamic.rules");
		List<Rule> rules = Arrays.asList(r1, r2);
		try {
			Engine engine = new Engine(rules, true);
			String result = engine.getBestOutcome(flvl);
			return Boolean.valueOf(result);
		} catch (DuplicateNameException | CompileException | ParseException e) {
			e.printStackTrace();
			throw new RuntimeException("Error while parsing Schedule Logic: "+e.getMessage(), e);
		} catch (NoMatchingRuleFoundException e) {//although it would never be thrown
			e.printStackTrace();
			throw new RuntimeException("Error while parsing Schedule Logic: "+e.getMessage(), e);
		}
	}
	
	
	
	public ActionType action() {
		return action;
	}
	public List<String> forms() {
		return forms;
	}
	public String schedule() {
		return schedule;
	}
	public String milestone() {
		return milestone;
	}
	public List<String> triggerDateFields() {
		return triggerDateFields;
	}
	public String entityType() {
		return entityType;
	}
	public String passLogic() {
		return passLogic;
	}

	public ActionType getAction() {
		return action;
	}

	public List<String> getForms() {
		return forms;
	}

	public String getSchedule() {
		return schedule;
	}

	public String getMilestone() {
		return milestone;
	}

	public List<String> getTriggerDateFields() {
		return triggerDateFields;
	}

	public String getEntityType() {
		return entityType;
	}

	public String getPassLogic() {
		return passLogic;
	}
}