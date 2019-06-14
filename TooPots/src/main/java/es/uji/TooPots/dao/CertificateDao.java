package es.uji.TooPots.dao;

import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import es.uji.TooPots.model.Certificate;

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
				certificateId.getAndIncrement(), certificate.getOwnerMail(),
				certificate.getStatus(), certificate.getRoute(), certificate.getActivityType());
	}
	
	public void deleteCertificate(Certificate certificate) {
		jdbcTemplate.update("DELETE FROM Certificate WHERE certificateId=?",
				certificate.getCertificateId());
	}
}
