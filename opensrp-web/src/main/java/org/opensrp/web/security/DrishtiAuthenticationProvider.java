package org.opensrp.web.security;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.Converter;
import org.opensrp.domain.DrishtiUser;
import org.opensrp.repository.AllOpenSRPUsers;
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

import java.util.List;

import static java.text.MessageFormat.format;

@Component
public class DrishtiAuthenticationProvider implements AuthenticationProvider {
    private static Logger logger = LoggerFactory.getLogger(DrishtiAuthenticationProvider.class.toString());
    public static final String USER_NOT_FOUND = "The username or password you entered is incorrect. Please enter the correct credentials.";
    public static final String USER_NOT_ACTIVATED = "The user has been registered but not activated. Please contact your local administrator.";
    public static final String INTERNAL_ERROR = "Failed to authenticate user due to internal server error.";

    private AllOpenSRPUsers allOpenSRPUsers;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public DrishtiAuthenticationProvider(AllOpenSRPUsers allDrishtiUsers, @Qualifier("shaPasswordEncoder") PasswordEncoder passwordEncoder) {
        this.allOpenSRPUsers = allDrishtiUsers;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        DrishtiUser user = getDrishtiUser(authentication);
        if (user == null) {
            throw new BadCredentialsException(USER_NOT_FOUND);
        }

        String credentials = (String) authentication.getCredentials();
        String hashedCredentials = passwordEncoder.encodePassword(credentials, user.getSalt());
        if (!user.getPassword().equals(hashedCredentials)) {
            throw new BadCredentialsException(USER_NOT_FOUND);
        }

        if (!user.isActive()) {
            throw new BadCredentialsException(USER_NOT_ACTIVATED);
        }
        return new UsernamePasswordAuthenticationToken(authentication.getName(), credentials, getRolesAsAuthorities(user));
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication)
                && authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    private List<SimpleGrantedAuthority> getRolesAsAuthorities(DrishtiUser user) {
        return Lambda.convert(user.getRoles(), new Converter<String, SimpleGrantedAuthority>() {
            @Override
            public SimpleGrantedAuthority convert(String role) {
                return new SimpleGrantedAuthority(role);
            }
        });
    }

    public DrishtiUser getDrishtiUser(Authentication authentication) {
        DrishtiUser user;
        try {
            user = allOpenSRPUsers.findByUsername((String) authentication.getPrincipal());
        } catch (Exception e) {
            logger.error(format("{0}. Exception: {1}", INTERNAL_ERROR, e));
            throw new BadCredentialsException(INTERNAL_ERROR);
        }
        return user;
    }

    public DrishtiUser getDrishtiUser(String username) {
        DrishtiUser user;
        try {
            user = allOpenSRPUsers.findByUsername(username);
        } catch (Exception e) {
            logger.error(format("{0}. Exception: {1}", INTERNAL_ERROR, e));
            throw new BadCredentialsException(INTERNAL_ERROR);
        }
        return user;
    }
}
