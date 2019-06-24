package es.uji.TooPots.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import es.uji.TooPots.dao.MessageDao;
import es.uji.TooPots.model.Message;
import es.uji.TooPots.model.Status;
import es.uji.TooPots.model.UserDetails;

@Controller
@RequestMapping("/messages")
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
		System.out.println(user.getMail());
		model.addAttribute("messages", messageDao.getMessagesByMail(user.getMail()));
		model.addAttribute("archivedMessages", messageDao.getArchivedMessagesByMail(user.getMail()));
		return "myMessages";
	}
	
	@RequestMapping("/delete/{id}")
	public String processDeleteMessage(@PathVariable int id) {
		messageDao.deleteMessage(id);
		return "redirect:/messages/list#tab1";
	}
	
	@RequestMapping("/archive/{id}")
	public String processArchiveMessage(@PathVariable int id) {
		Message message = messageDao.getMessage(id);
		message.setStatus(Status.ARCHIVED);
		messageDao.updateMessage(message);
		return "redirect:/messages/list#tab1";
	}
	
	@RequestMapping("/viewMessage/{id}")
	public String viewMessage(Model model, @PathVariable("id") int messageId) {
		model.addAttribute("message",messageDao.getMessage(messageId));
		return "viewMessage";
	}
	
	@RequestMapping("/reopen/{id}")
	public String reopen(@PathVariable("id") int messageId) {
		Message message = messageDao.getMessage(messageId);
		message.setStatus(Status.NOTARCHIVED);
		messageDao.updateMessage(message);
		return "redirect:/messages/list#tab2";
	}
}
