package org.opensrp.repository.postgres.handler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.opensrp.domain.Client;
import org.opensrp.repository.postgres.RepositoryHelper;

public class ClientTypeHandler implements TypeHandler<Client> {

	@Override
	public void setParameter(PreparedStatement ps, int i, Client parameter, JdbcType jdbcType) throws SQLException {
		if (parameter != null) {
			String jsonString = RepositoryHelper.gson.toJson(parameter);
			ps.setObject(i, jsonString);
		}
	}

	@Override
	public Client getResult(ResultSet rs, String columnName) throws SQLException {
		String jsonString = rs.getString(columnName);
		if (StringUtils.isBlank(jsonString)) {
			return null;
		}
		return RepositoryHelper.gson.fromJson(jsonString, Client.class);
	}

	@Override
	public Client getResult(ResultSet rs, int columnIndex) throws SQLException {
		String jsonString = rs.getString(columnIndex);
		if (StringUtils.isBlank(jsonString)) {
			return null;
		}
		return RepositoryHelper.gson.fromJson(jsonString, Client.class);
	}

	@Override
	public Client getResult(CallableStatement cs, int columnIndex) throws SQLException {
		String jsonString = cs.getString(columnIndex);
		if (StringUtils.isBlank(jsonString)) {
			return null;
		}
		return RepositoryHelper.gson.fromJson(jsonString, Client.class);
	}

}
