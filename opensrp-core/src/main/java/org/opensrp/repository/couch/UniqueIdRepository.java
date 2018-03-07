package org.opensrp.repository.couch;

import org.opensrp.domain.UniqueId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.List;

//import org.opensrp.domain.UniqueId;

@Repository
public class UniqueIdRepository {


	@Autowired
	JdbcTemplate jdbcTemplate;

	
	public int save(UniqueId uniqueId) throws Exception {
		String insertQuery = "insert into " + UniqueId.tbName + " (" + UniqueId.COL_LOCATION + "," + UniqueId.COL_OPENMRSID
		        + "," + UniqueId.COL_STATUS + "," + UniqueId.COL_USEDBY + "," + UniqueId.COL_CREATED_AT
		        + ") values (?, ?, ?,?,?) ";
		Object[] params = new Object[] { uniqueId.getLocation(), uniqueId.getOpenmrsId(), uniqueId.getStatus(),
		        uniqueId.getUsedBy(), uniqueId.getCreatedAt() };
		int[] types = new int[] { Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.TIMESTAMP };
		
		return jdbcTemplate.update(insertQuery, params, types);
		
	}
	
	public void executeQuery(String query) throws Exception {
		jdbcTemplate.execute(query);
	}
	
	public int checkIfExists(String query, String[] args) throws Exception {
		return this.jdbcTemplate.queryForObject(query, args, Integer.class);
		
	}
	
	public void clearTable() throws Exception {
		String query = "DELETE FROM " + UniqueId.tbName;
		executeQuery(query);
	}
	
	public List<UniqueId> getNotUsedIds(int limit) {
		
		String query = "SELECT * FROM " + UniqueId.tbName + " WHERE " + UniqueId.COL_STATUS + " ='"
		        + UniqueId.STATUS_NOT_USED + "' limit " + limit;
		
		List<UniqueId> ids = jdbcTemplate.query(query, new UniqueIdRowMapper());
		
		return ids;
	}
	
	public List<String> getNotUsedIdsAsString(int limit) {
		
		String query = "SELECT " + UniqueId.COL_OPENMRSID + " FROM " + UniqueId.tbName + " WHERE " + UniqueId.COL_STATUS
		        + " ='" + UniqueId.STATUS_NOT_USED + "' limit " + limit;
		
		List<String> data = jdbcTemplate.queryForList(query, String.class);
		
		return data;
	}
	
	public class UniqueIdRowMapper implements RowMapper<UniqueId> {
		
		public UniqueId mapRow(ResultSet rs, int rowNum) throws SQLException {
			UniqueId uniqueId = new UniqueId();
			uniqueId.setCreatedAt(new Date(rs.getTimestamp(rs.findColumn(UniqueId.COL_CREATED_AT)).getTime()));
			uniqueId.setOpenmrsId(rs.getString(rs.findColumn(UniqueId.COL_OPENMRSID)));
			uniqueId.setLocation(rs.getString(rs.findColumn(UniqueId.COL_LOCATION)));
			uniqueId.setStatus(rs.getString(rs.findColumn(UniqueId.COL_STATUS)));
			uniqueId.setUsedBy(rs.getString(rs.findColumn(UniqueId.COL_USEDBY)));
			return uniqueId;
		}
		
	}
	
	public Integer totalUnUsedIds() {
		String sql = "select count(*) from " + UniqueId.tbName + " where " + UniqueId.COL_STATUS + "='"
		        + UniqueId.STATUS_NOT_USED + "'";
		return jdbcTemplate.queryForInt(sql);
	}
	
	public int[] markAsUsed(final List<String> ids) {
		
		int[] updateCnt = jdbcTemplate.batchUpdate(
		    "update " + UniqueId.tbName + " set " + UniqueId.COL_STATUS + "= ? where " + UniqueId.COL_OPENMRSID + " = ?",
		    new BatchPreparedStatementSetter() {
			    
			    public void setValues(PreparedStatement ps, int i) throws SQLException {
				    ps.setString(1, UniqueId.STATUS_USED);
				    ps.setString(2, ids.get(i));
			    }
			    
			    public int getBatchSize() {
				    return ids.size();
			    }
		    });
		return updateCnt;
	}
}
