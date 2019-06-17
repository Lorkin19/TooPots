package es.uji.TooPots.dao;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import es.uji.TooPots.model.ReceiveInformation;

@Repository
public class ReceiveInformationDao {
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	public void addReceiveInformation(ReceiveInformation receiveInformation) {
		jdbcTemplate.update("INSERT INTO ReceiveInformation VALUES(?, ?)",
				receiveInformation.getMail(), receiveInformation.getActivityTypeName());
	}
	
	public void deleteReceiveInformation(ReceiveInformation receiveInformation) {
		jdbcTemplate.update("DELETE FROM ReceiveInformation WHERE mail=? and acitivityTypeName=?",
				receiveInformation.getMail(), receiveInformation.getActivityTypeName());
	}
	
	public List<String> getCustomersForActivityType(String activityType){
		try {
			return jdbcTemplate.query("SELECT mail FROM ReceiveInformation WHERE activityTypeName=?",
					new StringRowMapper(), activityType);
		}catch (EmptyResultDataAccessException e) {
			return new ArrayList<String>();
		}
	}
	
}
