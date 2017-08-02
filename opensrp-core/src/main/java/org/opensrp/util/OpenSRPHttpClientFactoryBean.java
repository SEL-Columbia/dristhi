package org.opensrp.util;

import java.util.Properties;

import org.ektorp.spring.HttpClientFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * OpenSrp Factory Bean 
 * The application context must define properties along the line of:
 * <code>
 * <util:properties id="couchdbProperties" location="classpath:couchdb.properties"/>
 * </code>
 * @author keyman
 *
 */
public class OpenSRPHttpClientFactoryBean extends HttpClientFactoryBean {

	@Autowired
	@Qualifier("couchdbProperties")
	private Properties opensrpCouchdbProperties;

	/**
	 * Create the couchDB connection when starting the bean factory
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		if (opensrpCouchdbProperties != null) {
			setProperties(opensrpCouchdbProperties);
		}
		super.afterPropertiesSet();
	}

}