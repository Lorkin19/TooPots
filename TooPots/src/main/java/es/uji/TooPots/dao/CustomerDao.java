package es.uji.TooPots.dao;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import es.uji.TooPots.model.Customer;

@Repository
public class CustomerDao {
	private JdbcTemplate jdbcTemplate;
	//private FakeUserProvider fup = new FakeUserProvider();
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	public void addCustomer(Customer customer) {
		jdbcTemplate.update("INSERT INTO Customer VALUES(?, ?, ?, ?, ?)",
				 customer.getMail(), customer.getUsername(), customer.getPwd(),
				 customer.getName(), customer.getSurname());
		FakeUserProvider.addNewUser(customer, 0);
	}
	
	public void deleteCustomer(Customer customer) {
		jdbcTemplate.update("DELETE FROM Customer WHERE mail=?",
				customer.getMail());
		FakeUserProvider.deleteUser(customer.getMail());
	}
	
	public void updateCustomer(Customer customer) {
		jdbcTemplate.update("UPDATE Customer SET username=?, "
				+ "name=?, surname=? where mail=?",
				 customer.getUsername(),
				 customer.getName(), customer.getSurname(), customer.getMail());
	}
	
	public Customer getCustomer(String mail) {
		try {
			return jdbcTemplate.queryForObject("SELECT * FROM Customer WHERE"
					+ " mail=?", new CustomerRowMapper(), mail);
		}catch(EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	public List<Customer> getCustomers(){
		try {
			return jdbcTemplate.query("SELECT * FROM Customer", new CustomerRowMapper());
		}catch(EmptyResultDataAccessException e) {
			return new ArrayList<Customer>();
		}
	}
	
}