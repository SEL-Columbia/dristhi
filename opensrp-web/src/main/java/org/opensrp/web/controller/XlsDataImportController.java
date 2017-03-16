package org.opensrp.web.controller;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
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
import org.opensrp.service.ClientService;
import org.opensrp.service.EventService;
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
	private ClientService clientService;
	private EventService eventService;
	
	@Autowired
	public XlsDataImportController(ClientService clientService, EventService eventService) {
		this.clientService = clientService;
		this.eventService = eventService;
	}
	
	@RequestMapping(headers = { "Accept=multipart/form-data" }, method = POST, value = "/file")
	public ResponseEntity<String> importXlsData(@RequestParam("file") MultipartFile file) {
		Map<String,String> stats = new HashMap<>();
		Integer clientCount = 0;
		try {
			Reader reader = new InputStreamReader(file.getInputStream());
			CSVParser parser = new CSVParser(reader, CSVFormat.EXCEL.withHeader());
			for (CSVRecord record : parser) {
				// Mother data
			    String motherFirstName = record.get("Childs_Particulars/Mother_Guardian_First_Name");
			    String motherLastName = record.get("Childs_Particulars/Mother_Guardian_Last_Name");
			    String motherNRC = record.get("Childs_Particulars/Mother_Guardian_NRC");
			    
			    
				// Child data
			    String childCardNumber = record.get("Childs_Particulars/Child_Register_Card_Number");
			    String firstName = record.get("Childs_Particulars/First_Name");
			    String lastName = record.get("Childs_Particulars/Last_Name");
			    String gender = record.get("Childs_Particulars/Sex");
			    String birthDate = record.get("Childs_Particulars/Date_Birth");
			    String dateFirstSeen = record.get("Childs_Particulars/First_Health_Facility_Contact");
			    
			    // Address data
			    String startDate = record.get("today");
			    String endDate = record.get("today");
			    String homeFacility = record.get("Childs_Particulars/Home_Facility");
			    String placeOfBirth = record.get("Childs_Particulars/Place_Birth");
			    String birthFacilityName = record.get("Childs_Particulars/Birth_Facility_Name");
			    String residentialArea = record.get("Childs_Particulars/Residential_Area");
			    String residentialAreaOther = record.get("Childs_Particulars/Residential_Area_Other");
			    String residentialAddress = record.get("Childs_Particulars/Residential_Address");
			    String physicalLandmark = record.get("Childs_Particulars/Physical_Landmark");
			    String birthWeight = record.get("Childs_Particulars/Birth_Weight");
			    String chwName = record.get("Childs_Particulars/CHW_Name");
			    String chwPhoneNumber = record.get("Childs_Particulars/CHW_Phone_Number");
			    
			    String motherId = UUID.randomUUID().toString();
			    String childId = UUID.randomUUID().toString();
			    
			    DateTimeFormatter parseDate = DateTimeFormat.forPattern("yyyy-MM-dd");
			    DateTime dateOfBirth = parseDate.parseDateTime(birthDate);
			    
			    // Build address object
			    DateTime addressStartDate = parseDate.parseDateTime(startDate);
			    DateTime addressEndDate = parseDate.parseDateTime(endDate);
			    
			    Map<String, String> addressFields = new HashMap<>();
			    addressFields.put("Birth_Facility_Name", birthFacilityName);
			    addressFields.put("Residential_Area", residentialArea);
			    addressFields.put("Residential_Area_Other", residentialAreaOther);
			    addressFields.put("Residential_Address", residentialAddress);
			    addressFields.put("Physical_Landmark", physicalLandmark);
			    
			    Address address = new Address(placeOfBirth, addressStartDate, addressEndDate, addressFields, null, null, null, homeFacility, null);
			    ArrayList<Address> addressList = new ArrayList<Address>();
			    addressList.add(address);
			    
			    Client motherClient = new Client(motherId, motherFirstName, "", motherLastName, null, null, false, false, "Female", addressList, null, null);
			    // Create child record
			    Client childClient = new Client(childId, firstName, "", lastName, dateOfBirth, null, false, false, gender, addressList, null, null);
			    
			    childClient.addRelationship("mother", motherId);
			    
			    clientService.addClient(motherClient);
			    clientService.addClient(childClient);
			    
			    clientCount+=2;
			}
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
}
