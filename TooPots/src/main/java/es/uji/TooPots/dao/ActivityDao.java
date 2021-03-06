package es.uji.TooPots.dao;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import es.uji.TooPots.model.Activity;

@Repository
public class ActivityDao {
	private JdbcTemplate jdbcTemplate;
	private static AtomicInteger activityId; 
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		try {
			activityId = new AtomicInteger(jdbcTemplate.queryForObject("SELECT activityId FROM Activity ORDER BY "
					+ "activityId DESC LIMIT 1", Integer.class));	
			activityId.getAndIncrement();
		}catch(EmptyResultDataAccessException e) {
			activityId = new AtomicInteger();
		}
	}
	
	public void addActivity(Activity activity) {
		jdbcTemplate.update("INSERT INTO Activity VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
				activityId.getAndIncrement(), activity.getName(), activity.getLocation(),
				activity.getDuration(), activity.getVacancies(), activity.getDescription(),
				activity.getLevel(), activity.getActivityType(), activity.getPrice(), activity.getMailInstructor(),
				 activity.getTime(),activity.getDate());
	}
	
	public void deleteActivity(int activityId) {
		jdbcTemplate.update("DELETE from Activity WHERE activityId=?",
				activityId);
	}
	public void updateActivity(Activity activity) {
		jdbcTemplate.update("UPDATE Activity SET name=?,"
				+ "location=?, date=?, duration=?, vacancies=?,"
				+ "level=?, price=?, time=? WHERE activityId=? ",
				activity.getName(), activity.getLocation(),
				activity.getDate(), activity.getDuration(), activity.getVacancies(),
				activity.getLevel().toString(), activity.getPrice(), activity.getTime(), activity.getActivityId());
	}
	
	public Activity getActivity(int activityCode) {
		try {
			return jdbcTemplate.queryForObject("SELECT *"
					+ "from Activity WHERE activityId=?",
					new ActivityRowMapper(), activityCode);
		}catch(EmptyResultDataAccessException e){
			return new Activity();
		}
	}
	
	public List<Activity> getActivities(){
		try {
			return jdbcTemplate.query("SELECT * FROM Activity WHERE vacancies >= 1 and date>=?", new ActivityRowMapper(), LocalDate.now());
		}catch(EmptyResultDataAccessException e) {
			return new ArrayList<Activity>();
		}
	}
	
	public List<Activity> getInstructorActivities(String mailInstructor){
		try {
			return jdbcTemplate.query("SELECT * FROM Activity WHERE mailInstructor=?", new ActivityRowMapper(), mailInstructor);
		}catch(EmptyResultDataAccessException e) {
			return new ArrayList<Activity>();
		}
	}
	
	public List<Activity> getActivitiesOfType(String activityTypeName){
		try {
			return jdbcTemplate.query("SELECT * FROM Activity WHERE activityType=?", new ActivityRowMapper(), activityTypeName);
		}catch(EmptyResultDataAccessException e) {
			return new ArrayList<Activity>();
		}
	}
	
	public int getActivityId() {
		return activityId.get();
	}
}