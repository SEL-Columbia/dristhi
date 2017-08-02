package org.opensrp.common.domain;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

/**
 * Created by real on 11/07/17.
 */
public class LocationTest {
    @Test
    public void testConstructorAndGetters() {
        Location location = new Location("Nandanpur", "gangapur", "house");
        assertEquals("Nandanpur", location.village());
        assertNotSame("PalerHut", location.village());

        assertEquals("gangapur", location.subCenter());
        assertNotSame("postOffice", location.subCenter());

        assertEquals("house", location.phc());
        assertNotSame("PalerHut", location.phc());

    }
}
