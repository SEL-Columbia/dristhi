package org.opensrp.services;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Test;
import org.opensrp.SpringApplicationContextProvider;
import org.opensrp.domain.Address;
import org.opensrp.domain.Client;
import org.opensrp.service.OpenmrsIDService;
import org.springframework.beans.factory.annotation.Autowired;

public class OpenmrsIDServiceTest  {
    /*@Autowired
    OpenmrsIDService openmrsIDService;


    @After
    public void tearDown() {
        openmrsIDService.clearRecords();
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
        client.addIdentifier(OpenmrsIDService.CHILD_REGISTER_CARD_NUMBER, childRegisterCardNumber);

        return client;
    }

    @Test
    public void testDownloadOpenmrsIds() throws SQLException {
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
    }*/
}
