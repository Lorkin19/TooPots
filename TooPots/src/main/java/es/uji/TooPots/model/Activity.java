package es.uji.TooPots.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

public class Activity {
	
	/**
	 * Las actividades se identificaran mediante su activityCode.
	 * Queda decidir como se obtendra dicho codigo.
	 */
	private int activityCode;
	private String name;
	private String location;
	@DateTimeFormat(iso=DateTimeFormat.ISO.DATE)
	private LocalDate date;
	private String duration;
	private int vacancies;
	private double price;
	private String description;
	private String level;
	private String activityType;
	private String mailInstructor;
	
	public Activity() {}

	public int getActivityCode() {
		return activityCode;
	}

	public void setActivityCode(int activityCode) {
		this.activityCode = activityCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public LocalDate getDateTime() {
		return date;
	}

	public void setDateTime(LocalDate dateTime) {
		this.date = dateTime;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public int getVacancies() {
		return vacancies;
	}

	public void setVacancies(int vacancies) {
		this.vacancies = vacancies;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getActivityType() {
		return activityType;
	}

	public void setActivityType(String activityType) {
		this.activityType = activityType;
	}

	public String getEmail() {
		return mailInstructor;
	}

	public void setEmail(String email) {
		this.mailInstructor = email;
	}
	
	
}
