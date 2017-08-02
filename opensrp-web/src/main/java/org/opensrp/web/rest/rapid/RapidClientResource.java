package org.opensrp.web.rest.rapid;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.joda.time.DateTime;
import org.joda.time.Weeks;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.json.JSONException;
import org.opensrp.connector.openmrs.service.EncounterService;
import org.opensrp.connector.openmrs.service.PatientService;
import org.opensrp.domain.Client;
import org.opensrp.domain.Event;
import org.opensrp.domain.Obs;
import org.opensrp.form.domain.FormData;
import org.opensrp.form.domain.FormField;
import org.opensrp.form.domain.FormInstance;
import org.opensrp.form.domain.FormSubmission;
import org.opensrp.form.repository.AllFormSubmissions;
import org.opensrp.service.ClientService;
import org.opensrp.service.EventService;
import org.opensrp.service.formSubmission.FormEntityConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mysql.jdbc.StringUtils;

@Controller
@RequestMapping ("/rest/rapid/client")
public class RapidClientResource {

private ClientService clientService;
private EventService eventService;
private AllFormSubmissions allFormSubmission;
private FormEntityConverter fec;
private PatientService ps;
private EncounterService es;

static Map<String, String[]> vs = new HashMap<String, String[]>(){{
	put("0", new String[]{"bcg"});
	put("6", new String[]{"penta1", "pcv1", "opv1"});
	put("10", new String[]{"penta2", "pcv2", "opv2"});
	put("14", new String[]{"penta3", "pcv3", "opv3"});
	put("36", new String[]{"measles1"});
	put("60", new String[]{"measles2"});

}}	;

	@Autowired
	public RapidClientResource(ClientService clientService, EventService eventService, 
			AllFormSubmissions allFormSubmission, FormEntityConverter fec, PatientService ps, EncounterService es) {
		this.clientService = clientService;
		this.eventService = eventService;
		this.allFormSubmission = allFormSubmission;
		this.fec = fec;
		this.ps = ps;
		this.es = es;
	}

	@RequestMapping(value="/cv", method= RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> createChild(HttpServletRequest req) {
		Map<String, Object> res = new HashMap<>();
		String firstName = req.getParameter("firstName");
		String lastName = req.getParameter("lastName");
		String gender = req.getParameter("gender");
		String birthdate = req.getParameter("birthdate");
		String identifier = req.getParameter("identifier");
		
		if(StringUtils.isEmptyOrWhitespaceOnly(identifier)
				|| StringUtils.isEmptyOrWhitespaceOnly(firstName)||StringUtils.isEmptyOrWhitespaceOnly(lastName)
				||StringUtils.isEmptyOrWhitespaceOnly(gender)||StringUtils.isEmptyOrWhitespaceOnly(birthdate)){
			res.put("ERROR", "Request MUST have parameters identifier, firstName, lastName, gender, birthdate with valid values");
			return res;
		}
		
		Client c = new Client(UUID.randomUUID().toString());
		c.addIdentifier("Program Client ID", identifier);
		c.setFirstName(firstName);
		c.setLastName(lastName);
		c.setGender(gender);
		c.setBirthdate(new DateTime(birthdate));
		
		clientService.addClient(c);
		
		res.put("SUCESS", true);
		res.put("SUCCESS", true);
		res.put("data", c);
		
		try {
			ps.createPatient(c);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return res;
	}
	
	private static ArrayList<String> eligibleVaccines(int age) {
		ArrayList<String> a = new ArrayList<>();
		for (String ag : vs.keySet()) {
			if(Integer.parseInt(ag) <= age){
				for (String v : vs.get(ag)) {
					a.add(v);
				}
			}
		}
		return a;
	}
	
	
	
	private static Map<String, Object> makeVaccineCard(List<Event> el, List<String> eligibleVaccines) {
		Map<String, Object> receivedVacines = new HashMap<>();

		System.out.println(el.size()+ " FOUND EVENT");
		for (Event e : el) {
			System.out.println();
			for (Obs o : e.getObs()) {
				System.out.println(o+ " FOUND OBS");

				if(o.getFormSubmissionField().toString().toLowerCase().matches("bcg|penta1|penta2|penta3|measles1|measles2")){
					receivedVacines.put(o.getFormSubmissionField(), o.getValue().toString());
				}
			}
		}
		
		Map<String, Object> vaccineCard = new HashMap<>();
		for (String vl : vs.keySet()) {
			for (String v : vs.get(vl)) {
				if(receivedVacines.containsKey(v)){
					vaccineCard.put(v, receivedVacines.get(v));
				}
				else if(eligibleVaccines.contains(v)){
					vaccineCard.put(v, "due");
				}
				else {
					vaccineCard.put(v, "n/a");
				}
			}
		}
		return vaccineCard;
	}
	@RequestMapping(value="/uvo", method=RequestMethod.POST)
	@ResponseBody
	public Map<String, String> updateVaccineO(HttpServletRequest req){
		Map<String, String> resp = new HashMap<>();
		String id = req.getParameter("clientId");
		String location = req.getParameter("location");
		String date = req.getParameter("date");
		String vaccine = req.getParameter("vaccine");
		
		try {
			if(StringUtils.isEmptyOrWhitespaceOnly(vaccine)){
				resp.put("ERROR", "vaccine MUST be specified.");
				return resp;
			}
			if(StringUtils.isEmptyOrWhitespaceOnly(date)){
				resp.put("ERROR", "date MUST be specified.");
				return resp;
			}
			if(StringUtils.isEmptyOrWhitespaceOnly(id)){
				resp.put("ERROR", "clientId MUST be specified.");
				return resp;
			}
			Client c = clientService.find(id);
			if(c == null){
				resp.put("ERROR", "ID Not found");
				return resp;
			}
			
			Event e = new Event(c.getBaseEntityId(), "Immunization", new DateTime(), 
					"testentity", "demotest", location, System.currentTimeMillis()+"");
			List<Object> values = new ArrayList<>();
			values.add(date);
			e.addObs(new Obs("concept", "txt", "1025AAAAAAAAAAAAAAAA", null, values , "", vaccine));

			eventService.addEvent(e);
			resp.put("SUCCESS", Boolean.toString(true));
			return resp;
		}
		catch(Exception e){
			e.printStackTrace();
			resp.put("ERROR", e.getMessage());
			return resp;
		}
	}
	
	@RequestMapping(value="/uv", method=RequestMethod.POST)
	@ResponseBody
	public Map<String, String> updateVaccine(HttpServletRequest req){
		Map<String, String> resp = new HashMap<>();
		String id = req.getParameter("clientId");
		String location = req.getParameter("location");
		String date = req.getParameter("date");
		String vaccine = req.getParameter("vaccine");
		Date dt = null;
		try {
			dt = new SimpleDateFormat("dd-MM-yyyy").parse(date);
		} catch (Exception e) {
			e.printStackTrace();
			try {
				dt = new SimpleDateFormat("yyyy-MM-dd").parse(date);
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
		}
			
		try {
			if(StringUtils.isEmptyOrWhitespaceOnly(vaccine)){
				resp.put("ERROR", "vaccine MUST be specified.");
				return resp;
			}
			if(StringUtils.isEmptyOrWhitespaceOnly(date)){
				resp.put("ERROR", "date MUST be specified.");
				return resp;
			}
			if(StringUtils.isEmptyOrWhitespaceOnly(id)){
				resp.put("ERROR", "clientId MUST be specified.");
				return resp;
			}
			Client c = clientService.find(id);
			if(c == null){
				resp.put("ERROR", "ID Not found");
				return resp;
			}
			
			String bindType = "pkchild";
			String user = usernameWithBasicAuthentication(req);
			String insId = UUID.randomUUID().toString();
			String eid = c.getBaseEntityId();

			FormData form = new FormData(bindType, "/model/instance/Child_Vaccination_Followup/", 
					generateChildFields(eid, insId, location, user, location, DateTime.now(), vaccine, new DateTime(dt)), null);
			FormInstance formInstance = new FormInstance(form, "1");
			FormSubmission fs = new FormSubmission(user, insId, "child_followup", eid, DateTime.now().getMillis(), "1", formInstance, DateTime.now().getMillis());

			allFormSubmission.add(fs);

			Event e = fec.getEventFromFormSubmission(fs);
		
			eventService.addEvent(e);
			
			try{
				System.out.println("Creating Encounter");
				System.out.println(es.createEncounter(e));
			}
			catch(Exception e2){
				e2.printStackTrace();
			}
			resp.put("SUCCESS", Boolean.toString(true));

			return resp;
		}
		catch(Exception e){
			e.printStackTrace();
			resp.put("ERROR", e.getMessage());
			return resp;
		}
	}
	
	@RequestMapping("/c")
	@ResponseBody
	public Map<String, Object> findInfo(HttpServletRequest req) {
		String id = req.getParameter("id");
		Map<String, Object> m = new HashMap<String, Object>();
		Client c = clientService.find(id );
		if(c == null){
			m.put("found", false);
			return m;
		}
		m.put("found", true);
		String eventType = req.getParameter("eventType");
		m.put("client", c);
		
		int age = Weeks.weeksBetween(c.getBirthdate(), new DateTime().now()).getWeeks();
		m.put("age", age);
		
		ArrayList<String> ev = eligibleVaccines(age);
		m.put("eligibleVaccines", ev);
		Map<String, Object> receivedVacines = new HashMap<>();
		try{
			List<Event> el = eventService.findByBaseEntityId(c.getBaseEntityId()); //eventService.findEventsBy(c.getBaseEntityId(), null, null, eventType, null, null, null, null, null);
			m.put("vaccineCard", makeVaccineCard(el, ev));
		} catch(Exception e){
			e.printStackTrace();
		}
		m.put("receivedVaccines", receivedVacines);
		return m;
	}
	
	private void addField(List<FormField> fields, String name, String value, String bindType) {
		fields.add(new FormField(name, value, bindType+"."+name));
	}
	
	private List<FormField> generateChildFields(String entityId, String instanceId, String uc, String provider, String center, DateTime eDate, String vaccine, DateTime vaccineDate) {
		List<FormField> fields = new ArrayList<>();
		String df = "yyyy-MM-dd";
		String bindType = "pkchild";
		addField(fields, "id", entityId, bindType);
		addField(fields, "instanceID", "uid:"+instanceId, bindType);
		addField(fields, "provider_id", provider, bindType);
		addField(fields, "provider_location_id", center, bindType);
		addField(fields, "start", eDate.toString("yyyy-MM-yy HH:mm:ss"), bindType);
		addField(fields, "end", eDate.toString("yyyy-MM-yy HH:mm:ss"), bindType);
		addField(fields, "today", eDate.toString(df), bindType);

		addField(fields, "vaccination_date", vaccineDate.toString(df), bindType);
		addField(fields, "vaccines", vaccine, bindType);
		
		addField(fields, vaccine, vaccineDate.toString(df), bindType);
		String dose = vaccine.substring(vaccine.length()-1);
		if(dose.matches("[0-9]")){
			addField(fields, vaccine+"_dose_today", dose, bindType);
		}
		return fields;
	}
	
	public String usernameWithBasicAuthentication(HttpServletRequest req) {
	    String authHeader = req.getHeader("Authorization");
	    if (authHeader != null) {
	        StringTokenizer st = new StringTokenizer(authHeader);
	        if (st.hasMoreTokens()) {
	            String basic = st.nextToken();

	            if (basic.equalsIgnoreCase("Basic")) {
	                try {
	                    String credentials = new String(Base64.decodeBase64(st.nextToken()), "UTF-8");
	                    System.out.println("Credentials: " + credentials);
	                    int p = credentials.indexOf(":");
	                    if (p != -1) {
	                        String login = credentials.substring(0, p).trim();
	                        String password = credentials.substring(p + 1).trim();

	                        return login;
	                    } else {
	                    	System.out.println("Invalid authentication token");
	                    }
	                } catch (UnsupportedEncodingException e) {
	                	e.printStackTrace();
	                	System.out.println("Couldn't retrieve authentication");
	                }
	            }
	        }
	    }

	    return null;
	}
	
public static void main(String[] args) {
	System.out.println(new DateTime(new Date()));
	System.out.println("vp".substring("vp".length()-1).matches("[0-9]"));
	System.out.println(makeVaccineCard(new ArrayList<Event>(), eligibleVaccines(8)));
}
}

