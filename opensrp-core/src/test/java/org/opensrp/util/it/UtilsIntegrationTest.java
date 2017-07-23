package org.opensrp.util.it;


import org.ektorp.CouchDbConnector;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensrp.SpringApplicationContextProvider;
import org.opensrp.util.Utils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.MalformedURLException;

import static junit.framework.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-applicationContext-opensrp.xml")
public class UtilsIntegrationTest {

    @Value("#{opensrp['couchdb.server']}")
    String url;
    @Value("#{opensrp['couchdb.username']}")
    String userName;
    @Value("#{opensrp['couchdb.password']}")
    String password;
    @Value("#{opensrp['couchdb.port']}")
    String portNumber;

    @Test
    public void testConnectToDb() throws MalformedURLException {
        Utils.DatabaseConnectionParams databaseConnectionParams = new Utils.DatabaseConnectionParams();
        databaseConnectionParams.url = "http://" + url;
        databaseConnectionParams.dbName = "opensrp";
        databaseConnectionParams.password = password;
        databaseConnectionParams.userName = userName;
        databaseConnectionParams.portNumber = portNumber;

        CouchDbConnector couchDbConnector = Utils.connectToDB(databaseConnectionParams);

        assertEquals("opensrp", couchDbConnector.getDatabaseName());
    }

    @Test(expected = MalformedURLException.class)
    public void testConnectDbMalFormedURLException() throws MalformedURLException {
        Utils.DatabaseConnectionParams databaseConnectionParams = new Utils.DatabaseConnectionParams();
        databaseConnectionParams.url = "127.0.0.1";
        databaseConnectionParams.portNumber = portNumber;

        CouchDbConnector couchDbConnector = Utils.connectToDB(databaseConnectionParams);
    }

    @Test(expected = NullPointerException.class)
    public void testConnectDbNullPointerException() throws MalformedURLException {
        Utils.DatabaseConnectionParams databaseConnectionParams = new Utils.DatabaseConnectionParams();
        CouchDbConnector couchDbConnector = Utils.connectToDB(databaseConnectionParams);
    }

    @Test
    public void testConnectDbWithOutUserNameAndPassword() throws MalformedURLException {
        Utils.DatabaseConnectionParams databaseConnectionParams = new Utils.DatabaseConnectionParams();
        databaseConnectionParams.url = "http://" + url;
        databaseConnectionParams.portNumber = portNumber;
        databaseConnectionParams.dbName = "opensrp";

        CouchDbConnector couchDbConnector = Utils.connectToDB(databaseConnectionParams);
        assertEquals("opensrp", couchDbConnector.getDatabaseName());
    }
}
