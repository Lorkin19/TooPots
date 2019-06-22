package es.uji.TooPots.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import es.uji.TooPots.model.CanOrganize;

public class CanOrganizeRowMapper implements RowMapper<CanOrganize> {

	@Override
	public CanOrganize mapRow(ResultSet rs, int rowNum) throws SQLException {
		// TODO Auto-generated method stub
		CanOrganize cO = new CanOrganize();
		cO.setActivityTypeName(rs.getString("activityTypeName"));
		cO.setMail(rs.getString("mail"));
		
		return cO;
	}

}
