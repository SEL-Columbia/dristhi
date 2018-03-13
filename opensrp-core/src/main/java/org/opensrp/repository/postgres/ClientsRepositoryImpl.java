package org.opensrp.repository.postgres;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.opensrp.common.AllConstants;
import org.opensrp.domain.Client;
import org.opensrp.domain.postgres.ClientMetadata;
import org.opensrp.domain.postgres.ClientMetadataExample;
import org.opensrp.repository.ClientsRepository;
import org.opensrp.repository.postgres.mapper.custom.MyClientMapper;
import org.opensrp.repository.postgres.mapper.custom.MyClientMetadataMapper;
import org.opensrp.search.AddressSearchBean;
import org.opensrp.search.ClientSearchBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ClientsRepositoryImpl extends RepositoryHelper implements ClientsRepository {
	
	private static Logger logger = LoggerFactory.getLogger(ClientsRepository.class.toString());
	
	@Autowired
	private MyClientMetadataMapper myClientMetadataMapper;
	
	@Autowired
	private MyClientMapper myClientMapper;
	
	@Override
	public Client get(String id) {
		if (StringUtils.isBlank(id)) {
			return null;
		}
		
		org.opensrp.domain.postgres.Client pgClient = myClientMapper.selectByPrimaryKey(Long.valueOf(id));
		if (pgClient == null) {
			return null;
		}
		return convert(pgClient);
	}
	
	@Override
	public void add(Client entity) {
		if (entity == null || entity.getBaseEntityId() == null) {
			return;
		}
		
		org.opensrp.domain.postgres.Client pgClient = myClientMetadataMapper.selectOne(entity.getBaseEntityId());
		if (pgClient != null) {
			update(entity);
		}
		
		pgClient = convert(entity);
		if (pgClient == null) {
			return;
		}
		
		int rowsAffected = myClientMapper.insertSelectiveAndSetId(pgClient);
		if (rowsAffected <= 0 || pgClient.getId() == null) {
			return;
		}
		
		ClientMetadata clientMetadata = createMetadata(entity, pgClient.getId());
		if (clientMetadata != null) {
			myClientMetadataMapper.insertSelective(clientMetadata);
		}
	}
	
	@Override
	public void update(Client entity) {
		if (entity == null || entity.getBaseEntityId() == null) {
			return;
		}
		
		org.opensrp.domain.postgres.Client pgClient = myClientMetadataMapper.selectOne(entity.getBaseEntityId());
		if (pgClient == null) {
			return;
		}
		
		org.opensrp.domain.postgres.Client convertedPgClient = convert(entity);
		if (convertedPgClient == null) {
			return;
		}
		
		pgClient.setJson(convertedPgClient.getJson());
		myClientMapper.updateByPrimaryKey(pgClient);
	}
	
	@Override
	public List<Client> getAll() {
		List<org.opensrp.domain.postgres.Client> clients = myClientMetadataMapper.selectMany(new ClientMetadataExample());
		return convert(clients);
	}
	
	@Override
	public void safeRemove(Client entity) {
		if (entity == null || entity.getBaseEntityId() == null) {
			return;
		}
		org.opensrp.domain.postgres.Client pgClient = myClientMetadataMapper.selectOne(entity.getBaseEntityId());
		if (pgClient == null) {
			return;
		}
		
		ClientMetadataExample clientMetadataExample = new ClientMetadataExample();
		clientMetadataExample.createCriteria().andClientIdEqualTo(pgClient.getId());
		myClientMetadataMapper.deleteByExample(clientMetadataExample);
		
		myClientMapper.deleteByPrimaryKey(pgClient.getId());
	}
	
	@Override
	public Client findByBaseEntityId(String baseEntityId) {
		if (StringUtils.isBlank(baseEntityId)) {
			return null;
		}
		ClientMetadataExample clientMetadataExample = new ClientMetadataExample();
		clientMetadataExample.createCriteria().andBaseEntityIdEqualTo(baseEntityId);
		List<org.opensrp.domain.postgres.Client> clients = myClientMetadataMapper.selectMany(clientMetadataExample);
		if (clients == null || clients.isEmpty()) {
			return null;
		}
		
		return convert(clients.get(0));
	}
	
	@Override
	public List<Client> findAllClients() {
		return getAll();
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
		ClientMetadataExample clientMetadataExample = new ClientMetadataExample();
		clientMetadataExample.createCriteria().andServerVersionIsNull();
		
		List<org.opensrp.domain.postgres.Client> clients = myClientMetadataMapper.selectMany(clientMetadataExample);
		return convert(clients);
	}
	
	@Override
	public List<Client> findByServerVersion(long serverVersion) {
		ClientMetadataExample clientMetadataExample = new ClientMetadataExample();
		clientMetadataExample.createCriteria().andServerVersionEqualTo(serverVersion);
		
		List<org.opensrp.domain.postgres.Client> clients = myClientMetadataMapper.selectMany(clientMetadataExample);
		return convert(clients);
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
	
	private List<Client> convert(List<org.opensrp.domain.postgres.Client> clients) {
		if (clients == null || clients.isEmpty()) {
			return new ArrayList<>();
		}
		
		List<Client> convertedClients = new ArrayList<>();
		for (org.opensrp.domain.postgres.Client client : clients) {
			Client convertedClient = convert(client);
			if (convertedClient != null) {
				convertedClients.add(convertedClient);
			}
		}
		
		return convertedClients;
	}
	
	private Client convert(org.opensrp.domain.postgres.Client client) {
		if (client == null || client.getJson() == null) {
			return null;
		}
		
		try {
			
			Object json = client.getJson();
			if (StringUtils.isBlank(json.toString())) {
				return null;
			}
			
			return RepositoryHelper.gson.fromJson(json.toString(), Client.class);
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}
	
	private org.opensrp.domain.postgres.Client convert(Client client) {
		if (client == null) {
			return null;
		}
		
		try {
			String jsonString = RepositoryHelper.gson.toJson(client);
			
			org.opensrp.domain.postgres.Client pgClient = new org.opensrp.domain.postgres.Client();
			pgClient.setJson(jsonString);
			
			return pgClient;
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}
	
	private ClientMetadata createMetadata(Client client, Long savedId) {
		try {
			ClientMetadata clientMetadata = new ClientMetadata();
			clientMetadata.setBaseEntityId(client.getBaseEntityId());
			if (client.getBirthdate() != null) {
				clientMetadata.setBirthDate(client.getBirthdate().toDate());
			}
			clientMetadata.setClientId(savedId);
			clientMetadata.setFirstName(client.getFirstName());
			clientMetadata.setLastName(client.getLastName());
			
			String relationalId = null;
			Map<String, List<String>> relationShips = client.getRelationships();
			if (relationShips != null && !relationShips.isEmpty()) {
				for (Map.Entry<String, List<String>> maEntry : relationShips.entrySet()) {
					List<String> values = maEntry.getValue();
					if (values != null && !values.isEmpty()) {
						relationalId = values.get(0);
						break;
					}
				}
			}
			clientMetadata.setRelationalId(relationalId);
			
			String uniqueId = null;
			String openmrsUUID = null;
			Map<String, String> identifiers = client.getIdentifiers();
			if (identifiers != null && !identifiers.isEmpty()) {
				for (Map.Entry<String, String> entry : identifiers.entrySet()) {
					String value = entry.getValue();
					if (StringUtils.isNotBlank(value)) {
						if (AllConstants.Client.OPENMRS_UUID_IDENTIFIER_TYPE.equalsIgnoreCase(entry.getKey())) {
							openmrsUUID = value;
						} else {
							uniqueId = value;
						}
					}
				}
			}
			
			clientMetadata.setUniqueId(uniqueId);
			clientMetadata.setOpenmrsUuid(openmrsUUID);
			return clientMetadata;
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}
}
