package es.uji.TooPots.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import es.uji.TooPots.dao.ActivityDao;
import es.uji.TooPots.dao.CustomerDao;
import es.uji.TooPots.model.Customer;

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
		this.activityDao = activityDao;
	}
	
	/*
	 * Una vez funcional incluir filtros:
	 * 		por nivel
	 * 		por tipo de actividad
	 * 		...
	 */
	@RequestMapping("/activities/{mail}")
	public String listActivities(Model model) {
		model.addAttribute("activities", activityDao.getActivities());
		return "/customer/activities";
	}
	
	@RequestMapping(value = "/signup")
	public String addCustomer(Model model) {
		model.addAttribute("customer", new Customer());
		return "customer/signup";
	}
	
	
	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	public String processAddSubmit(@ModelAttribute("Customer") Customer customer, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "customer/signup";
		}
		customerDao.addCustomer(customer);
		return "redirect:activities";
	}
	
	
}
