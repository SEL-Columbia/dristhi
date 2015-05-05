package org.opensrp.reporting.repository.it;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensrp.reporting.repository.TestANMReportDataAccessTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/applicationContext-opensrp-reporting-test.xml")
public class ANMReportsIntegrationTestBase {
    @Autowired
    protected TestANMReportDataAccessTemplate template;
    
    @Test
    public void test(){
    	
    }
}
