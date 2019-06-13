package es.uji.TooPots.dao;

import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

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
}
