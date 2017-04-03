package org.opensrp.service;

import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.opensrp.domain.Client;
import org.opensrp.repository.SearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SearchService {
	
	private final SearchRepository search;
	
	@Autowired
	public SearchService(SearchRepository search) {
		this.search = search;
	}
	
	public List<Client> searchClient(String firstName, String middleName, String lastName, String gender,
	                                 Map<String, String> identifiers, Map<String, String> attributes,
	                                 DateTime birthdateFrom, DateTime birthdateTo, DateTime lastEditFrom, DateTime lastEditTo) {
		return search.findByCriteria(firstName, middleName, lastName, gender, identifiers, attributes, birthdateFrom,
		    birthdateTo, lastEditFrom, lastEditTo);
	}
	
}
