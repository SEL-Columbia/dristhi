package org.opensrp.scheduler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensrp.scheduler.Schedule.ActionType;
import org.opensrp.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Repository;

import ch.maxant.rules.CompileException;
import ch.maxant.rules.DuplicateNameException;
import ch.maxant.rules.Engine;
import ch.maxant.rules.NoMatchingRuleFoundException;
import ch.maxant.rules.ParseException;
import ch.maxant.rules.Rule;

@Repository
public class ScheduleConfig {
    private static Logger logger = LoggerFactory.getLogger("org.opensrp");

	private List<Schedule> schedules = new ArrayList<>();
	
	@Autowired
	public ScheduleConfig(@Value("#{opensrp['schedule.config.path']}") String scheduleConfigPath) throws IOException, JSONException {
		ResourceLoader loader=new DefaultResourceLoader();
    	scheduleConfigPath = loader.getResource(scheduleConfigPath).getURI().getPath();

    	logger.info("Loading automated schedules from "+scheduleConfigPath);
    	
		JSONArray jarr = Utils.getXlsToJson(scheduleConfigPath);
		
		logger.info("Found "+jarr.length()+" automated schedules");
		for (int i = 0; i < jarr.length(); i++) {
			JSONObject jo = jarr.getJSONObject(i);
			logger.debug(jo.toString());
			schedules.add(new Schedule(jo));
		}
	}
	
	public void addSchedule(Schedule sch) {
		if(schedules == null){
			schedules = new ArrayList<>();
		}
		schedules.add(sch);
	}
	
	public List<Schedule> getSchedules() {
		return Collections.unmodifiableList(schedules);
	}
	
	public List<Schedule> searchSchedules(String formSubmission) {
		List<Schedule> schl = new ArrayList<>();
		for (Schedule schedule : schedules) {
			if(schedule.hasForm(formSubmission)){
				schl.add(schedule);
			}
		}
		return schl;
	}
	
	public List<Schedule> searchSchedules(String form, String schedule, String milestone, ActionType action) {
		List<Schedule> schl = new ArrayList<>();
		for (Schedule sc : schedules) {
			if(sc.hasForm(form) && sc.schedule().equalsIgnoreCase(schedule)
					&& sc.milestone().equalsIgnoreCase(milestone) && sc.action().equals(action)){
				schl.add(sc);
			}
		}
		return schl;
	}
	
	
	public static void main(String[] args) throws DuplicateNameException, CompileException, ParseException, NoMatchingRuleFoundException {
		Map<String, Object> m = new HashMap<>();
		m.put("a", "14");
		m.put("val", "");
		Rule r1 = new Rule("R1", "input.a > 13", "true", 4, "age.rules");
		Rule r2 = new Rule("R2", "!#R1", "false", 4, "age.rules");
		List<Rule> rules = Arrays.asList(r1, r2);

		Engine engine = new Engine(rules, true);

		String tarif = engine.getBestOutcome(m); 
		System.out.println(tarif);
		
		String xpr = "${fs.pentavalent_1} == empty and ${fs.pentavalent_1_retro} == empty";
		String kv = "pentavalent_1";
		xpr = xpr.replace("${fs."+kv +"}", "input."+kv);
		System.out.println(xpr);
	}
	
}
