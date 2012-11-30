package org.ei.drishti.reporting.repository.it;

import org.ei.drishti.reporting.domain.*;
import org.ei.drishti.reporting.repository.TestANMReportDataAccessTemplate;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/applicationContext-drishti-reporting.xml")
public class ANMReportsRepositoryIntegrationTestBase {
    @Autowired
    protected TestANMReportDataAccessTemplate template;

    @Before
    public void setUp() throws Exception {
        template.deleteAll(template.loadAll(ANMReportData.class));
        template.deleteAll(template.loadAll(AnnualTarget.class));
        template.deleteAll(template.loadAll(ANM.class));
        template.deleteAll(template.loadAll(Dates.class));
        template.deleteAll(template.loadAll(Indicator.class));
    }
}
