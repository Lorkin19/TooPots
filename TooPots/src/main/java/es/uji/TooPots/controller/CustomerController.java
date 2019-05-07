package es.uji.TooPots.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import es.uji.TooPots.dao.ActivityDao;
import es.uji.TooPots.dao.CustomerDao;

@Controller
@RequestMapping("/customer")
public class CustomerController {
	
	private CustomerDao customerDao;
	
	@Autowired
	public void setCustomerDao(CustomerDao customerDao) {
		this.customerDao = customerDao;
	}
	
	private ActivityDao activityDao;

	@Autowired
	public void setActivityDao(ActivityDao activityDao) {
		this.activityDao=activityDao;
	}
	
	@RequestMapping("/activities")
	public String listActivities(Model model) {
		model.addAttribute("activities", activityDao.getActivities());
		return "/customer/activities";
	}
}
