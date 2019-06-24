package es.uji.TooPots.model;

public class Instructor implements User{
	/**
	 * Aquí podríamos crear una clase abstracta para
	 * que instructor e instructorRequest tengan en común
	 * algunos de sus atributos y no tener que repetirlos
	 * 
	 * Los instructores se identificaran mediante un username unico.
	 */
	private String mail;
	private String name;
	private String surname;
	private String username;
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
