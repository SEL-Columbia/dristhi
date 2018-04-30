package org.opensrp.repository.postgres.handler;

import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;
import org.ektorp.impl.StdObjectMapperFactory;
import org.joda.time.DateTime;
import org.opensrp.util.DateTimeDeserializer;

public class BaseTypeHandler {
	
	public static final ObjectMapper mapper = new StdObjectMapperFactory().createObjectMapper();;
	
	protected BaseTypeHandler() {
		SimpleModule dateTimeModule = new SimpleModule("DateTimeModule", new Version(0, 0, 0, null));
		dateTimeModule.addDeserializer(DateTime.class, new DateTimeDeserializer());
		mapper.registerModule(dateTimeModule);
	}
	
}
