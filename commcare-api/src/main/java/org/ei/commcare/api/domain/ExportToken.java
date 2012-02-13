package org.ei.commcare.api.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;

@TypeDiscriminator("doc.type === 'ExportToken'")
public class ExportToken extends MotechBaseDataObject {
    @JsonProperty
    private String nameSpace;
    @JsonProperty
    private String value;

    private ExportToken() {
    }

    public ExportToken(String nameSpace, String value) {
        this.nameSpace = nameSpace;
        this.value = value;
    }

    public String value() {
        return value;
    }

    private String getNameSpace() {
        return nameSpace;
    }
}
