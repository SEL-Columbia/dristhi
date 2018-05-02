package org.opensrp.repository.postgres.mapper.custom;

import org.opensrp.domain.postgres.Stock;
import org.opensrp.repository.postgres.mapper.StockMapper;


public interface CustomStockMapper extends StockMapper {

	int insertSelectiveAndSetId(Stock stock);
	
}
