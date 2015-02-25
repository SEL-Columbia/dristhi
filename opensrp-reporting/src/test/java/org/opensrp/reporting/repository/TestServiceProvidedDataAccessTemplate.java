package org.opensrp.reporting.repository;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "prototype")
public class TestServiceProvidedDataAccessTemplate extends HibernateTemplate {
    @Autowired
    public TestServiceProvidedDataAccessTemplate(@Qualifier(value = "serviceProvidedSessionFactory") SessionFactory sessionFactory) {
        super(sessionFactory);
        setAllowCreate(true);
    }
}
