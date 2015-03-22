package org.opensrp.web.security;

import static java.text.MessageFormat.format;

import java.util.List;

import org.json.JSONException;
import org.opensrp.api.domain.User;
import org.opensrp.connector.openmrs.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.Converter;

@Component
public class DrishtiAuthenticationProvider implements AuthenticationProvider {
    private static Logger logger = LoggerFactory.getLogger(DrishtiAuthenticationProvider.class.toString());
    public static final String USER_NOT_FOUND = "The username or password you entered is incorrect. Please enter the correct credentials.";
    public static final String USER_NOT_ACTIVATED = "The user has been registered but not activated. Please contact your local administrator.";
    public static final String INTERNAL_ERROR = "Failed to authenticate user due to internal server error.";

    //private AllOpenSRPUsers allOpenSRPUsers;
    private PasswordEncoder passwordEncoder;
    private UserService openmrsUserService;


    @Autowired
    public DrishtiAuthenticationProvider(UserService openmrsUserService, @Qualifier("shaPasswordEncoder") PasswordEncoder passwordEncoder) {
        this.openmrsUserService = openmrsUserService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        User user = getDrishtiUser(authentication);
        if (user == null) {
            throw new BadCredentialsException(USER_NOT_FOUND);
        }

        String credentials = (String) authentication.getCredentials();
        //String hashedCredentials = passwordEncoder.encodePassword(credentials, user.getSalt());
        try {
			if (!openmrsUserService.authenticate(user.getUsername(), credentials)) {
			    throw new BadCredentialsException(USER_NOT_FOUND);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

        /*if (!user.isActive()) {
            throw new BadCredentialsException(USER_NOT_ACTIVATED);
        }*/
        return new UsernamePasswordAuthenticationToken(authentication.getName(), credentials, getRolesAsAuthorities(user));
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication)
                && authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    private List<SimpleGrantedAuthority> getRolesAsAuthorities(User user) {
        return Lambda.convert(user.getRoles(), new Converter<String, SimpleGrantedAuthority>() {
            @Override
            public SimpleGrantedAuthority convert(String role) {
                return new SimpleGrantedAuthority("ROLE_OPENMRS");
            }
        });
    }

    public User getDrishtiUser(Authentication authentication) {
        User user;
        try {
            user = openmrsUserService.getUser((String) authentication.getPrincipal());
        } catch (Exception e) {
            logger.error(format("{0}. Exception: {1}", INTERNAL_ERROR, e));
            throw new BadCredentialsException(INTERNAL_ERROR);
        }
        return user;
    }
    

    public User getDrishtiUser(String username) {
        User user;
        try {
            user = openmrsUserService.getUser(username);
        } catch (Exception e) {
            logger.error(format("{0}. Exception: {1}", INTERNAL_ERROR, e));
            throw new BadCredentialsException(INTERNAL_ERROR);
        }
        return user;
    }
}
