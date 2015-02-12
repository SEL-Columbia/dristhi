package org.ei.drishti.repository;

import java.util.List;

import org.ei.drishti.common.AllConstants;
import org.ei.drishti.domain.Child;
import org.ei.drishti.domain.Person;
import org.ektorp.CouchDbConnector;
import org.motechproject.dao.MotechBaseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

@Repository
public class AllPersons extends MotechBaseRepository<Person> {
    private static Logger logger = LoggerFactory.getLogger(AllEligibleCouples.class);
	@Autowired
    public AllPersons(@Qualifier(AllConstants.DRISHTI_DATABASE_CONNECTOR) CouchDbConnector db) {
        super(Person.class, db);
    }
	public Person findByCaseId(String caseId) {
		 List<Person> persons = queryView("by_caseId", caseId);
	        if (persons == null || persons.isEmpty()) {
	            return null;
	        }
	        return persons.get(0);
	}

}
