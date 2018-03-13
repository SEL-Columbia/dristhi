package org.opensrp.repository.postgres;

import java.util.List;

import org.opensrp.domain.Stock;
import org.opensrp.repository.StocksRepository;
import org.opensrp.search.StockSearchBean;
import org.springframework.stereotype.Repository;

@Repository
public class StocksRepositoryImpl implements StocksRepository {
	
	@Override
	public Stock get(String id) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void add(Stock entity) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void update(Stock entity) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public List<Stock> getAll() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void safeRemove(Stock entity) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public List<Stock> findAllByProviderid(String providerid) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<Stock> findAllByIdentifier(String vaccine_type, String vaccine_type_id) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Stock findById(String id) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<Stock> findStocks(StockSearchBean searchBean, String sortBy, String sortOrder, int limit) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<Stock> findStocks(StockSearchBean searchBean) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<Stock> findAllStocks() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
