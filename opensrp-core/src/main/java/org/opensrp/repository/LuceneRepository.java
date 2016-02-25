package org.opensrp.repository;

import org.opensrp.domain.Client;

import com.github.ldriscoll.ektorplucene.CouchDbRepositorySupportWithLucene;
import com.github.ldriscoll.ektorplucene.LuceneAwareCouchDbConnector;

public class LuceneRepository extends CouchDbRepositorySupportWithLucene<Client>{

	protected LuceneRepository(Class<Client> type, LuceneAwareCouchDbConnector db) {
		super(type, db);
	}

	
	
}
