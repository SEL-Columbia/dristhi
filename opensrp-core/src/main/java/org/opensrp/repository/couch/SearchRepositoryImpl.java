package org.opensrp.repository.couch;

import java.util.List;

import org.ektorp.CouchDbConnector;
import org.motechproject.dao.MotechBaseRepository;
import org.opensrp.common.AllConstants;
import org.opensrp.domain.Client;
import org.opensrp.domain.Search;
import org.opensrp.repository.SearchRepository;
import org.opensrp.repository.lucene.LuceneSearchRepository;
import org.opensrp.search.ClientSearchBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository("couchSearchRepository")
@Primary
public class SearchRepositoryImpl extends MotechBaseRepository<Search> implements SearchRepository {
	
	private LuceneSearchRepository sr;
	
	@Autowired
	protected SearchRepositoryImpl(@Qualifier(AllConstants.OPENSRP_DATABASE_CONNECTOR) CouchDbConnector db,
	    LuceneSearchRepository sr) {
		super(Search.class, db);
		this.sr = sr;
	}
	
	public List<Client> findByCriteria(ClientSearchBean clientSearchBean, String firstName, String middleName,
	                                   String lastName, Integer limit) {
		return sr.getByCriteria(clientSearchBean, firstName, middleName, lastName, limit);
	}
	
}
