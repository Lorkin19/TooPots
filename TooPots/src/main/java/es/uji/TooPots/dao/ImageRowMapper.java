package es.uji.TooPots.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import es.uji.TooPots.model.Image;

public class ImageRowMapper implements RowMapper<Image> {

	@Override
	public Image mapRow(ResultSet rs, int rowNum) throws SQLException {
		// TODO Auto-generated method stub
		Image image = new Image();
		
		image.setOwnerMail(rs.getString("ownerMail"));
		image.setRoute(rs.getString("route"));
		
		return image;
	}

}
