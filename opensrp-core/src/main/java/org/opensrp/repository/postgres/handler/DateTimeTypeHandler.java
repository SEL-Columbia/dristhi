package org.opensrp.repository.postgres.handler;

import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.joda.time.DateTime;

public class DateTimeTypeHandler implements TypeHandler<DateTime> {
	
	@Override
	public void setParameter(PreparedStatement ps, int i, DateTime parameter, JdbcType jdbcType) throws SQLException {
		if (parameter != null) {
			ps.setDate(i, new Date(parameter.getMillis()));
		} else {
			ps.setDate(i, null);
		}
	}
	
	@Override
	public DateTime getResult(ResultSet resultSet, String columnName) throws SQLException {
		Date date = resultSet.getDate(columnName);
		if (date != null) {
			return new DateTime(date);
		} else {
			return null;
		}
	}
	
	@Override
	public DateTime getResult(ResultSet resultSet, int columnIndex) throws SQLException {
		Date date = resultSet.getDate(columnIndex);
		if (date != null) {
			return new DateTime(date);
		} else {
			return null;
		}
	}
	
	@Override
	public DateTime getResult(CallableStatement cs, int columnIndex) throws SQLException {
		Date date = cs.getDate(columnIndex);
		if (date != null) {
			return new DateTime(date);
		} else {
			return null;
		}
	}
	
}
