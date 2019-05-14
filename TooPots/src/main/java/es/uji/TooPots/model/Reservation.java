package es.uji.TooPots.model;

public class Reservation {
	private int reservationId;
	private int vacancies;
	private String place;
	private double price;
	private String mail;
	private int activityId;
	private String status;
	
	public Reservation() {}

	public int getReservationId() {
		return reservationId;
	}

	public void setReservationId(int reservationNumber) {
		this.reservationId = reservationNumber;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}
	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public int getActivityId() {
		return activityId;
	}

	public void setActivityId(int activityCode) {
		this.activityId = activityCode;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public int getVacancies() {
		return vacancies;
	}

	public void setVacancies(int vacancies) {
		this.vacancies = vacancies;
	}
}
