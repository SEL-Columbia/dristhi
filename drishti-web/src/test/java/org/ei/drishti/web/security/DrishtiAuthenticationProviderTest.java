package org.ei.drishti.web.security;

import org.ei.drishti.domain.DrishtiUser;
import org.ei.drishti.repository.AllDrishtiUsers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class DrishtiAuthenticationProviderTest {
    @Mock
    private AllDrishtiUsers allDrishtiUsers;
    @Rule
    public ExpectedException exception = ExpectedException.none();

    private DrishtiAuthenticationProvider authenticationProvider;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        authenticationProvider = new DrishtiAuthenticationProvider(allDrishtiUsers);
    }

    @Test
    public void shouldAuthenticateValidUser() throws Exception {
        when(allDrishtiUsers.findByUsername("user 1")).thenReturn(new DrishtiUser("user 1", "password 1", asList("ROLE_USER", "ROLE_ADMIN"), true));

        Authentication authentication = authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken("user 1", "password 1"));

        assertEquals(new UsernamePasswordAuthenticationToken("user 1", "password 1", asList(new SimpleGrantedAuthority("ROLE_USER"), new SimpleGrantedAuthority("ROLE_ADMIN"))), authentication);
    }

    @Test
    public void shouldNotAuthenticateUserWithWrongUsername() throws Exception {
        when(allDrishtiUsers.findByUsername("user 1")).thenReturn(null);
        exception.expect(BadCredentialsException.class);
        exception.expectMessage("The username or password you entered is incorrect. Please enter the correct credentials.");

        authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken("user 1", "password 1"));
    }

    @Test
    public void shouldNotAuthenticateUserWithWrongPassword() throws Exception {
        when(allDrishtiUsers.findByUsername("user 1")).thenReturn(new DrishtiUser("user 1", "correct password", asList("ROLE_USER"), true));
        exception.expect(BadCredentialsException.class);
        exception.expectMessage("The username or password you entered is incorrect. Please enter the correct credentials.");

        authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken("user 1", "wrong password"));
    }

    @Test
    public void shouldNotAuthenticateInactiveUser() throws Exception {
        when(allDrishtiUsers.findByUsername("user 1")).thenReturn(new DrishtiUser("user 1", "password 1", asList("ROLE_USER"), false));
        exception.expect(BadCredentialsException.class);
        exception.expectMessage("The user has been registered but not activated. Please contact your local administrator.");

        authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken("user 1", "password 1"));
    }
}
