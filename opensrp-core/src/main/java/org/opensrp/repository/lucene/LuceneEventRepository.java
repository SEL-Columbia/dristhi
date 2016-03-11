package org.opensrp.repository.lucene;

import java.io.IOException;
import java.util.List;

import org.joda.time.DateTime;
import org.opensrp.domain.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.ldriscoll.ektorplucene.CouchDbRepositorySupportWithLucene;
import com.github.ldriscoll.ektorplucene.LuceneQuery;
import com.github.ldriscoll.ektorplucene.LuceneResult;
import com.github.ldriscoll.ektorplucene.designdocument.annotation.FullText;
import com.github.ldriscoll.ektorplucene.designdocument.annotation.Index;
import com.mysql.jdbc.StringUtils;

import static org.opensrp.common.AllConstants.Event.*;

@FullText({
    @Index(
        name = "by_all_criteria",
        index = "function(doc) {"
        		+ "	if(doc.type !== 'Event') return null;"
        		+ "	var arr1 = ['eventType','entityType','providerId','locationId'];"
        		+ "	var ret = new Document();"
        		+ "	for (var i in arr1){"
        		+ "		ret.add(doc[arr1[i]], {'field':arr1[i]});"
        		+ "	}"
        		+ "	if(doc.eventDate){"
        		+ "		var bd=doc.eventDate.substring(0,19); "
        		+ "		ret.add(bd, {'field':'eventDate','type':'date'});"
        		+ "	}"
        		+ " return ret;"
        		+ "}")
})
@Component
public class LuceneEventRepository extends CouchDbRepositorySupportWithLucene<Event>{

	private LuceneDbConnector ldb;
	
	@Autowired
	protected LuceneEventRepository(LuceneDbConnector db) {
		super(Event.class, db);
		this.ldb = db;
		initStandardDesignDocument();
	}
	
	public List<Event> getByCriteria(DateTime eventDatefrom, DateTime eventDateto, String eventType, String entityType, String providerId, String locationId) {
		// create a simple query against the view/search function that we've created
		LuceneQuery query = new LuceneQuery("Event", "by_all_criteria");
		
		Query qf = new Query(FilterType.AND);
		if(eventDatefrom != null && eventDateto != null){
			qf.between(EVENT_DATE, eventDatefrom, eventDateto);
		}
		if(!StringUtils.isEmptyOrWhitespaceOnly(eventType)){
			qf.eq(EVENT_TYPE, eventType);
		}
		if(!StringUtils.isEmptyOrWhitespaceOnly(entityType)){
			qf.eq(ENTITY_TYPE, entityType);
		}
		if(!StringUtils.isEmptyOrWhitespaceOnly(providerId)){
			qf.eq(PROVIDER_ID, providerId);
		}
		if(!StringUtils.isEmptyOrWhitespaceOnly(locationId)){
			qf.eq(LOCATION_ID, locationId);
		}
		
		if(StringUtils.isEmptyOrWhitespaceOnly(qf.query())){
			throw new RuntimeException("Atleast one search filter must be specified");
		}
		query.setQuery(qf.query());
		// stale must not be ok, as we've only just loaded the docs
		query.setStaleOk(false);
		query.setIncludeDocs(true);

		try {
			LuceneResult result = db.queryLucene(query);
			return ldb.asList(result, Event.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} 
	}
}
