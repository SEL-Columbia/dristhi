package org.ei.drishti.repository.it;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.ei.drishti.domain.Mother;
import org.ei.drishti.repository.AllMothers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
        Mother mother = new Mother("CASE-1", "THAAYI-CARD-1", "Theresa").withAnm("ANM ID 1", "12345").withLMP(DateUtil.tomorrow()).withECNumber("EC Number 1");

        mothers.register(mother);

        List<Mother> allTheMothers = mothers.getAll();
        assertThat(allTheMothers.size(), is(1));

        Mother motherFromDB = allTheMothers.get(0);
        assertThat(motherFromDB, is(motherWithSameFieldsAs(mother)));
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

    @Test
    public void shouldSayThatAMotherDoesNotExistWhenTheMotherIsNotInTheDB() {
        String caseId = "CASE-1";
        Mother motherToRegister = new Mother(caseId, "THAAYI-CARD-1", "Theresa");
        mothers.register(motherToRegister);

        assertTrue(mothers.motherExists("CASE-1"));
        assertFalse(mothers.motherExists("CASE-NOT-KNOWN"));
    }

    private ArgumentMatcher<Mother> motherWithSameFieldsAs(final Mother mother) {
        return new ArgumentMatcher<Mother>() {
            @Override
            public boolean matches(Object o) {
                return EqualsBuilder.reflectionEquals(mother, o);
            }
        };
    }

}
