package org.opensrp.repository.postgres;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.opensrp.domain.postgres.ViewConfigurationMetadata;
import org.opensrp.domain.postgres.ViewConfigurationMetadataExample;
import org.opensrp.domain.viewconfiguration.ViewConfiguration;
import org.opensrp.repository.ViewConfigurationRepository;
import org.opensrp.repository.postgres.mapper.custom.CustomViewConfigurationMapper;
import org.opensrp.repository.postgres.mapper.custom.CustomViewConfigurationMetadataMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("viewConfigurationRepositoryPostgres")
public class ViewConfigurationRepositoryImpl extends BaseRepositoryImpl<ViewConfiguration> implements ViewConfigurationRepository {
	
	@Autowired
	private CustomViewConfigurationMapper viewConfigurationMapper;
	
	@Autowired
	private CustomViewConfigurationMetadataMapper viewConfigurationMetadataMapper;
	
	@Override
	public ViewConfiguration get(String id) {
		return convert(viewConfigurationMetadataMapper.selectByDocumentId(id));
	}
	
	@Override
	public void add(ViewConfiguration entity) {
		if (entity == null || entity.getIdentifier() == null) {
			return;
		}
		
		if (retrievePrimaryKey(entity) != null) { //ViewConfiguration already added
			return;
		}
		
		if (entity.getId() == null)
			entity.setId(UUID.randomUUID().toString());
		setRevision(entity);
		
		org.opensrp.domain.postgres.ViewConfiguration pgViewConfiguration = convert(entity, null);
		if (pgViewConfiguration == null) {
			return;
		}
		
		int rowsAffected = viewConfigurationMapper.insertSelectiveAndSetId(pgViewConfiguration);
		if (rowsAffected < 1 || pgViewConfiguration.getId() == null) {
			return;
		}
		
		ViewConfigurationMetadata metadata = createMetadata(entity, pgViewConfiguration.getId());
		if (metadata != null) {
			viewConfigurationMetadataMapper.insertSelective(metadata);
		}
	}
	
	@Override
	public void update(ViewConfiguration entity) {
		if (entity == null || entity.getId() == null || entity.getIdentifier() == null) {
			return;
		}
		
		Long id = retrievePrimaryKey(entity);
		
		if (id == null) { //ViewConfiguration not exists 
			return;
		}
		
		setRevision(entity);
		
		org.opensrp.domain.postgres.ViewConfiguration pgViewConfiguration = convert(entity, id);
		
		if (pgViewConfiguration == null) {
			return;
		}
		
		ViewConfigurationMetadata metadata = createMetadata(entity, id);
		if (metadata == null) {
			return;
		}
		
		int rowsAffected = viewConfigurationMapper.updateByPrimaryKey(pgViewConfiguration);
		if (rowsAffected < 1) {
			return;
		}
		
		ViewConfigurationMetadataExample metadataExample = new ViewConfigurationMetadataExample();
		metadataExample.createCriteria().andViewConfigurationIdEqualTo(id);
		metadata.setId(viewConfigurationMetadataMapper.selectByExample(metadataExample).get(0).getId());
		viewConfigurationMetadataMapper.updateByPrimaryKey(metadata);
		
	}
	
	@Override
	public List<ViewConfiguration> getAll() {
		return convert(
		    viewConfigurationMetadataMapper.selectMany(new ViewConfigurationMetadataExample(), 0, DEFAULT_FETCH_SIZE));
	}
	
	@Override
	public void safeRemove(ViewConfiguration entity) {
		if (entity == null) {
			return;
		}
		
		Long id = retrievePrimaryKey(entity);
		if (id == null) {
			return;
		}
		
		ViewConfigurationMetadataExample metadataExample = new ViewConfigurationMetadataExample();
		metadataExample.createCriteria().andViewConfigurationIdEqualTo(id);
		int rowsAffected = viewConfigurationMetadataMapper.deleteByExample(metadataExample);
		if (rowsAffected < 1) {
			return;
		}
		
		viewConfigurationMapper.deleteByPrimaryKey(id);
		
	}
	
	@Override
	public List<ViewConfiguration> findAllViewConfigurations() {
		return getAll();
	}
	
	@Override
	public List<ViewConfiguration> findViewConfigurationsByVersion(Long lastSyncedServerVersion) {
		ViewConfigurationMetadataExample metadataExample = new ViewConfigurationMetadataExample();
		metadataExample.createCriteria().andServerVersionGreaterThanOrEqualTo(lastSyncedServerVersion);
		return convert(viewConfigurationMetadataMapper.selectMany(metadataExample, 0, DEFAULT_FETCH_SIZE));
	}
	
	@Override
	public List<ViewConfiguration> findByEmptyServerVersion() {
		ViewConfigurationMetadataExample metadataExample = new ViewConfigurationMetadataExample();
		metadataExample.createCriteria().andServerVersionIsNull();
		metadataExample.or(metadataExample.createCriteria().andServerVersionEqualTo(0l));
		return convert(viewConfigurationMetadataMapper.selectMany(metadataExample, 0, DEFAULT_FETCH_SIZE));
	}
	
	@Override
	protected Long retrievePrimaryKey(ViewConfiguration viewConfiguration) {
		if (getUniqueField(viewConfiguration) == null) {
			return null;
		}
		String documentId = viewConfiguration.getId();
		
		ViewConfigurationMetadataExample metadataExample = new ViewConfigurationMetadataExample();
		metadataExample.createCriteria().andDocumentIdEqualTo(documentId);
		
		org.opensrp.domain.postgres.ViewConfiguration pgViewConfiguration = viewConfigurationMetadataMapper
		        .selectByDocumentId(documentId);
		if (pgViewConfiguration == null) {
			return null;
		}
		return pgViewConfiguration.getId();
	}
	
	@Override
	protected Object getUniqueField(ViewConfiguration viewConfiguration) {
		return viewConfiguration == null ? null : viewConfiguration.getId();
	}
	
	//private Methods
	private ViewConfiguration convert(org.opensrp.domain.postgres.ViewConfiguration viewConfiguration) {
		if (viewConfiguration == null || viewConfiguration.getJson() == null
		        || !(viewConfiguration.getJson() instanceof ViewConfiguration)) {
			return null;
		}
		return (ViewConfiguration) viewConfiguration.getJson();
	}
	
	private org.opensrp.domain.postgres.ViewConfiguration convert(ViewConfiguration entity, Long id) {
		if (entity == null) {
			return null;
		}
		
		org.opensrp.domain.postgres.ViewConfiguration pgViewConfiguration = new org.opensrp.domain.postgres.ViewConfiguration();
		pgViewConfiguration.setId(id);
		pgViewConfiguration.setJson(entity);
		
		return pgViewConfiguration;
	}
	
	private List<ViewConfiguration> convert(List<org.opensrp.domain.postgres.ViewConfiguration> views) {
		if (views == null || views.isEmpty()) {
			return new ArrayList<>();
		}
		
		List<ViewConfiguration> viewConfigurations = new ArrayList<>();
		for (org.opensrp.domain.postgres.ViewConfiguration view : views) {
			ViewConfiguration convertedView = convert(view);
			if (convertedView != null) {
				viewConfigurations.add(convertedView);
			}
		}
		return viewConfigurations;
	}
	
	private ViewConfigurationMetadata createMetadata(ViewConfiguration entity, Long id) {
		ViewConfigurationMetadata metadata = new ViewConfigurationMetadata();
		metadata.setViewConfigurationId(id);
		metadata.setDocumentId(entity.getId());
		metadata.setIdentifier(entity.getIdentifier());
		metadata.setServerVersion(entity.getServerVersion());
		return metadata;
	}
	
}
