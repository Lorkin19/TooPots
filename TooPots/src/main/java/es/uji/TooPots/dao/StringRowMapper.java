package es.uji.TooPots.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class StringRowMapper implements RowMapper<String> {

	@Override
	public String mapRow(ResultSet rs, int rowNum) throws SQLException {
		// TODO Auto-generated method stub
		String mail = rs.getString("mail");
		return mail;
	}
	
}
