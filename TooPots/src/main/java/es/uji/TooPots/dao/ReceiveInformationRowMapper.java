package es.uji.TooPots.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import es.uji.TooPots.model.ReceiveInformation;

public class ReceiveInformationRowMapper implements RowMapper<ReceiveInformation> {

	@Override
	public ReceiveInformation mapRow(ResultSet rs, int rowNum) throws SQLException {
		// TODO Auto-generated method stub
		ReceiveInformation rI = new ReceiveInformation();
		
		rI.setActivityTypeName(rs.getString("activityTypeName"));
		rI.setMail(rs.getString("mail"));
		
		return rI;
	}

}
