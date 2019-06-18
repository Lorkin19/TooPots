package es.uji.TooPots.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import es.uji.TooPots.dao.MessageDao;
import es.uji.TooPots.model.Message;
import es.uji.TooPots.model.UserDetails;

@Controller
@RequestMapping("/message")
public class MessageController {
	
	private MessageDao messageDao;
	
	@Autowired
	public void setMessageDao(MessageDao messageDao) {
		this.messageDao = messageDao;
	}
	
	@RequestMapping("/list")
	public String listMyMessages(Model model, HttpSession session) {
		UserDetails user = (UserDetails) session.getAttribute("user");
		if (user == null) {
			session.setAttribute("pagAnt", "/message/list");
			return "redirect:/login";
		}
		model.addAttribute("messages", messageDao.getMessagesByMail(user.getMail()));
		return "customer/messages";
	}
	
	@RequestMapping("/delete/{id}")
	public String processDeleteMessage(@PathVariable int id) {
		messageDao.deleteMessage(id);
		return "customer/messages";
	}
	
	@RequestMapping("/archive/{id}")
	public String processArchiveMessage(@PathVariable int id) {
		Message message = messageDao.getMessage(id);
		message.setStatus("Archived");
		messageDao.updateMessage(message);
		return "customer/messages";
	}
}
