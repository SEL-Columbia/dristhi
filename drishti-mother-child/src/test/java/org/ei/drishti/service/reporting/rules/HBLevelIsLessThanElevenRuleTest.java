package org.ei.drishti.service.reporting.rules;

import org.ei.drishti.util.EasyMap;
import org.ei.drishti.util.SafeMap;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.ei.drishti.util.EasyMap.create;
import static org.mockito.MockitoAnnotations.initMocks;

public class HBLevelIsLessThanElevenRuleTest {

    private HBLevelIsLessThanElevenRule hbLevelIsLessThanElevenRule;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        hbLevelIsLessThanElevenRule = new HBLevelIsLessThanElevenRule();
    }

    @Test
    public void shouldReturnTrueWhenHBLevelIsLessThanEleven() throws Exception {
        EasyMap easyMap = create("serviceProvidedPlace", "sub_center").put("hbLevel","10.3");
        boolean rulePassed = hbLevelIsLessThanElevenRule.apply(new SafeMap(easyMap.map()));

        assertTrue(rulePassed);
    }

    @Test
    public void shouldReturnFalseWhenHBLevelIsMoreThanEleven() throws Exception {
        EasyMap easyMap = create("serviceProvidedPlace", "sub_center").put("hbLevel", "11.3");
        boolean rulePassed = hbLevelIsLessThanElevenRule.apply(new SafeMap(easyMap.map()));

        assertFalse(rulePassed);
    }
}
