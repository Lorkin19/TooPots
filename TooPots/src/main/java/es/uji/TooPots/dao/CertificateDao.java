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
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

import es.uji.TooPots.model.Certificate;
import es.uji.TooPots.model.Image;
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
	
	public void deleteCertificate(int certificate) {
		jdbcTemplate.update("DELETE FROM Certificate WHERE certificateId=?",
				certificate);
	}
	
	public void deleteInstructorCertificates(String mail) {
		jdbcTemplate.update("DELETE FROM Certificate WHERE ownerMail=?",
				mail);
	}
	
	public void updateCertificate(Certificate certificate) {
		jdbcTemplate.update("UPDATE Certificate SET status=?, activityType=? WHERE certificateId=?",
				certificate.getStatus(), certificate.getActivityType(), certificate.getCertificateId());
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
			return jdbcTemplate.query("SELECT * FROM Certificate WHERE ownerMail=? and status=?", new CertificateRowMapper(), mail, Status.PENDING);
		}catch (EmptyResultDataAccessException e) {
			return new ArrayList<Certificate>();
		}
	}
	
	public List<Certificate> getInstructorApprovedCertificates(String mail){
		try {
			return jdbcTemplate.query("SELECT * FROM Certificate WHERE ownerMail=? and status=?", new CertificateRowMapper(), mail, Status.APPROVED);
		}catch (EmptyResultDataAccessException e) {
			return new ArrayList<Certificate>();
		}
	}
	
	public List<Certificate> getInstructorRejectedCertificates(String mail){
		try {
			return jdbcTemplate.query("SELECT * FROM Certificate WHERE ownerMail=? and status=?", new CertificateRowMapper(), mail, Status.REJECTED);
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
	
	public List<Certificate> getInstructorsCertificates(){
		try {
			return jdbcTemplate.query("SELECT * FROM Certificate WHERE status=? and ownerMail IN (SELECT mail FROM Instructor)", new CertificateRowMapper(), Status.PENDING);
		}catch (EmptyResultDataAccessException e) {
			return new ArrayList<Certificate>();
		}
	}
	
	public String uploadCertificate(MultipartFile[] files, String mail, String uploadDirectory) {
		StringBuilder message = new StringBuilder();
		StringBuilder paths = new StringBuilder(); 
		Certificate certificate = new Certificate();
		CertificateValidator cV = new CertificateValidator();

		try {
		for (MultipartFile file : files) {
			if (file.isEmpty()) {
				message.append("Please select a file to upload. Empty file: " + file.getOriginalFilename());
				return message.toString();
			}
				
			
			byte[] bytes = file.getBytes();
			Path path = Paths.get(uploadDirectory + "/pdfs/request/"+mail);
			if (!Files.isDirectory(path)) {
				Files.createDirectories(path);
			}
			path = Paths.get(uploadDirectory + "/pdfs/request/"+mail+"/"+ file.getOriginalFilename());

			
			certificate.setOwnerMail(mail);
			certificate.setRoute("/pdfs/request/"+mail+"/" + file.getOriginalFilename());
			certificate.setActivityType("");
			certificate.setStatus(Status.PENDING);
			certificate.setFileName(file.getOriginalFilename());
			
			
			String extension = certificate.getRoute().substring(certificate.getRoute().lastIndexOf("."));
			
			if (!extension.equals(".pdf")) {
				message.append("Extension not supported. "+file.getOriginalFilename());
				return message.toString();
			}
			
			List<Certificate> certificates = getInstructorCertificates(mail);
			if (certificates.size()>0) {
				for (Certificate c:certificates) {
					if (c.getRoute().equals(certificate.getRoute())) {
						message.append("File already exists. "+file.getOriginalFilename());
						return message.toString();
					}
				}
			}

			Files.write(path, bytes);
			paths.append(uploadDirectory+"/pdfs/request/"+mail+"/" + file.getOriginalFilename()+"\n");
			
			addCertificate(certificate);
		}			
		message.append("Success");
		}catch(IOException e) {
			message = new StringBuilder("An error has occurred.\n");
			message.append(e.getStackTrace());
		}
		return message.toString();
	}

}

class CertificateValidator implements Validator{

	@Override
	public boolean supports(Class<?> clazz) {
		// TODO Auto-generated method stub
		return Certificate.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		// TODO Auto-generated method stub
		Certificate certificate = (Certificate) target;
		String extension = certificate.getRoute().substring(certificate.getRoute().lastIndexOf("."));
		
		if (!extension.equals(".pdf")) {
			errors.reject("Extension not supported");
		}

	}
	
}
