package org.opensrp.repository.lucene;

import static org.opensrp.common.AllConstants.BaseEntity.BASE_ENTITY_ID;
import static org.opensrp.common.AllConstants.BaseEntity.LAST_UPDATE;
import static org.opensrp.common.AllConstants.Event.ENTITY_TYPE;
import static org.opensrp.common.AllConstants.Event.EVENT_DATE;
import static org.opensrp.common.AllConstants.Event.EVENT_TYPE;
import static org.opensrp.common.AllConstants.Event.LOCATION_ID;
import static org.opensrp.common.AllConstants.Event.PROVIDER_ID;
import static org.opensrp.common.AllConstants.Event.TEAM;
import static org.opensrp.common.AllConstants.Event.TEAM_ID;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;
import org.opensrp.common.AllConstants.BaseEntity;
import org.opensrp.domain.Event;
import org.opensrp.search.EventSearchBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.ldriscoll.ektorplucene.CouchDbRepositorySupportWithLucene;
import com.github.ldriscoll.ektorplucene.LuceneQuery;
import com.github.ldriscoll.ektorplucene.LuceneResult;
import com.github.ldriscoll.ektorplucene.designdocument.annotation.FullText;
import com.github.ldriscoll.ektorplucene.designdocument.annotation.Index;
import com.mysql.jdbc.StringUtils;

@FullText({
        @Index(name = "by_all_criteria", analyzer = "perfield:{baseEntityId:\"keyword\",locationId:\"keyword\"}", index = "function(doc) {   if(doc.type !== 'Event') return null;   var arr1 = ['baseEntityId','eventType','entityType','providerId','locationId','teamId','team'];   var ret = new Document(); var serverVersion = doc.serverVersion;ret.add(serverVersion, {'field': 'serverVersion'});  for (var i in arr1){     ret.add(doc[arr1[i]], {'field':arr1[i]});   }   if(doc.eventDate){     var bd=doc.eventDate.substring(0,19);      ret.add(bd, {'field':'eventDate','type':'date'});   }          var crd = doc.dateCreated.substring(0, 19);     ret.add(crd, {'field' : 'lastEdited','type' : 'date'});          if(doc.dateEdited){     var led = doc.dateEdited.substring(0, 19);     ret.add(led, {'field' : 'lastEdited','type' : 'date'});         }        return ret;   }"),
        @Index(name = "by_all_criteria_v2", analyzer = "perfield:{baseEntityId:\"keyword\",locationId:\"keyword\"}", index = "function(doc) {   if(doc.type !== 'Event') return null;   var arr1 = ['baseEntityId','eventType','entityType','providerId','locationId','teamId','team'];   var ret = new Document(); var serverVersion = doc.serverVersion;ret.add(serverVersion, {'field': 'serverVersion'});  for (var i in arr1){     ret.add(doc[arr1[i]], {'field':arr1[i]});   }   if(doc.eventDate){     var bd=doc.eventDate.substring(0,19);      ret.add(bd, {'field':'eventDate','type':'date'});   }          var crd = doc.dateCreated.substring(0, 19);     ret.add(crd, {'field' : 'lastEdited','type' : 'date'});          if(doc.dateEdited){     var led = doc.dateEdited.substring(0, 19);     ret.add(led, {'field' : 'lastEdited','type' : 'date'});         }        return ret;   }") })
@Component
public class LuceneEventRepository extends CouchDbRepositorySupportWithLucene<Event> {
	
	private LuceneDbConnector ldb;
	
	@Autowired
	protected LuceneEventRepository(LuceneDbConnector db) {
		super(Event.class, db);
		this.ldb = db;
		initStandardDesignDocument();
	}
	
	public List<Event> getByCriteria(EventSearchBean eventSearchBean) {
		// create a simple query against the view/search function that we've created
		LuceneQuery query = new LuceneQuery("Event", "by_all_criteria");
		
		Query qf = new Query(FilterType.AND);
		addQueryParameter(qf, EVENT_DATE, eventSearchBean.getEventDateFrom(), eventSearchBean.getEventDateTo());
		addQueryParameter(qf, LAST_UPDATE, eventSearchBean.getLastEditFrom(), eventSearchBean.getLastEditTo());
		addQueryParameter(qf, BASE_ENTITY_ID, eventSearchBean.getBaseEntityId());
		addQueryParameter(qf, EVENT_TYPE, eventSearchBean.getEventType());
		addQueryParameter(qf, ENTITY_TYPE, eventSearchBean.getEntityType());
		addQueryParameter(qf, PROVIDER_ID, eventSearchBean.getProviderId());
		addQueryParameter(qf, LOCATION_ID, eventSearchBean.getLocationId());
		addQueryParameter(qf, TEAM, eventSearchBean.getTeam());
		addQueryParameter(qf, TEAM_ID, eventSearchBean.getTeamId());
		
		if (StringUtils.isEmptyOrWhitespaceOnly(qf.query())) {
			throw new RuntimeException("Atleast one search filter must be specified");
		}
		query.setQuery(qf.query());
		// stale must not be ok, as we've only just loaded the docs
		query.setStaleOk(false);
		query.setIncludeDocs(true);
		
		try {
			LuceneResult result = db.queryLucene(query);
			return ldb.asList(result, Event.class);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void addQueryParameter(Query query, String parameter, String value) {
		if (!StringUtils.isEmptyOrWhitespaceOnly(value))
			query.eq(parameter, value);
	}
	
	private void addQueryParameter(Query query, String parameter, DateTime from, DateTime to) {
		if (from != null && to != null) {
			query.between(parameter, from, to);
		}
	}
	
	/**
	 * @param providerId- health worker id or comma separated health worker ids
	 * @param locationId
	 * @param baseEntityId
	 * @param serverVersion
	 * @param sortBy Prefix with / for ascending order and \ for descending order (ascending is the
	 *            default if not specified).
	 * @param sortOrder either descending or ascending
	 * @param limit
	 * @param team this is a comma separated string of team names
	 * @return
	 */
	
	public List<Event> getByCriteria(EventSearchBean eventSearchBean, String sortBy, String sortOrder, int limit) {

		// create a simple query against the view/search function that we've created
		LuceneQuery query = new LuceneQuery("Event", "by_all_criteria_v2");
		
		Query qf = new Query(FilterType.AND);
		
		if (eventSearchBean.getServerVersion() != null) {
			qf.between(BaseEntity.SERVER_VERSIOIN, eventSearchBean.getServerVersion(), Long.MAX_VALUE);
		}
		
		if (eventSearchBean.getTeam() != null && !StringUtils.isEmptyOrWhitespaceOnly(eventSearchBean.getTeam())) {
			if (eventSearchBean.getTeam().contains(",")) {
				String[] teamArray = org.apache.commons.lang.StringUtils.split(eventSearchBean.getTeam(), ",");
				List<String> teams = new ArrayList<>(Arrays.asList(teamArray));
				qf.inList(TEAM, teams);
			} else {
				qf.eq(TEAM, eventSearchBean.getTeam());
			}
		}
		
		if (eventSearchBean.getTeamId() != null && !StringUtils.isEmptyOrWhitespaceOnly(eventSearchBean.getTeamId())) {
			if (eventSearchBean.getTeamId().contains(",")) {
				String[] teamArray = org.apache.commons.lang.StringUtils.split(eventSearchBean.getTeamId());
				List<String> teams = new ArrayList<>(Arrays.asList(teamArray));
				qf.inList(TEAM_ID, teams);
			} else {
				qf.eq(TEAM_ID, eventSearchBean.getTeamId());
			}
		}
		
		if ((eventSearchBean.getProviderId() != null && !StringUtils.isEmptyOrWhitespaceOnly(eventSearchBean.getProviderId()))) {
			if (eventSearchBean.getProviderId().contains(",")) {
				String[] providerArray = org.apache.commons.lang.StringUtils.split(eventSearchBean.getProviderId(), ",");
				List<String> providers = new ArrayList<>(Arrays.asList(providerArray));
				qf.inList(PROVIDER_ID, providers);
			} else {
				qf.eq(PROVIDER_ID, eventSearchBean.getProviderId());
			}
		}

		if (eventSearchBean.getLocationId() != null || !StringUtils.isEmptyOrWhitespaceOnly(eventSearchBean.getLocationId())) {
			if (eventSearchBean.getLocationId().contains(",")) {
				String[] locationArray = org.apache.commons.lang.StringUtils.split(eventSearchBean.getLocationId(), ",");
				List<String> locations = new ArrayList<>(Arrays.asList(locationArray));
				qf.inList(LOCATION_ID, locations);
			} else {
				qf.eq(LOCATION_ID, eventSearchBean.getLocationId());
			}
		}
		
		if (!StringUtils.isEmptyOrWhitespaceOnly(eventSearchBean.getBaseEntityId())) {
			if (eventSearchBean.getBaseEntityId().contains(",")) {
				Query q = new Query(FilterType.OR);
				String[] idsArray = org.apache.commons.lang.StringUtils.split(eventSearchBean.getBaseEntityId(), ",");
				List<String> ids = new ArrayList<String>(Arrays.asList(idsArray));
				q.inList(BASE_ENTITY_ID, ids);

				qf.addToQuery(q);
			} else {
				qf.eq(BASE_ENTITY_ID, eventSearchBean.getBaseEntityId());
			}
		}
		
		if (StringUtils.isEmptyOrWhitespaceOnly(qf.query())) {
			throw new RuntimeException("Atleast one search filter must be specified");
		}
		query.setQuery(qf.query());
		// stale must not be ok, as we've only just loaded the docs
		query.setStaleOk(false);
		query.setIncludeDocs(true);
		query.setLimit(limit);
		query.setSort((sortOrder.toLowerCase().contains("desc") ? "\\" : "/") + sortBy);
		
		try {
			LuceneResult result = db.queryLucene(query);
			return ldb.asList(result, Event.class);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public List<Event> getByCriteria(String query) {
		// create a simple query against the view/search function that we've created
		LuceneQuery q = new LuceneQuery("Event", "by_all_criteria");
		
		q.setQuery(query);
		// stale must not be ok, as we've only just loaded the docs
		q.setStaleOk(false);
		q.setIncludeDocs(true);
		
		try {
			LuceneResult result = db.queryLucene(q);
			return ldb.asList(result, Event.class);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
