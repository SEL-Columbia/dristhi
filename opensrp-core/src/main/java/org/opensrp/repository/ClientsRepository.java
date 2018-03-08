package org.opensrp.repository;

import java.util.Calendar;
import java.util.List;

import org.joda.time.DateTime;
import org.opensrp.domain.Client;
import org.opensrp.search.AddressSearchBean;
import org.opensrp.search.ClientSearchBean;

public interface ClientsRepository extends BaseRepository<Client> {
	
	Client findByBaseEntityId(String baseEntityId);
	
	List<Client> findAllClients();
	
	List<Client> findAllByIdentifier(String identifier);
	
	List<Client> findAllByIdentifier(String identifierType, String identifier);
	
	List<Client> findAllByAttribute(String attributeType, String attribute);
	
	List<Client> findAllByMatchingName(String nameMatches);
	
	/**
	 * Find a client based on the relationship id and between a range of date created dates e.g
	 * given mother's id get children born at a given time
	 *
	 * @param relationalId
	 * @param dateFrom
	 * @param dateTo
	 * @return
	 */
	List<Client> findByRelationshipIdAndDateCreated(String relationalId, String dateFrom, String dateTo);
	
	List<Client> findByRelationshipId(String relationshipType, String entityId);
	
	List<Client> findByCriteria(ClientSearchBean searchBean, AddressSearchBean addressSearchBean, DateTime lastEditFrom,
	                            DateTime lastEditTo);
	
	List<Client> findByDynamicQuery(String query);
	
	List<Client> findByCriteria(ClientSearchBean searchBean);
	
	List<Client> findByCriteria(AddressSearchBean addressSearchBean, DateTime lastEditFrom, DateTime lastEditTo);
	
	List<Client> findByRelationShip(String relationIndentier);
	
	List<Client> findByEmptyServerVersion();
	
	List<Client> findByServerVersion(long serverVersion);
	
	List<Client> findByFieldValue(String field, List<String> ids);
	
	List<Client> notInOpenMRSByServerVersion(long serverVersion, Calendar calendar);
}
