package org.ei.drishti.form.repository;

import java.util.Iterator;
import java.util.List;
import org.ei.drishti.form.domain.Poc_table;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class AllPoc_tableRepository {
//	private static Logger logger = LoggerFactory
//			.getLogger(AllPoc_tableRepository.class.toString());
//
//	private SessionFactory sessionFactory;
//
//	protected AllPoc_tableRepository() {
//	}
//	//@Qualifier("serviceProvidedDataFormTemplate")
//	@Autowired
//	public AllPoc_tableRepository(
//			 SessionFactory sessionfactory) {
//		this.sessionFactory = sessionFactory;
//
//	}
//
//	@Transactional("service_provided")
//	public void insertAll(String anmid, String visittype, String visitentityid,
//			String entityidEC) {
//	
//		Session session = sessionFactory.openSession();
//
//		Query qry = session
//				.createQuery("select d.name from PHC d where d.id=(select da.phc from SP_ANM da where da.anmidentifier = '"
//						+ anmid + "')");
//		List phc = qry.list();
//
//		Poc_table pt = new Poc_table();
//
//		pt.setAnmid(anmid);
//		pt.setEntityidEC(entityidEC);
//		pt.setVisitentityid(visitentityid);
//		pt.setVisittype(visittype);
//		pt.setLevel("");
//		pt.setServerversion("");
//		pt.setClientversion("");
//		pt.setPhc(phc);
//		session.save(pt);
//		logger.info("poc_table saved");
//
//		session.close();
//		sessionFactory.close();
//	}

}
