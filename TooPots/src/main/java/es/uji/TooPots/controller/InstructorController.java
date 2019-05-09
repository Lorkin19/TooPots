package es.uji.TooPots.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMethod;

import es.uji.TooPots.dao.InstructorDao;
import es.uji.TooPots.dao.ActivityDao;
import es.uji.TooPots.dao.ActivityTypeDao;
import es.uji.TooPots.model.Activity;

@Controller
@RequestMapping("/instructor")
public class InstructorController {
	
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
	
	private ActivityTypeDao activityTypeDao;
	
	@Autowired
	public void setActivityTypeDao(ActivityTypeDao activityTypeDao) {
		this.activityTypeDao = activityTypeDao;
	}
	
	/* 
	 * TODO Faltaria gestionar login para poder tener el mail del instructor
	 * TODO Preguntar a Malo sobre poner el mailInstructor en requestMapping y obtener el propio
	 * email para utilizarlo más adelante.
	 */
	@RequestMapping("/menu/{mailInstructor}")
	public String listInstructor(Model model, @PathVariable String mailInstructor) {
		model.addAttribute("activities", activityDao.getInstructorActivities(mailInstructor));
		return "/instructor/menu";
	}
	
	@RequestMapping(value = "/delete/{activity}")
    public String processDeleteActivity(@PathVariable int activityId) {
        activityDao.deleteActivity(activityId);
        return "redirect:../../";
    }
	
	
	@RequestMapping(value = "/add")
    public String addActivity(Model model) {
        model.addAttribute("activity", new Activity());
        model.addAttribute("type", activityTypeDao.getActivityTypes());
        return "instructor/add";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String processAddSubmit(@ModelAttribute("activity") Activity activity,
                                   BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "instructor/add";
        activityDao.addActivity(activity);
        return "redirect:menu";
    }
    
    
    @RequestMapping(value = "/update/{activityId}", method = RequestMethod.GET)
    public String editActivity(Model model, @PathVariable int activityId) {
        model.addAttribute("activity", activityDao.getActivity(activityId));
        return "instructor/update";
    }

    @RequestMapping(value="/update/{activityId}", method = RequestMethod.POST)
    public String processUpdateSubmit(@PathVariable String activityId,
                                      @ModelAttribute("activity") Activity activity,
                                      BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "instructor/update";
        activityDao.updateActivity(activity);
        return "redirect:../../menu";
    }
}
