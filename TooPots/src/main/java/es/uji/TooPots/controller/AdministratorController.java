package es.uji.TooPots.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import es.uji.TooPots.dao.ActivityTypeDao;
import es.uji.TooPots.dao.CertificateDao;
import es.uji.TooPots.dao.InstructorDao;
import es.uji.TooPots.dao.MessageDao;
import es.uji.TooPots.dao.RequestDao;
import es.uji.TooPots.model.ActivityType;
import es.uji.TooPots.model.Certificate;
import es.uji.TooPots.model.Instructor;
import es.uji.TooPots.model.Message;
import es.uji.TooPots.model.Request;
import es.uji.TooPots.model.Status;

@Controller
@RequestMapping("/administrator")
public class AdministratorController {
	
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
	public String listRequests(Model model) {
		model.addAttribute("requests", requestDao.getRequests());
		model.addAttribute("approvedRequests", requestDao.getApprovedRequests());
		model.addAttribute("rejectedRequests", requestDao.getRejectedRequests());
		model.addAttribute("idDelCertificado", 0);

		return "administrator/myRequests";
	}
	
	@RequestMapping("/myApprovedRequests")
	public String listApprovedRequests(Model model) {
		model.addAttribute("requests", requestDao.getApprovedRequests());
		return "administrator/myApprovedRequests";
	}
	
	@RequestMapping("/myRejectedRequests")
	public String listRejectedRequests(Model model) {
		model.addAttribute("requests", requestDao.getRejectedRequests());
		return "administrator/myRejectedRequests";
	}
	
	@RequestMapping("/showCertificates/{mail}")
	public String viewCertificate(Model model, @PathVariable("mail") String mail) {
		model.addAttribute("certificates", certificateDao.getInstructorCertificates(mail));
		return "administrator/showCertificates";
	}
	
	@RequestMapping("/acceptRequest/{id}")
	public String acceptRequest(@PathVariable("id") int requestId) {
		Request request = requestDao.getRequest(requestId);
		request.setStatus(Status.APPROVED);
		requestDao.updateRequest(request);
		
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
		requestDao.updateRequest(request);
		System.out.println(request.getMail()+": Your request has been rejected.");
		return "redirect:/administrator/myRequests";
	}
	
	
	
	@RequestMapping("/certificateRequests")
	public String listCertificates(Model model) {
		model.addAttribute("certificates", certificateDao.getCertificates());
		model.addAttribute("approvedCertificates", certificateDao.getApprovedCertificates());
		model.addAttribute("rejectedCertificates", certificateDao.getRejectedCertificates());
		model.addAttribute("message", new Message());
		return "administrator/pendingCertificates";
	}
	
	@RequestMapping("/approvedCertificateRequests")
	public String listApprovedCertificates(Model model) {
		model.addAttribute("certificates", certificateDao.getApprovedCertificates());
		return "administrator/approvedCertificateRequests";
	}
	
	@RequestMapping("/rejectedCertificateRequests")
	public String listRejectedCertificateRequests(Model model) {
		model.addAttribute("certificates", certificateDao.getRejectedCertificates());
		return "administrator/rejectedCertificateRequests";
	}
	
	@RequestMapping("/newTypeCertificates")
	public String listNewTypeCertificates(Model model) {
		model.addAttribute("certificates", certificateDao.getNewTypeCertificates());
		return "administrator/newTypeCertificates";
	}
	
	@RequestMapping("/acceptCertificate/{id}")
	public String acceptCertificate(@PathVariable("id") int certificateId) {
		Certificate certificate = certificateDao.getCertificate(certificateId);
		
		certificate.setStatus(Status.APPROVED);
		
		String mail =certificate.getOwnerMail();
		String issue ="Certificate Approved";
		String text="Your certificate has been approved. You can now create new activites.";
		
		certificateDao.updateCertificate(certificate);
		messageDao.sendMessage(issue, text, mail);
		return "redirect:/administrator/certificateRequests";
	}
	
	@RequestMapping("/rejectCertificate/{id}")
	public String rejectCertificate(@PathVariable("id") int certificateId, @ModelAttribute("message") Message message) {
		Certificate certificate = certificateDao.getCertificate(certificateId);
		
		certificate.setStatus(Status.REJECTED);	
		
		message.setMailReceiver(certificate.getOwnerMail());
		message.setIssue("Certificate Rejected");
		message.setStatus(Status.NOTARCHIVED);
		
		certificateDao.updateCertificate(certificate);
		messageDao.addMessage(message);
		return "redirect:/administrator/certificateRequests";
	}
	
	@RequestMapping("/manageType/{id}")
	public String manageType(Model model, @PathVariable("id") int certificateId) {
		model.addAttribute("message", new Message());
		model.addAttribute("certificate", certificateDao.getCertificate(certificateId));
		model.addAttribute("activityType", new ActivityType());
		
		return "administrator/manageType"; 
	}
	
	@RequestMapping(value="/manageType/{id}", method=RequestMethod.POST)
	public String processManageType(@PathVariable("id") int certificateId, @ModelAttribute("activityType") ActivityType activityType) {
		
		Certificate certificate = certificateDao.getCertificate(certificateId);
		
		certificate.setStatus(Status.APPROVED);
		certificate.setActivityType(activityType.getName());
		
		String mail =certificate.getOwnerMail();
		String issue ="Certificate Approved";
		String text="Your certificate has been approved. You can now create new activites.";
		
		activityTypeDao.addActivityType(activityType);
		certificateDao.updateCertificate(certificate);
		messageDao.sendMessage(issue, text, mail);
		
		return "redirect:/administrator/certificateRequests";
	}
	
	@RequestMapping("/updateCertificate")
	public String updateCertificate(@ModelAttribute("certificate") Certificate certificate) {
		certificateDao.updateCertificate(certificate);
		return "redirect:/administrator/newTypeCertificates";
	}
}
