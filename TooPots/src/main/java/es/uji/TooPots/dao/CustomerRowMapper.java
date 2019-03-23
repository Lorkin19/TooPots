package es.uji.TooPots.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import es.uji.TooPots.model.Customer;

public class CustomerRowMapper implements RowMapper<Customer> {

	@Override
	public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
		// TODO Auto-generated method stub
		Customer customer = new Customer();
		customer.setUsername(rs.getString("username"));
		customer.setName(rs.getString("name"));
		customer.setSurname(rs.getString("surname"));
		customer.setPwd(rs.getString("pwd"));
		customer.setDni(rs.getString("dni"));
		customer.setMail(rs.getString("mail"));		
		return customer;
	}

}
