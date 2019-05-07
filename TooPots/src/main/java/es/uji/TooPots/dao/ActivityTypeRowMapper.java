package es.uji.TooPots.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import org.springframework.jdbc.core.RowMapper;

import es.uji.TooPots.model.Activity;
import es.uji.TooPots.model.ActivityType;

public class ActivityTypeRowMapper implements RowMapper<ActivityType> {


	@Override
	public ActivityType mapRow(ResultSet rs, int rowNum) throws SQLException {
		ActivityType act = new ActivityType();
		
		act.setName(rs.getString("name"));
		act.setDescription(rs.getString("description"));
		
		return act;
		
	}

}
