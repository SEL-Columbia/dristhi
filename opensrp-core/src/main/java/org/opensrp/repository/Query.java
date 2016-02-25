package org.opensrp.repository;

import org.joda.time.DateTime;

import com.mysql.jdbc.StringUtils;

public class Query{
		private String query = "";
		private FilterType filterType;
		
		public String query(){
			return query;
		}
		
		public Query(FilterType filterType) {
			this.filterType = filterType;
		}
		public Query(FilterType filterType, Query from) {
			this.filterType = filterType;
			if(from != null && !StringUtils.isEmptyOrWhitespaceOnly(from.query)){
				this.query = "("+from.query+")";
			}
		}
		public Query eq(String name, String value){
			addToQuery(name+":"+value);
			return this;
		}
		public Query like(String name, String value) {
			addToQuery(name+":["+value+" TO "+value+"\uFFFF]");
			return this;
		}
		public Query eq(String name, DateTime value){
			addToQuery(name+"<date>:["+value.toString("yyyy-MM-dd")+" TO "+value.toString("yyyy-MM-dd")+"]");
			return this;
		}
		private void addToQuery(String q){
			if(!StringUtils.isEmptyOrWhitespaceOnly(query)){
				query += filterType.name()+" "+q;
			}
			else query += q;
		}
	}