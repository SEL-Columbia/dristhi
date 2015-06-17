package org.ei.drishti.reporting.repository;




import org.ei.drishti.reporting.domain.Poc_table;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
@Repository
public class AllPoc_tableRepository {
	private static Logger logger = LoggerFactory
			.getLogger(AllPoc_tableRepository.class.toString());
	
	private  DataAccessTemplate dataAccessTemplate;

	protected AllPoc_tableRepository() {
    }

    @Autowired
    public AllPoc_tableRepository(@Qualifier("serviceProvidedDataAccessTemplate") DataAccessTemplate dataAccessTemplate) {
        this.dataAccessTemplate = dataAccessTemplate;
    }

    @Transactional()
    public  void insertAll(Poc_table  pt) {
    	logger.info("poc_table saved");
    	
        dataAccessTemplate.saveOrUpdate(pt);
    }

}
