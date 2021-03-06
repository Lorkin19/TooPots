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

import es.uji.TooPots.model.Reservation;
import es.uji.TooPots.model.Status;

@Repository
public class ReservationDao {
	private JdbcTemplate jdbcTemplate;
	private static AtomicInteger reservationId;
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		try {
			reservationId = new AtomicInteger(jdbcTemplate.queryForObject("SELECT reservationId FROM Reservation ORDER BY "
					+ "reservationId DESC LIMIT 1", Integer.class));
			reservationId.getAndIncrement();
		}catch(EmptyResultDataAccessException e) {
			reservationId = new AtomicInteger();
		}
	}
	
	public void addReservation(Reservation reservation) {
		jdbcTemplate.update("INSERT INTO Reservation VALUES(?, ?, ?, ?, ?, ?, ?, ?)",
				reservationId.getAndIncrement(), reservation.getVacancies(),
				reservation.getPrice(), reservation.getMail(), reservation.getActivityId(),
				Status.PENDING, reservation.getPlace(), reservation.getDate());
	}
	
	public void deleteReservation(int reservationId) {
		jdbcTemplate.update("DELETE FROM Reservation WHERE reservationId=?",
				reservationId);
	}
	
	public void updateReservation(Reservation reservation) {
		jdbcTemplate.update("UPDATE Reservation SET "
				+ "price=?, mail=?, activityId=?, status=? WHERE reservationId=?",
				reservation.getPrice(), reservation.getMail(),
				reservation.getActivityId(), reservation.getStatus(),reservation.getReservationId());
	}	
	public Reservation getReservation(int reservationId) {
		try {
			return jdbcTemplate.queryForObject("SELECT * FROM Reservation "
					+ "WHERE reservationId=?", new ReservationRowMapper(), reservationId);
		}catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	public List<Reservation> getActivityReservations(int activityId){
		try {
			return jdbcTemplate.query("SELECT * FROM Reservation WHERE activityId=? ORDER BY date",
					new ReservationRowMapper(), activityId);
		}catch (EmptyResultDataAccessException e) {
			return new ArrayList<Reservation>();
		}
	}	
	public List<Reservation> getCustomerReservations(String customerMail){
		try {
			return jdbcTemplate.query("SELECT * FROM Reservation WHERE mail=? and status=? and date>=? ORDER BY date",
					new ReservationRowMapper(), customerMail, Status.PENDING, LocalDate.now());
		}catch (EmptyResultDataAccessException e) {
			return new ArrayList<Reservation>();
		}
	}
	public List<Reservation> getPaidCustomerReservations(String customerMail){
		try {
			return jdbcTemplate.query("SELECT * FROM Reservation WHERE mail=? and status=? and date>=? ORDER BY date",
					new ReservationRowMapper(), customerMail, Status.PAID, LocalDate.now());
		}catch (EmptyResultDataAccessException e) {
			return new ArrayList<Reservation>();
		}
	}
	
	public List<Reservation> checkCustomerReservations(String customerMail){
		try {
			return jdbcTemplate.query("SELECT * FROM Reservation WHERE mail=? and date>=? ORDER BY date",
					new ReservationRowMapper(), customerMail, LocalDate.now());
		}catch (EmptyResultDataAccessException e) {
			return new ArrayList<Reservation>();
		}
	}
	
	
	public List<Reservation> getReservations(){
		try {
			return jdbcTemplate.query("SELECT * FROM Reservation ORDER BY date",
					new ReservationRowMapper());
		}catch (EmptyResultDataAccessException e) {
			return new ArrayList<Reservation>();
		}
	}
}
