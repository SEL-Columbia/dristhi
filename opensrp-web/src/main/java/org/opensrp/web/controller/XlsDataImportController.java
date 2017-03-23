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
	
	public static final String M_ZEIR_ID = "M_ZEIR_ID";
	public static final String MOTHER_NRC_NUMBER = "NRC_Number";
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
	
	private ClientService clientService;
	private EventService eventService;
	private OpenmrsIDService openmrsIDService;
	
	private DateTimeFormatter parseDate = DateTimeFormat.forPattern("yyyy-MM-dd");
	
	@Autowired
	public XlsDataImportController(ClientService clientService, EventService eventService, OpenmrsIDService openmrsIDService) {
		this.clientService = clientService;
		this.eventService = eventService;
		this.openmrsIDService = openmrsIDService;
	}
	
	@RequestMapping(headers = { "Accept=multipart/form-data" }, method = POST, value = "/file")
	public ResponseEntity<String> importXlsData(@RequestParam("file") MultipartFile file) throws SQLException {
		Map<String,String> stats = new HashMap<>();
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
			    Address address = this.buildAddress(record);
			    ArrayList<Address> addressList = new ArrayList<Address>();
			    addressList.add(address);
			    
			    // Assign zeir and m_zeir ids
			    String zeirId = openmrsIds.get(counter);
			    String motherZeirId = zeirId + "_mother";

			    // Create mother record
			    Client motherClient = this.createMotherClient(record, addressList);
			    motherClient.addIdentifier(M_ZEIR_ID, motherZeirId);

			    // Create child record
			    Client childClient = this.createChildClient(record, addressList);
			    childClient = openmrsIDService.assignOpenmrsIdToClient(zeirId, childClient, false);
			    
			    // Create mother relationship
			    childClient.addRelationship("mother", motherClient.getBaseEntityId());
			    
			    // Create vaccination events
			    
			    
			    clientService.addClient(motherClient);
			    clientService.addClient(childClient);
			    
			    clientCount+=2;
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
		
		stats.put("Clients Added", "" + clientCount);
		
		return new ResponseEntity<>(new Gson().toJson(stats), HttpStatus.OK);
	}
	
	private Address buildAddress(CSVRecord record) {
		// Address data
	    String startDate = record.get("today");
	    String endDate = record.get("today");
	    String homeFacility = record.get("Childs_Particulars/Home_Facility");
	    String birthFacilityName = record.get("Childs_Particulars/Birth_Facility_Name");
	    String residentialArea = record.get("Childs_Particulars/Residential_Area");
	    String residentialAreaOther = record.get("Childs_Particulars/Residential_Area_Other");
	    String residentialAddress = record.get("Childs_Particulars/Residential_Address");
	    String physicalLandmark = record.get("Childs_Particulars/Physical_Landmark");
	    
	    String resolvedResidentialAddress = residentialAreaOther != "n/a" ? residentialArea : residentialAreaOther; 
	    
	    // Build address object
	    DateTime addressStartDate = this.parseDate.parseDateTime(startDate);
	    DateTime addressEndDate = this.parseDate.parseDateTime(endDate);
	    
	    Map<String, String> addressFields = new HashMap<>();
	    addressFields.put("address4", birthFacilityName);
	    addressFields.put("address3", resolvedResidentialAddress);
	    addressFields.put("address2", residentialAddress);
	    addressFields.put("address1", physicalLandmark);
	    
	    Address address = new Address(ADDRESS_TYPE, addressStartDate, addressEndDate, addressFields, null, null, null, homeFacility, null);
	    
	    return address;
	}
	
	private Client createMotherClient(CSVRecord record, ArrayList<Address> addressList) {
		// Mother data
	    String motherFirstName = record.get("Childs_Particulars/Mother_Guardian_First_Name");
	    String motherLastName = record.get("Childs_Particulars/Mother_Guardian_Last_Name");
	    String motherNRC = record.get("Childs_Particulars/Mother_Guardian_NRC");
	    String motherId = UUID.randomUUID().toString();
	    
	    Client motherClient = new Client(motherId, motherFirstName, "", motherLastName, null, null, false, false, "Female", addressList, null, null);
	    motherClient.addAttribute(MOTHER_NRC_NUMBER, motherNRC);
	    
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
	    String pmtctStatus = record.get("PMTCT/PMTCT_Status");
	    
	    String childId = UUID.randomUUID().toString();

	    DateTime dateOfBirth = this.parseDate.parseDateTime(birthDate);
	    
	    Client childClient = new Client(childId, firstName, "", lastName, dateOfBirth, null, false, false, gender, addressList, null, null);
	    childClient.addAttribute(CHILD_REGISTER_CARD_NUMBER, childCardNumber);
	    childClient.addAttribute(CHW_PHONE_NUMBER, chwPhoneNumber);
	    childClient.addAttribute(FATHER_NRC_NUMBER, fatherNRCNumber);
	    childClient.addAttribute(CHW_NAME, chwName);
	    childClient.addAttribute(PMTCT_STATUS, pmtctStatus);
	    
	    return childClient;
	}
	
	private List<Event> buildVaccinationEvents(CSVRecord record, Client client) {
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
		
		if(bcg1Value != "n/a") {
			List<Obs> bcg1Obs = this.buildVaccineObservation(BCG_VACCINE, "1", bcg1Value);
			Event bcg1Event = this.createVaccinationEvent(client, bcg1Obs);
			vaccinationEvents.add(bcg1Event);
		}
		
		if(bcg2Value != "n/a") {
			List<Obs> bcg1Obs = this.buildVaccineObservation(BCG_VACCINE, "2", bcg2Value);
			Event bcg2Event = this.createVaccinationEvent(client, bcg1Obs);
			vaccinationEvents.add(bcg2Event);
		}
		
		if(opv0Value != "n/a") {
			List<Obs> opv0Obs = this.buildVaccineObservation(OPV_VACCINE, "0", opv0Value);
			Event opv0Event = this.createVaccinationEvent(client, opv0Obs);
			vaccinationEvents.add(opv0Event);
		}
		
		if(opv1Value != "n/a") {
			List<Obs> opv1Obs = this.buildVaccineObservation(OPV_VACCINE, "1", opv1Value);
			Event opv1Event = this.createVaccinationEvent(client, opv1Obs);
			vaccinationEvents.add(opv1Event);
		}
		
		if(opv2Value != "n/a") {
			List<Obs> opv2Obs = this.buildVaccineObservation(OPV_VACCINE, "2", opv2Value);
			Event opv2Event = this.createVaccinationEvent(client, opv2Obs);
			vaccinationEvents.add(opv2Event);
		}
		
		if(opv3Value != "n/a") {
			List<Obs> opv3Obs = this.buildVaccineObservation(OPV_VACCINE, "3", opv3Value);
			Event opv3Event = this.createVaccinationEvent(client, opv3Obs);
			vaccinationEvents.add(opv3Event);
		}
		
		if(penta1Value != "n/a") {
			List<Obs> penta1Obs = this.buildVaccineObservation(PENTA_VACCINE, "1", penta1Value);
			Event penta1Event = this.createVaccinationEvent(client, penta1Obs);
			vaccinationEvents.add(penta1Event);
		}
		
		if(penta2Value != "n/a") {
			List<Obs> penta2Obs = this.buildVaccineObservation(PENTA_VACCINE, "2", penta2Value);
			Event penta2Event = this.createVaccinationEvent(client, penta2Obs);
			vaccinationEvents.add(penta2Event);
		}
		
		if(penta3Value != "n/a") {
			List<Obs> penta3Obs = this.buildVaccineObservation(PENTA_VACCINE, "3", penta3Value);
			Event penta3Event = this.createVaccinationEvent(client, penta3Obs);
			vaccinationEvents.add(penta3Event);
		}
		
		if(pcv1Value != "n/a") {
			List<Obs> pcv1Obs = this.buildVaccineObservation(PCV_VACCINE, "1", pcv1Value);
			Event pcv1Event = this.createVaccinationEvent(client, pcv1Obs);
			vaccinationEvents.add(pcv1Event);
		}
		
		if(pcv2Value != "n/a") {
			List<Obs> pcv2Obs = this.buildVaccineObservation(PCV_VACCINE, "2", pcv2Value);
			Event pcv2Event = this.createVaccinationEvent(client, pcv2Obs);
			vaccinationEvents.add(pcv2Event);
		}
		
		if(pcv3Value != "n/a") {
			List<Obs> pcv3Obs = this.buildVaccineObservation(PCV_VACCINE, "3", pcv3Value);
			Event pcv3Event = this.createVaccinationEvent(client, pcv3Obs);
			vaccinationEvents.add(pcv3Event);
		}
		
		if(rota1Value != "n/a") {
			List<Obs> rota1Obs = this.buildVaccineObservation(ROTA_VACCINE, "1", rota1Value);
			Event rota1Event = this.createVaccinationEvent(client, rota1Obs);
			vaccinationEvents.add(rota1Event);
		}

		if(rota2Value != "n/a") {
			List<Obs> rota2Obs = this.buildVaccineObservation(ROTA_VACCINE, "2", rota2Value);
			Event rota2Event = this.createVaccinationEvent(client, rota2Obs);
			vaccinationEvents.add(rota2Event);
		}		
		
		if(opv4Value != "n/a") {
			List<Obs> opv4Obs = this.buildVaccineObservation(OPV_VACCINE, "4", opv4Value);
			Event opv4Event = this.createVaccinationEvent(client, opv4Obs);
			vaccinationEvents.add(opv4Event);
		}
		
		if(measles1Value != "n/a") {
			List<Obs> measles1Obs = this.buildVaccineObservation(MEASLES_VACCINE, "1", measles1Value);
			Event measles1Event = this.createVaccinationEvent(client, measles1Obs);
			vaccinationEvents.add(measles1Event);
		}
		
		if(measles2Value != "n/a") {
			List<Obs> measles2Obs = this.buildVaccineObservation(MEASLES_VACCINE, "2", measles2Value);
			Event measles2Event = this.createVaccinationEvent(client, measles2Obs);
			vaccinationEvents.add(measles2Event);
		}
		
		if(mr1Value != "n/a") {
			List<Obs> mr1Obs = this.buildVaccineObservation(MR_VACCINE, "1", mr1Value);
			Event mr1Event = this.createVaccinationEvent(client, mr1Obs);
			vaccinationEvents.add(mr1Event);
		}
		
		if(mr2Value != "n/a") {
			List<Obs> mr2Obs = this.buildVaccineObservation(MR_VACCINE, "2", mr1Value);
			Event mr2Event = this.createVaccinationEvent(client, mr2Obs);
			vaccinationEvents.add(mr2Event);
		}	
		
		return vaccinationEvents;
	}
	
	private Event createVaccinationEvent(Client client, List<Obs> obs) {
		String eventType = "Vaccination";
        String entityType = "vaccination";
        DateTime eventDate = new DateTime();
        String formSubmissionId = UUID.randomUUID().toString();
        String providerId = openmrsIDService.getOpenmrsUserName();
        String locationId = "FIXME";
        
        Event vaccinationEvent = new Event(client.getBaseEntityId(), eventType, eventDate, entityType, providerId, locationId, formSubmissionId);
        vaccinationEvent = this.addMultipleObs(vaccinationEvent, obs);
        
        return vaccinationEvent;
	}
	
	private Event addMultipleObs(Event event, List<Obs> multipleObs) {
		for(Obs obs: multipleObs) {
			event.addObs(obs);
		}
		
		return event;
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
		values1.add(dose);
		
		Obs bcgDateObs = new Obs(fieldType, dateFieldDataType, dateFieldCode, parentCode, values1, null, formSubmissionField1);
		Obs bcgCalculateObs = new Obs(fieldType, calculateFieldDataType, calculateFieldCode, parentCode, values2, null, formSubmissionField2);
		
		List<Obs> bcgObs = new ArrayList<Obs>();
		bcgObs.add(bcgDateObs);
		bcgObs.add(bcgCalculateObs);
		
		return bcgObs;
	}
	
}
