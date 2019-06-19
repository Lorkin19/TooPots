package es.uji.TooPots.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import es.uji.TooPots.model.Certificate;

public class CertificateRowMapper implements RowMapper<Certificate> {

	@Override
	public Certificate mapRow(ResultSet rs, int rowNum) throws SQLException {
		// TODO Auto-generated method stub
		Certificate c = new Certificate();
		
		c.setActivityType(rs.getString("ActivityType"));
		c.setCertificateId(Integer.parseInt(rs.getString("certificateId")));
		c.setOwnerMail(rs.getString("ownerMail"));
		c.setRoute(rs.getString("route"));
		c.setStatus(rs.getString("status"));
		c.setFileName(rs.getString("fileName"));
		
		return c;
	}

}
