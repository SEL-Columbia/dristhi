package org.opensrp.repository.couch;

import java.util.List;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;
import org.motechproject.dao.MotechBaseRepository;
import org.opensrp.common.AllConstants;
import org.opensrp.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllUsers extends MotechBaseRepository<User> {

	@Autowired
	protected AllUsers(
			@Qualifier(AllConstants.OPENSRP_DATABASE_CONNECTOR) CouchDbConnector db) {
		super(User.class, db);
	}

	@GenerateView
	public User findByBaseEntityId(String baseEntityId) {
		List<User> users = queryView("by_baseEntityId", baseEntityId);
		if (users == null || users.isEmpty()) {
			return null;
		}
		return users.get(0);
	}

	public boolean exists(String caseId) {
		return findByBaseEntityId(caseId) != null;
	}
	
	@View(name = "all_users", map = "function(doc) { if (doc.type === 'User') { emit(doc.baseEntityId); } }")
	public List<User> findAllUsers() {
		return db.queryView(createQuery("all_users").includeDocs(true),
				User.class);
	}

	@View(name = "all_users_by_CaseIDs", map = "function(doc) { if (doc.type === 'User' && doc.baseEntityId) { emit(doc.baseEntityId); } }")
	public List<User> findAllByUserByIds(List<String> Ids) {
		return db.queryView(createQuery("all_users_by_CaseIDs").keys(Ids)
				.includeDocs(true), User.class);
	}

}
