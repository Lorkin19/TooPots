package es.uji.TooPots.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.jdbc.core.RowMapper;

import es.uji.TooPots.model.Activity;
import es.uji.TooPots.model.Level;

public class ActivityRowMapper implements RowMapper<Activity>{

	@Override
	public Activity mapRow(ResultSet rs, int rowNum) throws SQLException {
		// TODO Auto-generated method stub
		Activity act = new Activity();
		
		act.setActivityCode(Integer.parseInt(rs.getString("activityCode")));
		act.setName(rs.getString("name"));
		act.setDateTime(LocalDate.parse(rs.getString("dateTime")));
		act.setLevel(rs.getString("level"));
		act.setDuration(rs.getString("duration"));
		act.setLocation(rs.getString("location"));
		act.setVacancies(Integer.parseInt(rs.getString("vacancies")));
		act.setPrice(Double.parseDouble(rs.getString("price")));	
		return act;
	}
}
