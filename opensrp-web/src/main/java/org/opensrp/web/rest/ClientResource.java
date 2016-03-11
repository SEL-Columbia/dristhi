package org.opensrp.web.rest;

import static org.opensrp.common.AllConstants.BaseEntity.ADDRESS_TYPE;
import static org.opensrp.common.AllConstants.BaseEntity.BASE_ENTITY_ID;
import static org.opensrp.common.AllConstants.BaseEntity.CITY_VILLAGE;
import static org.opensrp.common.AllConstants.BaseEntity.COUNTRY;
import static org.opensrp.common.AllConstants.BaseEntity.COUNTY_DISTRICT;
import static org.opensrp.common.AllConstants.BaseEntity.STATE_PROVINCE;
import static org.opensrp.common.AllConstants.BaseEntity.SUB_DISTRICT;
import static org.opensrp.common.AllConstants.BaseEntity.SUB_TOWN;
import static org.opensrp.common.AllConstants.BaseEntity.TOWN;
import static org.opensrp.common.AllConstants.Client.BIRTH_DATE;
import static org.opensrp.common.AllConstants.Client.DEATH_DATE;
import static org.opensrp.common.AllConstants.Client.FIRST_NAME;
import static org.opensrp.common.AllConstants.Client.GENDER;
import static org.opensrp.web.rest.RestUtils.getDateFilter;
import static org.opensrp.web.rest.RestUtils.getStringFilter;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTime;
import org.opensrp.domain.Client;
import org.opensrp.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mysql.jdbc.StringUtils;

@Controller
@RequestMapping(value = "/rest/client")
public class ClientResource extends RestResource<Client>{
	private ClientService clientService;
	
	@Autowired
	public ClientResource(ClientService clientService) {
		this.clientService = clientService;
	}

	@Override
	public Client getByUniqueId(String uniqueId) {
		return clientService.find(uniqueId);
	}
	
	@Override
    public Client create(Client o) {
		return clientService.addClient(o);
	}

	@Override
	public List<String> requiredProperties() {
		List<String> p = new ArrayList<>();
		p.add(FIRST_NAME);
		p.add(BIRTH_DATE);
		p.add(GENDER);
		p.add(BASE_ENTITY_ID);
		return p;
	}

	@Override
	public Client update(Client entity) {
		return clientService.mergeClient(entity);
	}
	
	@Override
	public List<Client> search(HttpServletRequest request) throws ParseException {
		String nameLike = getStringFilter("name", request);
		String gender = getStringFilter(GENDER, request);
		DateTime birthdate = getDateFilter(BIRTH_DATE, request);
		DateTime deathdate = getDateFilter(DEATH_DATE, request);
		String addressType = getStringFilter(ADDRESS_TYPE, request);
		String country = getStringFilter(COUNTRY, request);
		String stateProvince = getStringFilter(STATE_PROVINCE, request);
		String cityVillage = getStringFilter(CITY_VILLAGE, request);
		String countyDistrict = getStringFilter(COUNTY_DISTRICT, request);
		String subDistrict = getStringFilter(SUB_DISTRICT, request);
		String town = getStringFilter(TOWN, request);
		String subTown = getStringFilter(SUB_TOWN, request);
		
		String attributes = getStringFilter("attributes", request);
		String attributeType = StringUtils.isEmptyOrWhitespaceOnly(attributes)?null:attributes.split(":",-1)[0];
		String attributeValue = StringUtils.isEmptyOrWhitespaceOnly(attributes)?null:attributes.split(":",-1)[1];
		
		return clientService.findByCriteria(nameLike, gender, birthdate, deathdate, attributeType, attributeValue,
				addressType, country, stateProvince, cityVillage, countyDistrict, subDistrict, town, subTown);
	}

}
