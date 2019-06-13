package es.uji.TooPots.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import es.uji.TooPots.model.Message;

public class MessageRowMapper implements RowMapper<Message> {

	@Override
	public Message mapRow(ResultSet rs, int rowNum) throws SQLException {
		// TODO Auto-generated method stub
		Message m = new Message();
		m.setMessageId(Integer.parseInt(rs.getString("messageId")));
		m.setIssue(rs.getString("issue"));
		m.setMailReceiver(rs.getString("mailReceiver"));
		m.setText(rs.getString("text"));
		m.setStatus(rs.getString("status"));
		
		return m;
	}

}
