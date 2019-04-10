package es.uji.TooPots.dao;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import es.uji.TooPots.model.Activity;

@Repository
public class ActivityDao {
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	public void addActivity(Activity activity) {
		jdbcTemplate.update("INSERT INTO Activity VALUES(?, ?, ?, ?, ?, ?, ?, ?)",
				activity.getActivityCode(), activity.getName(), activity.getLocation(),
				activity.getDateTime(), activity.getDuration(), activity.getVacancies(),
				activity.getPrice(), activity.getLevel());
	}
	
	public void deleteActivity(int activityId) {
		jdbcTemplate.update("DELETE from Activity WHERE activityCode=?",
				activityId);
	}
	public void updateActivity(Activity activity) {
		jdbcTemplate.update("UPDATE Activity SET activityCode=?, name=?,"
				+ "location=?, dateTime=?, duration=?, vacancies=?,"
				+ "price=?, level=?",
				activity.getActivityCode(), activity.getName(), activity.getLocation(),
				activity.getDateTime().toString(), activity.getDuration(), activity.getVacancies(),
				activity.getPrice(), activity.getLevel().toString());
	}
	
	public Activity getActivity(int activityCode) {
		try {
			return jdbcTemplate.queryForObject("SELECT *"
					+ "from Activity WHERE activityCode=?",
					new ActivityRowMapper(), activityCode);
		}catch(EmptyResultDataAccessException e){
			return null;
		}
	}
	
	public List<Activity> getActivities(){
		try {
			return jdbcTemplate.query("SELECT * FROM Activity", new ActivityRowMapper());
		}catch(EmptyResultDataAccessException e) {
			return new ArrayList<Activity>();
		}
	}
}
