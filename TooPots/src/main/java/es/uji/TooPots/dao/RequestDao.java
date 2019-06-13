package es.uji.TooPots.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import es.uji.TooPots.model.Instructor;
import es.uji.TooPots.model.Request;

@Repository
public class RequestDao {
	private JdbcTemplate jdbcTemplate;
	
	private static AtomicInteger requestId; 
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		try {
			requestId = new AtomicInteger(jdbcTemplate.queryForObject("SELECT requestId FROM Request ORDER BY "
					+ "requestId DESC LIMIT 1", Integer.class));	
			requestId.getAndIncrement();
		}catch(EmptyResultDataAccessException e) {
			requestId = new AtomicInteger();
		}
	}
	
	public void addRequest(Request request) {
		jdbcTemplate.update("INSERT INTO Request VALUES(?, ?, ?, ?, ?, ?, ?, ?)",
				requestId.getAndIncrement(),
				request.getMail(), request.getName(),
				request.getSurname(),request.getUsername(),
				request.getPwd(), request.getBankAccount(), "Pending");
	}
	
	public void deleteRequest(Request request) {
		jdbcTemplate.update("DELETE FROM Request WHERE requestId=?",
				request.getRequestId());
	}
	
	public void updateRequest(Request request) {
		jdbcTemplate.update("UPDATE Request SET username=?, mail=?, pwd=?, bankAccount=?, status=?"
				+ "WHERE requestId=?",
				request.getUsername(), request.getMail(), request.getPwd(),
				request.getBankAccount(), request.getStatus(), request.getRequestId());
	}
	
	public Request getRequest(int requestId) {
		try {
			return jdbcTemplate.queryForObject("SELECT * FROM Request "
					+ "WHERE requestId=?", new RequestRowMapper(), requestId);
		}catch(EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	public List<Request> getRequests(){
		try {
			return jdbcTemplate.query("SELECT * FROM Request WHERE status='Pending'", new RequestRowMapper());
		}catch(EmptyResultDataAccessException e) {
			return new ArrayList<Request>();
		}
	}
	
	public List<Request> getAcceptedRequests(){
		try {
			return jdbcTemplate.query("SELECT * FROM Request WHERE status='Accepted'", new RequestRowMapper());
		}catch(EmptyResultDataAccessException e) {
			return new ArrayList<Request>();
		}
	}
	
	public List<Request> getRejectedRequests(){
		try {
			return jdbcTemplate.query("SELECT * FROM Request WHERE status='Rejected'", new RequestRowMapper());
		}catch(EmptyResultDataAccessException e) {
			return new ArrayList<Request>();
		}
	}

	
	public Instructor convertToInstructor(Request request) {
		Instructor ins = new Instructor();
		ins.setBankAccount(request.getBankAccount());
		ins.setUsername(request.getUsername());
		ins.setMail(request.getMail());
		ins.setName(request.getName());
		ins.setSurname(request.getSurname());
		ins.setRoutesCertificates(request.getRoutesCertificates());
		ins.setPwd(request.getPwd());
		return ins;
	}

}
