package es.uji.TooPots.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import es.uji.TooPots.dao.ActivityTypeDao;
import es.uji.TooPots.dao.CanOrganizeDao;
import es.uji.TooPots.dao.CertificateDao;
import es.uji.TooPots.dao.InstructorDao;
import es.uji.TooPots.dao.MessageDao;
import es.uji.TooPots.dao.RequestDao;
import es.uji.TooPots.model.ActivityType;
import es.uji.TooPots.model.CanOrganize;
import es.uji.TooPots.model.Certificate;
import es.uji.TooPots.model.Instructor;
import es.uji.TooPots.model.Message;
import es.uji.TooPots.model.Request;
import es.uji.TooPots.model.Status;
import es.uji.TooPots.model.UserDetails;

@Controller
@RequestMapping("/administrator")
public class AdministratorController {
	
	private CanOrganizeDao canOrganizeDao;
	
	@Autowired
	public void setCanOrganizeDao(CanOrganizeDao canOrganizeDao) {
		this.canOrganizeDao = canOrganizeDao;
	}
	
	private ActivityTypeDao activityTypeDao;
	
	@Autowired
	public void setActivityTypeDao(ActivityTypeDao activityTypeDao) {
		this.activityTypeDao = activityTypeDao;
	}
	
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
	
	MessageDao messageDao;
	
	@Autowired
	public void setMessageDao(MessageDao messageDao) {
		this.messageDao = messageDao;
	}
	
	CertificateDao certificateDao;
	
	@Autowired
	public void setCertificateDao(CertificateDao certificateDao) {
		this.certificateDao = certificateDao;
	}
	
	@RequestMapping("/myRequests")
	public String listRequests(Model model, HttpSession session) {
		UserDetails user = (UserDetails) session.getAttribute("user");
		
		if (user == null || user.getUserType()!=2) {
			session.setAttribute("pagAntAdmin", "/administrator/myRequests#tab1");
			return "redirect:/login";
		}
		
		model.addAttribute("requests", requestDao.getRequests());
		model.addAttribute("approvedRequests", requestDao.getApprovedRequests());
		model.addAttribute("rejectedRequests", requestDao.getRejectedRequests());

		return "administrator/myRequests";
	}
	
	@RequestMapping(value="/showCertificates/{mail}")
	public String viewCertificate(Model model, @PathVariable("mail") String mail, HttpSession session) {
		
		UserDetails user = (UserDetails) session.getAttribute("user");
		
		if (user == null || user.getUserType()!=2) {
			session.setAttribute("pagAntAdmin", "/administrator/showCertificates/"+mail);
			return "redirect:/login";
		}
		
		
		session.setAttribute("nextPageAdmin", "administrator/showCertificates/"+mail);
		model.addAttribute("certificates", certificateDao.getInstructorCertificates(mail));
		model.addAttribute("types", activityTypeDao.getActivityTypes());
		model.addAttribute("activityType", new ActivityType());
		return "administrator/showCertificates";
	}
	
	@RequestMapping(value="/acceptRequest/{id}")
	public String acceptRequest(@PathVariable("id") int requestId) {
		Request request = requestDao.getRequest(requestId);
		request.setStatus(Status.APPROVED);
		requestDao.updateRequest(request);
		List<Certificate> certificates = certificateDao.getInstructorCertificates(request.getMail());
		CanOrganize cO = new CanOrganize();
		for (Certificate c:certificates) {
			String mail = request.getMail();
			String activityType = c.getActivityType();
			if (!canOrganizeDao.isCanOrganize(mail, activityType)) {
				cO.setActivityTypeName(activityType);
				cO.setMail(mail);
				canOrganizeDao.addCanOrganize(cO);
			}
		}
		Instructor instructor = requestDao.convertToInstructor(request);
		instructorDao.addInstructor(instructor);
		
		String issue = "Request Approved";
		String text = "Your request has been approved. You are now part from our family. Welcome!!";
		String mail = request.getMail();
		
		messageDao.sendMessage(issue, text, mail);
		
		System.out.println(request.getMail()+": Your request has been approved.");
		return "redirect:/administrator/myRequests";
	}
	
	@RequestMapping("/rejectRequest/{id}")
	public String rejectRequest(@PathVariable("id") int requestId) {
		Request request = requestDao.getRequest(requestId);
		request.setStatus(Status.REJECTED);
		
		List<Certificate> certificates = certificateDao.getInstructorCertificates(request.getMail());
		
		for (Certificate c : certificates) {
			c.setStatus(Status.REJECTED);
			certificateDao.updateCertificate(c);
		}
		
		requestDao.updateRequest(request);
		System.out.println(request.getMail()+": Your request has been rejected.");
		return "redirect:/administrator/myRequests";
	}
	
	
	
	@RequestMapping("/certificateRequests")
	public String listCertificates(Model model, HttpSession session) {
		
		UserDetails user = (UserDetails) session.getAttribute("user");
		
		if (user == null || user.getUserType()!=2) {
			session.setAttribute("pagAntAdmin", "/administrator/certificateRequests");
			return "redirect:/login";
		}
		
		
		session.setAttribute("nextPageAdmin", "administrator/certificateRequests#tab1");
		model.addAttribute("certificates", certificateDao.getInstructorsCertificates());
		model.addAttribute("approvedCertificates", certificateDao.getApprovedCertificates());
		model.addAttribute("rejectedCertificates", certificateDao.getRejectedCertificates());
		model.addAttribute("activityType", new ActivityType());
		model.addAttribute("types", activityTypeDao.getActivityTypes());
		model.addAttribute("message", new Message());
		return "administrator/pendingCertificates";
	}
	
	@RequestMapping(value="/acceptCertificate/{id}")
	public String acceptCertificate(@PathVariable("id") String certificateId, HttpSession session, @ModelAttribute("activityType") ActivityType act, BindingResult bindingResult) {
		
		System.out.println(act.getName());
		
		if (act.getDescription()!=null) {
			NewActivityTypeValidator aV = new NewActivityTypeValidator();
			aV.validate(act, bindingResult);
			if (bindingResult.hasErrors()) {
				return (String) session.getAttribute("nextPageAdmin");
			}
			activityTypeDao.addActivityType(act);
		}else {
			ActivityTypeValidator aV = new ActivityTypeValidator();
			aV.validate(act, bindingResult);
			if (bindingResult.hasErrors()) {
				return (String) session.getAttribute("nextPageAdmin");
			}
		}
		Certificate certificate = certificateDao.getCertificate(Integer.parseInt(certificateId));

		if (instructorDao.isInstructor(certificate.getOwnerMail())) {
			CanOrganize co = new CanOrganize();
			if (!canOrganizeDao.isCanOrganize(certificate.getOwnerMail(), act.getName())) {
				co.setActivityTypeName(act.getName());
				co.setMail(certificate.getOwnerMail());
				canOrganizeDao.addCanOrganize(co);				
			}
		}
		
		
		certificate.setActivityType(act.getName());
		certificate.setStatus(Status.APPROVED);
		
		String mail =certificate.getOwnerMail();
		String issue ="Certificate Approved";
		String text="Your certificate has been approved. You can now create new activites.";
		
		certificateDao.updateCertificate(certificate);
		messageDao.sendMessage(issue, text, mail);
		return "redirect:/"+session.getAttribute("nextPageAdmin");
	}
	
	@RequestMapping("/rejectCertificate/{id}")
	public String rejectCertificate(@PathVariable("id") int certificateId, @ModelAttribute("message") Message message, HttpSession session) {
		Certificate certificate = certificateDao.getCertificate(certificateId);
		
		certificate.setStatus(Status.REJECTED);	
		
		message.setMailReceiver(certificate.getOwnerMail());
		message.setIssue("Certificate Rejected");
		message.setStatus(Status.NOTARCHIVED);
		
		certificateDao.updateCertificate(certificate);
		messageDao.addMessage(message);
		return "redirect:"+session.getAttribute("nextPageAdmin");
	}
	
	@RequestMapping("/rejectRequestCertificate/{id}")
	public String rejectRequestCertificate(@PathVariable("id") int certificateId, HttpSession session) {
		Certificate certificate = certificateDao.getCertificate(certificateId);

		certificate.setStatus(Status.REJECTED);	
		certificateDao.updateCertificate(certificate);
		return "redirect:"+session.getAttribute("nextPageAdmin");
	}
	
	@RequestMapping("/instructorList")
	public String instructorList(Model model, HttpSession session) {
		
		UserDetails user = (UserDetails) session.getAttribute("user");
		
		if (user == null || user.getUserType()!=2) {
			session.setAttribute("pagAntAdmin", "/administrator/instructorList");
			return "redirect:/login";
		}
		
		
		model.addAttribute("instructors", instructorDao.getInstructors());
		session.setAttribute("nextPage", "/administrator/instructorList");
		return "/administrator/instructorList";
	}
}

class NewActivityTypeValidator implements Validator{

	@Override
	public boolean supports(Class<?> clazz) {
		// TODO Auto-generated method stub
		return ActivityType.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		// TODO Auto-generated method stub
		ActivityType act = (ActivityType) target;
		
		if (act.getName().equals("")) {
			errors.rejectValue("name", "This field cannot be empty.");
		}
		
		if (act.getDescription()==null||act.getDescription().equals("")) {
			errors.rejectValue("description", "This field cannot be empty");
		}
	}
}

class ActivityTypeValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		// TODO Auto-generated method stub
		return ActivityType.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		// TODO Auto-generated method stub
		ActivityType act = (ActivityType) target;
		
		if (act.getName().equals("Select a type")) {
			errors.rejectValue("name", "You must select a type.");
		}
	}
}
