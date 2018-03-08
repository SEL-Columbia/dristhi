package org.opensrp.repository.couch;

import java.util.List;

import org.ektorp.CouchDbConnector;
import org.ektorp.UpdateConflictException;
import org.ektorp.support.GenerateView;
import org.ektorp.util.Assert;
import org.ektorp.util.Documents;
import org.motechproject.dao.MotechBaseRepository;
import org.opensrp.common.AllConstants;
import org.opensrp.domain.AppStateToken;
import org.opensrp.repository.AppStateTokensRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllAppStateTokens extends MotechBaseRepository<AppStateToken> implements AppStateTokensRepository {
	
	private CouchDbConnector db;
	
    @Autowired
    protected AllAppStateTokens(@Qualifier(AllConstants.OPENSRP_DATABASE_CONNECTOR) CouchDbConnector db) {
        super(AppStateToken.class, db);
        this.db=db;
    }
    
    @GenerateView
	public List<AppStateToken> findByName(String name) {
    	return queryView("by_name", name);
	}
    @GenerateView
	public List<AppStateToken> findByName(CouchDbConnector db,String name) {
    	return db.queryView(createQuery("by_name")
				.includeDocs(true)
				.key(name),
				AppStateToken.class);
	}
    /**
	 * @throws UpdateConflictException if there was an update conflict.
	 */
	public void update(AppStateToken entity) {
		Assert.notNull(entity, "entity may not be null");
		db.update(entity);
	}
	/**
	 * @throws UpdateConflictException if there was an update conflict.
	 */
	public void add(AppStateToken entity) {
		add(db,entity);
	}
	
	/**
	 * @throws UpdateConflictException if there was an update conflict.
	 */
	public void add(CouchDbConnector db,AppStateToken entity) {
		Assert.notNull(entity, "entity may not be null");
		Assert.isTrue(Documents.isNew(entity), "entity must be new");
		db.create(entity);
	}

}
