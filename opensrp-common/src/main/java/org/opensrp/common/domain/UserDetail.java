package org.opensrp.common.domain;

import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;
import java.util.List;

public class UserDetail implements Serializable {
    @JsonProperty
    private String userName;
    @JsonProperty
    private List<String> roles;

    public UserDetail(String userName, List<String> roles) {
        this.userName = userName;
        this.roles = roles;
    }

    public String userName() {
        return userName;
    }

    public List<String> roles() {
        return roles;
    }
}
