package org.opensrp.repository.postgres;

import java.util.Calendar;
import java.util.List;

import org.joda.time.DateTime;
import org.opensrp.domain.Client;
import org.opensrp.repository.ClientsRepository;
import org.opensrp.search.AddressSearchBean;
import org.opensrp.search.ClientSearchBean;
import org.springframework.stereotype.Repository;

@Repository
public class ClientsRepositoryImpl implements ClientsRepository {

	@Override
	public Client get(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void add(Client entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(Client entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Client> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void safeRemove(Client entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Client findByBaseEntityId(String baseEntityId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Client> findAllClients() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Client> findAllByIdentifier(String identifier) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Client> findAllByIdentifier(String identifierType, String identifier) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Client> findAllByAttribute(String attributeType, String attribute) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Client> findAllByMatchingName(String nameMatches) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Client> findByRelationshipIdAndDateCreated(String relationalId, String dateFrom, String dateTo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Client> findByRelationshipId(String relationshipType, String entityId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Client> findByCriteria(ClientSearchBean searchBean, AddressSearchBean addressSearchBean,
	                                   DateTime lastEditFrom, DateTime lastEditTo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Client> findByDynamicQuery(String query) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Client> findByCriteria(ClientSearchBean searchBean) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Client> findByCriteria(AddressSearchBean addressSearchBean, DateTime lastEditFrom, DateTime lastEditTo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Client> findByRelationShip(String relationIndentier) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Client> findByEmptyServerVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Client> findByServerVersion(long serverVersion) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Client> findByFieldValue(String field, List<String> ids) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Client> notInOpenMRSByServerVersion(long serverVersion, Calendar calendar) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
