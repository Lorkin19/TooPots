package es.uji.TooPots.model;

public class Message {
	
	private int messageId;
	private String issue;
	private String mailReceiver;
	private String text;
	private String status; 

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public int getMessageId() {
		return messageId;
	}
	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}
	public String getIssue() {
		return issue;
	}
	public void setIssue(String issue) {
		this.issue = issue;
	}
	public String getMailReceiver() {
		return mailReceiver;
	}
	public void setMailReceiver(String mailReceiver) {
		this.mailReceiver = mailReceiver;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	

}
