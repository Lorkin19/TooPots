package es.uji.TooPots.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMethod;

import es.uji.TooPots.dao.InstructorDao;
import es.uji.TooPots.dao.MessageDao;
import es.uji.TooPots.dao.ReceiveInformationDao;
import es.uji.TooPots.dao.RequestDao;
import es.uji.TooPots.dao.ActivityDao;
import es.uji.TooPots.dao.ActivityTypeDao;
import es.uji.TooPots.dao.CanOrganizeDao;
import es.uji.TooPots.model.Activity;
import es.uji.TooPots.model.ActivityType;
import es.uji.TooPots.model.Request;
import es.uji.TooPots.model.UserDetails;

@Controller
@RequestMapping("/instructor")
public class InstructorController {
	
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
	
	@RequestMapping("/menu")
	public String listInstructor(Model model, HttpSession session) {
		UserDetails user = (UserDetails) session.getAttribute("user");
		if (user == null || user.getUserType()!=1) {
			session.setAttribute("pagAnt", "/instructor/menu");
			return "redirect:/login";
		}
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
        model.addAttribute("activity", act);
        model.addAttribute("type", canOrganizeDao.getInstructorCanOrganize(user.getMail()));
        model.addAttribute("session", session);
        
        return "instructor/add";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("activity") Activity activity, @ModelAttribute("type")  ActivityType type,
                                   BindingResult bindingResult, HttpSession session) {
    	
    	UserDetails user = (UserDetails) session.getAttribute("user");
		activity.setMailInstructor(user.getMail());
		ActivityValidator actVal = new ActivityValidator();
		actVal.validate(activity, bindingResult);
		
		if (bindingResult.hasErrors()) {
        	return "instructor/add";
        }
        if (!activity.getTime().matches("\\d{2}:\\d{2}")) {
        	
        }
        activityDao.addActivity(activity);
        
        String activityType = activity.getActivityType();
        
        List<String> customers = receiveInformationDao.getCustomersForActivityType(activityType);
        
        String issue;
        String text;
                
        for (String mail:customers) {
        	issue = "New Activity.";
        	text="A new activity of type " + activityType + " has been created. Check it out!";
        	
        	messageDao.sendMessage(issue, text, mail);        	
        }
        
        issue="Activity Created";
		text ="A new activity has been created.\n"
				+ "Activity Name: ";
		String mail = user.getMail();
		
		messageDao.sendMessage(issue, text, mail);
        
        return "redirect:menu";
    }
    
    
    @RequestMapping(value = "/update/{id}", method = RequestMethod.GET)
    public String editActivity(Model model, @PathVariable int id, HttpSession session) {
        UserDetails user = (UserDetails) session.getAttribute("user");
    	
        if (user == null || user.getUserType()!=1) {
        	session.setAttribute("pagAnt", "/instructor/update/"+id);
			return "redirect:/login";
        }
    	
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
    								BindingResult bindingResult, HttpSession session) {
    	if (bindingResult.hasErrors()) {
    		return "instructor/signup";
    	}
    	
    	requestDao.addRequest(request);
    	return "redirect:/instructor/wait";
    }
    /**
     * TODO - Jaime: mira esto, simplemente redirige a una página para que haga algo mientras se está
     * procesando la petición del instructor
     * @param model
     * @return to the wait page.
     */ 
    @RequestMapping("/wait")
    public String waitForAccept(Model model) {
    	
    	return "instructor/wait";
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
	}
	
}