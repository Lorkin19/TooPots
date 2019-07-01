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


import es.uji.TooPots.dao.InstructorDao;
import es.uji.TooPots.dao.MessageDao;
import es.uji.TooPots.dao.ReceiveInformationDao;
import es.uji.TooPots.dao.RequestDao;
import es.uji.TooPots.dao.ActivityDao;
import es.uji.TooPots.dao.ActivityTypeDao;
import es.uji.TooPots.dao.CanOrganizeDao;
import es.uji.TooPots.dao.CertificateDao;
import es.uji.TooPots.dao.ImageDao;
import es.uji.TooPots.model.Activity;
import es.uji.TooPots.model.ActivityType;
import es.uji.TooPots.model.CanOrganize;
import es.uji.TooPots.model.Certificate;
import es.uji.TooPots.model.Customer;
import es.uji.TooPots.model.Instructor;
import es.uji.TooPots.model.ReceiveInformation;
import es.uji.TooPots.model.Request;
import es.uji.TooPots.model.UserDetails;

@Controller
@RequestMapping("/instructor")
public class InstructorController {
	
	@Value("${upload.file.directory}")
	private String uploadDirectory;
	
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
	
	private CanOrganizeDao canOrganizeDao;
	
	@Autowired
	public void setCanOrganizeDao(CanOrganizeDao canOrganizeDao) {
		this.canOrganizeDao = canOrganizeDao;
	}
	
	private ActivityDao activityDao;

	@Autowired
	public void setActivityDao(ActivityDao activityDao) {
		this.activityDao=activityDao;
	}
	
	private ActivityTypeDao activityTypeDao;
	
	@Autowired
	public void setActivityTypeDao(ActivityTypeDao activityTypeDao) {
		this.activityTypeDao = activityTypeDao;
	}
	
	private RequestDao requestDao;
	
	@Autowired
	public void setRequestDao(RequestDao requestDao) {
		this.requestDao = requestDao;
	}
	
	private ReceiveInformationDao receiveInformationDao;
	
	@Autowired
	public void setReceiveInformationDao(ReceiveInformationDao receiveInformationDao) {
		this.receiveInformationDao = receiveInformationDao;
	}
	
	private MessageDao messageDao;
	
	@Autowired
	public void setMessageDao(MessageDao messageDao) {
		this.messageDao = messageDao;
	}
	
	@RequestMapping("/editAccount/{mail}")
	public String editAccount(Model model, HttpSession session, @PathVariable("mail") String mail) {
		UserDetails user = (UserDetails) session.getAttribute("user");
		if (user == null || user.getUserType()!=1) {
			session.setAttribute("pagAnt", "/instructor/editAccount/"+mail);
			return "redirect:/login";
		}	
		model.addAttribute("user", user);
		session.setAttribute("nextPage", "/");
		session.setAttribute("returnUsers", "/instructor/editAccount/"+mail);
		model.addAttribute("instructor", instructorDao.getInstructor(mail));
		return "instructor/editAccount";
	}
	
	@RequestMapping(value="/editAccount/{mail}", method=RequestMethod.POST)
	public String proccessEditAccount(@PathVariable("mail") String mail, @ModelAttribute("instructor") Instructor instructor, BindingResult bindingResult) {
		InstructorValidator iV = new InstructorValidator();
		iV.validate(instructor, bindingResult);
		
		if (bindingResult.hasErrors()) {
			return "redirect:/instructor/editAccount/"+mail;
		}
		
		instructorDao.updateInstructor(instructor);




		return "redirect:/instructor/menu";
	}
	
	
	
	@RequestMapping("/menu")
	public String listInstructor(Model model, HttpSession session) {
		UserDetails user = (UserDetails) session.getAttribute("user");
		if (user == null || user.getUserType()!=1) {
			session.setAttribute("pagAnt", "/instructor/menu");
			return "redirect:/login";
		}	
		model.addAttribute("user", user);
        model.addAttribute("type", canOrganizeDao.getInstructorCanOrganize(user.getMail()).size());
		model.addAttribute("activities", activityDao.getInstructorActivities(user.getMail()));
		model.addAttribute("session", session);
		return "instructor/menu";
	}
	
	@RequestMapping(value = "/delete/{id}")
    public String processDeleteActivity(@PathVariable int id) {
        activityDao.deleteActivity(id);
        return "redirect:../menu";
    }
	
	
	@RequestMapping(value = "/add")
    public String addActivity(Model model, HttpSession session) {
		
		UserDetails user = (UserDetails) session.getAttribute("user");
		
		if (user == null || user.getUserType() != 1) {
			session.setAttribute("pagAnt", "/instructor/add");
			return "redirect:/login";
		}
		
		Activity act = new Activity();
		model.addAttribute("user", user);

        model.addAttribute("activity", act);
        model.addAttribute("type", canOrganizeDao.getInstructorCanOrganize(user.getMail()));
        model.addAttribute("session", session);
        
        return "instructor/add";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String processAddSubmit(@RequestParam("file") MultipartFile[] files, @ModelAttribute("activity") Activity activity, 
                                   BindingResult bindingResult, HttpSession session, RedirectAttributes redirectAttributes) {
    	
    	UserDetails user = (UserDetails) session.getAttribute("user");
		activity.setMailInstructor(user.getMail());
		ActivityValidator actVal = new ActivityValidator();
		actVal.validate(activity, bindingResult);
		
		if (bindingResult.hasErrors()) {
        	return "instructor/add";
        }

		String message = imageDao.uploadImage(files, user, uploadDirectory, activityDao.getActivityId(), bindingResult);
		if (!message.equals("Success")) {
			session.setAttribute("nextPage", "/instructor/add");
			redirectAttributes.addFlashAttribute("message", message);
			return "redirect:/uploadStatus";
		}
        activityDao.addActivity(activity);
        
        String activityType = activity.getActivityType();
        
        
        String issue;
        String text;
        
        for (ReceiveInformation customer:receiveInformationDao.getCustomersForActivityType(activityType)) {
        	issue = "New Activity.";
        	text="A new activity of type " + activityType + " has been created. Check it out!";
        	
        	messageDao.sendMessage(issue, text, customer.getMail());        	
        }
        
        issue="Activity Created";
		text ="A new activity has been created.\n"
				+ "Activity Name: ";
		String mail = user.getMail();
		
		messageDao.sendMessage(issue, text, mail);
		session.setAttribute("nextPage", "/instructor/menu");
        return "redirect:/uploadStatus";
    }
    
    
    @RequestMapping(value = "/update/{id}", method = RequestMethod.GET)
    public String editActivity(Model model, @PathVariable int id, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
    	
        if (user == null || user.getUserType()!=1) {
        	session.setAttribute("pagAnt", "/instructor/update/"+id);
			return "redirect:/login";
        }
		model.addAttribute("user", user);

        model.addAttribute("session", session);
    	model.addAttribute("activity", activityDao.getActivity(id));
        return "instructor/update";
    }

    @RequestMapping(value="/update/{id}", method = RequestMethod.POST)
    public String processUpdateSubmit(@PathVariable int id,
                                      @ModelAttribute("activity") Activity activity,
                                      BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "instructor/update";
        activityDao.updateActivity(activity);
        return "redirect:../../menu";
    }
    
    @RequestMapping(value="signup")
    public String addInstructor(Model model) {
    	model.addAttribute("instructor", new Request());
    	return "instructor/signup";
    }
    
    @RequestMapping(value="/signup", method=RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("instructor") Request request,
    								BindingResult bindingResult, HttpSession session, @RequestParam("file") MultipartFile[] files, RedirectAttributes redirectAttributes) {
    	
    	SignupValidator sV = new SignupValidator();
    	
    	sV.validate(request, bindingResult);
    	
    	if (bindingResult.hasErrors()) {
    		return "instructor/signup";
    	}
    	String message = certificateDao.uploadCertificate(files, request.getMail(), uploadDirectory);
    	if (!message.equals("Success")) {
			session.setAttribute("nextPage", "/instructor/signup");
			redirectAttributes.addFlashAttribute("message", message);
			return "redirect:/uploadStatus";
		}
    	
    	redirectAttributes.addAttribute("message", message);
    	
    	session.setAttribute("nextPage", "/instructor/wait");
    	
    	requestDao.addRequest(request);
    	return "redirect:/instructor/wait";
    }
    
    @RequestMapping("/deleteCertificate/{id}")
    public String deleteCertificate(@PathVariable("id") int certificateId, RedirectAttributes redirectAttributes, HttpSession session) {
    	Certificate certificate = certificateDao.getCertificate(certificateId);
    	Path path = Paths.get(uploadDirectory + certificate.getRoute());
    	try {
        	certificateDao.deleteCertificate(certificateId);
			Files.deleteIfExists(path);
			messageDao.sendMessage("Certificate Delete", "The certificate " + certificate.getFileName() + " has been deleted successfully.", certificate.getOwnerMail());
			return "redirect:/instructor/certificates#tab1";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			redirectAttributes.addFlashAttribute("message", "A fatal error has occured.");
			session.setAttribute("nextPage", "/instructor/certificates#tab1");
		}
    	return "redirect:/uploadStatus";
    }
    
	@RequestMapping(value="/certificates")
	public String listCertificates(Model model, HttpSession session) {
		UserDetails user = (UserDetails) session.getAttribute("user");
		if (user == null || user.getUserType()!=1) {
			session.setAttribute("pagAnt", "/instructor/certificates");
			return "redirect:/login";
		}
		model.addAttribute("user", user);

		model.addAttribute("certificates", certificateDao.getInstructorCertificates(user.getMail()));
		model.addAttribute("approvedCertificates", certificateDao.getInstructorApprovedCertificates(user.getMail()));
		model.addAttribute("rejectedCertificates", certificateDao.getInstructorRejectedCertificates(user.getMail()));
		model.addAttribute("session", session);
		return "/instructor/certificates";
	}

    
    @RequestMapping("/wait")
    public String waitForAccept(Model model) {
    	
    	return "instructor/wait";
    }
    
    @RequestMapping("/deleteInstructor/{mail}")
    public String deleteInstructor(@PathVariable("mail") String instructorMail, HttpSession session, RedirectAttributes redirectAttributes) {
    	Instructor instructor = instructorDao.getInstructor(instructorMail);
    	List<Activity> lA = activityDao.getInstructorActivities(instructorMail);
    	
    	if (lA.isEmpty()) {
    		instructorDao.deleteInstructor(instructor);
    		return"redirect:"+session.getAttribute("nextPage");
    	}
    	redirectAttributes.addFlashAttribute("message", "Instructor with mail " + instructorMail + " cannot be deleted because he still has activities." );
    	instructorDao.deleteInstructor(instructor);
    	certificateDao.deleteInstructorCertificates(instructorMail);
    	imageDao.deleteInstructorImages(instructorMail);
    	return "redirect:"+session.getAttribute("nextPage");
    }
    
}

class ActivityValidator implements Validator{

	@Override
	public boolean supports(Class<?> clazz) {
		// TODO Auto-generated method stub
		return Activity.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		// TODO Auto-generated method stub
		Activity act = (Activity) target;
		if (!act.getTime().matches("\\d{2}:\\d{2}")) {
			errors.rejectValue("time", "Format", "The time has to be in a specific format: 'hh:mm'");
		}
		String[] time = act.getTime().split(":");
		try {
			int hour = Integer.parseInt(time[0]);
			int minutes = Integer.parseInt(time[1]);
			if (hour < 0 || hour > 23) {
				errors.rejectValue("time", "Format", "Time must be between 00:00 and 23:59");
			}
			if (minutes<0 || minutes>59) {
				errors.rejectValue("time", "Format", "Time must be between 00:00 and 23:59");
			}
		}catch(Exception e) {
			errors.rejectValue("time", "Format", "Time must be between 00:00 and 23:59");
		}
		
		if (act.getActivityType().equals("Select a type")) {
			errors.rejectValue("activityType", "selectType", "You must select a type");
		}
		
		if (act.getLevel().equals("Select a level")) {
			errors.rejectValue("level", "selectLevel", "You must select a level");
		}
		
		if (act.getLocation().equals("")) {
			errors.rejectValue("location", "EmptyField", "This field cannot be empty.");
		}
		
		if (act.getLocation().length()>50) {
			errors.rejectValue("location", "Value too long", "This field has a limit of 50 characters.");
		}
		
		if (act.getDuration().equals("")) {
			errors.rejectValue("duration", "EmptyField", "This field cannot be empty.");
		}
		
		if (act.getDuration().length()>20) {
			errors.rejectValue("duration", "Value too long", "This field has a limit of 20 characters.");
		}
		
		if (act.getDescription().equals("")) {
			errors.rejectValue("description", "EmptyField", "This field cannot be empty.");
		}
		
		if (act.getDescription().length()>500) {
			errors.rejectValue("description", "Value too long", "This field has a limit of 500 characters.");
		}
		
		if (act.getMailInstructor().equals("")) {
			errors.rejectValue("mailInstructor", "EmptyField", "This field cannot be empty.");
		}
		
		if (act.getMailInstructor().length()>500) {
			errors.rejectValue("mailInstructor", "Value too long", "This field has a limit of 500 characters.");
		}
		
		if (act.getDate().compareTo(LocalDate.now())<0) {
			errors.rejectValue("date", "Format", "Dates must be from now on");
		}
	}
}

class SignupValidator implements Validator{
	@Override
	public boolean supports(Class<?> clazz) {
		// TODO Auto-generated method stub
		return Request.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		// TODO Auto-generated method stub
		Request request = (Request) target;
		
		if (request.getMail().length() > 40) {
			errors.rejectValue("mail", "Value too long", "This field has a limit of 40 characters.");
		}
		
		if (request.getMail().equals("")) {
			errors.rejectValue("mail", "EmptyField", "This field cannot be empty.");
		}
		
		if (request.getName().length() > 20) {
			errors.rejectValue("name", "Value too long", "This field has a limit of 20 characters.");
		}
		
		if (request.getName().equals("")) {
			errors.rejectValue("name", "EmptyField", "This field cannot be empty.");
		}
		
		if (request.getSurname().equals("")) {
			errors.rejectValue("surname", "EmptyField", "This field cannot be empty.");
		}
		
		if (request.getSurname().length() > 20) {
			errors.rejectValue("surname","Value too long", "This field has a limit of 20 characters.");
		}
		
		if (request.getPwd().equals("")) {
			errors.rejectValue("pwd", "EmptyField", "This field cannot be empty.");
		}
		
		if (request.getPwd().length() > 20) {
			errors.rejectValue("pwd","Value too long", "This field has a limit of 20 characters.");
		}
		
		if (request.getBankAccount().equals("")) {
			errors.rejectValue("bankAccount", "EmptyField", "This field cannot be empty.");
		}
		
		if (request.getBankAccount().length() > 12) {
			errors.rejectValue("bankAccount","Value too long", "This field has a limit of 121h characters.");
		}
	}
}

class InstructorValidator implements Validator{
	@Override
	public boolean supports(Class<?> clazz) {
		// TODO Auto-generated method stub
		return Instructor.class.equals(clazz); 
	}

	@Override
	public void validate(Object target, Errors errors) {
		// TODO Auto-generated method stub
		Instructor instructor = (Instructor) target;
		
		if (instructor.getMail().length() > 40) {
			errors.rejectValue("mail", "Value too long", "This field has a limit of 40 characters.");
		}
		if (instructor.getMail() == null) {
			errors.rejectValue("mail", "Value null", "This field cannot be empty.");
		}
		
		if (instructor.getMail().equals("")) {
			errors.rejectValue("mail", "EmptyField", "This field cannot be empty.");
		}
		
		if (instructor.getName().length() > 20) {
			errors.rejectValue("name", "Value too long", "This field has a limit of 20 characters.");
		}
		if (instructor.getName() == null) {
			errors.rejectValue("mail", "Value null", "This field cannot be empty.");
		}
		
		if (instructor.getName().equals("")) {
			errors.rejectValue("name", "EmptyField", "This field cannot be empty.");
		}
		
		if (instructor.getSurname().equals("")) {
			errors.rejectValue("surname", "EmptyField", "This field cannot be empty.");
		}
		if (instructor.getSurname() == null) {
			errors.rejectValue("mail", "Value null", "This field cannot be empty.");
		}
		
		if (instructor.getSurname().length() > 20) {
			errors.rejectValue("surname","Value too long", "This field has a limit of 20 characters.");
		}
		
		if (instructor.getPwd().equals("")) {
			errors.rejectValue("pwd", "EmptyField", "This field cannot be empty.");
		}
		if (instructor.getPwd() == null) {
			errors.rejectValue("mail", "Value null", "This field cannot be empty.");
		}
		
		if (instructor.getPwd().length() > 20) {
			errors.rejectValue("pwd","Value too long", "This field has a limit of 20 characters.");
		}
		
		if (instructor.getBankAccount().equals("")) {
			errors.rejectValue("bankAccount", "EmptyField", "This field cannot be empty.");
		}
		if (instructor.getBankAccount() == null) {
			errors.rejectValue("mail", "Value null", "This field cannot be empty.");
		}
		
		if (instructor.getBankAccount().length() > 12) {
			errors.rejectValue("pwd","Value too long", "This field has a limit of 20 characters.");
		}
	}
}