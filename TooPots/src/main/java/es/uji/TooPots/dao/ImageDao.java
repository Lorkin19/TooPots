package es.uji.TooPots.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import es.uji.TooPots.model.Image;

@Repository
public class ImageDao {
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	public void addImage(Image image) {
		jdbcTemplate.update("INSERT INTO Image VALUES(?, ?)",
				image.getOwnerMail(), image.getRoute());
	}
	
	public void deleteImage(String route) {
		jdbcTemplate.update("DELETE FROM Image WHERE route=?", route);
	}
	
	public List<Image> getInstructorImages(String mail) {
		try {
			return jdbcTemplate.query("SELECT * FROM Image WHERE ownerMail=?", new ImageRowMapper(), mail);
		}catch (EmptyResultDataAccessException e) {
			return new ArrayList<Image>();
		}
	}
}
