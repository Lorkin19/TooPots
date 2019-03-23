package es.uji.TooPots.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import es.uji.TooPots.model.InstructorRequest;
import es.uji.TooPots.model.Status;

@Repository
public class InstructorRequestDao {
	private JdbcTemplate jdbcTemplate;
	
	/**
	 * Con el nextRequestId se obtiene el siguiente RequestId cuando se cre un nuevo InstructorRequest. Problema: obtener la ultima
	 * requestId para saber a partir de cual seguir.
	 * En principio se queda en cero hasta solventar el problema anterior.
	 */
	private static AtomicInteger nextRequestId = new AtomicInteger(0);
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	public void addInstructorRequest(InstructorRequest instructorRequest) {
		jdbcTemplate.update("INSERT INTO InstructorRequest VALUES(?, ?, ?)",
				nextRequestId.getAndIncrement(), Status.PENDING,
				instructorRequest.getMail());
	}	
	public void deleteInstructorRequest(InstructorRequest instructorRequest) {
		jdbcTemplate.update("UPDATE InstructorRequest SET status=? WHERE requestId=?",
				Status.REJECTED, instructorRequest.getRequestId());
	}
	
	public void updateInstructorRequest(InstructorRequest instructorRequest) {
		jdbcTemplate.update("UPDATE InstructorRequest SET requestId=?, status=?, "
				+ "mail=?",
				instructorRequest.getRequestId(), instructorRequest.getStatus(),
				instructorRequest.getMail());
	}
	
	public InstructorRequest getInstructorRequest(int requestId) {
		try {
			return jdbcTemplate.queryForObject("SELECT * FROM InstructorRequest WHERE requestId=?",
					new InstructorRequestRowMapper(), requestId);
		}catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	public List<InstructorRequest> getInstructorRequests(){
		try {
			return jdbcTemplate.query("SELECT * FROM InstructorRequest",
					new InstructorRequestRowMapper());
		}catch(EmptyResultDataAccessException e) {
			return new ArrayList<InstructorRequest>();
		}
	}
}
