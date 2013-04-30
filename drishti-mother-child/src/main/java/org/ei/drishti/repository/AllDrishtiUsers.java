package org.ei.drishti.repository;

import org.ei.drishti.domain.DrishtiUser;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllDrishtiUsers extends MotechBaseRepository<DrishtiUser> {
    @Autowired
    protected AllDrishtiUsers(@Qualifier("drishtiDatabaseConnector") CouchDbConnector db) {
        super(DrishtiUser.class, db);
    }

    @GenerateView()
    public DrishtiUser findByUsername(String username) {
        if(username == null) {
            return null;
        }
        List<DrishtiUser> users = queryView("by_username", username);
        if (users == null || users.isEmpty()) {
            return null;
        }
        return users.get(0);
    }
}
