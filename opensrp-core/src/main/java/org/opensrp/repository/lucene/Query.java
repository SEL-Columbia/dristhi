package org.opensrp.repository.lucene;

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
			addToQuery(name+":"+value+" ");
			return this;
		}
		public Query like(String field, String value) {
			addToQuery(field+":\""+value+"\"");
			return this;
		}
		public Query eq(String name, DateTime value){
			addToQuery(name+"<date>:["+value.withTimeAtStartOfDay().toString("yyyy-MM-dd'T'HH:mm:ss")+" TO "+value.plusDays(1).withTimeAtStartOfDay().toString("yyyy-MM-dd'T'HH:mm:ss")+"] ");
			return this;
		}
		public Query between(String name, DateTime from, DateTime to){
			addToQuery(name+"<date>:["+from.toString("yyyy-MM-dd'T'HH:mm:ss")+" TO "+to.toString("yyyy-MM-dd'T'HH:mm:ss")+"] ");
			return this;
		}
		public Query between(String field,Object start, Object to){
			addToQuery(field+":["+start +" TO "+ to+"]");
			return this;
		}
		private void addToQuery(String q){
			if(!StringUtils.isEmptyOrWhitespaceOnly(query)){
				query += filterType.name()+" "+q;
			}
			else query += q;
		}
	}