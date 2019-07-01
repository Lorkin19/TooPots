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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import es.uji.TooPots.dao.ActivityDao;
import es.uji.TooPots.dao.ActivityTypeDao;
import es.uji.TooPots.dao.CustomerDao;
import es.uji.TooPots.dao.ImageDao;
import es.uji.TooPots.dao.MessageDao;
import es.uji.TooPots.dao.ReceiveInformationDao;
import es.uji.TooPots.dao.ReservationDao;
import es.uji.TooPots.dao.UserDao;
import es.uji.TooPots.model.Activity;
import es.uji.TooPots.model.Customer;
import es.uji.TooPots.model.Image;
import es.uji.TooPots.model.Instructor;
import es.uji.TooPots.model.Message;
import es.uji.TooPots.model.ReceiveInformation;
import es.uji.TooPots.model.Reservation;
import es.uji.TooPots.model.Status;
import es.uji.TooPots.model.UserDetails;

@Controller
@RequestMapping("/customer")
public class CustomerController {
	
	private UserDao userDao;
	
	@Autowired
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	
	private ImageDao imageDao;
	
	@Autowired
	public void setImageDao(ImageDao imageDao) {
		this.imageDao = imageDao;
	}
	
	private ActivityTypeDao activityTypeDao;
	
	@Autowired
	public void setActivityTypeDao(ActivityTypeDao activityTypeDao) {
		this.activityTypeDao=activityTypeDao;
	}
	
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
	
	private ReceiveInformationDao receiveInformationDao;
	
	@Autowired
	public void setReceiveInformationDao(ReceiveInformationDao receiveInformationDao) {
		this.receiveInformationDao = receiveInformationDao;
	}
	
	/*
	 * Una vez funcional incluir filtros:
	 * 		por nivel
	 * 		por tipo de actividad
	 * 		...
	 */
	@RequestMapping("/activities")
	public String listActivities(Model model, HttpSession session) {

		UserDetails user = (UserDetails) session.getAttribute("user");
		
		model.addAttribute("user", session.getAttribute("user"));
		model.addAttribute("activities", activityDao.getActivities());
		
		session.setAttribute("nextPageCustomer", "/customer/activities");

		return "customer/activities";
	}
	 
	@RequestMapping("/activityTypes")
	public String listActivityTypes(Model model, HttpSession session) {

		UserDetails user = (UserDetails) session.getAttribute("user");
		
		model.addAttribute("user", user);
		model.addAttribute("activityTypes", activityTypeDao.getActivityTypes());
		return "customer/activityTypes";
	}
	
	@RequestMapping("/activitiesOfType/{activityType}")
	public String listActivitiesOfType(Model model, @PathVariable("activityType") String activityTypeName, HttpSession session) {
		UserDetails user = (UserDetails) session.getAttribute("user");

		session.setAttribute("pagAnt", "/customer/activitiesOfType/"+activityTypeName);

		model.addAttribute("user", user);
		model.addAttribute("activities", activityDao.getActivitiesOfType(activityTypeName));
		model.addAttribute("type", activityTypeName);
		session.setAttribute("nextPageCustomer", "/customer/activitiesOfType/"+activityTypeName);

		return "customer/activitiesOfType";
	}
	
	@RequestMapping(value = "/signup")
	public String addCustomer(Model model) {
		model.addAttribute("customer", new Customer());
		return "customer/signup";
	}
	
	
	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	public String processAddSubmit(@ModelAttribute("Customer") Customer customer, BindingResult bindingResult, HttpSession session) {
		CustomerSignupValidator cSV = new CustomerSignupValidator();
		
		cSV.validate(customer, bindingResult);
		
		if (bindingResult.hasErrors()) {
			return "customer/signup";
		}
		customerDao.addCustomer(customer);
		UserDetails user = userDao.loadUserByMail(customer.getMail(), customer.getPwd());
		session.setAttribute("user", user);
		return "redirect:activities";
	}
	
	@RequestMapping(value="/bookActivity/{id}")
	public String enroll(Model model, @PathVariable("id") int activityId, HttpSession session) {

		UserDetails user = (UserDetails) session.getAttribute("user");
		if (user == null || user.getUserType()!=0) {
			session.setAttribute("pagAnt", "/customer/bookActivity/"+activityId);
			return "redirect:/login";
		}
		model.addAttribute("user", user);
		model.addAttribute("activity", activityDao.getActivity(activityId));
		model.addAttribute("reservation", new Reservation());
		model.addAttribute("nextPageCustomer", session.getAttribute("nextPageCustomer"));
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
		reservation.setDate(activity.getDate());

		//REVISAR
		if (reservation.getVacancies() > activity.getVacancies()) {
			bindingResult.rejectValue("vacancies", "badVacancies", "Reservation vacancies are greater than available vacancies.");
			return "customer/bookActivity";
		}
		
		String mail = user.getMail();
		String issue="Activity Vacancies Reservation";
		String text="You have successfully enrolled in the activity with code " + activityId + " and name "+activity.getName()+".\n You have booked " + reservation.getVacancies()
		+ ". Hope you enjoy.";
		
		//cambiar para que se haga directamente en la vista
		reservation.setPrice(activity.getPrice()*reservation.getVacancies());
		
		activity.setVacancies(activity.getVacancies()-reservation.getVacancies());
		
		reservationDao.addReservation(reservation);
		messageDao.sendMessage(issue, text, mail);
		activityDao.updateActivity(activity);
		return "redirect:/customer/myReservations#tab1";
	}

	@RequestMapping("/myReservations")
	public String listMyReservations(Model model, HttpSession session) {
		UserDetails user = (UserDetails) session.getAttribute("user");
		if (user == null || user.getUserType()!=0) {
			session.setAttribute("pagAnt", "/customer/myReservations");
			return "redirect:/login";
		}
		session.setAttribute("nextPageCustomer", "/customer/myReservations");
		model.addAttribute("user", user);
		model.addAttribute("reservations", reservationDao.getCustomerReservations(user.getMail()));
		model.addAttribute("paidReservations", reservationDao.getPaidCustomerReservations(user.getMail()));
		model.addAttribute("allReservations", reservationDao.getReservations());
		return "customer/myReservations";
	}
	
	@RequestMapping("/activityInfo/{id}")
	public String activityInformation(Model model, @PathVariable("id") int activityId, HttpSession session) {

		UserDetails user = (UserDetails) session.getAttribute("user");
		if (user == null || user.getUserType()!=0) {
			session.setAttribute("pagAnt", "/customer/activityInfo/"+activityId);
			return "redirect:/login";
		}
		
		model.addAttribute("user", user);
		model.addAttribute("activity", activityDao.getActivity(activityId));
		List<Image> l =  imageDao.getActivityImages(activityId, activityDao.getActivity(activityId).getMailInstructor());
		if (l.size() > 0) {
			model.addAttribute("images",l);
		}
		model.addAttribute("nextPageCustomer", session.getAttribute("nextPageCustomer"));
		return "customer/activityInfo";
	}
	
	@RequestMapping("/subscribe/{activityType}")
	public String subscribe(@PathVariable("activityType") String activityType, RedirectAttributes redirectAttributes, HttpSession session) {		
		UserDetails user = (UserDetails) session.getAttribute("user");
		ReceiveInformation receiveInformation = new ReceiveInformation();
		receiveInformation.setActivityTypeName(activityType);
		receiveInformation.setMail(user.getMail());
		
		
		if (!receiveInformationDao.isSubscribed(user.getMail(), activityType)){
			receiveInformationDao.addReceiveInformation(receiveInformation);
			String issue="Subscription Success";
			String text ="You have just get subscripted to a new type of activity.\n Activity type: " + activityType;
			String mail = user.getMail();
			
			messageDao.sendMessage(issue, text, mail);
		}else {
			redirectAttributes.addFlashAttribute("message", "You are already subscribed to this type of activity.");
		}
		
		
		
		return "redirect:/customer/activityTypes";
	}
	
	@RequestMapping("/unsubscribe/{activityTypeName}")
	public String unsubscribe(@PathVariable("activityTypeName") String activityTypeName, HttpSession session) {
		UserDetails user = (UserDetails) session.getAttribute("user");
		if (user == null || user.getUserType()!=0) {
			session.setAttribute("pagAnt", "/customer/mySubscriptions");
			return "redirect:/login";
		}
				
		receiveInformationDao.deleteReceiveInformation(user.getMail(), activityTypeName);
		
		String issue="Unsubscription Success";
		String text ="You have just get unsubscripted of a type of activity.\n Activity type: " + activityTypeName;
		String mail = user.getMail();
		
		messageDao.sendMessage(issue, text, mail);
		
		return "redirect:/customer/mySubscriptions";
	}
	
	@RequestMapping("/mySubscriptions")
	public String mySubscriptions(Model model, HttpSession session) {
		UserDetails user = (UserDetails) session.getAttribute("user");
		if (user==null) {
			session.setAttribute("pagAnt", "/customer/mySubscriptions");
			return "redirect:/login";
		}
		model.addAttribute("user", user);
		model.addAttribute("subscriptions", receiveInformationDao.getCustomerSubscriptions(user.getMail()));
		return "customer/mySubscriptions";
	}
	
	@RequestMapping("/payReservation/{id}")
	public String payReservation(@PathVariable("id") int reservationId) {
		Reservation reservation = reservationDao.getReservation(reservationId);
		reservation.setStatus(Status.PAID);
		reservationDao.updateReservation(reservation);
		
		return "redirect:/customer/myReservations#tab1";
	}
	
	@RequestMapping("/cancelReservation/{id}")
	public String cancelReservation(@PathVariable("id") int reservationId) {
		Reservation reservation = reservationDao.getReservation(reservationId);
		Activity act = activityDao.getActivity(reservation.getActivityId());
		act.setVacancies(act.getVacancies() + reservation.getVacancies());
		reservationDao.deleteReservation(reservationId);
		activityDao.updateActivity(act);
		
		return "redirect:/customer/myReservations#tab1";

	}
	
	@RequestMapping("/editAccount/{mail}")
	public String editAccount(Model model, HttpSession session, @PathVariable("mail") String mail) {
		UserDetails user = (UserDetails) session.getAttribute("user");
		if (user == null || user.getUserType()!=0) {
			session.setAttribute("pagAnt", "/customer/editAccount/"+mail);
			return "redirect:/login";
		}	
		model.addAttribute("user", user);
		session.setAttribute("nextPage", "/");
		session.setAttribute("returnUsers", "/customer/editAccount/"+mail);
		model.addAttribute("customer", customerDao.getCustomer(mail));
		return "customer/editAccount";
	}
	
	@RequestMapping(value="/editAccount/{mail}", method=RequestMethod.POST)
	public String proccessEditAccount(@PathVariable("mail") String mail, @ModelAttribute("customer") Customer customer, BindingResult bindingResult) {
		CustomerValidator cV = new CustomerValidator();
		cV.validate(customer, bindingResult);
		
		if (bindingResult.hasErrors()) {
			return "redirect:/customer/editAccount/"+mail;
		}
		
		customerDao.updateCustomer(customer);
		return "redirect:/customer/activities";
	}
	}

class CustomerSignupValidator implements Validator{

	@Override
	public boolean supports(Class<?> clazz) {
		// TODO Auto-generated method stub
		return Customer.class.equals(clazz);
	}


	@Override
	public void validate(Object target, Errors errors) {
		// TODO Auto-generated method stub
		Customer customer = (Customer) target;
		
		if (customer.getMail().equals("")) {
			errors.rejectValue("mail", "EmptyField", "This field cannot be empty.");
		}
		/*if (customer.getMail() == null) {
			errors.rejectValue("mail", "EmptyField", "This field cannot be empty.");
		}*/
		
		if (customer.getMail().length() > 40) {
			errors.rejectValue("mail", "ValueTooLong", "This field has a limit of 40 characters.");
		}
		if (customer.getName().equals("")) {
			errors.rejectValue("name", "EmptyField", "This field cannot be empty.");
		}
		
		/*if (customer.getName() == null) {
			errors.rejectValue("mail", "EmptyField", "This field cannot be empty.");
		}*/
		if (customer.getName().length() > 20) {
			errors.rejectValue("name", "ValueTooLong", "This field has a limit of 20 characters.");
		}
		if (customer.getUsername().equals("")) {
			errors.rejectValue("username", "EmptyField", "This field cannot be empty.");
		}
		/*if (customer.getUsername() == null) {
			errors.rejectValue("mail", "EmptyField", "This field cannot be empty.");
		}*/
		if (customer.getUsername().length() > 20) {
			errors.rejectValue("username", "ValueTooLong", "This field has a limit of 20 characters.");
		}
		if (customer.getSurname().equals("")) {
			errors.rejectValue("surname", "EmptyField", "This field cannot be empty.");
		}
		/*if (customer.getSurname() == null) {
			errors.rejectValue("mail", "EmptyField", "This field cannot be empty.");
		}*/
		
		if (customer.getSurname().length() > 20) {
			errors.rejectValue("surname", "ValueTooLong", "This field has a limit of 20 characters.");
		}
		if (customer.getPwd().equals("")) {
			errors.rejectValue("pwd", "EmptyField", "This field cannot be empty.");
		}
		/*if (customer.getPwd() == null) {
			errors.rejectValue("mail", "EmptyField", "This field cannot be empty.");
		}*/
		if (customer.getPwd().length() > 20) {
			errors.rejectValue("pwd", "ValueTooLong", "This field has a limit of 20 characters.");
		}
	}
}

class CustomerValidator implements Validator {
	@Override
	public boolean supports(Class<?> clazz) {
		// TODO Auto-generated method stub
		return Customer.class.equals(clazz);
	}


	@Override
	public void validate(Object target, Errors errors) {
		// TODO Auto-generated method stub
		Customer customer = (Customer) target;
		if (customer.getName().equals("")) {
			errors.rejectValue("name", "EmptyField", "This field cannot be empty.");
		}
		
		if (customer.getName() == null) {
			errors.rejectValue("mail", "EmptyField", "This field cannot be empty.");
		}
		if (customer.getName().length() > 20) {
			errors.rejectValue("name", "ValueTooLong", "This field has a limit of 20 characters.");
		}
		if (customer.getUsername().equals("")) {
			errors.rejectValue("username", "EmptyField", "This field cannot be empty.");
		}
		if (customer.getUsername() == null) {
			errors.rejectValue("mail", "EmptyField", "This field cannot be empty.");
		}
		if (customer.getUsername().length() > 20) {
			errors.rejectValue("username", "ValueTooLong", "This field has a limit of 20 characters.");
		}
		if (customer.getSurname().equals("")) {
			errors.rejectValue("surname", "EmptyField", "This field cannot be empty.");
		}
		if (customer.getSurname() == null) {
			errors.rejectValue("mail", "EmptyField", "This field cannot be empty.");
		}
		
		if (customer.getSurname().length() > 20) {
			errors.rejectValue("surname", "ValueTooLong", "This field has a limit of 20 characters.");
		}
	}
}
