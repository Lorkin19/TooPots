package es.uji.TooPots.dao;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import es.uji.TooPots.model.ActivityType;
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
		jdbcTemplate.update("INSERT INTO Instructor VALUES(?, ?, ?, ?, ?, ?)",
				instructor.getMail(), instructor.getName(),
				instructor.getSurname(),instructor.getUsername(),
				instructor.getPwd(), instructor.getBankAccount());
		FakeUserProvider.addNewUser(instructor, 1);
	}
	
	public void deleteInstructor(Instructor instructor) {
		jdbcTemplate.update("DELETE FROM Instructor WHERE username=?",
				instructor.getUsername());
		FakeUserProvider.deleteUser(instructor.getMail());
	}
	public void updateInstructor(Instructor instructor) {
		jdbcTemplate.update("UPDATE Instructor SET username=?, mail=?, pwd=?, bankAccount=?",
				instructor.getUsername(), instructor.getMail(), instructor.getPwd(),
				instructor.getBankAccount());
	}
	
	public boolean isInstructor(String mail) {
		try {
			jdbcTemplate.queryForObject("SELECT * FROM Instructor "
					+ "WHERE mail=?", new InstructorRowMapper(), mail);
			return true;
		}catch (EmptyResultDataAccessException e) {
			return false;
		}
	}
	
	public Instructor getInstructor(String mail) {
		try {
			return jdbcTemplate.queryForObject("SELECT * FROM Instructor "
					+ "WHERE mail=?", new InstructorRowMapper(), mail);
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
	
	public List<ActivityType> getInstructorActivities(String mail){
		try {
			return jdbcTemplate.query("SELECT activityType FROM Activity "+
					"WHERE mailInstructor=?", new ActivityTypeRowMapper(), mail);
		}catch(EmptyResultDataAccessException e) {
			return new ArrayList<ActivityType>();
		}
	}	
}