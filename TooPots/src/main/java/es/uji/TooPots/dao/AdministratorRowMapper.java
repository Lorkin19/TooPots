package es.uji.TooPots.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import es.uji.TooPots.model.Administrator;

public class AdministratorRowMapper implements RowMapper<Administrator> {

	@Override
	public Administrator mapRow(ResultSet rs, int rowNum) throws SQLException {
		// TODO Auto-generated method stub
		Administrator admin = new Administrator();
		
		admin.setMail(rs.getString("mail"));
		admin.setName(rs.getString("name"));
		admin.setPwd(rs.getString("pwd"));
		admin.setSurname(rs.getString("surname"));
		admin.setUsername(rs.getString("username"));
		return admin;
	}

}
