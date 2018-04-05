package org.opensrp.repository.postgres;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.opensrp.common.AllConstants.BaseEntity;
import org.opensrp.domain.Stock;
import org.opensrp.repository.StocksRepository;
import org.opensrp.search.StockSearchBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class StocksRepositoryTest extends BaseRepositoryTest {
	
	@Autowired
	@Qualifier("stocksRepositoryPostgres")
	private StocksRepository stocksRepository;
	
	@Override
	protected Set<String> getDatabaseScripts() {
		Set<String> scripts = new HashSet<String>();
		scripts.add("stock.sql");
		return scripts;
	}
	
	@Test
	public void testGet() {
		Stock stock = stocksRepository.get("05934ae338431f28bf6793b241978ad9");
		assertEquals(5l, stock.getIdentifier().longValue());
		assertEquals(20, stock.getValue());
		assertEquals("1", stock.getVaccine_type_id());
		assertEquals(1521009418783l, stock.getServerVersion().longValue());
		assertNull(stocksRepository.get("07271855-4018-497a-b180-6af"));
	}
	
	@Test
	public void testAdd() {
		Stock stock = new Stock(521l, "VC1", "received", "tester1", 31, 1521499800000l, "VCC1", 1521536143239l,
		        1521536179443l);
		stocksRepository.add(stock);
		assertEquals(16, stocksRepository.getAll().size());
		
		List<Stock> stocks = stocksRepository.findAllByIdentifier("vaccine_type", "VC1");
		
		assertEquals(1, stocks.size());
		assertEquals("tester1", stocks.get(0).getProviderid());
		assertEquals(31, stock.getValue());
		assertEquals("VC1", stock.getVaccine_type_id());
	}
	
	@Test
	public void testUpdate() {
		Stock stock = stocksRepository.get("05934ae338431f28bf6793b241b2daa6");
		long now = System.currentTimeMillis();
		stock.setDate_updated(now);
		stock.setValue(23);
		stocksRepository.update(stock);
		
		Stock updatedStock = stocksRepository.get("05934ae338431f28bf6793b241b2daa6");
		assertEquals(now, updatedStock.getDate_updated().longValue());
		assertEquals(23, stock.getValue());
		
	}
	
	@Test
	public void testGetAll() {
		assertEquals(15, stocksRepository.getAll().size());
		stocksRepository.safeRemove(stocksRepository.get("05934ae338431f28bf6793b241b2daa6"));
		assertEquals(14, stocksRepository.getAll().size());
		
	}
	
	@Test
	public void testSafeRemove() {
		stocksRepository.safeRemove(stocksRepository.get("05934ae338431f28bf6793b2419a606f"));
		assertEquals(14, stocksRepository.getAll().size());
		assertNull(stocksRepository.get("05934ae338431f28bf6793b2419a606f"));
	}
	
	@Test
	public void testFindAllByProviderid() {
		assertEquals(12, stocksRepository.findAllByProviderid("biddemo").size());
		assertEquals(3, stocksRepository.findAllByProviderid("biddemo1").size());
		assertTrue(stocksRepository.findAllByProviderid("biddemo2").isEmpty());
	}
	
	@Test
	public void testFindAllByIdentifier() {
		assertEquals(11, stocksRepository.findAllByIdentifier("vaccine_type", "1").size());
		assertEquals(4, stocksRepository.findAllByIdentifier("vaccine_type", "2").size());
		assertTrue(stocksRepository.findAllByIdentifier("vaccine_type", "19").isEmpty());
	}
	
	@Test
	public void testFindById() {
		Stock stock = stocksRepository.findById("05934ae338431f28bf6793b241b2df09");
		assertEquals(12l, stock.getIdentifier().longValue());
		assertEquals(-2, stock.getValue());
		assertEquals("1", stock.getVaccine_type_id());
		assertEquals("Physical_recount", stock.getTo_from());
		assertEquals("loss_adjustment", stock.getTransaction_type());
		assertNull(stocksRepository.findById("07271855-4018-497a-b180-6af"));
	}
	
	@Test
	public void testFindStocksWithOrder() {
		StockSearchBean searchBean = new StockSearchBean();
		
		searchBean.setStockTypeId("1");
		List<Stock> stocks = stocksRepository.findStocks(searchBean, BaseEntity.SERVER_VERSIOIN, "asc", 5);
		assertEquals(5, stocks.size());
		long previousVersion = 0;
		for (Stock stock : stocks) {
			assertTrue(stock.getServerVersion() >= previousVersion);
			previousVersion=stock.getServerVersion();
		}
	
	}
	
	@Test
	public void testFindStocksStockbeanOnly() {
		StockSearchBean searchBean = new StockSearchBean();
		
		searchBean.setIdentifier("10");
		List<Stock> stocks = stocksRepository.findStocks(searchBean);
		assertEquals(1, stocks.size());
		
		assertEquals(-19, stocks.get(0).getValue());
		assertEquals("2", stocks.get(0).getVaccine_type_id());
		assertEquals("C/C", stocks.get(0).getTo_from());
		assertEquals("issued", stocks.get(0).getTransaction_type());
		
		searchBean = new StockSearchBean();
		searchBean.setStockTypeId("1");
		assertEquals(11, stocksRepository.findStocks(searchBean).size());
		
		searchBean.setTransactionType("issued");
		searchBean.setProviderId("biddemo");
		assertEquals(3, stocksRepository.findStocks(searchBean).size());
		
		searchBean = new StockSearchBean();
		searchBean.setValue("10");
		assertEquals(2, stocksRepository.findStocks(searchBean).size());
		
		searchBean.setValue("2");
		stocks = stocksRepository.findStocks(searchBean);
		assertEquals(1, stocks.size());
		assertEquals(14l, stocks.get(0).getIdentifier().longValue());
		assertEquals("1", stocks.get(0).getVaccine_type_id());
		assertEquals("DHO", stocks.get(0).getTo_from());
		assertEquals("received", stocks.get(0).getTransaction_type());
		
		searchBean = new StockSearchBean();
		searchBean.setDateCreated("1518559200000");
		assertEquals(4, stocksRepository.findStocks(searchBean).size());
		
		searchBean.setToFrom("DHO");
		assertEquals(2, stocksRepository.findStocks(searchBean).size());
		
		searchBean.setDateUpdated("1521007053945");
		stocks = stocksRepository.findStocks(searchBean);
		assertEquals(1, stocks.size());
		assertEquals(2l, stocks.get(0).getIdentifier().longValue());
		assertEquals("1", stocks.get(0).getVaccine_type_id());
		assertEquals("DHO", stocks.get(0).getTo_from());
		assertEquals("received", stocks.get(0).getTransaction_type());
		
		searchBean = new StockSearchBean();
		searchBean.setServerVersion(1521009418783l);
		assertEquals(11, stocksRepository.findStocks(searchBean).size());
		
		searchBean.setServerVersion(1521023046990l);
		assertEquals(8, stocksRepository.findStocks(searchBean).size());
		
	}
	
	@Test
	public void testFindAllStocks() {
		assertEquals(15, stocksRepository.findAllStocks().size());
		stocksRepository.safeRemove(stocksRepository.get("05934ae338431f28bf6793b241b2df09"));
		assertEquals(14, stocksRepository.findAllStocks().size());
	}
}
