package es.uji.TooPots.dao;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import es.uji.TooPots.model.Reservation;

@Repository
public class ReservationDao {
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	public void addReservation(Reservation reservation) {
		jdbcTemplate.update("INSERT INTO Reservation VALUES(?, ?, ?, ?, ?)",
				reservation.getReservationId(), reservation.getPlace(),
				reservation.getPrice(), reservation.getMail(), reservation.getActivityCode());
	}
	
	public void deleteReservation(int reservationId) {
		jdbcTemplate.update("DELETE FROM Reservation WHERE reservationId=?",
				reservationId);
	}
	
	public void updateReservation(Reservation reservation) {
		jdbcTemplate.update("UPDATE Reservation SET reservationId=?, "
				+ "place=?, price=?, username=?, activityCode=?",
				reservation.getReservationId(), reservation.getPlace(),
				reservation.getPrice(), reservation.getMail(),
				reservation.getActivityCode());
	}	
	public Reservation getReservation(int reservationId) {
		try {
			return jdbcTemplate.queryForObject("SELECT * FROM Reservation "
					+ "WHERE reservationId=?", new ReservationRowMapper(), reservationId);
		}catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	public List<Reservation> getReservations(){
		try {
			return jdbcTemplate.query("SELECT * FROM Reservation",
					new ReservationRowMapper());
		}catch (EmptyResultDataAccessException e) {
			return new ArrayList<Reservation>();
		}
	}
}
