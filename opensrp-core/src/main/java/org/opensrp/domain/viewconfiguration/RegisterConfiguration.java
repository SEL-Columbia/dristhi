package org.opensrp.domain.viewconfiguration;

import java.util.List;

public class RegisterConfiguration extends BaseConfiguration {
	
	private boolean enableAdvancedSearch;
	
	private boolean enableSortList;
	
	private boolean enableFilterList;
	
	private String searchBarText;
	
	private List<String> searchableFields;
	
	public boolean isEnableAdvancedSearch() {
		return enableAdvancedSearch;
	}
	
	public void setEnableAdvancedSearch(boolean enableAdvancedSearch) {
		this.enableAdvancedSearch = enableAdvancedSearch;
	}
	
	public boolean isEnableSortList() {
		return enableSortList;
	}
	
	public void setEnableSortList(boolean enableSortList) {
		this.enableSortList = enableSortList;
	}
	
	public boolean isEnableFilterList() {
		return enableFilterList;
	}
	
	public void setEnableFilterList(boolean enableFilterList) {
		this.enableFilterList = enableFilterList;
	}
	
	public String getSearchBarText() {
		return searchBarText;
	}
	
	public void setSearchBarText(String searchBarText) {
		this.searchBarText = searchBarText;
	}
	
	public List<String> getSearchableFields() {
		return searchableFields;
	}
	
	public void setSearchableFields(List<String> searchableFields) {
		this.searchableFields = searchableFields;
	}
	
}
