package org.ei.commcare.api.repository.it;

import org.ei.commcare.api.domain.ExportToken;
import org.ei.commcare.api.repository.AllExportTokens;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-commcare-api.xml")
public class AllExportTokensIntegrationTest {
    @Autowired
    AllExportTokens allExportTokens;

    @Before
    public void setUp() throws Exception {
        allExportTokens.removeAll();
    }

    @Test
    public void shouldCreateATokenWhenATokenDoesNotExist() {
        allExportTokens.updateToken("nameSpaceForTokenWhichDoesNotExistYet", "SOME-DATA-HERE");

        ExportToken token = allExportTokens.findByNameSpace("nameSpaceForTokenWhichDoesNotExistYet");
        assertThat(token.value(), is("SOME-DATA-HERE"));
        assertThat(allExportTokens.get(token.getId()).value(), is("SOME-DATA-HERE"));
    }

    @Test
    public void shouldUpdateATokenWhichExists() {
        String nameSpace = "nameSpaceForToken";

        allExportTokens.updateToken(nameSpace, "SOME-VALUE-HERE");
        assertThat(allExportTokens.findByNameSpace(nameSpace).value(), is("SOME-VALUE-HERE"));

        allExportTokens.updateToken(nameSpace, "NEW-VALUE-HERE");
        assertThat(allExportTokens.findByNameSpace(nameSpace).value(), is("NEW-VALUE-HERE"));
    }
}
