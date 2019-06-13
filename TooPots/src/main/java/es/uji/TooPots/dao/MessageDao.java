package es.uji.TooPots.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import es.uji.TooPots.model.Message;

@Repository
public class MessageDao {
	private JdbcTemplate jdbcTemplate;
	
	private static AtomicInteger messageId;
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
		try {
			messageId = new AtomicInteger(jdbcTemplate.queryForObject("SELECT messageId FROM Message ORDER BY "
					+ "requestId DESC LIMIT 1", Integer.class));	
			messageId.getAndIncrement();
		}catch(EmptyResultDataAccessException e) {
			messageId = new AtomicInteger();
		}
	}
	
	public void addMessage(Message message) {
		jdbcTemplate.update("INSERT INTO Message VALUES(?, ?, ?, ?, ?)",
				messageId.getAndIncrement(), message.getIssue(),
				message.getText(), message.getMailReceiver(), message.getStatus());
	}
	
	public void deleteMessage(int messageId) {
		jdbcTemplate.update("DELETE FROM Message WHERE messageId=?",
				messageId);
	}
	
	public void updateMessage(Message message) {
		jdbcTemplate.update("UPDATE Message SET issue=?, mailReceiver=?, status=?, text=? WHERE messageId=?",
				message.getIssue(), message.getMailReceiver(), message.getStatus(), message.getText(),
				message.getMessageId());
	}
	
	public Message getMessage(int messageId) {
		try {
			return jdbcTemplate.queryForObject("SELECT * FROM Message"
					+ "WHERE messageID=?", new MessageRowMapper(), messageId);
		}catch(EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	public List<Message> getMessagesByMail(String mail){
		try {
			return jdbcTemplate.query("SELECT * FROM Message WHERE mailReceiver=?",
					new MessageRowMapper(), mail);
		}catch(EmptyResultDataAccessException e) {
			return new ArrayList<>();
		}
	}
}
