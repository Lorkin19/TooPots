package es.uji.TooPots.dao;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import es.uji.TooPots.model.Image;
import es.uji.TooPots.model.UserDetails;

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
	
	public String uploadImage(MultipartFile[] files, UserDetails user, String uploadDirectory, int activityId) {
		StringBuilder message = new StringBuilder();
		try {
			byte[] bytes;
			Image image = new Image();
			StringBuilder paths = new StringBuilder(); 
			for (MultipartFile file : files) {
				if (file.isEmpty()) {
					message.append("Please select a image to upload");
					return message.toString();
				}
				bytes = file.getBytes();
				
				Path path = Paths.get(uploadDirectory + "/images/activities/"+activityId+"/"+user.getMail());
				if (!Files.isDirectory(path)) {
					Files.createDirectories(path);
				}
				
				path = Paths.get(uploadDirectory + "/images/activities/"+activityId+"/"+user.getMail()+"/" + file.getOriginalFilename());
				Files.write(path, bytes);
				paths.append(uploadDirectory+"/images/activities/"+activityId+"/"+user.getMail()+"/" + file.getOriginalFilename()+"\n");
				
				image.setOwnerMail(user.getMail());
				image.setRoute("/images/activities/"+activityId+"/"+user.getMail()+"/" + file.getOriginalFilename());
				
				addImage(image);
			}

		}catch(IOException e) {
			
		}
		return message.toString();
	}
}
