package es.uji.TooPots.model;

public class UserDetails {
	private String mail;
	private String username;
	private String password;
	private int userType;
	
	public String getMail(){
		return this.mail;
	}
	
	public void setMail(String mail) {
		this.mail = mail;
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public int getUserType() {
		return this.userType;
	}
	
	public void setUserType(int userType) {
		this.userType = userType;
	}
}

