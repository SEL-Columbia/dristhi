package org.ei.drishti.reporting.repository.it;

import org.ei.drishti.reporting.repository.TestServiceProvidedDataAccessTemplate;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/applicationContext-drishti-reporting-test.xml")
public class ServicesProvidedIntegrationTestBase {
    @Autowired
    protected TestServiceProvidedDataAccessTemplate template;
}
