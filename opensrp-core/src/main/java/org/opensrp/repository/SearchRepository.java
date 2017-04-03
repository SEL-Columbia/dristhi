package org.opensrp.repository;

import java.util.List;
import java.util.Map;

import org.ektorp.CouchDbConnector;
import org.joda.time.DateTime;
import org.motechproject.dao.MotechBaseRepository;
import org.opensrp.common.AllConstants;
import org.opensrp.domain.Client;
import org.opensrp.domain.Search;
import org.opensrp.repository.lucene.LuceneSearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class SearchRepository extends MotechBaseRepository<Search> {
	
	private LuceneSearchRepository sr;
	
	@Autowired
	protected SearchRepository(@Qualifier(AllConstants.OPENSRP_DATABASE_CONNECTOR) CouchDbConnector db,
	    LuceneSearchRepository sr) {
		super(Search.class, db);
		this.sr = sr;
	}
	
	public List<Client> findByCriteria(String firstName, String middleName, String lastName, String gender,
	                                   Map<String, String> identifiers, Map<String, String> attributes,
	                                   DateTime birthdateFrom, DateTime birthdateTo, DateTime lastEditFrom,
	                                   DateTime lastEditTo) {
		return sr.getByCriteria(firstName, middleName, lastName, gender, identifiers, attributes, birthdateFrom,
		    birthdateTo, lastEditFrom, lastEditTo);
	}
	
}
