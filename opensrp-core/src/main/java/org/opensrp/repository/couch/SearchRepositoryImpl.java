package org.opensrp.repository.couch;

import java.util.List;
import java.util.Map;

import org.ektorp.CouchDbConnector;
import org.joda.time.DateTime;
import org.motechproject.dao.MotechBaseRepository;
import org.opensrp.common.AllConstants;
import org.opensrp.domain.Client;
import org.opensrp.domain.Search;
import org.opensrp.repository.SearchRepository;
import org.opensrp.repository.lucene.LuceneSearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class SearchRepositoryImpl extends MotechBaseRepository<Search> implements SearchRepository {
	
	private LuceneSearchRepository sr;
	
	@Autowired
	protected SearchRepositoryImpl(@Qualifier(AllConstants.OPENSRP_DATABASE_CONNECTOR) CouchDbConnector db,
	    LuceneSearchRepository sr) {
		super(Search.class, db);
		this.sr = sr;
	}
	
	public List<Client> findByCriteria(String nameLike, String firstName, String middleName, String lastName, String gender,
	                                   Map<String, String> identifiers, Map<String, String> attributes,
	                                   DateTime birthdateFrom, DateTime birthdateTo, DateTime lastEditFrom,
	                                   DateTime lastEditTo, Integer limit) {
		return sr.getByCriteria(nameLike, firstName, middleName, lastName, gender, identifiers, attributes, birthdateFrom,
		    birthdateTo, lastEditFrom, lastEditTo, limit);
	}
	
}
