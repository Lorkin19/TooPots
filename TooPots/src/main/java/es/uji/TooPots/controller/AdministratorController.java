package es.uji.TooPots.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import es.uji.TooPots.dao.InstructorDao;
import es.uji.TooPots.dao.RequestDao;
import es.uji.TooPots.model.Instructor;
import es.uji.TooPots.model.Request;

@Controller
@RequestMapping("/administrator")
public class AdministratorController {
	private InstructorDao instructorDao;
	
	@Autowired
	public void setInstructorDao(InstructorDao instructorDao) {
		this.instructorDao = instructorDao;
	}
	
	RequestDao requestDao;
	
	@Autowired
	public void setRequestDao(RequestDao requestDao) {
		this.requestDao = requestDao;
	}
	@RequestMapping("/myRequests")
	public String listRequests(Model model) {
		model.addAttribute("requests", requestDao.getRequests());
		return "administrator/myRequests";
	}
	
	@RequestMapping("/acceptRequest/{id}")
	public String acceptRequest(@PathVariable("id") int requestId) {
		Request request = requestDao.getRequest(requestId);
		request.setStatus("Accepted");
		requestDao.updateRequest(request);
		
		Instructor instructor = requestDao.convertToInstructor(request);
		instructorDao.addInstructor(instructor);

		return "redirect:/administrator/myRequests";
	}
	
	@RequestMapping("/rejectRequest/{id}")
	public String rejectRequest(@PathVariable("id") int requestId) {
		Request request = requestDao.getRequest(requestId);
		request.setStatus("Rejected");
		requestDao.updateRequest(request);
		return "redirect:/administrator/myRequests";
	}
	
}
