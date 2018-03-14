package org.opensrp.repository.couch;

import java.util.List;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.UpdateConflictException;
import org.ektorp.support.View;
import org.ektorp.util.Assert;
import org.ektorp.util.Documents;
import org.motechproject.dao.MotechBaseRepository;
import org.opensrp.common.AllConstants;
import org.opensrp.domain.Stock;
import org.opensrp.repository.StocksRepository;
import org.opensrp.repository.lucene.LuceneStockRepository;
import org.opensrp.search.StockSearchBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository("couchStocksRepository")
@Primary
public class AllStocks extends MotechBaseRepository<Stock> implements StocksRepository {
	
	private LuceneStockRepository lsr;
	
	@Autowired
	protected AllStocks(@Qualifier(AllConstants.OPENSRP_DATABASE_CONNECTOR) CouchDbConnector db, LuceneStockRepository ler) {
		super(Stock.class, db);
		this.lsr = ler;
	}
	
	@View(name = "all_stock_by_providerid", map = "function(doc) {if (doc.type === 'Stock') {for(var key in doc.providerid) {emit(doc.providerid[key]);}}}")
	public List<Stock> findAllByProviderid(String providerid) {
		return db.queryView(createQuery("all_stock_by_providerid").key(providerid).includeDocs(true), Stock.class);
	}
	
	@View(name = "all_stock_by_vaccine_type_id", map = "function(doc) {if (doc.type === 'Stock') {for(var key in doc.vaccine_type_id) {emit([key, doc.vaccine_type_id[key]]);}}}")
	public List<Stock> findAllByIdentifier(String vaccine_type, String vaccine_type_id) {
		ComplexKey ckey = ComplexKey.of(vaccine_type, vaccine_type_id);
		return db.queryView(createQuery("all_stock_by_vaccine_type_id").key(ckey).includeDocs(true), Stock.class);
	}
	
	public Stock findById(String id) {
		Stock stock = db.get(Stock.class, id);
		return stock;
	}
	
	public List<Stock> findStocks(StockSearchBean searchBean, String sortBy, String sortOrder, int limit) {
		return lsr.getByCriteria(searchBean.getIdentifier(), searchBean.getStockTypeId(), searchBean.getTransactionType(),
		    searchBean.getProviderId(), searchBean.getValue(), searchBean.getDateCreated(), searchBean.getToFrom(),
		    searchBean.getDateUpdated(), searchBean.getServerVersion(), sortBy, sortOrder, limit);
	}
	
	public List<Stock> findStocks(StockSearchBean searchBean) {
		return lsr.getByCriteria(searchBean.getIdentifier(), searchBean.getStockTypeId(), searchBean.getTransactionType(),
		    searchBean.getProviderId(), searchBean.getValue(), searchBean.getDateCreated(), searchBean.getToFrom(),
		    searchBean.getDateUpdated(), searchBean.getServerVersion().toString());
	}
	
	@View(name = "all_stocks", map = "function(doc) { if (doc.type === 'Stock') { emit(doc.dateCreated); } }")
	public List<Stock> findAllStocks() {
		return db.queryView(createQuery("all_stocks").includeDocs(true), Stock.class);
	}
	
	/**
	 * Save stock to the specified db
	 * 
	 * @throws UpdateConflictException if there was an update conflict.
	 */
	public void add(CouchDbConnector targetDb, Stock stock) {
		Assert.isTrue(Documents.isNew(stock), "entity must be new");
		targetDb.create(stock);
	}
	
}
