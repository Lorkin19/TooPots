package es.uji.TooPots.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import es.uji.TooPots.model.Reservation;

public class ReservationRowMapper implements RowMapper<Reservation> {

	public Reservation mapRow(ResultSet rs, int rowNum) throws SQLException {
		// TODO Auto-generated method stub
		
		Reservation reservation = new Reservation();
		reservation.setPlace(rs.getString("place"));
		reservation.setPrice(Double.parseDouble(rs.getString("price")));
		reservation.setReservationId(Integer.parseInt(rs.getString("reservationId")));
		reservation.setStatus(rs.getString("status"));
		reservation.setMail(rs.getString("mail"));
		reservation.setVacancies(Integer.parseInt(rs.getString("vacancies")));
		reservation.setActivityId(Integer.parseInt(rs.getString("activityId")));
		return reservation;
	}
}
