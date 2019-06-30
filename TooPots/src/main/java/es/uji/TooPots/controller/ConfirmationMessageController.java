package es.uji.TooPots.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import es.uji.TooPots.dao.ActivityDao;
import es.uji.TooPots.dao.CertificateDao;
import es.uji.TooPots.dao.CustomerDao;
import es.uji.TooPots.dao.ImageDao;
import es.uji.TooPots.dao.InstructorDao;
import es.uji.TooPots.dao.ReservationDao;
import es.uji.TooPots.model.Activity;
import es.uji.TooPots.model.Instructor;
import es.uji.TooPots.model.Reservation;
import es.uji.TooPots.model.UserDetails;

@Controller
public class ConfirmationMessageController {

	private ReservationDao reservationDao;
	
	@Autowired
	public void setReservationDao(ReservationDao reservationDao) {
		this.reservationDao = reservationDao;
	}
	
	private CustomerDao customerDao;
	
	@Autowired
	public void setCustomerDao(CustomerDao customerDao) {
		this.customerDao = customerDao;
	}
	
	private CertificateDao certificateDao;

	@Autowired
	public void setCertificateDao(CertificateDao certificateDao) {
		this.certificateDao = certificateDao;
	}
	
	private ImageDao imageDao;
	
	@Autowired
	public void setImageDao(ImageDao imageDao) {
		this.imageDao= imageDao;
	}

	private InstructorDao instructorDao;
	
	@Autowired
	public void setInstructorDao(InstructorDao instructorDao) {
        this.instructorDao = instructorDao;
    }
	
	private ActivityDao activityDao;

	@Autowired
	public void setActivityDao(ActivityDao activityDao) {
		this.activityDao=activityDao;
	}
	
	@RequestMapping("/confirmationMessage/{mail}")
	public String confirmationMessage(@PathVariable("mail") String mail, Model model, HttpSession session) {
		
		UserDetails user = (UserDetails) session.getAttribute("user");
		if (user == null) {
			session.setAttribute("pagAnt", "/message/list");
			return "redirect:/login";
		}
		model.addAttribute("user", user);
		model.addAttribute("mail", mail);
		model.addAttribute("nextPage", session.getAttribute("nextPage"));
		model.addAttribute("return", session.getAttribute("returnUsers"));
		return "/confirmationMessage";
	}
	
	@RequestMapping("/deleteAccount/{mail}")
	public String deleteAccount(@PathVariable("mail") String mail, @ModelAttribute("user") UserDetails user, RedirectAttributes redirectAttributes, HttpSession session) {
		String ret;
		if (user.getUserType() == 0) {
			List<Reservation> lR = reservationDao.checkCustomerReservations(mail);
			if (lR.isEmpty()) {
				customerDao.deleteCustomer(customerDao.getCustomer(mail));
				ret = "redirect:/";
			}else {
				redirectAttributes.addFlashAttribute("message", "The customer with mail " + mail + " cannot be deleted because he still has activities to be done.");
				ret = "redirect:/confirmationMessage";
			}
		}else{
	    	Instructor instructor = instructorDao.getInstructor(mail);
	    	List<Activity> lA = activityDao.getInstructorActivities(mail);
	    	
	    	if (lA.isEmpty()) {
	    		instructorDao.deleteInstructor(instructor);
	    	}else {
		    	redirectAttributes.addFlashAttribute("message", "Instructor with mail " + mail + " cannot be deleted because he still has activities." );
		    	instructorDao.deleteInstructor(instructor);
		    	certificateDao.deleteInstructorCertificates(mail);
		    	imageDao.deleteInstructorImages(mail);
		    }
	    	if (user.getUserType() == 1) {
    			ret="redirect:/";
    		}else {
    			ret="redirect:"+session.getAttribute("nextPage");
    		}
		}
		
		return ret;
	}
	
}
