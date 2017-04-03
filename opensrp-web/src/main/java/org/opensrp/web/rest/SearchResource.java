package org.opensrp.web.rest;

import static org.opensrp.common.AllConstants.BaseEntity.LAST_UPDATE;
import static org.opensrp.common.AllConstants.Client.BIRTH_DATE;
import static org.opensrp.common.AllConstants.Client.FIRST_NAME;
import static org.opensrp.common.AllConstants.Client.GENDER;
import static org.opensrp.common.AllConstants.Client.LAST_NAME;
import static org.opensrp.common.AllConstants.Client.MIDDLE_NAME;
import static org.opensrp.web.rest.RestUtils.getDateRangeFilter;
import static org.opensrp.web.rest.RestUtils.getStringFilter;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTime;
import org.opensrp.common.AllConstants.BaseEntity;
import org.opensrp.domain.Client;
import org.opensrp.service.ClientService;
import org.opensrp.service.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.mysql.jdbc.StringUtils;

@Controller
@RequestMapping(value = "/rest/search")
public class SearchResource extends RestResource<Client> {
	
	private static Logger logger = LoggerFactory.getLogger(SearchResource.class.toString());
	
	private SearchService searchService;
	
	private ClientService clientService;
	
	@Autowired
	public SearchResource(SearchService searchService, ClientService clientService) {
		this.searchService = searchService;
		this.clientService = clientService;
	}
	
	@Override
	public List<Client> search(HttpServletRequest request) throws ParseException {//TODO search should not call different url but only add params
		String firstName = getStringFilter(FIRST_NAME, request);
		String middleName = getStringFilter(MIDDLE_NAME, request);
		String lastName = getStringFilter(LAST_NAME, request);
		String gender = getStringFilter(GENDER, request);
		DateTime[] birthdate = getDateRangeFilter(BIRTH_DATE, request);//TODO add ranges like fhir do http://hl7.org/fhir/search.html
		DateTime[] lastEdit = getDateRangeFilter(LAST_UPDATE, request);//TODO client by provider id
		//TODO lookinto Swagger https://slack-files.com/files-pri-safe/T0EPSEJE9-F0TBD0N77/integratingswagger.pdf?c=1458211183-179d2bfd2e974585c5038fba15a86bf83097810a
		
		Map<String, String> attributeMap = null;
		String attributes = getStringFilter("attribute", request);
		if (StringUtils.isEmptyOrWhitespaceOnly(attributes)) {
			String attributeType = StringUtils.isEmptyOrWhitespaceOnly(attributes) ? null : attributes.split(":", -1)[0];
			String attributeValue = StringUtils.isEmptyOrWhitespaceOnly(attributes) ? null : attributes.split(":", -1)[1];
			
			attributeMap = new HashMap<String, String>();
			attributeMap.put(attributeType, attributeValue);
		}
		
		return searchService.searchClient(firstName, middleName, lastName, gender, null, attributeMap,
		    birthdate == null ? null : birthdate[0], birthdate == null ? null : birthdate[1], lastEdit == null ? null
		            : lastEdit[0], lastEdit == null ? null : lastEdit[1]);
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/path")
	@ResponseBody
	private ResponseEntity<String> searchPathBy(HttpServletRequest request) throws ParseException {
		
		Map<String, Object> response = new HashMap<String, Object>();
		try {
			String ZEIR_ID = "zeir_id";
			
			String FIRST_NAME = "first_name";
			String MIDDLE_NAME = "middle_name";
			String LAST_NAME = "last_name";
			String BIRTH_DATE = "birth_date";
			
			String INACTIVE = "inactive";
			String LOST_TO_FOLLOW_UP = "lost_to_follow_up";
			
			String zeirId = getStringFilter(ZEIR_ID, request);
			String firstName = getStringFilter(FIRST_NAME, request);
			String middleName = getStringFilter(MIDDLE_NAME, request);
			String lastName = getStringFilter(LAST_NAME, request);
			String gender = getStringFilter(GENDER, request);
			String inActive = getStringFilter(INACTIVE, request);
			String lostToFollowUp = getStringFilter(LOST_TO_FOLLOW_UP, request);
			
			DateTime[] birthdate = getDateRangeFilter(BIRTH_DATE, request);//TODO add ranges like fhir do http://hl7.org/fhir/search.html
			DateTime[] lastEdit = getDateRangeFilter(LAST_UPDATE, request);//TODO client by provider id
			//TODO lookinto Swagger https://slack-files.com/files-pri-safe/T0EPSEJE9-F0TBD0N77/integratingswagger.pdf?c=1458211183-179d2bfd2e974585c5038fba15a86bf83097810a
			
			String ZEIR_ID_KEY = "ZEIR_ID";
			Map<String, String> identifiers = null;
			if (!StringUtils.isEmptyOrWhitespaceOnly(zeirId)) {
				identifiers = new HashMap<String, String>();
				identifiers.put(ZEIR_ID_KEY, zeirId);
			}
			
			String INACTIVE_KEY = "Inactive";
			String LOST_TO_FOLLOW_UP_KEY = "Lost_to_Follow_Up";
			Map<String, String> attributes = null;
			if (!StringUtils.isEmptyOrWhitespaceOnly(inActive) || !StringUtils.isEmptyOrWhitespaceOnly(lostToFollowUp)) {
				attributes = new HashMap<String, String>();
				if (!StringUtils.isEmptyOrWhitespaceOnly(inActive)) {
					attributes.put(INACTIVE_KEY, inActive);
				}
				
				if (!StringUtils.isEmptyOrWhitespaceOnly(lostToFollowUp)) {
					attributes.put(LOST_TO_FOLLOW_UP_KEY, lostToFollowUp);
				}
			}
			
			List<Client> children = new ArrayList<Client>();
			
			if (!StringUtils.isEmptyOrWhitespaceOnly(firstName) || !StringUtils.isEmptyOrWhitespaceOnly(middleName)
			        || !StringUtils.isEmptyOrWhitespaceOnly(lastName) || !StringUtils.isEmptyOrWhitespaceOnly(gender)
			        || identifiers != null || attributes != null || birthdate != null || lastEdit != null) {
				
				children = searchService.searchClient(firstName, middleName, lastName, gender, identifiers, attributes,
				    birthdate == null ? null : birthdate[0], birthdate == null ? null : birthdate[1],
				    lastEdit == null ? null : lastEdit[0], lastEdit == null ? null : lastEdit[1]);
				
			}
			
			// Mother
			String MOTHER_GUARDIAN_FIRST_NAME = "mother_first_name";
			String MOTHER_GUARDIAN_LAST_NAME = "mother_last_name";
			String MOTHER_GUARDIAN_NRC_NUMBER = "mother_nrc_number";
			//String MOTHER_GUARDIAN_PHONE_NUMBER = "mother_contact_phone_number";
			
			String motherFirstName = getStringFilter(MOTHER_GUARDIAN_FIRST_NAME, request);
			String motherLastName = getStringFilter(MOTHER_GUARDIAN_LAST_NAME, request);
			String motherGuardianNrc = getStringFilter(MOTHER_GUARDIAN_NRC_NUMBER, request);
			
			String NRC_NUMBER_KEY = "NRC_Number";
			Map<String, String> motherAttributes = null;
			if (!StringUtils.isEmptyOrWhitespaceOnly(motherGuardianNrc)) {
				motherAttributes = new HashMap<String, String>();
				motherAttributes.put(NRC_NUMBER_KEY, motherGuardianNrc);
			}
			
			List<Client> mothers = new ArrayList<Client>();
			if (!StringUtils.isEmptyOrWhitespaceOnly(motherFirstName)
			        || !StringUtils.isEmptyOrWhitespaceOnly(motherLastName)
			        || !StringUtils.isEmptyOrWhitespaceOnly(motherGuardianNrc)) {
				
				mothers = searchService.searchClient(motherFirstName, null, motherLastName, null, null, motherAttributes,
				    null, null, lastEdit == null ? null : lastEdit[0], lastEdit == null ? null : lastEdit[1]);
				
			}
			
			List<Client> linkedMothers = new ArrayList<Client>();
			
			String RELATIONSHIP_KEY = "mother";
			if (!children.isEmpty()) {
				List<String> clientIds = new ArrayList<String>();
				for (Client c : children) {
					List<String> rList = c.getRelationships(RELATIONSHIP_KEY);
					if (!rList.isEmpty()) {
						clientIds.add(rList.get(0));
					}
				}
				
				linkedMothers = clientService.findByFieldValue(BaseEntity.BASE_ENTITY_ID, clientIds);
				
			}
			
			List<Client> linkedChildren = new ArrayList<Client>();
			
			String M_ZEIR_ID = "M_ZEIR_ID";
			if (!mothers.isEmpty()) {
				List<String> cIndentifers = new ArrayList<String>();
				for (Client m : mothers) {
					String identifier = m.getIdentifier(M_ZEIR_ID);
					if (!StringUtils.isEmptyOrWhitespaceOnly(identifier)) {
						String[] arr = identifier.split("_");
						if (arr != null && arr.length == 2 && arr[1].contains(RELATIONSHIP_KEY)) {
							cIndentifers.add(arr[0]);
						}
					}
				}
				
				linkedChildren = clientService.findByFieldValue(ZEIR_ID_KEY, cIndentifers);
				
			}
			
			for (Client linkedChild : linkedChildren) {
				if (!children.contains(linkedChild)) {
					children.add(linkedChild);
				}
			}
			
			for (Client linkedMother : linkedMothers) {
				if (!mothers.contains(linkedMother)) {
					mothers.add(linkedMother);
				}
			}
			
			response.put("children", children);
			response.put("mothers", mothers);
			
			return new ResponseEntity<>(new Gson().toJson(response), HttpStatus.OK);
		}
		catch (Exception e) {
			response.put("msg", "Error occurred");
			logger.error("", e);
			return new ResponseEntity<>(new Gson().toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Override
	public List<Client> filter(String query) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Client getByUniqueId(String uniqueId) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<String> requiredProperties() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Client create(Client entity) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Client update(Client entity) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
