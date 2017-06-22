package org.opensrp.service;

import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.opensrp.domain.Stock;
import org.opensrp.repository.AllStocks;
import org.opensrp.service.StockService;


/**
 * Tests {@link RegistrationDataService}.
 */
public class StockServiceTest {
	
	@Mock
    private AllStocks allStocks;
  
    @Mock
    private StockService stockService;
   


    @Before
    public void setUp() throws Exception {
        initMocks(this);
        stockService = new StockService(allStocks);
    }

    @Test
    public void shouldSaveStock() {
        Stock stock = new Stock(Long.parseLong("123"), "VT", "TT", "4-2", 10,
    			Long.parseLong("20062017"), "TF", "TRUE",Long.parseLong("20062017"), Long.parseLong("12345"));
        		
        Stock savedStock = stockService.addorUpdateStock(stock);
        Assert.assertNotNull(savedStock.getServerVersion());
      
    }

   
	
	
	
	
	
}

