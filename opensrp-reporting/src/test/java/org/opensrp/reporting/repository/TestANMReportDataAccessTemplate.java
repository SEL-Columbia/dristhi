package org.opensrp.reporting.repository;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "prototype")
public class TestANMReportDataAccessTemplate extends HibernateTemplate {
    @Autowired
    public TestANMReportDataAccessTemplate(@Qualifier(value = "anmReportsSessionFactory") SessionFactory sessionFactory) {
        super(sessionFactory);
        setAllowCreate(true);
    }
}
