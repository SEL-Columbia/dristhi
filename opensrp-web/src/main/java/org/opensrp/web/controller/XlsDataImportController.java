package org.opensrp.web.controller;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.opensrp.domain.Address;
import org.opensrp.domain.Client;
import org.opensrp.domain.Event;
import org.opensrp.domain.Obs;
import org.opensrp.service.ClientService;
import org.opensrp.service.EventService;
import org.opensrp.service.OpenmrsIDService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;

@Controller
@RequestMapping("/import")
public class XlsDataImportController {
	
	public static final String LOCATION = "Location";
	public static final String M_ZEIR_ID = "M_ZEIR_ID";
	public static final String MOTHER_NRC_NUMBER = "NRC_Number";
	public static final String HOME_FACILITY = "Home_Facility";
	public static final String CHW_NAME = "CHW_Name";
	public static final String PMTCT_STATUS = "PMTCT_Status";
	public static final String CHILD_REGISTER_CARD_NUMBER = "Child_Register_Card_Number";
	public static final String CHW_PHONE_NUMBER = "CHW_Phone_Number";
	public static final String CHILD_BIRTH_CERTIFICATE = "Child_Birth_Certificate";
	public static final String FATHER_NRC_NUMBER = "Father_NRC_Number";
	public static final String ADDRESS_TYPE = "usual_residence";
	public static final String BCG_VACCINE = "bcg";
	public static final String OPV_VACCINE = "opv";
	public static final String PCV_VACCINE = "pcv";
	public static final String PENTA_VACCINE = "penta";
	public static final String ROTA_VACCINE = "rota";
	public static final String MR_VACCINE = "mr";
	public static final String MEASLES_VACCINE = "measles";
	private static final String DATE_FORMAT = "yyyy-MM-dd";
	private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	private ClientService clientService;
	private EventService eventService;
	private OpenmrsIDService openmrsIDService;
	 
	private DateTimeFormatter parseDate = DateTimeFormat.forPattern(DATE_FORMAT);
	
	@Autowired
	public XlsDataImportController(ClientService clientService, EventService eventService, OpenmrsIDService openmrsIDService) {
		this.clientService = clientService;
		this.eventService = eventService;
		this.openmrsIDService = openmrsIDService;
	}
	
	@RequestMapping(headers = { "Accept=multipart/form-data" }, method = POST, value = "/file")
	public ResponseEntity<String> importXlsData(@RequestParam("file") MultipartFile file) throws SQLException {
		Map<String, Object> stats = new HashMap<>();

		this.openmrsIDService.initializeImportTable(false);

		int eventCount = 0;
		int clientCount = 0;
		CSVParser parser;
		try {
			Reader reader = new InputStreamReader(file.getInputStream());
			parser = new CSVParser(reader, CSVFormat.EXCEL.withHeader());
			List<CSVRecord> records = parser.getRecords();
			int recordCount = records.size();
			List<String> openmrsIds = this.openmrsIDService.downloadOpenmrsIds(recordCount);
			
			int counter = 0;

			for (CSVRecord record : records) {
				int eventCounter = 0;
			    Address address = this.buildAddress(record);
			    ArrayList<Address> addressList = new ArrayList<Address>();
			    addressList.add(address);

			    // Create child record
			    Client childClient = this.createChildClient(record, addressList);
			    
			    if(!openmrsIDService.checkIfClientExists(childClient, false)) {
			    	// Assign zeir and m_zeir ids
				    String zeirId = openmrsIds.get(counter);
				    String motherZeirId = zeirId + "_mother";

				    // Create mother record
				    Client motherClient = this.createMotherClient(record, addressList);
				    motherClient.addIdentifier(M_ZEIR_ID, motherZeirId);

				    openmrsIDService.assignOpenmrsIdToClient(zeirId, childClient, false);

				    // Create mother relationship
				    childClient.addRelationship("mother", motherClient.getBaseEntityId());

				    // Create common observations to all events
				    // 2017-03-20T12:40:02.000+02:00
				    DateTimeFormatter parseDate = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
				    
					// start
					String start = record.get("start");
					DateTime startDate = parseDate.parseDateTime(start);
					
					Obs startObs = buildObservation("concept", "start", "163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", startDate.toString(DATE_TIME_FORMAT), "start");

					// end
					String end = record.get("end");
					DateTime endDate = parseDate.parseDateTime(end);
					Obs endObs = buildObservation("concept", "end", "163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", endDate.toString(DATE_TIME_FORMAT), "end");

					// deviceid
					String deviceid = record.get("deviceid");
					Obs deviceIdObs = buildObservation("concept", "deviceid", "163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", deviceid, "deviceid");
				    
				    // Create Birth Registration Event
				    Event birthRegistrationEvent = this.buildBirthRegistrationEvent(record, childClient);
				    birthRegistrationEvent.addObs(startObs);
				    birthRegistrationEvent.addObs(endObs);
				    birthRegistrationEvent.addObs(deviceIdObs);

				    eventService.addEvent(birthRegistrationEvent);
				    eventCounter++;

				    // Create New Woman Registration Event
				    Event womanRegistrationEvent = this.buildNewWomanRegistrationEvent(record, motherClient);
				    womanRegistrationEvent.addObs(startObs);
				    womanRegistrationEvent.addObs(endObs);
				    womanRegistrationEvent.addObs(deviceIdObs);

				    eventService.addEvent(womanRegistrationEvent);
				    eventCounter++;

				    // Create vaccination events
				    for(Event e: this.buildVaccinationEvents(record, childClient)) {
						e.addObs(startObs);
						e.addObs(endObs);
						e.addObs(deviceIdObs);

				    	eventService.addEvent(e);
				    	eventCounter++;
				    }
				    
				    //Create growth monitoring events
				    for(Event e: this.buildGrowthMonitoringEvents(record, childClient)) {
						e.addObs(startObs);
						e.addObs(endObs);
						e.addObs(deviceIdObs);

				    	eventService.addEvent(e);
				    	eventCounter++;
				    }

				    clientService.addorUpdate(motherClient);
				    clientService.addorUpdate(childClient);
				    eventCount += eventCounter;
				    clientCount +=2;
				    counter++;
			    }
			}
			parser.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Retrieve xls file details
		// Read xls file to retrieve birth registration data
		// loop through all the records creating a client and entity information for each patient
		// respond with success response and summary statistics of data imported
		
		stats.put("summary_client_count", clientCount);
		stats.put("summary_event_count", eventCount);

		return new ResponseEntity<>(new Gson().toJson(stats), HttpStatus.OK);
	}

	private Address buildAddress(CSVRecord record) {
		// Address data
	    String startDate = record.get("today");
	    String endDate = record.get("today");
	    String homeFacility = record.get("Childs_Particulars/Home_Facility");
	    String residentialArea = record.get("Childs_Particulars/Residential_Area");
	    String residentialAreaOther = record.get("Childs_Particulars/Residential_Area_Other");
	    String residentialAddress = record.get("Childs_Particulars/Residential_Address");
	    String physicalLandmark = record.get("Childs_Particulars/Physical_Landmark");
	    
	    // Build address object
	    DateTime addressStartDate = this.parseDate.parseDateTime(startDate);
	    DateTime addressEndDate = this.parseDate.parseDateTime(endDate);
	    
	    String homeFacilityUUID = this.getLocationUUID(homeFacility);

	    Map<String, String> addressFields = new HashMap<>();
	    addressFields.put("address5", residentialAreaOther);
	    addressFields.put("address4", homeFacilityUUID);
	    addressFields.put("address3", residentialArea);
	    addressFields.put("address2", residentialAddress);
	    addressFields.put("address1", physicalLandmark);
	    
	    Address address = new Address(ADDRESS_TYPE, addressStartDate, addressEndDate, addressFields, null, null, null, null, null);
	    
	    return address;
	}
	
	private Client createMotherClient(CSVRecord record, ArrayList<Address> addressList) {
		// Mother data
	    String motherFirstName = record.get("Childs_Particulars/Mother_Guardian_First_Name");
	    String motherLastName = record.get("Childs_Particulars/Mother_Guardian_Last_Name");
	    String motherNRC = record.get("Childs_Particulars/Mother_Guardian_NRC");
	    String homeFacility = record.get("Childs_Particulars/Home_Facility");
	    String homeFacilityId = this.getLocationId(homeFacility);
	    String motherId = UUID.randomUUID().toString();
	    
	    DateTime dateOfBirth = new DateTime(1960, 01, 01, 12, 0,  DateTimeZone.forOffsetHours(2));
	    Client motherClient = new Client(motherId, motherFirstName, "", motherLastName, dateOfBirth, null, false, false, "Female", addressList, null, null);
	    motherClient.addAttribute(MOTHER_NRC_NUMBER, motherNRC);
	    motherClient.addAttribute(HOME_FACILITY, homeFacilityId);
	    
	    return motherClient;
	}
	
	private Client createChildClient(CSVRecord record, ArrayList<Address> addressList) {
		// Child data
	    String firstName = record.get("Childs_Particulars/First_Name");
	    String lastName = record.get("Childs_Particulars/Last_Name");
	    String gender = record.get("Childs_Particulars/Sex");
	    String birthDate = record.get("Childs_Particulars/Date_Birth");
	    
	    // Child attributes
	    String childCardNumber = record.get("Childs_Particulars/Child_Register_Card_Number");
	    String chwPhoneNumber = record.get("Childs_Particulars/CHW_Phone_Number");
	    String fatherNRCNumber = record.get("Childs_Particulars/Father_Guardian_NRC");
	    String chwName = record.get("Childs_Particulars/CHW_Name");
	    String homeFacility = record.get("Childs_Particulars/Home_Facility");
	    String homeFacilityId = this.getLocationId(homeFacility);
	    
	    String childId = UUID.randomUUID().toString();

	    DateTime dateOfBirth = this.parseDate.parseDateTime(birthDate);
	    
	    // validate names
	    firstName = firstName == "n/a" ? "" : firstName;

	    Client childClient = new Client(childId, firstName, "", lastName, dateOfBirth, null, false, false, gender, addressList, null, null);
	    childClient.addAttribute(CHILD_REGISTER_CARD_NUMBER, childCardNumber);
	    childClient.addAttribute(CHW_PHONE_NUMBER, chwPhoneNumber);
	    childClient.addAttribute(FATHER_NRC_NUMBER, fatherNRCNumber);
	    childClient.addAttribute(CHW_NAME, chwName);
	    childClient.addAttribute(HOME_FACILITY, homeFacilityId);
	    
	    return childClient;
	}
	
	private Event buildBirthRegistrationEvent(CSVRecord record, Client client) {
		String eventType = "Birth Registration";
        String entityType = "child";
		String locationName = record.get("Childs_Particulars/Home_Facility");
		String dateOfFacilityVisit = record.get("Childs_Particulars/First_Health_Facility_Contact");
		
		List<Obs> birthRegistrationObs = this.buildBirthRegistrationObs(record);
		
		
		DateTime date = parseDate.parseDateTime(dateOfFacilityVisit);
		Event birthRegistrationEvent = this.createEvent(client, birthRegistrationObs, eventType, entityType, date, locationName);
		
		return birthRegistrationEvent;
	}
	
	private Event buildNewWomanRegistrationEvent(CSVRecord record, Client client) {
		String locationName = record.get("Childs_Particulars/Home_Facility");
		String firstHealthFacilityContact = record.get("Childs_Particulars/First_Health_Facility_Contact");
		DateTime eventDate = parseDate.parseDateTime(firstHealthFacilityContact);
		Event newWomanRegistration = this.createEvent(client, null, "New Woman Registration", "mother", eventDate, locationName);

		return newWomanRegistration;
	}

	private List<Event> buildVaccinationEvents(CSVRecord record, Client client) {
		String eventType = "Vaccination";
        String entityType = "vaccination";
		List<Event> vaccinationEvents = new ArrayList<Event>();
		String bcg1Value = record.get("Immunisation_Record/bcg");
		String bcg2Value = record.get("Immunisation_Record/bcg2");
		String opv0Value = record.get("Immunisation_Record/opv0");
		String opv1Penta1Pcv1Rota1Value = record.get("Immunisation_Record/opv1_penta1_pcv1_rota1");
		String opv1Value = opv1Penta1Pcv1Rota1Value != "n/a" ? opv1Penta1Pcv1Rota1Value : record.get("Immunisation_Record/opv1");
		String penta1Value = opv1Penta1Pcv1Rota1Value != "n/a" ? opv1Penta1Pcv1Rota1Value : record.get("Immunisation_Record/penta1");
		String pcv1Value = opv1Penta1Pcv1Rota1Value != "n/a" ? opv1Penta1Pcv1Rota1Value : record.get("Immunisation_Record/pcv1");
		String rota1Value = opv1Penta1Pcv1Rota1Value != "n/a" ? opv1Penta1Pcv1Rota1Value : record.get("Immunisation_Record/rota1");
		String opv2Penta2Pcv2Rota2Value = record.get("Immunisation_Record/opv2_penta2_pcv2_rota2");
		String opv2Value = opv2Penta2Pcv2Rota2Value != "n/a" ? opv2Penta2Pcv2Rota2Value : record.get("Immunisation_Record/opv2");
		String penta2Value = opv2Penta2Pcv2Rota2Value != "n/a" ? opv2Penta2Pcv2Rota2Value : record.get("Immunisation_Record/penta2");
		String pcv2Value = opv2Penta2Pcv2Rota2Value != "n/a" ? opv2Penta2Pcv2Rota2Value : record.get("Immunisation_Record/pcv2");
		String rota2Value = opv2Penta2Pcv2Rota2Value != "n/a" ? opv2Penta2Pcv2Rota2Value : record.get("Immunisation_Record/rota2");
		String opv3Penta3Pcv3Value = record.get("Immunisation_Record/opv3_penta3_pcv3");
		String opv3Value = opv3Penta3Pcv3Value != "n/a" ? opv3Penta3Pcv3Value : record.get("Immunisation_Record/opv3");
		String penta3Value = opv3Penta3Pcv3Value != "n/a" ? opv3Penta3Pcv3Value : record.get("Immunisation_Record/penta3");
		String pcv3Value = opv3Penta3Pcv3Value != "n/a" ? opv3Penta3Pcv3Value : record.get("Immunisation_Record/pcv3");
		String opv4Value = record.get("Immunisation_Record/opv4");
		String measles1Value = record.get("Immunisation_Record/measles1");
		String measles2Value = record.get("Immunisation_Record/measles2");
		String mr1Value = record.get("Immunisation_Record/mr1");
		String mr2Value = record.get("Immunisation_Record/mr2");
		String locationName = record.get("Childs_Particulars/Home_Facility");
		
		if(!bcg1Value.equals("n/a")) {
			List<Obs> bcg1Obs = this.buildBCGVaccineObservation(bcg1Value);
			DateTime date = parseDate.parseDateTime(bcg1Value);
			Event bcg1Event = this.createEvent(client, bcg1Obs, eventType, entityType, date, locationName);
			vaccinationEvents.add(bcg1Event);
		}
		
		if(!bcg2Value.equals("n/a")) {
			List<Obs> bcg2Obs = this.buildVaccineObservation(BCG_VACCINE, "2", bcg2Value);
			DateTime date = parseDate.parseDateTime(bcg2Value);
			Event bcg2Event = this.createEvent(client, bcg2Obs, eventType, entityType, date, locationName);
			vaccinationEvents.add(bcg2Event);
		}
		
		if(!opv0Value.equals("n/a")) {
			List<Obs> opv0Obs = this.buildVaccineObservation(OPV_VACCINE, "0", opv0Value);
			DateTime date = parseDate.parseDateTime(opv0Value);
			Event opv0Event = this.createEvent(client, opv0Obs, eventType, entityType, date, locationName);
			vaccinationEvents.add(opv0Event);
		}
		
		if(!opv1Value.equals("n/a")) {
			List<Obs> opv1Obs = this.buildVaccineObservation(OPV_VACCINE, "1", opv1Value);
			DateTime date = parseDate.parseDateTime(opv1Value);
			Event opv1Event = this.createEvent(client, opv1Obs, eventType, entityType, date, locationName);
			vaccinationEvents.add(opv1Event);
		}
		
		if(!opv2Value.equals("n/a")) {
			List<Obs> opv2Obs = this.buildVaccineObservation(OPV_VACCINE, "2", opv2Value);
			DateTime date = parseDate.parseDateTime(opv2Value);
			Event opv2Event = this.createEvent(client, opv2Obs, eventType, entityType, date, locationName);
			vaccinationEvents.add(opv2Event);
		}
		
		if(!opv3Value.equals("n/a")) {
			List<Obs> opv3Obs = this.buildVaccineObservation(OPV_VACCINE, "3", opv3Value);
			DateTime date = parseDate.parseDateTime(opv3Value);
			Event opv3Event = this.createEvent(client, opv3Obs, eventType, entityType, date, locationName);
			vaccinationEvents.add(opv3Event);
		}
		
		if(!penta1Value.equals("n/a")) {
			List<Obs> penta1Obs = this.buildVaccineObservation(PENTA_VACCINE, "1", penta1Value);
			DateTime date = parseDate.parseDateTime(penta1Value);
			Event penta1Event = this.createEvent(client, penta1Obs, eventType, entityType, date, locationName);
			vaccinationEvents.add(penta1Event);
		}
		
		if(!penta2Value.equals("n/a")) {
			List<Obs> penta2Obs = this.buildVaccineObservation(PENTA_VACCINE, "2", penta2Value);
			DateTime date = parseDate.parseDateTime(penta2Value);
			Event penta2Event = this.createEvent(client, penta2Obs, eventType, entityType, date, locationName);
			vaccinationEvents.add(penta2Event);
		}
		
		if(!penta3Value.equals("n/a")) {
			List<Obs> penta3Obs = this.buildVaccineObservation(PENTA_VACCINE, "3", penta3Value);
			DateTime date = parseDate.parseDateTime(penta3Value);
			Event penta3Event = this.createEvent(client, penta3Obs, eventType, entityType, date, locationName);
			vaccinationEvents.add(penta3Event);
		}
		
		if(!pcv1Value.equals("n/a")) {
			List<Obs> pcv1Obs = this.buildVaccineObservation(PCV_VACCINE, "1", pcv1Value);
			DateTime date = parseDate.parseDateTime(pcv1Value);
			Event pcv1Event = this.createEvent(client, pcv1Obs, eventType, entityType, date, locationName);
			vaccinationEvents.add(pcv1Event);
		}
		
		if(!pcv2Value.equals("n/a")) {
			List<Obs> pcv2Obs = this.buildVaccineObservation(PCV_VACCINE, "2", pcv2Value);
			DateTime date = parseDate.parseDateTime(penta2Value);
			Event pcv2Event = this.createEvent(client, pcv2Obs, eventType, entityType, date, locationName);
			vaccinationEvents.add(pcv2Event);
		}
		
		if(!pcv3Value.equals("n/a")) {
			List<Obs> pcv3Obs = this.buildVaccineObservation(PCV_VACCINE, "3", pcv3Value);
			DateTime date = parseDate.parseDateTime(penta3Value);
			Event pcv3Event = this.createEvent(client, pcv3Obs, eventType, entityType, date, locationName);
			vaccinationEvents.add(pcv3Event);
		}
		
		if(!rota1Value.equals("n/a")) {
			List<Obs> rota1Obs = this.buildVaccineObservation(ROTA_VACCINE, "1", rota1Value);
			DateTime date = parseDate.parseDateTime(rota1Value);
			Event rota1Event = this.createEvent(client, rota1Obs, eventType, entityType, date, locationName);
			vaccinationEvents.add(rota1Event);
		}

		if(!rota2Value.equals("n/a")) {
			List<Obs> rota2Obs = this.buildVaccineObservation(ROTA_VACCINE, "2", rota2Value);
			DateTime date = parseDate.parseDateTime(rota2Value);
			Event rota2Event = this.createEvent(client, rota2Obs, eventType, entityType, date, locationName);
			vaccinationEvents.add(rota2Event);
		}		
		
		if(!opv4Value.equals("n/a")) {
			List<Obs> opv4Obs = this.buildVaccineObservation(OPV_VACCINE, "4", opv4Value);
			DateTime date = parseDate.parseDateTime(opv4Value);
			Event opv4Event = this.createEvent(client, opv4Obs, eventType, entityType, date, locationName);
			vaccinationEvents.add(opv4Event);
		}
		
		if(!measles1Value.equals("n/a")) {
			List<Obs> measles1Obs = this.buildVaccineObservation(MEASLES_VACCINE, "1", measles1Value);
			DateTime date = parseDate.parseDateTime(measles1Value);
			Event measles1Event = this.createEvent(client, measles1Obs, eventType, entityType, date, locationName);
			vaccinationEvents.add(measles1Event);
		}
		
		if(!measles2Value.equals("n/a")) {
			List<Obs> measles2Obs = this.buildVaccineObservation(MEASLES_VACCINE, "2", measles2Value);
			DateTime date = parseDate.parseDateTime(measles2Value);
			Event measles2Event = this.createEvent(client, measles2Obs, eventType, entityType, date, locationName);
			vaccinationEvents.add(measles2Event);
		}
		
		if(!mr1Value.equals("n/a")) {
			List<Obs> mr1Obs = this.buildVaccineObservation(MR_VACCINE, "1", mr1Value);
			DateTime date = parseDate.parseDateTime(mr1Value);
			Event mr1Event = this.createEvent(client, mr1Obs, eventType, entityType, date, locationName);
			vaccinationEvents.add(mr1Event);
		}
		
		if(!mr2Value.equals("n/a")) {
			List<Obs> mr2Obs = this.buildVaccineObservation(MR_VACCINE, "2", mr2Value);
			DateTime date = parseDate.parseDateTime(mr2Value);
			Event mr2Event = this.createEvent(client, mr2Obs, eventType, entityType, date, locationName);
			vaccinationEvents.add(mr2Event);
		}
		
		return vaccinationEvents;
	}
	
	private List<Event> buildGrowthMonitoringEvents(CSVRecord record, Client client) {
		List<Event> growthMonitoringEvents = new ArrayList<Event>();
		String eventType = "Growth Monitoring";
        String entityType = "weight";
		String weight1 = record.get("Growth_Chart/weight1");
		String weight1Date = record.get("Growth_Chart/weight1_date");
		String weight2 = record.get("Growth_Chart/weight2");
		String weight2Date = record.get("Growth_Chart/weight2_date");
		String weight3 = record.get("Growth_Chart/weight3");
		String weight3Date = record.get("Growth_Chart/weight3_date");
		String locationName = record.get("Childs_Particulars/Home_Facility");

		if(!weight1.equals("n/a") && !weight1Date.equals("n/a")) {
			List<Obs> obsList = new ArrayList<Obs>();
			Obs weight1Obs = this.buildGrowthMonitoringObservation(weight1);
			DateTime eventDate = this.parseDate.parseDateTime(weight1Date);
			obsList.add(weight1Obs);
			Event gmEvent = this.createEvent(client, obsList, eventType, entityType, eventDate, locationName);
			growthMonitoringEvents.add(gmEvent);
		}

		if(!weight2.equals("n/a") && !weight2Date.equals("n/a")) {
			List<Obs> obsList = new ArrayList<Obs>();
			Obs weight2Obs = this.buildGrowthMonitoringObservation(weight2);
			DateTime eventDate = this.parseDate.parseDateTime(weight2Date);
			obsList.add(weight2Obs);
			Event gm2Event = this.createEvent(client, obsList, eventType, entityType, eventDate, locationName);
			growthMonitoringEvents.add(gm2Event);
		}

		if(!weight3.equals("n/a") && !weight3Date.equals("n/a")) {
			List<Obs> obsList = new ArrayList<Obs>();
			Obs weight3Obs = this.buildGrowthMonitoringObservation(weight3);
			DateTime eventDate = this.parseDate.parseDateTime(weight3Date);
			obsList.add(weight3Obs);
			Event gm3Event = this.createEvent(client, obsList, eventType, entityType, eventDate, locationName);
			growthMonitoringEvents.add(gm3Event);
		}

		return growthMonitoringEvents;
	}

	private Event createEvent(Client client, List<Obs> obs, String eventType, String entityType, DateTime eventDate, String locationName) {
        eventDate = eventDate == null ? new DateTime() : eventDate;
        String formSubmissionId = UUID.randomUUID().toString();
        String providerId = getProviderId(locationName);
        String locationId = getLocationUUID(locationName);
        
        Event event = new Event(client.getBaseEntityId(), eventType, eventDate, entityType, providerId, locationId, formSubmissionId);
        event = this.addMultipleObs(event, obs);
        
        return event;
	}
	
	private Event addMultipleObs(Event event, List<Obs> multipleObs) {
		if(multipleObs != null) {
			for(Obs obs: multipleObs) {
				event.addObs(obs);
			}
		}

		return event;
	}
	
	private String getLocationUUID(String location) {
		switch(location) {
			case "Mahatma_Gandhi":
	            return "5bf3b4ca-9482-4e85-ab7a-0c44e4edb329";
	        case "Libuyu":
	            return "e0d37af3-50b7-424d-a6b0-94b1035271b3";
	        case "Linda":
	            return "9e4fc064-d8e7-4fcb-942e-cbcf6524fb24";
	        case "Dambwa_North_Clinic":
	            return "29414e77-c0dc-4834-b92a-10cd6c2a8d93";
	        case "Victoria_Falls_Clinic":
	        	return "7567285c-0929-4723-9cf8-faee530adb70";
	        case "Airport_Clininc":
	        	return "39d0a527-d4dc-4946-ad8f-7cb045cc0bb8";
	        default:
	        	return "";
		}
	}
	
	private String getLocationId(String location) {
		switch(location) {
			case "Mahatma_Gandhi":
	            return "448";
	        case "Libuyu":
	            return "443";
	        case "Linda":
	            return "532";
	        case "Dambwa_North_Clinic":
	            return "456";
	        case "Victoria_Falls_Clinic":
	        	return "1009";
	        case "Airport_Clininc":
	        	return "426";
	        default:
	        	return "";
		}
	}

	private String getProviderId(String location) {
		switch(location) {
			case "Mahatma_Gandhi":
	            return "DLucia";
	        case "Libuyu":
	            return "pmufwinda";
	        case "Linda":
	            return "Lmusonda";
	        case "Dambwa_North_Clinic":
	            return "vmutila";
	        case "Victoria_Falls_Clinic":
	        	return "vshowa";
	        case "Airport_Clininc":
	        	return "cmalindi";
	        default:
	        	return "";
		}
	}
	
	private String getVaccineParentCode(String vaccine) {
		switch(vaccine) {
			case BCG_VACCINE:
	            return "886AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	        case OPV_VACCINE:
	            return "783AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	        case PCV_VACCINE:
	            return "162342AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	        case PENTA_VACCINE:
	            return "1685AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	        case ROTA_VACCINE:
	            return "159698AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	        case MEASLES_VACCINE:
	            return "36AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	        case MR_VACCINE:
	            return "162586AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
	        default:
	        	return "";
		}
	}
	
	private List<Obs> buildBCGVaccineObservation(String date) {
		String fieldType = "concept";
		String dateFieldDataType = "date";
		String dateFieldCode = "1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
		String parentCode = this.getVaccineParentCode(BCG_VACCINE);
		String formSubmissionField = "bcg";
		List<Object> values1 = new ArrayList<Object>();
		values1.add(date);

		Obs bcgDateObs = new Obs(fieldType, dateFieldDataType, dateFieldCode, parentCode, values1, null, formSubmissionField);

		List<Obs> bcgObs = new ArrayList<Obs>();
		bcgObs.add(bcgDateObs);

		return bcgObs;
	}

	private List<Obs> buildVaccineObservation(String vaccine, String dose, String value) {
		String fieldType = "concept";
		String dateFieldDataType = "date";
		String calculateFieldDataType = "calculate";
		String dateFieldCode = "1410AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
		String calculateFieldCode = "1418AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
		String parentCode = this.getVaccineParentCode(vaccine);
		String formSubmissionField1 = vaccine + "_" + dose;
		String formSubmissionField2 = vaccine + "_" + dose + "_dose";
		List<Object> values1 = new ArrayList<Object>();
		values1.add(value);
		List<Object> values2 = new ArrayList<Object>();
		values2.add(dose);
		
		Obs bcgDateObs = new Obs(fieldType, dateFieldDataType, dateFieldCode, parentCode, values1, null, formSubmissionField1);
		Obs bcgCalculateObs = new Obs(fieldType, calculateFieldDataType, calculateFieldCode, parentCode, values2, null, formSubmissionField2);
		
		List<Obs> bcgObs = new ArrayList<Obs>();
		bcgObs.add(bcgDateObs);
		bcgObs.add(bcgCalculateObs);
		
		return bcgObs;
	}
	
	private List<Obs> buildBirthRegistrationObs(CSVRecord record) {
		String value = "";
		List<Obs> birthRegistrationObs = new ArrayList<Obs>();
		// First_Health_Facility_Contact
		String firstHealthFacilityContact = record.get("Childs_Particulars/First_Health_Facility_Contact");
		birthRegistrationObs.add(buildObservation("concept", "text", "163260AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", firstHealthFacilityContact, "First_Health_Facility_Contact"));
		
		// Birth_Weight
		String birthWeight = record.get("Childs_Particulars/Birth_Weight");
		birthRegistrationObs.add(buildObservation("concept", "text", "5916AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", birthWeight, "Birth_Weight"));

		// Father_Guardian_Name
		String fatherGuardianName = record.get("Childs_Particulars/Father_Guardian_Name");
		birthRegistrationObs.add(buildObservation("concept", "text", "1594AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", fatherGuardianName, "Father_Guardian_Name"));
		
		// Place_Birth
		String placeBirth = record.get("Childs_Particulars/Place_Birth");
		value = placeBirth == "Health_Facility" ? "1537AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" : "1536AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";

		String humanReadableValue = placeBirth == "Health_Facility" ? "Health facility" : placeBirth;
		birthRegistrationObs.add(buildObservationWithHumanReadableValues("concept", "select one", "1572AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", value, "Place_Birth", humanReadableValue));
		
		// Birth_Facility_Name
		String birthFacilityName = record.get("Childs_Particulars/Birth_Facility_Name");
		String birthFacilityNameOther = record.get("Childs_Particulars/Birth_Facility_Name_Other");
		
		if(birthFacilityName != "n/a") {
			value = this.getLocationUUID(birthFacilityName) != "" ? this.getLocationUUID(birthFacilityName) : birthFacilityNameOther;
			birthRegistrationObs.add(buildObservation("concept", "text", "163531AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", value, "Birth_Facility_Name"));
		}
		// PMTCT_Status
		
		String pmtctStatus = record.get("PMTCT/PMTCT_Status");
		birthRegistrationObs.add(buildObservation("concept", "text", "1396AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", pmtctStatus, "PMTCT_Status"));
		
		return birthRegistrationObs;
	}
	
	private Obs buildObservation(String fieldType, String fieldDataType, String fieldCode, String value, String formSubmissionField) {
		List<Object> values = new ArrayList<Object>();
		values.add(value);
		
		Obs obs = new Obs(fieldType, fieldDataType, fieldCode, "", value, "", formSubmissionField);
		
		return obs;
	}
	
	private Obs buildObservationWithHumanReadableValues(String fieldType, String fieldDataType, String fieldCode, String value, String formSubmissionField, String humanReadableValue) {
		List<Object> values = new ArrayList<Object>();
		values.add(value);

		List<Object> humanReadableValues = new ArrayList<Object>();
		humanReadableValues.add(humanReadableValue);

		Obs obs = new Obs(fieldType, fieldDataType, fieldCode, "", value, "", formSubmissionField);
		obs.setHumanReadableValues(humanReadableValues);

		return obs;
	}

	private Obs buildGrowthMonitoringObservation(String weight) {
		String fieldType = "concept";
		String fieldDataType = "decimal";
		String fieldCode = "5089AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
		List<Object> values = new ArrayList<Object>();
		values.add(weight);

		String formSubmissionField = "Weight_Kgs";

		Obs growthMonitoringObs = new Obs(fieldType, fieldDataType, fieldCode, "", weight, "", formSubmissionField);
		growthMonitoringObs.setValues(values);

		return growthMonitoringObs;
	}
}
