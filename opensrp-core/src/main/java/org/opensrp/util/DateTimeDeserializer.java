package org.opensrp.util;

import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.deser.std.StdScalarDeserializer;
import org.joda.time.DateTime;

public class DateTimeDeserializer extends StdScalarDeserializer<DateTime> {
	
	public DateTimeDeserializer() {
		super(DateTime.class);
	}
	
	@Override
	public DateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
	    throws JsonParseException, IOException {
		JsonToken currentToken = jsonParser.getCurrentToken();
		if (currentToken == JsonToken.VALUE_STRING) {
			String dateTimeAsString = jsonParser.getText().trim();
			return new DateTime(dateTimeAsString);
		}
		return null;
		
	}
}
