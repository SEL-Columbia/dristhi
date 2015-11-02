package org.ei.drishti.web.controller;

import org.ei.drishti.common.domain.PhcDetail;
import org.ei.drishti.common.domain.UserDetail;
import org.ei.drishti.domain.DrishtiUser;
import org.ei.drishti.domain.EligibleCouple;
import org.ei.drishti.repository.AllEligibleCouples;
import org.ei.drishti.web.security.DrishtiAuthenticationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import static org.ei.drishti.web.HttpHeaderFactory.allowOrigin;
import static org.springframework.http.HttpStatus.OK;

@Controller
public class UserController {
	private static Logger logger = LoggerFactory.getLogger(UserController.class
			.toString());
	private String drishtiSiteUrl;
	private DrishtiAuthenticationProvider drishtiAuthenticationProvider;

	@Autowired
	public UserController(
			@Value("#{drishti['drishti.site.url']}") String drishtiSiteUrl,
			DrishtiAuthenticationProvider drishtiAuthenticationProvider) {
		this.drishtiSiteUrl = drishtiSiteUrl;
		this.drishtiAuthenticationProvider = drishtiAuthenticationProvider;

	}

	@RequestMapping(method = RequestMethod.GET, value = "/authenticate-user")
	public ResponseEntity<HttpStatus> authenticateUser() {
		return new ResponseEntity<>(null, allowOrigin(drishtiSiteUrl), OK);
	}

	public DrishtiUser currentUser() {
		Authentication authentication = SecurityContextHolder.getContext()
				.getAuthentication();
		return drishtiAuthenticationProvider.getDrishtiUser(authentication);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/user-details")
	public ResponseEntity<UserDetail> userDetail(
			@RequestParam("anm-id") String anmIdentifier)

	{

		DrishtiUser user = drishtiAuthenticationProvider
				.getDrishtiUser(anmIdentifier);
		logger.info("fetched details for user" + user);

		return new ResponseEntity<>(new UserDetail(user.getUsername(),
				user.getRoles()), allowOrigin(drishtiSiteUrl), OK);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/user-role")
	public ResponseEntity<UserDetail> userRoleDetail(
			@RequestParam("anm-id") String anmIdentifier) {
		logger.info("trying to fetch the user details");
		String anmIdentifier1 = this.currentUser().getUsername();
		logger.info("anmidentifier1 details" + anmIdentifier1);
		DrishtiUser user = drishtiAuthenticationProvider
				.getDrishtiUser(anmIdentifier1);
		logger.info("fetched details for user" + user);

		return new ResponseEntity<>(new UserDetail(user.getUsername(),
				user.getRoles()), allowOrigin(drishtiSiteUrl), OK);
	}
}
