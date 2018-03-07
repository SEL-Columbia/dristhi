package org.opensrp.repository;

import java.util.Calendar;
import java.util.List;

import org.joda.time.DateTime;
import org.opensrp.domain.Client;

public interface ClientsRepository extends BaseRepository<Client> {
	
	public Client findByBaseEntityId(String baseEntityId);
	
	public List<Client> findAllClients();
	
	public List<Client> findAllByIdentifier(String identifier);
	
	public List<Client> findAllByIdentifier(String identifierType, String identifier);
	
	public List<Client> findAllByAttribute(String attributeType, String attribute);
	
	public List<Client> findAllByMatchingName(String nameMatches);
	
	/**
	 * Find a client based on the relationship id and between a range of date created dates e.g
	 * given mother's id get children born at a given time
	 *
	 * @param relationalId
	 * @param dateFrom
	 * @param dateTo
	 * @return
	 */
	public List<Client> findByRelationshipIdAndDateCreated(String relationalId, String dateFrom, String dateTo);
	
	public List<Client> findByRela3tionshipId(String relationshipType, String entityId);
	
	public List<Client> findByCriteria(String nameLike, String gender, DateTime birthdateFrom, DateTime birthdateTo,
	                                   DateTime deathdateFrom, DateTime deathdateTo, String attributeType,
	                                   String attributeValue, String addressType, String country, String stateProvince,
	                                   String cityVillage, String countyDistrict, String subDistrict, String town,
	                                   String subTown, DateTime lastEditFrom, DateTime lastEditTo);
	
	public List<Client> findByDynamicQuery(String query);
	
	public List<Client> findByCriteria(String nameLike, String gender, DateTime birthdateFrom, DateTime birthdateTo,
	                                   DateTime deathdateFrom, DateTime deathdateTo, String attributeType,
	                                   String attributeValue, DateTime lastEditFrom, DateTime lastEditTo);
	
	public List<Client> findByCriteria(String addressType, String country, String stateProvince, String cityVillage,
	                                   String countyDistrict, String subDistrict, String town, String subTown,
	                                   DateTime lastEditFrom, DateTime lastEditTo);
	
	public List<Client> findByRelationShip(String relationIndentier);
	
	public List<Client> findByEmptyServerVersion();
	
	public List<Client> findByServerVersion(long serverVersion);
	
	public List<Client> findByFieldValue(String field, List<String> ids);
	
	public List<Client> notInOpenMRSByServerVersion(long serverVersion, Calendar calendar);
}
