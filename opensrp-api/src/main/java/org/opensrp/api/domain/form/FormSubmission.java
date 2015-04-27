package org.opensrp.api.domain.form;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.opensrp.api.domain.Provider;

/**
 * 
 */
public class FormSubmission {

	private String providerId;
	
	private Provider provider;

	private String instanceId;
    
    private String formName;

    private String baseEntityId;

    private long clientVersion;

    private String formDataDefinitionVersion;

    private FormInstance formInstance;

    private long serverVersion;

    public FormSubmission() {
    }

    public FormSubmission(String providerId, String instanceId, String formName, String baseEntityId, long clientVersion, String formDataDefinitionVersion, FormInstance formInstance, long serverVersion) {
        this.instanceId = instanceId;
        this.formName = formName;
        this.providerId = providerId;
        this.clientVersion = clientVersion;
        this.baseEntityId = baseEntityId;
        this.formInstance = formInstance;
        this.serverVersion = serverVersion;
        this.formDataDefinitionVersion = formDataDefinitionVersion;
    }

    public FormSubmission(String providerId, String instanceId, String formName, String baseEntityId, String formDataDefinitionVersion, long clientVersion, FormInstance formInstance) {
        this(providerId, instanceId, formName, baseEntityId, clientVersion, formDataDefinitionVersion, formInstance, 0L);
    }

    public String getProviderId() {
		return providerId;
	}

	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}

	public Provider getProvider() {
		return provider;
	}

	public void setProvider(Provider provider) {
		this.provider = provider;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public String getFormName() {
		return formName;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}

	public String getBaseEntityId() {
		return baseEntityId;
	}

	public void setBaseEntityId(String baseEntityId) {
		this.baseEntityId = baseEntityId;
	}

	public long getClientVersion() {
		return clientVersion;
	}

	public void setClientVersion(long clientVersion) {
		this.clientVersion = clientVersion;
	}

	public String getFormDataDefinitionVersion() {
		return formDataDefinitionVersion;
	}

	public void setFormDataDefinitionVersion(String formDataDefinitionVersion) {
		this.formDataDefinitionVersion = formDataDefinitionVersion;
	}

	public FormInstance getFormInstance() {
		return formInstance;
	}

	public void setFormInstance(FormInstance formInstance) {
		this.formInstance = formInstance;
	}

	public long getServerVersion() {
		return serverVersion;
	}

	public void setServerVersion(long serverVersion) {
		this.serverVersion = serverVersion;
	}

	public String getField(String name) {
        return formInstance.getField(name);
    }

    public Map<String, String> getFields(List<String> fieldNames) {
        Map<String, String> fieldsMap = new HashMap<>();
        for (String fieldName : fieldNames) {
            fieldsMap.put(fieldName, getField(fieldName));
        }
        return fieldsMap;
    }

    public SubFormData getSubFormByName(String name) {
        return formInstance.getSubFormByName(name);
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(o, this);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, "id");
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
