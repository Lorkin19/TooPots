package es.uji.TooPots.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import org.springframework.jdbc.core.RowMapper;

import es.uji.TooPots.model.Activity;

public class ActivityRowMapper implements RowMapper<Activity>{

	@Override
	public Activity mapRow(ResultSet rs, int rowNum) throws SQLException {
		// TODO Auto-generated method stub
		Activity act = new Activity();
		
		act.setActivityId(Integer.parseInt(rs.getString("activityId")));
		act.setName(rs.getString("name"));
		act.setDate(LocalDate.parse(rs.getString("date")));
		act.setLevel(rs.getString("level"));
		act.setDuration(rs.getString("duration"));
		act.setLocation(rs.getString("location"));
		act.setVacancies(Integer.parseInt(rs.getString("vacancies")));
		act.setPrice(Double.parseDouble(rs.getString("price")));	
		act.setMailInstructor(rs.getString("mailInstructor"));
		act.setDescription(rs.getString("description"));
		act.setActivityType(rs.getString("activityType"));
		act.setTime(rs.getString("time"));
		return act;
	}
}