package es.uji.TooPots.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import es.uji.TooPots.model.Certificate;
import es.uji.TooPots.model.Status;

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
		jdbcTemplate.update("INSERT INTO certificate VALUES(?, ?, ?, ?, ?)", 
				certificateId.getAndIncrement(),
				certificate.getStatus(), certificate.getActivityType(), certificate.getRoute(), 
				certificate.getOwnerMail());
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
			return jdbcTemplate.query("SELECT * FROM Certificate WHERE ownerMail=? and status=?", new CertificateRowMapper(), mail, Status.PENDING);
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
	
	
}
