package org.opensrp;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public class ScheduleConfig {

	private List<Schedule> schedules;
	
	public ScheduleConfig() {
		
	}
	
	public void addSchedule(Schedule sch) {
		if(schedules == null){
			schedules = new ArrayList<>();
		}
		schedules.add(sch);
	}
	
	public class Schedule {
		private String form;
		private String schedule;
		
		public Schedule(String form, String schedule) {
			this.form = form;
			this.schedule = schedule;
		}
		
		public String form() {
			return form;
		}
		public String schedule() {
			return schedule;
		}
	}
}
