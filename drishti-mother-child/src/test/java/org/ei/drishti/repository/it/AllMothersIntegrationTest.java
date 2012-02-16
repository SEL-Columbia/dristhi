package org.ei.drishti.repository.it;

import org.ei.drishti.domain.Mother;
import org.ei.drishti.repository.AllMothers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-applicationContext-drishti.xml")
public class AllMothersIntegrationTest {
    @Autowired
    private AllMothers mothers;

    @Before
    public void setUp() throws Exception {
        mothers.removeAll();
    }

    @Test
    public void shouldRegisterAMother() {
        Mother mother = new Mother("CASE-1", "THAAYI-CARD-1", "Theresa");

        mothers.register(mother);

        List<Mother> allTheMothers = mothers.getAll();
        assertThat(allTheMothers.size(), is(1));

        Mother motherFromDB = allTheMothers.get(0);
        assertThat(motherFromDB, is(new Mother("CASE-1", "THAAYI-CARD-1", "Theresa")));
        assertThat(motherFromDB.name(), is("Theresa"));
    }

    @Test
    public void shouldFindARegisteredMotherByThaayiCardNumber() {
        String caseId = "CASE-1";
        Mother motherToRegister = new Mother(caseId, "THAAYI-CARD-1", "Theresa");
        mothers.register(motherToRegister);

        Mother mother = mothers.findByCaseId(caseId);

        assertThat(mother, is(new Mother(caseId, "THAAYI-CARD-1", "Theresa")));
        assertThat(mother.name(), is("Theresa"));
    }
}
