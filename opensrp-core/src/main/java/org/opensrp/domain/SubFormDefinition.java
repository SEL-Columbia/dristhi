package org.opensrp.domain;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;



public class SubFormDefinition {
    
    private String name;

    private String bind_type;

    private String default_bind_path;
    
    private List<FormField> fields;

    //private List<Map<String, String>> instances;

    public SubFormDefinition() {
       // this.instances =  new ArrayList<>();
        this.name = "";
    }

    public SubFormDefinition(String name, List<FormField> fields) {
      //  this.instances = instances;
        this.fields=fields;
    	this.name = name;
    }

    public String name() {
        return name;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBind_type() {
		return bind_type;
	}

	public void setBind_type(String bind_type) {
		this.bind_type = bind_type;
	}

	public String getDefault_bind_path() {
		return default_bind_path;
	}

	public void setDefault_bind_path(String default_bind_path) {
		this.default_bind_path = default_bind_path;
	}

	public List<FormField> getFields() {
		return fields;
	}

	public void setFields(List<FormField> fields) {
		this.fields = fields;
	}
    
    

   

}
