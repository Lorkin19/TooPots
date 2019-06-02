package es.uji.TooPots.dao;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import es.uji.TooPots.model.Request;

public class RequestRowMapper implements RowMapper<Request> {
	@Override
	public Request mapRow(ResultSet rs, int rowNum) throws SQLException {
		Request req = new Request();
		
		req.setMail(rs.getString("mail"));
		req.setBankAccount(rs.getString("bankAccount"));
		req.setName(rs.getString("name"));
		req.setSurname(rs.getString("surname"));
		req.setPwd(rs.getString("pwd"));
		req.setUsername(rs.getString("username"));
		req.setStatus(rs.getString("status"));
		req.setRequestId(Integer.parseInt(rs.getString("requestId")));
		return req;		
	}

}
