package es.uji.TooPots.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import es.uji.TooPots.model.Activity;
import es.uji.TooPots.model.ActivityType;
import es.uji.TooPots.model.CanOrganize;

@Repository
public class CanOrganizeDao {
	
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);

	}
	
	public void addCanOrganize(CanOrganize canOrganize) {
		System.out.println(canOrganize.getActivityTypeName());
		jdbcTemplate.update("INSERT INTO CanOrganize VALUES(?, ?)",
				canOrganize.getMail(), canOrganize.getActivityTypeName());
	}
	
	public void deleteCanOrganize(CanOrganize canOrganize) {
		jdbcTemplate.update("DELETE FROM CanOrganize WHERE mail=? and activityTypeName=?", canOrganize.getMail(), canOrganize.getActivityTypeName());
	}
	
	public List<ActivityType> getInstructorCanOrganize(String mail){
		try {
			return jdbcTemplate.query("SELECT activityTypeName as name FROM CanOrganize WHERE mail=?", new ActivityTypeRowMapper(), mail);
		}catch (EmptyResultDataAccessException e) {
			return new ArrayList<ActivityType>();
		}
	} 
	
	public Boolean isCanOrganize(String mail, String activityType) {
		try {
			jdbcTemplate.queryForObject("SELECT * FROM CanOrganize WHERE mail=? and activityTypeName=?", new CanOrganizeRowMapper(), mail, activityType);
			return true;
		}catch(EmptyResultDataAccessException e) {
			return false;
		}
	}
}
