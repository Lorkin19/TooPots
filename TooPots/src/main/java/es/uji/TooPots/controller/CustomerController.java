package es.uji.TooPots.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import es.uji.TooPots.dao.ActivityDao;
import es.uji.TooPots.dao.CustomerDao;
import es.uji.TooPots.dao.MessageDao;
import es.uji.TooPots.dao.ReservationDao;
import es.uji.TooPots.model.Activity;
import es.uji.TooPots.model.Customer;
import es.uji.TooPots.model.Message;
import es.uji.TooPots.model.Reservation;
import es.uji.TooPots.model.UserDetails;

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
	
	private ReservationDao reservationDao;
	
	@Autowired
	public void setReservationDao(ReservationDao reservationDao) {
		this.reservationDao = reservationDao;
	}
	
	private MessageDao messageDao;
	
	@Autowired
	public void setMessageDao(MessageDao messageDao) {
		this.messageDao = messageDao;
	}
	
	/*
	 * Una vez funcional incluir filtros:
	 * 		por nivel
	 * 		por tipo de actividad
	 * 		...
	 */
	@RequestMapping("/activities")
	public String listActivities(Model model) {
		model.addAttribute("activities", activityDao.getActivities());
		return "customer/activities";
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
	
	@RequestMapping(value="/bookActivity/{id}")
	public String enroll(Model model, @PathVariable("id") int activityId, HttpSession session) {

		UserDetails user = (UserDetails) session.getAttribute("user");
		if (user == null || user.getUserType()!=0) {
			session.setAttribute("pagAnt", "/customer/bookActivity/"+activityId);
			return "redirect:/login";
		}
		
		model.addAttribute("activity", activityDao.getActivity(activityId));
		model.addAttribute("reservation", new Reservation());
		return "customer/bookActivity";
	}
	
	@RequestMapping(value="/bookActivity/{id}", method=RequestMethod.POST)
	public String processEnrollSubmit(@PathVariable("id") int activityId, @ModelAttribute("reservation") Reservation reservation, HttpSession session,
		BindingResult bindingResult) {
		
		Activity activity = activityDao.getActivity(activityId);
		
		UserDetails user = (UserDetails) session.getAttribute("user");
		reservation.setMail(user.getMail());
		reservation.setPlace(activity.getLocation());
		reservation.setActivityId(activity.getActivityId());
				
		//REVISAR
		if (reservation.getVacancies() > activity.getVacancies()) {
			bindingResult.rejectValue("vacancies", "badVacancies", "Reservation vacancies are greater than available vacancies.");
			return "customer/bookActivity";
		}
		
		Message message = new Message();
		message.setMailReceiver(user.getMail());
		message.setIssue("Activity Vacancies Reservation");
		message.setText("You have successfully enrolled in the activity with code " + activityId + " and name "+activity.getName()+".\n You have booked " + reservation.getVacancies()
		+ ". Hope you enjoy.");
		message.setStatus("Not archived");
		
		//cambiar para que se haga directamente en la vista
		reservation.setPrice(activity.getPrice()*reservation.getVacancies());
		
		activity.setVacancies(activity.getVacancies()-reservation.getVacancies());
		
		
		reservationDao.addReservation(reservation);
		messageDao.addMessage(message);
		activityDao.updateActivity(activity);
		return "redirect:../myReservations";
	}

	@RequestMapping("/myReservations")
	public String listMyReservations(Model model, HttpSession session) {
		UserDetails user = (UserDetails) session.getAttribute("user");
		session.setAttribute("pagAnt", "/customer/myReservations");
		if (user == null || user.getUserType()!=0) {
			return "redirect:../login";
		}
		model.addAttribute("reservations", reservationDao.getCustomerReservations(user.getMail()));
		return "customer/myReservations";
	}
	
	@RequestMapping("/activityInfo/{id}")
	public String activityInformation(Model model, @PathVariable("id") int activityId) {
		model.addAttribute("activity", activityDao.getActivity(activityId));
		return "customer/activityInfo";
	}
	

}
