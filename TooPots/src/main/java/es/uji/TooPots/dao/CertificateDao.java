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

import es.uji.TooPots.model.Certificate;
import es.uji.TooPots.model.Status;
import es.uji.TooPots.model.UserDetails;

@Repository
public class CertificateDao {
	private JdbcTemplate jdbcTemplate;
	private static AtomicInteger certificateId;
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		try {
			certificateId = new AtomicInteger(jdbcTemplate.queryForObject("SELECT certificateId FROM Certificate ORDER BY "
					+ "certificateId DESC LIMIT 1", Integer.class));
			certificateId.getAndIncrement();
		}catch(EmptyResultDataAccessException e) {
			certificateId = new AtomicInteger();
		}
	}
	
	public void addCertificate(Certificate certificate) {
		jdbcTemplate.update("INSERT INTO certificate VALUES(?, ?, ?, ?, ?, ?)", 
				certificateId.getAndIncrement(),
				certificate.getStatus(), certificate.getActivityType(), certificate.getRoute(), 
				certificate.getOwnerMail(), certificate.getFileName());
	}
	
	public void deleteCertificate(Certificate certificate) {
		jdbcTemplate.update("DELETE FROM Certificate WHERE certificateId=?",
				certificate.getCertificateId());
	}
	
	public void updateCertificate(Certificate certificate) {
		jdbcTemplate.update("UPDATE Certificate SET status=? WHERE certificateId=?",
				certificate.getStatus(), certificate.getCertificateId());
	}
	
	public Certificate getCertificate(int certificateId) {
		try {
			return jdbcTemplate.queryForObject("SELECT * FROM Certificate WHERE certificateId=?", new CertificateRowMapper(), certificateId);
		}catch (EmptyResultDataAccessException e) {
			return new Certificate();
		}
	}
	
	public List<Certificate> getInstructorCertificates(String mail){
		try {
			return jdbcTemplate.query("SELECT * FROM Certificate WHERE ownerMail=?", new CertificateRowMapper(), mail);
		}catch (EmptyResultDataAccessException e) {
			return new ArrayList<Certificate>();
		}
	}
	
	public List<Certificate> getCertificates(){
		try {
			return jdbcTemplate.query("SELECT * FROM Certificate WHERE status=?", new CertificateRowMapper(), Status.PENDING);
		}catch (EmptyResultDataAccessException e) {
			return new ArrayList<Certificate>();
		}
	}
	
	public List<Certificate> getApprovedCertificates(){
		try {
			return jdbcTemplate.query("SELECT * FROM Certificate WHERE status=?", new CertificateRowMapper(), Status.APPROVED);
		}catch (EmptyResultDataAccessException e) {
			return new ArrayList<Certificate>();
		}
	}
	
	public List<Certificate> getRejectedCertificates(){
		try {
			return jdbcTemplate.query("SELECT * FROM Certificate WHERE status=?", new CertificateRowMapper(), Status.REJECTED);
		}catch (EmptyResultDataAccessException e) {
			return new ArrayList<Certificate>();
		}
	}
	
	public List<Certificate> getNewTypeCertificates(){
		try {
			return jdbcTemplate.query("SELECT * FROM Certificate WHERE status=?", new CertificateRowMapper(), Status.PENDINGTYPE);
		}catch (EmptyResultDataAccessException e) {
			return new ArrayList<Certificate>();
		}
	}
	
	public String uploadCertificate(MultipartFile[] files, UserDetails user, String uploadDirectory) {
		StringBuilder message = new StringBuilder();
		StringBuilder paths = new StringBuilder(); 
		Certificate certificate = new Certificate();

		try {
		for (MultipartFile file : files) {
			if (file.isEmpty()) {
				message.append("Please select a file to upload. Empty file: " + file.getOriginalFilename());
				return message.toString();
			}
							
			
			byte[] bytes = file.getBytes();
			Path path = Paths.get(uploadDirectory + "/pdfs/"+user.getMail());
			if (!Files.isDirectory(path)) {
				Files.createDirectories(path);
			}
			path = Paths.get(uploadDirectory + "/pdfs/"+user.getMail()+"/"+ file.getOriginalFilename());
			Files.write(path, bytes);

			
			certificate.setOwnerMail(user.getMail());
			certificate.setRoute("/pdfs/"+user.getMail()+"/" + file.getOriginalFilename());
			certificate.setActivityType("");
			certificate.setStatus(Status.PENDING);
			certificate.setFileName(file.getOriginalFilename());
		
			paths.append(uploadDirectory+"/pdfs/"+user.getMail()+"/" + file.getOriginalFilename()+"\n");
			
			addCertificate(certificate);
		}			
		message.append("You successfully uploaded:\n"+paths.toString());
		}catch(IOException e) {
			message = new StringBuilder("An error has occurred.\n");
			message.append(e.getStackTrace());
		}
		return message.toString();
	}
	
	
}
