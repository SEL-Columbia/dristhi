package org.opensrp.service;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.junit.Assert;
import org.junit.Test;
import org.opensrp.domain.Stock;
import org.opensrp.repository.StocksRepository;
import org.opensrp.repository.postgres.BaseRepositoryTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Integration Tests for {@link StockService}.
 */
public class StockServiceTest extends BaseRepositoryTest {
	
	private StockService stockService;
	
	@Autowired
	@Qualifier("stocksRepositoryPostgres")
	private StocksRepository stocksRepository;
	
	@Override
	protected Set<String> getDatabaseScripts() {
		Set<String> scripts = new HashSet<>();
		scripts.add("stock.sql");
		return scripts;
	}
	
	@Override
	public void populateDatabase() throws SQLException {
		super.populateDatabase();
		stockService = new StockService(stocksRepository);
		Stock stock1 = new Stock(Long.parseLong("123"), "VT", "TT", "4-2", 10, Long.parseLong("20062017"), "TF",
		        Long.parseLong("20062017"), Long.parseLong("12345"));
		Stock stock2 = new Stock(Long.parseLong("123"), "VT", "TT", "4-2", 10, Long.parseLong("20062017"), "TF",
		        Long.parseLong("20062017"), Long.parseLong("12345"));
		stockService.addStock(stock1);
		stockService.addStock(stock2);
	}
	
	@Test
	public void shouldSaveStock() {
		Stock stock = new Stock(Long.parseLong("124"), "VT1", "TT1", "4-2", 10, Long.parseLong("20062017"), "	TF",
		        Long.parseLong("20062017"), Long.parseLong("12345"));
		
		Stock savedStock = stockService.addorUpdateStock(stock);
		Assert.assertNotNull(savedStock.getServerVersion());
		Assert.assertEquals(savedStock.getProviderid(), stock.getProviderid());
		
	}
	
	@Test
	public void shouldUpdateStockWithSaveorUpdate() {
		List<Stock> stocks = stockService.findAllByProviderid("4-2");
		for (Stock stock : stocks) {
			stock.setVaccine_type_id("12334");
			stock.setTransaction_type("Restocking_1");
			stock.setValue(stock.getValue() * 2);
			stockService.addorUpdateStock(stock);
		}
		stocks = stockService.findAllByProviderid("4-2");
		for (Stock savedStock : stocks) {
			Assert.assertEquals(0, Minutes.minutesBetween(savedStock.getDateEdited(), DateTime.now()).getMinutes());
			Assert.assertEquals("12334", savedStock.getVaccine_type_id());
			Assert.assertEquals("Restocking_1", savedStock.getTransaction_type());
			Assert.assertEquals(20, savedStock.getValue());
		}
	}
	
	@Test
	public void shouldUpdateStock() {
		List<Stock> stocks = stockService.findAllByProviderid("4-2");
		for (Stock stock : stocks) {
			stock.setVaccine_type_id("12334");
			stock.setTransaction_type("Restocking_1");
			stock.setValue(stock.getValue() * 2);
			stockService.updateStock(stock);
		}
		stocks = stockService.findAllByProviderid("4-2");
		for (Stock savedStock : stocks) {
			Assert.assertEquals(0, Minutes.minutesBetween(savedStock.getDateEdited(), DateTime.now()).getMinutes());
			Assert.assertEquals("12334", savedStock.getVaccine_type_id());
			Assert.assertEquals("Restocking_1", savedStock.getTransaction_type());
			Assert.assertEquals(20, savedStock.getValue());
		}
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void shouldNotUpdateStockForNewStock() {
		Stock stock = new Stock(Long.parseLong("124"), "VT1", "TT1", "4-2", 10, Long.parseLong("20062017"), "	TF",
		        Long.parseLong("20062017"), Long.parseLong("12345"));
		stockService.updateStock(stock);
	}
	
	@Test
	public void shouldFetchStocksByProcider() throws Exception {
		List<Stock> fecthedListByProviderid = stockService.findAllByProviderid("4-2");
		for (Stock stock : fecthedListByProviderid)
			Assert.assertEquals("4-2", stock.getProviderid());
	}
	
	@Test
	public void shouldFetchAll() throws Exception {
		List<Stock> fecthedListAll = stockService.getAll();
		Assert.assertEquals(17, fecthedListAll.size());
	}
	
}
