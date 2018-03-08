package org.opensrp.repository;

import java.util.List;

import org.opensrp.domain.Stock;
import org.opensrp.search.StockSearchBean;

public interface StocksRepository extends BaseRepository<Stock> {
	
	List<Stock> findAllByProviderid(String providerid);
	
	public List<Stock> findAllByIdentifier(String vaccine_type, String vaccine_type_id);
	
	public Stock findById(String id);
	
	public List<Stock> findStocks(StockSearchBean searchBean, String sortBy, String sortOrder, int limit);
	
	public List<Stock> findStocks(StockSearchBean searchBean);
	
	public List<Stock> findAllStocks();
	
}
