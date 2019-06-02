package es.uji.TooPots.dao;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.jasypt.util.password.BasicPasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import es.uji.TooPots.model.UserDetails;
import es.uji.TooPots.model.Administrator;
import es.uji.TooPots.model.Customer;
import es.uji.TooPots.model.Instructor;
import es.uji.TooPots.model.User;

@Repository
public class FakeUserProvider implements UserDao{
	
	private static Map<String, UserDetails> knownUsers = new HashMap<String, UserDetails>();
	private JdbcTemplate jdbcTemplate;

	
	public FakeUserProvider() {
	}

	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		initializeMap();
	}
	
	private void initializeMap() {
		try {
			List<Customer> cL = this.jdbcTemplate.query("SELECT * FROM Customer", new CustomerRowMapper());
			List<Instructor> iL = this.jdbcTemplate.query("SELECT * FROM Instructor", new InstructorRowMapper());
			List<Administrator> aL = this.jdbcTemplate.query("SELECT * FROM Admin", new AdministratorRowMapper());
			BasicPasswordEncryptor bpe = new BasicPasswordEncryptor();

			for (Customer c : cL) {
				UserDetails d = new UserDetails();
				d.setUsername(c.getUsername());
				d.setMail(c.getMail());
				d.setPassword(bpe.encryptPassword(c.getPwd()));
				d.setUserType(0);
				knownUsers.put(d.getMail(), d);
			}
			
			for (Instructor i : iL) {
				UserDetails d = new UserDetails();
				d.setUsername(i.getUsername());
				d.setMail(i.getMail());
				d.setPassword(bpe.encryptPassword(i.getPwd()));
				d.setUserType(1);
				knownUsers.put(d.getMail(), d);
			}
			
			for (Administrator a : aL) {
				UserDetails d = new UserDetails();
				d.setUsername(a.getUsername());
				d.setMail(a.getMail());
				d.setPassword(bpe.encryptPassword(a.getPwd()));
				d.setUserType(2);
				knownUsers.put(d.getMail(), d);
			}
		}catch (EmptyResultDataAccessException e) {}
	}
	
	
	@Override
	public UserDetails loadUserByMail(String mail, String password) {
		// TODO Auto-generated method stub
		UserDetails user = knownUsers.get(mail.trim());
		if (user == null) {
			return null;
		}
		BasicPasswordEncryptor bpe = new BasicPasswordEncryptor();
		if (bpe.checkPassword(password, user.getPassword())) {
			return user;
		}	
		return null;
	}

	@Override
	public Collection<UserDetails> listAllUsers() {
		// TODO Auto-generated method stub
		return knownUsers.values();
	}
	
	public static void addNewUser(User c, int userType) {
		BasicPasswordEncryptor bpe = new BasicPasswordEncryptor();
		UserDetails d = new UserDetails();
		d.setMail(c.getMail());
		d.setUsername(c.getUsername());
		d.setPassword(bpe.encryptPassword(c.getPwd()));
		d.setUserType(userType);
		knownUsers.put(d.getMail(), d);
	}
	
	public static void deleteUser(String mail) {
		knownUsers.remove(mail);		
	}
	
	public static boolean checkMail(String mail) {
		return knownUsers.containsKey(mail);
	}

}
