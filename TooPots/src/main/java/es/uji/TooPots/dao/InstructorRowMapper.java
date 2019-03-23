package es.uji.TooPots.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import es.uji.TooPots.model.Instructor;

public class InstructorRowMapper implements RowMapper<Instructor> {

	@Override
	public Instructor mapRow(ResultSet rs, int rowNum) throws SQLException {
		// TODO Auto-generated method stub
		Instructor instructor = new Instructor();
		
		instructor.setUsername(rs.getString("username"));
		instructor.setBankAccount(rs.getString("bankAccount"));
		instructor.setDni(rs.getString("dni"));
		instructor.setMail(rs.getString("mail"));
		instructor.setName(rs.getString("name"));
		instructor.setPwd(rs.getString("pwd"));
		//instructor.setRoutesCertificates();
		instructor.setSurname(rs.getString("surname"));
		
		
		return instructor;
	}
}
