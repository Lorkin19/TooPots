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

import es.uji.TooPots.model.Customer;
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
	
	public void deleteReceiveInformation(String mail, String activityTypeName) {
		jdbcTemplate.update("DELETE FROM ReceiveInformation WHERE mail=? and activityTypeName=?",
				mail, activityTypeName);
	}
	
	public List<ReceiveInformation> getCustomersForActivityType(String activityType){
		try {
			return jdbcTemplate.query("SELECT * FROM ReceiveInformation WHERE activityTypeName=?",
					new ReceiveInformationRowMapper(), activityType);
		}catch (EmptyResultDataAccessException e) {
			return new ArrayList<ReceiveInformation>();
		}
	}
	
	public boolean isSubscribed(String mail, String activityTypeName) {
		try {
			jdbcTemplate.queryForObject("SELECT * FROM ReceiveInformation WHERE mail=? and activityTypeName=?",
					new ReceiveInformationRowMapper(), mail, activityTypeName);
			return true;
		}catch (EmptyResultDataAccessException e) {
			return false;
		}
	}
	public List<ReceiveInformation> getCustomerSubscriptions(String mail){
		try {
			return jdbcTemplate.query("SELECT * FROM ReceiveInformation WHERE mail=?",
					new ReceiveInformationRowMapper(), mail);
		}catch (EmptyResultDataAccessException e) {
			return new ArrayList<ReceiveInformation>();
		}
	}
	
}
