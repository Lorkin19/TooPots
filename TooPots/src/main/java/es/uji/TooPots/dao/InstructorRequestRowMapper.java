package es.uji.TooPots.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import es.uji.TooPots.model.Activity;
import es.uji.TooPots.model.InstructorRequest;

public class InstructorRequestRowMapper implements RowMapper<InstructorRequest> {
	
	
	@Override
	public InstructorRequest mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		InstructorRequest instructorRequest = new InstructorRequest();
		
		instructorRequest.setRequestId(Integer.parseInt(rs.getString("requestId")));
		instructorRequest.setStatus(rs.getString("status"));
		instructorRequest.setMail(rs.getString("mail"));
		instructorRequest.setName(rs.getString("name"));
		instructorRequest.setSurname(rs.getString("surname"));
		instructorRequest.setDni(rs.getString("dni"));
		return instructorRequest;
	}
}
