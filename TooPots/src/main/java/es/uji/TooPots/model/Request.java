package es.uji.TooPots.model;

public class Request {
	private int requestId;
	private String mail;
	private String name;
	private String surname;
	private String username;
	private String pwd;
	private String bankAccount;
	private String status;
	private String[] routesCertificates; //Se guardará la ruta a cada certificado

	
	
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getRequestId() {
		return this.requestId;
	}
	
	public void setRequestId(int requestId) {
		this.requestId = requestId;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

	public String[] getRoutesCertificates() {
		return routesCertificates;
	}
	public void setRoutesCertificates(String[] routesCertificates) {
		this.routesCertificates = routesCertificates;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getBankAccount() {
		return bankAccount;
	}
	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
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

}

