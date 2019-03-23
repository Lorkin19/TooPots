package es.uji.TooPots.model;

import java.util.concurrent.atomic.AtomicInteger;

public class InstructorRequest extends Person{
	
	private int requestId;
	private String status;
	private String[] routesCertificates; //Se guardará la ruta a cada certificado
	
	public InstructorRequest() {
		super();
	}
	
	public int getRequestId() {
		return requestId;
	}
	
	public void setRequestId(int requestId) {
		this.requestId = requestId;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getMail() {
		return mail;
	}
	
	public void setMail(String mail) {
		this.mail = mail;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getSurname() {
		return surname;
	}
	
	public void setSurname(String surname) {
		this.surname = surname;
	}
	
	public String getDni() {
		return dni;
	}
	
	public void setDni(String dni) {
		this.dni = dni;
	}
	
	public String[] getRoutesCertificates() {
		return routesCertificates;
	}
	
	public void setRoutesCertificates(String[] routesCertificates) {
		this.routesCertificates = routesCertificates;
	}
	
	public int getAge() {
		return this.age;
	}
	
	public void setAge(int age) {
		this.age = age;
	}
}
