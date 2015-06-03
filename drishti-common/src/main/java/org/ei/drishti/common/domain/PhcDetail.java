package org.ei.drishti.common.domain;

import java.io.Serializable;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

public class PhcDetail implements Serializable {
	     @JsonProperty
	    private String ecNumber;
	    @JsonProperty
	    private String caseId;

	    public PhcDetail(String ecNumber, String caseId) {
	        this.ecNumber = ecNumber;
	        this.caseId = caseId;
	    }

	    public String ecNumber() {
	        return ecNumber;
	    }

	    public String caseId() {
	        return caseId;
	    }

}
