package es.uji.TooPots.dao;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import es.uji.TooPots.model.Person;

@Repository
public class PersonDao {
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	public void addPerson(Person person) {
		//mail, name, surname, age, dni
		jdbcTemplate.update("INSERT INTO Person VALUES(?, ?, ?, ?, ?)",
				person.getMail(), person.getName(), person.getSurname(),
				person.getAge(), person.getDni());
	}
	
	public void updatePerson(Person person) {
		//mail, name, surname, age, dni
		jdbcTemplate.update("UPDATE Person SET mail=?, name=?, surname=?"
				+ "age=?, dni=?",
				person.getMail(), person.getName(), person.getSurname(),
				person.getAge(), person.getDni());
	}
	
	public void deletePerson(Person person) {
		jdbcTemplate.update("DELETE FROM Person WHERE mail=?",
				person.getMail());
	}
	
	//No sé si sería necesario añadir getPerson y getPeople
}
