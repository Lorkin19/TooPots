package es.uji.TooPots.dao;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import es.uji.TooPots.model.Activity;
import es.uji.TooPots.model.ActivityType;

@Repository
public class ActivityTypeDao {
	
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	public void addActivityType(ActivityType act) {
		jdbcTemplate.update("INSERT INTO ActivityType VALUES(?, ?)",
				act.getName(), act.getDescription());
	}
	
	public ActivityType getActivityType(String actName) {
		try {
			return jdbcTemplate.queryForObject("SELECT *"+
					"FROM ActivityType WHERE name=?",
					new ActivityTypeRowMapper(), actName);
		}catch(EmptyResultDataAccessException e) {
			return new ActivityType();
		}
	}
	
	public List<ActivityType> getActivityTypes(){
		try {
			return jdbcTemplate.query("SELECT * FROM ActivityType",
					new ActivityTypeRowMapper());
		}catch(EmptyResultDataAccessException e) {
			return new ArrayList<ActivityType>();
		}
	}
}
