package org.opensrp.repository.postgres.handler;

import java.lang.reflect.Type;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.joda.time.DateTime;
import org.opensrp.util.DateTimeTypeConverter;
import org.postgresql.util.PGobject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public abstract class BaseTypeHandler<T> implements TypeHandler<T> {
	
	public static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
	        .registerTypeAdapter(DateTime.class, new DateTimeTypeConverter()).create();
	
	private Type typeToken = new TypeToken<T>() {}.getType();
	
	@Override
	public void setParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
		try {
			if (parameter != null) {
				String jsonString = gson.toJson(parameter);
				PGobject jsonObject = new PGobject();
				jsonObject.setType("jsonb");
				jsonObject.setValue(jsonString);
				ps.setObject(i, jsonObject);
			}
		}
		catch (Exception e) {
			throw new SQLException(e);
		}
	}
	
	@Override
	public T getResult(ResultSet rs, String columnName) throws SQLException {
		try {
			String jsonString = rs.getString(columnName);
			if (StringUtils.isBlank(jsonString)) {
				return null;
			}
			return gson.fromJson(jsonString, typeToken);
		}
		catch (Exception e) {
			throw new SQLException(e);
		}
	}
	
	@Override
	public T getResult(ResultSet rs, int columnIndex) throws SQLException {
		try {
			String jsonString = rs.getString(columnIndex);
			if (StringUtils.isBlank(jsonString)) {
				return null;
			}
			return gson.fromJson(jsonString, typeToken);
		}
		catch (Exception e) {
			throw new SQLException(e);
		}
	}
	
	@Override
	public T getResult(CallableStatement cs, int columnIndex) throws SQLException {
		try {
			String jsonString = cs.getString(columnIndex);
			if (StringUtils.isBlank(jsonString)) {
				return null;
			}
			return gson.fromJson(jsonString, typeToken);
		}
		catch (Exception e) {
			throw new SQLException(e);
		}
	}
	
}
