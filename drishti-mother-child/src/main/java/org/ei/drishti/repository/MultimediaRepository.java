/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ei.drishti.repository;

import java.util.List;
import org.ei.drishti.common.AllConstants;
import org.ei.drishti.domain.Multimedia;
import org.ei.drishti.service.MultimediaService;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;
import org.motechproject.dao.MotechBaseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

/**
 *
 * @author administrator
 */
@Repository
public class MultimediaRepository extends MotechBaseRepository<Multimedia> {
    
     private static Logger logger = LoggerFactory
			.getLogger(MultimediaRepository.class.toString());

	@Autowired
	protected MultimediaRepository(
			@Qualifier(AllConstants.DRISHTI_DATABASE_CONNECTOR) CouchDbConnector db) {
		super(Multimedia.class, db);
	}

	@GenerateView
	public Multimedia findByCaseId(String entityId) {
		List<Multimedia> files = queryView("by_caseId", entityId);
		if (files == null || files.isEmpty()) {
			return null;
		}
		return files.get(0);
	}

	@View(name = "all_multimedia_files", map = "function(doc) { if (doc.type === 'Multimedia' && doc.providerId) { emit(doc.providerId, doc); } }")
	public List<Multimedia> all(String providerId) {
		return db.queryView(createQuery("all_multimedia_files").key(providerId)
				.includeDocs(true), Multimedia.class);
	}
    
}
