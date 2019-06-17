package es.uji.TooPots.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import es.uji.TooPots.model.Activity;
import es.uji.TooPots.model.ActivityType;
import es.uji.TooPots.model.CanOrganize;

@Repository
public class CanOrganizeDao {
	
	private static AtomicInteger canOrganizeId;
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		try {
			canOrganizeId = new AtomicInteger(jdbcTemplate.queryForObject("SELECT canOrganizeId FROM CanOrganize ORDER BY "
					+ "canOrganizedId DESC LIMIT 1", Integer.class));	
			canOrganizeId.getAndIncrement();
		}catch(EmptyResultDataAccessException e) {
			canOrganizeId = new AtomicInteger();
		}
	}
	
	public void addCanOrganize(CanOrganize canOrganize) {
		jdbcTemplate.update("INSERT INTO CanOrganize VALUES(?, ?, ?)",
				this.canOrganizeId.getAndIncrement(), canOrganize.getMail(),
				canOrganize.getActivityTypeName());
	}
	
	public void deleteCanOrganize(int canOrganizeId) {
		jdbcTemplate.update("DELETE FROM CanOrganize WHERE canOrganizeId=?", canOrganizeId);
	}
	
	public List<ActivityType> getInstructorCanOrganize(String mail){
		try {
			return jdbcTemplate.query("SELECT activityTypeName FROM CanOrganize WHERE mail=?", new ActivityTypeRowMapper(), mail);
		}catch (EmptyResultDataAccessException e) {
			return new ArrayList<ActivityType>();
		}
	} 
}
