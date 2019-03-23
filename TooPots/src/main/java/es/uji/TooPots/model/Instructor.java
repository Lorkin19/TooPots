package es.uji.TooPots.model;

public class Instructor extends Person{
	/**
	 * Aquí podríamos crear una clase abstracta para
	 * que instructor e instructorRequest tengan en común
	 * algunos de sus atributos y no tener que repetirlos
	 * 
	 * Los instructores se identificaran mediante un username unico.
	 */
	
	private String username;
	private String[] routesCertificates; //Se guardará la ruta a cada certificado
	private String pwd;
	private String bankAccount;
	
	public Instructor() {
		super();
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
	
	
}
