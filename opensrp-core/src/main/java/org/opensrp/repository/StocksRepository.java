package org.opensrp.repository;

import java.util.List;

import org.opensrp.domain.Stock;

public interface StocksRepository extends BaseRepository<Stock> {
	
	List<Stock> findAllByProviderid(String providerid);
	
	public List<Stock> findAllByIdentifier(String vaccine_type, String vaccine_type_id);
	
	public Stock findById(String id);
	
	public List<Stock> findStocks(String identifier, String vaccine_type_id, String transaction_type, String providerid,
	                              String value, String date_created, String to_from, String date_updated, Long serverVersion,
	                              String sortBy, String sortOrder, int limit);
	
	public List<Stock> findStocks(String identifier, String vaccine_type_id, String transaction_type, String providerid,
	                              String value, String date_created, String to_from, String date_updated,
	                              String serverVersion);
	
	public List<Stock> findAllStocks();
	
}
