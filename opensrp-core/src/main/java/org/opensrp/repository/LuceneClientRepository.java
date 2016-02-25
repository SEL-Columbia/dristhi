package org.opensrp.repository;

import java.io.IOException;
import java.util.List;

import org.joda.time.DateTime;
import org.opensrp.domain.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.ldriscoll.ektorplucene.CouchDbRepositorySupportWithLucene;
import com.github.ldriscoll.ektorplucene.LuceneQuery;
import com.github.ldriscoll.ektorplucene.LuceneResult;
import com.github.ldriscoll.ektorplucene.designdocument.annotation.FullText;
import com.github.ldriscoll.ektorplucene.designdocument.annotation.Index;
import com.mysql.jdbc.StringUtils;

import static org.opensrp.common.AllConstants.Client.*;

@FullText({
    @Index(
        name = "by_all_criteria",
        index = "function(doc) {"
        		+ "	if(doc.type !== 'Client') return null;"
        		+ "	var docl = new Array();"
        		+ "	var len = doc.addresses?doc.addresses.length:1;"
        		+ "	for(var i = 0; i < len; i++) {	"
        		+ "		var arr1 = ['firstName','middleName','lastName','gender'];"
        		+ "		var arr2 = ['addressType','country','stateProvince','cityVillage','countyDistrict','subDistrict','town','subTown'];"
        		+ "		var ret = new Document();"
        		+ "		for (var i in arr1){"
        		+ "			ret.add(doc[arr1[i]], {'field':arr1[i]});"
        		+ "		}"
        		+ "		for(var key in doc.attributes) {"
        		+ "			ret.add(doc.attributes[key], {'field':key});"
        		+ "		}"
        		+ "		if(doc.addresses){"
        		+ "			var ad = doc.addresses[i];"
        		+ "			if(ad)"
        		+ "			for (var i in arr2){"
        		+ "				ret.add(ad[arr2[i]], {'field':arr2[i]});"
        		+ "			}"
        		+ "		}"
        		+ "		if(doc.birthdate){"
        		+ "			var bd=doc.birthdate.substring(0,19); "
        		+ "			ret.add(bd, {'field':'birthdate','type':'date'});"
        		+ "		}"
        		+ "		if(doc.deathdate){"
        		+ "			var dd=doc.deathdate.substring(0,19); "
        		+ "			ret.add(dd, {'field':'deathdate','type':'date'});"
        		+ "		}"
        		+ "		docl.push(ret);"
        		+ "	}"
        		+ "    return docl;"
        		+ "}")
})
@Component
public class LuceneClientRepository extends CouchDbRepositorySupportWithLucene<Client>{

	private LuceneDbConnector ldb;
	
	@Autowired
	protected LuceneClientRepository(LuceneDbConnector db) {
		super(Client.class, db);
		this.ldb = db;
		initStandardDesignDocument();
	}
	
	public List<Client> getByCriteria(String nameLike, String gender, DateTime birthdate, DateTime deathdate, 
			String attributeType, String attributeValue){
		return getByCriteria(nameLike, gender, birthdate, deathdate, attributeType, attributeValue, null, null, null, null, null, null, null, null);
	}
	
	public List<Client> getByCriteria(String addressType, String country, String stateProvince, String cityVillage, String countyDistrict, 
			String  subDistrict, String town, String subTown) {
		return getByCriteria(null, null, null, null, null, null, addressType, country, stateProvince, cityVillage, countyDistrict, subDistrict, town, subTown);
	}
	
	public List<Client> getByCriteria(String nameLike, String gender, DateTime birthdate, DateTime deathdate, 
			String attributeType, String attributeValue, 
			String addressType, String country, String stateProvince, String cityVillage, String countyDistrict, 
			String  subDistrict, String town, String subTown) {
		// create a simple query against the view/search function that we've created
		LuceneQuery query = new LuceneQuery("Client", "by_all_criteria");
		
		Query q = new Query(FilterType.OR);
		if(!StringUtils.isEmptyOrWhitespaceOnly(nameLike)){
			q.like(FIRST_NAME, nameLike);
			q.like(MIDDLE_NAME, nameLike);
			q.like(LAST_NAME, nameLike);
		}
		Query qf = new Query(FilterType.AND, q);
		if(!StringUtils.isEmptyOrWhitespaceOnly(gender)){
			qf.eq(GENDER, gender);
		}
		if(birthdate != null){
			qf.eq(BIRTH_DATE, birthdate);
		}
		if(deathdate != null){
			qf.eq(DEATH_DATE, deathdate);
		}
		if(!StringUtils.isEmptyOrWhitespaceOnly(attributeType)){
			qf.eq(attributeType, attributeValue);
		}
		if(!StringUtils.isEmptyOrWhitespaceOnly(addressType)){
			qf.eq(ADDRESS_TYPE, addressType);
		}
		if(!StringUtils.isEmptyOrWhitespaceOnly(country)){
			qf.eq(COUNTRY, country);
		}
		if(!StringUtils.isEmptyOrWhitespaceOnly(stateProvince)){
			qf.eq(STATE_PROVINCE, stateProvince);
		}
		if(!StringUtils.isEmptyOrWhitespaceOnly(cityVillage)){
			qf.eq(CITY_VILLAGE, cityVillage);
		}
		if(!StringUtils.isEmptyOrWhitespaceOnly(countyDistrict)){
			qf.eq(COUNTY_DISTRICT, countyDistrict);
		}
		if(!StringUtils.isEmptyOrWhitespaceOnly(subDistrict)){
			qf.eq(SUB_DISTRICT, subDistrict);
		}
		if(!StringUtils.isEmptyOrWhitespaceOnly(town)){
			qf.eq(TOWN, town);
		}
		if(!StringUtils.isEmptyOrWhitespaceOnly(subTown)){
			qf.eq(SUB_TOWN, subTown);
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
			return ldb.asList(result, Client.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} 
	}
}
