package org.opensrp.common.domain;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

/**
 * Created by real on 12/07/17.
 */
public class UserDetailTest {
    @Test
    public void testConstructorNGetters() {
        List<String> rolesList = new ArrayList<>();
        rolesList.add("TLI");
        rolesList.add("FD");
        UserDetail userDetail = new UserDetail("real", rolesList);
        assertEquals("real", userDetail.userName());
        assertNotSame("peal", userDetail.userName());
        assertEquals("TLI", userDetail.roles().get(0));
        assertNotSame("TLI", userDetail.roles().get(1));
    }
}
