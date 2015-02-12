package org.ei.drishti.web.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public interface AuthenticationService {
	
	Authentication authenticate(Authentication authentication) throws AuthenticationException;

}
