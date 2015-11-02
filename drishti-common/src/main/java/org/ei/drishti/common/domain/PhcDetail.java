package org.ei.drishti.common.domain;

import java.io.Serializable;


import org.codehaus.jackson.annotate.JsonProperty;

public class PhcDetail implements Serializable {
	     
	
		@JsonProperty
	    private String id;
	    @JsonProperty
	    private String name;

	    public PhcDetail(String ecNumber, String name) {
	        this.id = id;
	        this.name = name;
	    }

	    public String id() {
	        return id;
	    }

	    public String name() {
	        return name;
	    }

}
