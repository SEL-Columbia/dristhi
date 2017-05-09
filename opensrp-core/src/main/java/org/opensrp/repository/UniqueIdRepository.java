package org.opensrp.repository;

import java.sql.Types;

import org.opensrp.domain.UniqueId;
//import org.opensrp.domain.UniqueId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UniqueIdRepository {
	
	@Autowired
	JdbcTemplate jdbcTemplate;

	public int save(UniqueId uniqueId) throws Exception{
		String insertQuery = "insert into "+UniqueId.tbName+" ("+UniqueId.COL_LOCATION+","+UniqueId.COL_OPENMRSID+","+UniqueId.COL_STATUS+","+UniqueId.COL_USEDBY+","+UniqueId.COL_CREATED_AT+") values (?, ?, ?,?,?) ";
		Object[] params = new Object[] { uniqueId.getLocation(),uniqueId.getOpenmrsId(), uniqueId.getStatus(),uniqueId.getUsedBy(),uniqueId.getCreatedAt() };
		int[] types = new int[] { Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,Types.DATE };

		return jdbcTemplate.update(insertQuery, params, types);
		
	}
	public void executeQuery(String query) throws Exception{
		jdbcTemplate.execute(query);
	}
	public int checkIfExists(String query,String[] args) throws Exception{ 
		return this.jdbcTemplate.queryForObject(query, args, Integer.class);

	}
	public void clearTable() throws Exception{
		String query="DELETE FROM " + UniqueId.tbName;
		executeQuery(query);
	}
	
}
