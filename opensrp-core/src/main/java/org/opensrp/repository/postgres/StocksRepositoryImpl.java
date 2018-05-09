package org.opensrp.repository.postgres;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.opensrp.domain.Stock;
import org.opensrp.domain.postgres.StockMetadata;
import org.opensrp.domain.postgres.StockMetadataExample;
import org.opensrp.repository.StocksRepository;
import org.opensrp.repository.postgres.mapper.custom.CustomStockMapper;
import org.opensrp.repository.postgres.mapper.custom.CustomStockMetadataMapper;
import org.opensrp.search.StockSearchBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("stocksRepositoryPostgres")
public class StocksRepositoryImpl extends BaseRepositoryImpl<Stock> implements StocksRepository {
	
	@Autowired
	private CustomStockMapper stockMapper;
	
	@Autowired
	private CustomStockMetadataMapper stockMetadataMapper;
	
	@Override
	public Stock get(String id) {
		if (StringUtils.isBlank(id)) {
			return null;
		}
		org.opensrp.domain.postgres.Stock pgStock = stockMetadataMapper.selectByDocumentId(id);
		
		return convert(pgStock);
	}
	
	@Override
	public void add(Stock entity) {
		if (entity == null) {
			return;
		}
		
		if (retrievePrimaryKey(entity) != null) { //Stock already added
			return;
		}
		
		if (entity.getId() == null)
			entity.setId(UUID.randomUUID().toString());
		setRevision(entity);
		
		org.opensrp.domain.postgres.Stock pgStock = convert(entity, null);
		if (pgStock == null) {
			return;
		}
		
		int rowsAffected = stockMapper.insertSelectiveAndSetId(pgStock);
		
		if (rowsAffected < 1 || pgStock.getId() == null) {
			return;
		}
		
		StockMetadata stockMetadata = createMetadata(entity, pgStock.getId());
		if (stockMetadata != null) {
			stockMetadataMapper.insertSelective(stockMetadata);
		}
		
	}
	
	@Override
	public void update(Stock entity) {
		if (entity == null) {
			return;
		}
		
		Long id = retrievePrimaryKey(entity);
		if (id == null) { // Stock not added
			return;
		}
		
		setRevision(entity);
		org.opensrp.domain.postgres.Stock pgStock = convert(entity, id);
		if (pgStock == null) {
			return;
		}
		
		StockMetadata stockMetadata = createMetadata(entity, id);
		if (stockMetadata == null) {
			return;
		}
		
		int rowsAffected = stockMapper.updateByPrimaryKey(pgStock);
		if (rowsAffected < 1) {
			return;
		}
		
		StockMetadataExample stockMetadataExample = new StockMetadataExample();
		stockMetadataExample.createCriteria().andStockIdEqualTo(id);
		stockMetadata.setId(stockMetadataMapper.selectByExample(stockMetadataExample).get(0).getId());
		stockMetadataMapper.updateByPrimaryKey(stockMetadata);
		
	}
	
	@Override
	public List<Stock> getAll() {
		List<org.opensrp.domain.postgres.Stock> stocks = stockMetadataMapper.selectMany(new StockMetadataExample(), 0,
		    DEFAULT_FETCH_SIZE);
		return convert(stocks);
	}
	
	@Override
	public void safeRemove(Stock entity) {
		if (entity == null) {
			return;
		}
		
		Long id = retrievePrimaryKey(entity);
		if (id == null) {
			return;
		}
		
		StockMetadataExample stockMetadataExample = new StockMetadataExample();
		stockMetadataExample.createCriteria().andStockIdEqualTo(id);
		int rowsAffected = stockMetadataMapper.deleteByExample(stockMetadataExample);
		if (rowsAffected < 1) {
			return;
		}
		
		stockMapper.deleteByPrimaryKey(id);
		
	}
	
	@Override
	public List<Stock> findAllByProviderid(String providerid) {
		StockMetadataExample stockMetadataExample = new StockMetadataExample();
		stockMetadataExample.createCriteria().andProviderIdEqualTo(providerid);
		return convert(stockMetadataMapper.selectMany(stockMetadataExample, 0, DEFAULT_FETCH_SIZE));
	}
	
	/**
	 * implements the method equivalent in couch repository that return stocks matching stock type
	 * id
	 * 
	 * @param stockType the stock type
	 * @param stockTypeId the stock type id
	 * @return list of stock of a particluar stock type id
	 */
	@Override
	public List<Stock> findAllByIdentifier(String stockType, String stockTypeId) {
		return convert(stockMetadataMapper.selectByIdentifier(stockTypeId, 0, DEFAULT_FETCH_SIZE));
	}
	
	@Override
	public Stock findById(String id) {
		return get(id);
	}
	
	@Override
	public List<Stock> findStocks(StockSearchBean searchBean, String sortBy, String sortOrder, int limit) {
		String orderByClause = getOrderByClause(sortBy, sortOrder);
		return convert(stockMetadataMapper.selectManyBySearchBean(searchBean, orderByClause, 0, limit));
		
	}
	
	@Override
	public List<Stock> findStocks(StockSearchBean searchBean) {
		return findStocks(searchBean, null, null, DEFAULT_FETCH_SIZE);
	}
	
	@Override
	public List<Stock> findAllStocks() {
		return getAll();
	}
	
	@Override
	protected Long retrievePrimaryKey(Stock entity) {
		if (entity == null || entity.getId() == null) {
			return null;
		}
		String documentId = entity.getId();
		
		StockMetadataExample stockMetadataExample = new StockMetadataExample();
		stockMetadataExample.createCriteria().andDocumentIdEqualTo(documentId);
		
		org.opensrp.domain.postgres.Stock pgStock = stockMetadataMapper.selectByDocumentId(documentId);
		if (pgStock == null) {
			return null;
		}
		return pgStock.getId();
	}
	
	@Override
	protected Object getUniqueField(Stock entity) {
		return entity == null ? null : entity.getId();
	}
	
	//private methods
	private Stock convert(org.opensrp.domain.postgres.Stock pgStock) {
		if (pgStock == null || pgStock.getJson() == null || !(pgStock.getJson() instanceof Stock)) {
			return null;
		}
		return (Stock) pgStock.getJson();
	}
	
	private org.opensrp.domain.postgres.Stock convert(Stock entity, Long id) {
		if (entity == null) {
			return null;
		}
		
		org.opensrp.domain.postgres.Stock pgReport = new org.opensrp.domain.postgres.Stock();
		pgReport.setId(id);
		pgReport.setJson(entity);
		
		return pgReport;
	}
	
	private List<Stock> convert(List<org.opensrp.domain.postgres.Stock> stocks) {
		if (stocks == null || stocks.isEmpty()) {
			return new ArrayList<>();
		}
		
		List<Stock> convertedStocks = new ArrayList<>();
		for (org.opensrp.domain.postgres.Stock stock : stocks) {
			Stock convertedStock = convert(stock);
			if (convertedStock != null) {
				convertedStocks.add(convertedStock);
			}
		}
		return convertedStocks;
	}
	
	private StockMetadata createMetadata(Stock entity, Long id) {
		StockMetadata metadata = new StockMetadata();
		metadata.setStockId(id);
		metadata.setDocumentId(entity.getId());
		metadata.setProviderId(entity.getProviderid());
		//metadata.setLocationId(entity.get);
		metadata.setServerVersion(entity.getServerVersion());
		return metadata;
	}
	
}
