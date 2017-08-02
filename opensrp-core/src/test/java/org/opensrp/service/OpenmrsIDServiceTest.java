package org.opensrp.service;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.opensrp.SpringApplicationContextProvider;
import org.opensrp.domain.Address;
import org.opensrp.domain.Client;
import org.opensrp.domain.UniqueId;
import org.opensrp.repository.UniqueIdRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.intThat;
import static org.opensrp.service.OpenmrsIDService.CHILD_REGISTER_CARD_NUMBER;

public class OpenmrsIDServiceTest extends SpringApplicationContextProvider {
    @Autowired
    OpenmrsIDService openmrsIDService;
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    UniqueIdRepository uniqueIdRepository;

    @Before
    public void setUp() {
        String dropDbSql = "DROP TABLE IF EXISTS `unique_ids`;";
        jdbcTemplate.execute(dropDbSql);
        String tableCreationString =
                "CREATE TABLE `unique_ids` (\n" +
                        "  `_id` bigint(20) NOT NULL AUTO_INCREMENT,\n" +
                        "  `created_at` datetime DEFAULT NULL,\n" +
                        "  `location` varchar(255) DEFAULT NULL,\n" +
                        "  `openmrs_id` varchar(255) DEFAULT NULL,\n" +
                        "  `status` varchar(255) DEFAULT NULL,\n" +
                        "  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
                        "  `used_by` varchar(255) DEFAULT NULL,\n" +
                        "  PRIMARY KEY (`_id`)\n" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
        jdbcTemplate.execute(tableCreationString);
    }

    @After
    public void tearDown() {
        String dropDbSql = "DROP TABLE IF EXISTS `unique_ids`;";
        // jdbcTemplate.execute(dropDbSql);

    }

    public Client createClient(String baseEntityId, String firstName, String lastName, String gender, String childRegisterCardNumber) {
        DateTime dateOfBirth = new DateTime();
        Map<String, String> addressFields = new HashMap<>();
        addressFields.put("address4", "birthFacilityName");
        addressFields.put("address3", "resolvedResidentialAddress");
        addressFields.put("address2", "residentialAddress");
        addressFields.put("address1", "physicalLandmark");

        Address address = new Address("usual_residence", new DateTime(), new DateTime(), addressFields, null, null, null, "homeFacility", null);
        ArrayList<Address> addressList = new ArrayList<Address>();
        addressList.add(address);

        Client client = new Client(baseEntityId, firstName, "", lastName, dateOfBirth, null, false, false, gender, addressList, null, null);
        client.addAttribute(CHILD_REGISTER_CARD_NUMBER, childRegisterCardNumber);
        return client;
    }

    @Test
    public void testAssignOpenmrsIdToClient() throws SQLException {
        Client client = this.createClient("12345", "First", "Last", "Male", "454/16");

        openmrsIDService.assignOpenmrsIdToClient("12345-1", client);
        assertNotNull(client.getIdentifier(OpenmrsIDService.ZEIR_IDENTIFIER));
    }


    @Test
    public void testExistingClientsDoNotReceiveNewOpenmrsId() throws Exception {
        Client client = this.createClient("45678", "Jane", "Doe", "Female", "102/17");
        Client duplicateClient = this.createClient("45677", "Jane", "Doe", "Female", "102/17");

        openmrsIDService.assignOpenmrsIdToClient("12345-1", client);
        assertNotNull(client.getIdentifier(OpenmrsIDService.ZEIR_IDENTIFIER));

        openmrsIDService.assignOpenmrsIdToClient("12345-1", duplicateClient);
        assertTrue(openmrsIDService.checkIfClientExists(duplicateClient));
        assertNull(duplicateClient.getIdentifier(OpenmrsIDService.ZEIR_IDENTIFIER));
    }

    @Test
    public void testCheckClient() throws SQLException {
        Client client = this.createClient("45678", "Jane", "Doe", "Female", "102/17");
        openmrsIDService.assignOpenmrsIdToClient("12345-1", client);
        assertTrue(openmrsIDService.checkIfClientExists(client));
    }

    @Test
    public void testCheckClientWithFalseData() throws SQLException {
        Client client = this.createClient("45678", "Jane", "Doe", "Female", "102/17");
        assertFalse(openmrsIDService.checkIfClientExists(client));
    }

    @Test
    public void testCheckClientWithInvalidData() throws SQLException {Client client = this.createClient("*", "Jane", "Doe", "Female", "*");
        assertNull(openmrsIDService.checkIfClientExists(null));
    }

    @Test
    public void testDownloadAndSaveIds() {
        List<String> downloadedIds = new ArrayList<>();
        downloadedIds.add("1");
        downloadedIds.add("2");
        OpenmrsIDService openmrsIDServiceSpy = Mockito.spy(openmrsIDService);
        Mockito.doReturn(downloadedIds).when(openmrsIDServiceSpy).downloadOpenmrsIds(anyInt());

        openmrsIDServiceSpy.downloadAndSaveIds(2, "test");

        List<UniqueId> uniqueIds = uniqueIdRepository.getNotUsedIds(2);
        List<String> actualList = new ArrayList<>();
        for (UniqueId uniqueId : uniqueIds) {
            assertEquals("test", uniqueId.getUsedBy());
            actualList.add(uniqueId.getOpenmrsId());
        }

        assertEquals(2, (int) uniqueIdRepository.totalUnUsedIds());
        assertEquals(downloadedIds, actualList);
    }

    @Test
    public void testClearRecord() throws SQLException {
        Client client = this.createClient("12345", "First", "Last", "Male", "454/16");
        openmrsIDService.assignOpenmrsIdToClient("12345-1", client);

        assertTrue(openmrsIDService.checkIfClientExists(client));

        openmrsIDService.clearRecords();

        assertFalse(openmrsIDService.checkIfClientExists(client));
    }


    @Test
    public void testGetNotUsedId() throws Exception {
        int size = 10;
        List<UniqueId> expectedList = createNotUsedUniqIdEntries(size);
        List<UniqueId> actualList = openmrsIDService.getNotUsedIds(100);
        assertEquals(size, actualList.size());

        for (int i = 0; i < size; i++) {
            UniqueId expected = expectedList.get(i);
            UniqueId actual = actualList.get(i);
            assertUniqueId(expected, actual);
        }

    }

    @Test
    public void testGetNotUsedIdAsString() throws Exception {
        int size = 10;
        List<UniqueId> ids = createNotUsedUniqIdEntries(size);
        List<String> expectedList = new ArrayList<>();
        List<String> actualList = openmrsIDService.getNotUsedIdsAsString(100);
        for (int i = 0; i < size; i++) {
            expectedList.add(ids.get(i).getOpenmrsId());
        }

        assertEquals(size, actualList.size());
        assertEquals(expectedList, actualList);
    }

    @Test
    public void testMarkIdAsUsed() throws Exception {
        int size = 10;
        List<UniqueId> ids = createNotUsedUniqIdEntries(size);
        List<String> idListAsString = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            idListAsString.add(ids.get(i).getOpenmrsId());
        }

        int[] actualIds = openmrsIDService.markIdsAsUsed(idListAsString);
        List<String> actualList = openmrsIDService.getNotUsedIdsAsString(100);

        assertEquals(size, actualIds.length);
        assertEquals(0, actualList.size());

    }

    private List<UniqueId> createNotUsedUniqIdEntries(int size) throws Exception {
        List<UniqueId> notUsedUniqueIds = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            UniqueId uniqueId = new UniqueId();
            uniqueId.setOpenmrsId(String.valueOf(i));
            uniqueId.setStatus(UniqueId.STATUS_NOT_USED);
            uniqueId.setUsedBy("test" + i);
            uniqueId.setCreatedAt(new Date());
            uniqueId.setLocation("test");
            notUsedUniqueIds.add(uniqueId);
            uniqueIdRepository.save(uniqueId);
        }
        return notUsedUniqueIds;
    }

    private void assertUniqueId(UniqueId expected, UniqueId actual) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-mm-hh hh:MM:ss Z");
        assertEquals(dateFormat.format(expected.getCreatedAt()), dateFormat.format(actual.getCreatedAt()));
        assertEquals(expected.getLocation(), actual.getLocation());
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getOpenmrsId(), actual.getOpenmrsId());
        assertEquals(expected.getStatus(), actual.getStatus());
        assertEquals(expected.getUsedBy(), actual.getUsedBy());
    }
}
