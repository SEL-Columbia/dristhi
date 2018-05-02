package org.opensrp.repository.postgres.handler;

import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.joda.time.DateTime;

public class DateTimeTypeHandler implements TypeHandler<DateTime> {
	
	@Override
	public void setParameter(PreparedStatement ps, int i, DateTime parameter, JdbcType jdbcType) throws SQLException {
		if (parameter != null) {
			if (parameter.toLocalTime().toString().equals("00:00:00.000"))
				ps.setDate(i, new Date(parameter.getMillis()));
			else
				ps.setTimestamp(i, new Timestamp(parameter.getMillis()));
		} else {
			ps.setDate(i, null);
		}
	}
	
	@Override
	public DateTime getResult(ResultSet resultSet, String columnName) throws SQLException {
		Timestamp timestamp = resultSet.getTimestamp(columnName);
		if (timestamp != null) {
			return new DateTime(timestamp.getTime());
		} else {
			return null;
		}
	}
	
	@Override
	public DateTime getResult(ResultSet resultSet, int columnIndex) throws SQLException {
		Timestamp timestamp = resultSet.getTimestamp(columnIndex);
		if (timestamp != null) {
			return new DateTime(timestamp.getTime());
		} else {
			return null;
		}
	}
	
	@Override
	public DateTime getResult(CallableStatement cs, int columnIndex) throws SQLException {
		Timestamp timestamp = cs.getTimestamp(columnIndex);
		if (timestamp != null) {
			return new DateTime(timestamp.getTime());
		} else {
			return null;
		}
	}
	
}
