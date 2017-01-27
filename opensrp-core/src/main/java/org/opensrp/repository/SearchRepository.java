package org.opensrp.repository;

import java.util.List;

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
	protected SearchRepository(
			@Qualifier(AllConstants.OPENSRP_DATABASE_CONNECTOR) CouchDbConnector db,
			LuceneSearchRepository sr) {
		super(Search.class, db);
		this.sr = sr;
	}

	public List<Client> findByCriteria(String nameLike, String gender,
			DateTime birthdateFrom, DateTime birthdateTo, String attributeType,
			String attributeValue, DateTime lastEditFrom, DateTime lastEditTo) {
		return sr.getByCriteria(nameLike, gender, birthdateFrom, birthdateTo,
				attributeType, attributeValue, lastEditFrom, lastEditTo);
	}

}
