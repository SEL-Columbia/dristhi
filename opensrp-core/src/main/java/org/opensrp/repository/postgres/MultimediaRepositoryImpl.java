package org.opensrp.repository.postgres;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.opensrp.domain.Multimedia;
import org.opensrp.domain.postgres.MultiMedia;
import org.opensrp.domain.postgres.MultiMediaExample;
import org.opensrp.repository.MultimediaRepository;
import org.opensrp.repository.postgres.mapper.custom.CustomMultiMediaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class MultimediaRepositoryImpl extends BaseRepositoryImpl<Multimedia> implements MultimediaRepository {
	
	@Autowired
	private CustomMultiMediaMapper multiMediaMapper;
	
	@Override
	public Multimedia get(String id) {
		MultiMediaExample example = new MultiMediaExample();
		example.createCriteria().andDocumentIdEqualTo(id);
		List<MultiMedia> files = multiMediaMapper.selectByExample(example);
		return files.isEmpty() ? null : convert(files.get(0));
	}
	
	@Override
	public void add(Multimedia entity) {
		if (entity == null || entity.getCaseId() == null) {
			return;
		}
		
		if (retrievePrimaryKey(entity) != null) { //Multimedia already added
			return;
		}
		
		if (entity.getId() == null)
			entity.setId(UUID.randomUUID().toString());
		
		org.opensrp.domain.postgres.MultiMedia pgMultiMedia = convert(entity, null);
		if (pgMultiMedia == null) {
			return;
		}
		
		multiMediaMapper.insertSelective(pgMultiMedia);
		
	}
	
	@Override
	public void update(Multimedia entity) {
		if (entity == null || entity.getId() == null || entity.getCaseId() == null) {
			return;
		}
		
		Long id = retrievePrimaryKey(entity);
		
		if (id == null) { //Multimedia doesn't not exist
			return;
		}
		
		MultiMedia pgEntity = convert(entity, id);
		multiMediaMapper.updateByPrimaryKey(pgEntity);
		
	}
	
	@Override
	public List<Multimedia> getAll() {
		return convert(multiMediaMapper.selectMany(new MultiMediaExample(), 0, DEFAULT_FETCH_SIZE));
	}
	
	@Override
	public void safeRemove(Multimedia entity) {
		if (entity == null || entity.getCaseId() == null) {
			return;
		}
		
		Long id = retrievePrimaryKey(entity);
		if (id == null) {
			return;
		}
		
		multiMediaMapper.deleteByPrimaryKey(id);
		
	}
	
	@Override
	public Multimedia findByCaseId(String entityId) {
		MultiMediaExample example = new MultiMediaExample();
		example.createCriteria().andCaseIdEqualTo(entityId);
		List<MultiMedia> multiMediaFiles = multiMediaMapper.selectByExample(example);
		return multiMediaFiles.isEmpty() ? null : convert(multiMediaFiles.get(0));
	}
	
	@Override
	public List<Multimedia> all(String providerId) {
		MultiMediaExample example = new MultiMediaExample();
		example.createCriteria().andProviderIdEqualTo(providerId);
		List<MultiMedia> multiMediaFiles = multiMediaMapper.selectByExample(example);
		return convert(multiMediaFiles);
	}
	
	@Override
	protected Long retrievePrimaryKey(Multimedia multimedia) {
		if (multimedia == null) {
			return null;
		}
		String documentId = multimedia.getId();
		
		MultiMediaExample example = new MultiMediaExample();
		example.createCriteria().andDocumentIdEqualTo(documentId);
		List<MultiMedia> files = multiMediaMapper.selectByExample(example);
		return files.isEmpty() ? null : files.get(0).getId();
	}
	
	@Override
	protected Object getUniqueField(Multimedia multiMedia) {
		return multiMedia == null ? multiMedia : multiMedia.getId();
	}
	
	//private Methods
	private Multimedia convert(MultiMedia pgMultiMedia) {
		Multimedia multimedia = new Multimedia();
		multimedia.setId(pgMultiMedia.getDocumentId());
		multimedia.setCaseId(pgMultiMedia.getCaseId());
		multimedia.setProviderId(pgMultiMedia.getProviderId());
		multimedia.setContentType(pgMultiMedia.getContentType());
		multimedia.setFilePath(pgMultiMedia.getFilePath());
		multimedia.setFileCategory(pgMultiMedia.getFileCategory());
		return multimedia;
	}
	
	private MultiMedia convert(Multimedia entity, Long primaryKey) {
		if (entity == null) {
			return null;
		}
		MultiMedia pgMultiMedia = new MultiMedia();
		pgMultiMedia.setId(primaryKey);
		pgMultiMedia.setDocumentId(entity.getId());
		pgMultiMedia.setCaseId(entity.getCaseId());
		pgMultiMedia.setProviderId(entity.getProviderId());
		pgMultiMedia.setContentType(entity.getContentType());
		pgMultiMedia.setFilePath(entity.getFilePath());
		pgMultiMedia.setFileCategory(entity.getFileCategory());
		return pgMultiMedia;
	}
	
	private List<Multimedia> convert(List<MultiMedia> multiMediaFiles) {
		if (multiMediaFiles == null || multiMediaFiles.isEmpty()) {
			return new ArrayList<>();
		}
		List<Multimedia> convertedList = new ArrayList<>();
		for (MultiMedia pgMultiMedia : multiMediaFiles) {
			Multimedia multimedia = convert(pgMultiMedia);
			if (multimedia != null) {
				convertedList.add(multimedia);
			}
		}
		
		return convertedList;
	}
	
}
