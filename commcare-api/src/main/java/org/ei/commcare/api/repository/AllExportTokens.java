package org.ei.commcare.api.repository;

import org.ei.commcare.api.domain.ExportToken;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AllExportTokens extends MotechBaseRepository<ExportToken> {
    @Autowired
    public AllExportTokens(@Qualifier("commCareDatabaseConnector") CouchDbConnector db) {
        super(ExportToken.class, db);
    }

    @GenerateView
    public ExportToken findByNameSpace(String nameSpace) {
        List<ExportToken> tokens = queryView("by_nameSpace", nameSpace);
        if (tokens == null || tokens.isEmpty()) {
            return new ExportToken(nameSpace, "");
        }
        return tokens.get(0);
    }

    public void updateToken(String nameSpace, String tokenData) {
        addOrReplace(new ExportToken(nameSpace, tokenData), "nameSpace", nameSpace);
    }
}
