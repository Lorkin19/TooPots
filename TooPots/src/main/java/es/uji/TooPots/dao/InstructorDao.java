package es.uji.TooPots.dao;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import es.uji.TooPots.model.Instructor;

@Repository
public class InstructorDao {
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	public void addInstructor(Instructor instructor) {
		//Falta añadir los certificados que no sé como hacerlo. Preguntar.
		jdbcTemplate.update("INSERT INTO Instructor VALUES(?, ?, ?, ?)",
				instructor.getUsername(), instructor.getPwd(),
				instructor.getBankAccount(), instructor.getMail());
	}
	
	public void deleteInstructor(Instructor instructor) {
		jdbcTemplate.update("DELETE FROM Instructor WHERE username=?",
				instructor.getUsername());
	}
	public void updateInstructor(Instructor instructor) {
		jdbcTemplate.update("UPDATE Instructor SET username=?, mail=?, pwd=?, bankAccount=?",
				instructor.getUsername(), instructor.getMail(), instructor.getPwd(),
				instructor.getBankAccount());
	}
	
	public Instructor getInstructor(String username) {
		try {
			return jdbcTemplate.queryForObject("SELECT * FROM Instructor "
					+ "WHERE username=?", new InstructorRowMapper(), username);
		}catch(EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	public List<Instructor> getInstructors(){
		try {
			return jdbcTemplate.query("SELECT * FROM Instructor", new InstructorRowMapper());
		}catch(EmptyResultDataAccessException e) {
			return new ArrayList<Instructor>();
		}
	}
	
}
