package org.opensrp.web.security;

import com.google.gson.Gson;

import org.opensrp.repository.AllUsers;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.UUID;

import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.opensrp.web.security.DrishtiAuthenticationProvider;

public class DrishtiAuthenticationProviderTest {
    @Mock
    private AllUsers allOpenSRPUsers;
    @Mock
    private ShaPasswordEncoder passwordEncoder;
    @Rule
    public ExpectedException exception = ExpectedException.none();

    private DrishtiAuthenticationProvider authenticationProvider;

    /*@Before
    public void setUp() throws Exception {
        initMocks(this);
        authenticationProvider = new DrishtiAuthenticationProvider(allOpenSRPUsers, passwordEncoder);
    }

    @Test
    public void shouldAuthenticateValidUser() throws Exception {
        when(allOpenSRPUsers.findByUsername("user 1")).thenReturn(new DrishtiUser("user 1", "hashed password 1", "salt", asList("ROLE_USER", "ROLE_ADMIN"), true));
        when(passwordEncoder.encodePassword("password 1", "salt")).thenReturn("hashed password 1");

        Authentication authentication = authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken("user 1", "password 1"));

        assertEquals(new UsernamePasswordAuthenticationToken("user 1", "password 1", asList(new SimpleGrantedAuthority("ROLE_USER"), new SimpleGrantedAuthority("ROLE_ADMIN"))), authentication);
    }

    @Test
    public void shouldNotAuthenticateUserWithWrongUsername() throws Exception {
        when(allOpenSRPUsers.findByUsername("user 1")).thenReturn(null);
        exception.expect(BadCredentialsException.class);
        exception.expectMessage("The username or password you entered is incorrect. Please enter the correct credentials.");

        authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken("user 1", "password 1"));
    }

    @Test
    public void shouldNotAuthenticateUserWithWrongPassword() throws Exception {
        when(allOpenSRPUsers.findByUsername("user 1")).thenReturn(new DrishtiUser("user 1", "correct password", "salt", asList("ROLE_USER"), true));
        exception.expect(BadCredentialsException.class);
        exception.expectMessage("The username or password you entered is incorrect. Please enter the correct credentials.");

        authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken("user 1", "wrong password"));
    }

    @Test
    public void shouldNotAuthenticateInactiveUser() throws Exception {
        when(allOpenSRPUsers.findByUsername("user 1")).thenReturn(new DrishtiUser("user 1", "hashed password 1", "salt", asList("ROLE_USER"), false));
        when(passwordEncoder.encodePassword("password 1", "salt")).thenReturn("hashed password 1");
        exception.expect(BadCredentialsException.class);
        exception.expectMessage("The user has been registered but not activated. Please contact your local administrator.");

        authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken("user 1", "password 1"));
    }

    @Test
    public void shouldFetchDrishtiUserByUsername() throws Exception {
        authenticationProvider.getDrishtiUser("user 1");

        verify(allOpenSRPUsers).findByUsername("user 1");
    }*/

    /*@Test
    @Ignore
    public void toGenerateUserPasswordsAndSalt() throws Exception {
        String username = "username";
        String password = "password";
        UUID salt = randomUUID();
        String hashedPassword = new ShaPasswordEncoder().encodePassword(password, salt);
        System.out.println(new Gson().toJson(new DrishtiUser(username, hashedPassword, salt.toString(), asList("ROLE_USER"), true)));
    }*/
}
