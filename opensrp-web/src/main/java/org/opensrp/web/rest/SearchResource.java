package org.opensrp.web.rest;

import static org.opensrp.common.AllConstants.BaseEntity.LAST_UPDATE;
import static org.opensrp.common.AllConstants.Client.BIRTH_DATE;
import static org.opensrp.common.AllConstants.Client.GENDER;
import static org.opensrp.web.rest.RestUtils.getDateRangeFilter;
import static org.opensrp.web.rest.RestUtils.getStringFilter;

import java.text.ParseException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTime;
import org.opensrp.domain.Client;
import org.opensrp.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mysql.jdbc.StringUtils;

@Controller
@RequestMapping(value = "/rest/search")
public class SearchResource extends RestResource<Client>{
	private SearchService searchService;
	
	@Autowired
	public SearchResource(SearchService searchService) {
		this.searchService = searchService;
	}

	@Override
	public List<Client> search(HttpServletRequest request) throws ParseException {//TODO search should not call different url but only add params
		String nameLike = getStringFilter("name", request);
		String gender = getStringFilter(GENDER, request);
		DateTime[] birthdate = getDateRangeFilter(BIRTH_DATE, request);//TODO add ranges like fhir do http://hl7.org/fhir/search.html
		DateTime[] lastEdit = getDateRangeFilter(LAST_UPDATE, request);//TODO client by provider id
		//TODO lookinto Swagger https://slack-files.com/files-pri-safe/T0EPSEJE9-F0TBD0N77/integratingswagger.pdf?c=1458211183-179d2bfd2e974585c5038fba15a86bf83097810a
		
		String attributes = getStringFilter("attribute", request);
		String attributeType = StringUtils.isEmptyOrWhitespaceOnly(attributes)?null:attributes.split(":",-1)[0];
		String attributeValue = StringUtils.isEmptyOrWhitespaceOnly(attributes)?null:attributes.split(":",-1)[1];
		
		return searchService.searchClient(nameLike, gender, birthdate==null?null:birthdate[0], birthdate==null?null:birthdate[1], 
				 attributeType, attributeValue, lastEdit==null?null:lastEdit[0], lastEdit==null?null:lastEdit[1]);
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
