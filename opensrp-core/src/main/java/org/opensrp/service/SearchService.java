package org.opensrp.service;

import java.util.List;

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

	public List<Client> searchClient(String nameLike, String gender,
			DateTime birthdateFrom, DateTime birthdateTo, String attributeType,
			String attributeValue, DateTime lastEditFrom,
			DateTime lastEditTo) {
		return search.findByCriteria(nameLike, gender, birthdateFrom,
				birthdateTo, attributeType, attributeValue, lastEditFrom,
				lastEditTo);
	}

}
