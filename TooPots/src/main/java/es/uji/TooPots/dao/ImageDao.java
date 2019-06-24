package es.uji.TooPots.dao;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.validation.BindingResult;
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
	
	public void deleteInstructorImages(String mail) {
		List<Image> images = getInstructorImages(mail);
		
		for (Image i : images) {
			jdbcTemplate.update("DELETE FROM Image WHERE route=?", i.getRoute());
		}
	}
	
	public List<Image> getInstructorImages(String mail) {
		try {
			return jdbcTemplate.query("SELECT * FROM Image WHERE ownerMail=?", new ImageRowMapper(), mail);
		}catch (EmptyResultDataAccessException e) {
			return new ArrayList<Image>();
		}
	}
	
	public List<Image> getActivityImages(int activityId, String mail){
		try {
			List<Image> im = jdbcTemplate.query("SELECT * FROM Image WHERE ownerMail=?", new ImageRowMapper(), mail);
			List<Image> list = new ArrayList<Image>();
			for (Image i : im) {
				String route = i.getRoute().substring(0, i.getRoute().lastIndexOf("/"));
				if (route.equals("/images/activities/"+activityId+"/"+mail)) {
					list.add(i);
				}
			}
			return list;
		}catch (EmptyResultDataAccessException e) {
			return new ArrayList<Image>();
		}
	}
	
	public String uploadImage(MultipartFile[] files, UserDetails user, String uploadDirectory, int activityId, BindingResult bindingResult) {
		StringBuilder message = new StringBuilder();
		ImageValidator iV = new ImageValidator();
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
				
				image.setOwnerMail(user.getMail());
				image.setRoute("/images/activities/"+activityId+"/"+user.getMail()+"/" + file.getOriginalFilename());
				
				iV.validate(image, bindingResult);
				
				if (bindingResult.hasErrors()) {
					message.append("Extension not supported. "+file.getOriginalFilename());
					return message.toString();
				}
				List<Image> images = getInstructorImages(user.getMail());
				if (images.size()>0) {
					for (Image i:images) {
						if (i.getRoute().equals(image.getRoute())) {
							message.append("File already exists. "+file.getOriginalFilename());
							return message.toString();
						}
					}
				}
				
				path = Paths.get(uploadDirectory + "/images/activities/"+activityId+"/"+user.getMail()+"/" + file.getOriginalFilename());
				Files.write(path, bytes);
				paths.append(uploadDirectory+"/images/activities/"+activityId+"/"+user.getMail()+"/" + file.getOriginalFilename()+"\n");
				
				
				
				addImage(image);
			}
			message.append("Success");
		}catch(IOException e) {
			message = new StringBuilder("An error has occurred.\n");
			message.append(e.getStackTrace());
		}
		return message.toString();
	}
}
