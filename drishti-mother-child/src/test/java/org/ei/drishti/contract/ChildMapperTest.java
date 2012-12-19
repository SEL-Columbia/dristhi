package org.ei.drishti.contract;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.ei.drishti.util.EasyMap.create;
import static org.ei.drishti.util.EasyMap.mapOf;

public class ChildMapperTest {
    private Map<String, Map<String, String>> EXTRA_DATA = mapOf("details", mapOf("someKey", "someValue"));
    private ChildMapper mapper;

    @Before
    public void setUp() throws Exception {
        mapper = new ChildMapper();
    }

    @Test
    public void shouldMapDeliveryOutcomeInformationForOneChild() throws Exception {
        AnteNatalCareOutcomeInformation outcomeInformation = new AnteNatalCareOutcomeInformation("MOTHER-CASE-1", "ANM X", "live_birth", "2012-01-01", "1")
                .withChild("Case X", "0", "child0", "female", "2", "A", "bcg_0 opv_0");
        Map<String, Map<String, String>> EXTRA_DATA = mapOf("details", mapOf("someKey", "someValue"));
        Map<String, Map<String, String>> expectedExtraData = mapOf("details", create("someKey", "someValue").put("childWeight", "2").put("childBloodGroup", "A").map());

        mapper = new ChildMapper();
        List<ChildInformation> childInformationList = mapper.mapDeliveryOutcomeInformationToChildren(outcomeInformation, EXTRA_DATA);

        assertEquals(asList(new ChildInformation("Case X", "MOTHER-CASE-1", "ANM X", "child0", "female", "2012-01-01", "bcg_0 opv_0", "2", expectedExtraData)), childInformationList);
    }

    @Test
    public void shouldMapDeliveryOutcomeInformationForMoreThanOneChild() throws Exception {
        AnteNatalCareOutcomeInformation outcomeInformation = new AnteNatalCareOutcomeInformation("MOTHER-CASE-1", "ANM X", "live_birth", "2012-01-01", "2")
                .withChild("Case X", "0", "child0", "female", "2", "A", "bcg_0 opv_0")
                .withChild("Case Y", "1", "child1", "male", "3", "B", "bcg_0");
        Map<String, Map<String, String>> expectedExtraDataForChild1 = mapOf("details", create("someKey", "someValue").put("childWeight", "2").put("childBloodGroup", "A").map());
        Map<String, Map<String, String>> expectedExtraDataForChild2 = mapOf("details", create("someKey", "someValue").put("childWeight", "3").put("childBloodGroup", "B").map());
        ChildInformation childOneInformation = new ChildInformation("Case X", "MOTHER-CASE-1", "ANM X", "child0", "female", "2012-01-01", "bcg_0 opv_0", "2", expectedExtraDataForChild1);
        ChildInformation childTwoInformation = new ChildInformation("Case Y", "MOTHER-CASE-1", "ANM X", "child1", "male", "2012-01-01", "bcg_0", "3", expectedExtraDataForChild2);

        List<ChildInformation> childInformationList = mapper.mapDeliveryOutcomeInformationToChildren(outcomeInformation, EXTRA_DATA);

        assertEquals(asList(childOneInformation, childTwoInformation), childInformationList);
    }

    @Test
    public void shouldMapDeliveryOutcomeInformationForNoChild() throws Exception {
        AnteNatalCareOutcomeInformation outcomeInformation = new AnteNatalCareOutcomeInformation("MOTHER-CASE-1", "ANM X", "live_birth", "2012-01-01", "---");

        List<ChildInformation> childInformationList = mapper.mapDeliveryOutcomeInformationToChildren(outcomeInformation, EXTRA_DATA);

        assertEquals(0, childInformationList.size());
    }
}
