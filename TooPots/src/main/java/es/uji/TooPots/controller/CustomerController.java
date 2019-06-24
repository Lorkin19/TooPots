package es.uji.TooPots.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
import es.uji.TooPots.model.Activity;
import es.uji.TooPots.model.Customer;
import es.uji.TooPots.model.Image;
import es.uji.TooPots.model.Message;
import es.uji.TooPots.model.ReceiveInformation;
import es.uji.TooPots.model.Reservation;
import es.uji.TooPots.model.Status;
import es.uji.TooPots.model.UserDetails;

@Controller
@RequestMapping("/customer")
public class CustomerController {
	
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
		session.setAttribute("pagAnt", "/customer/activities");

		UserDetails user = (UserDetails) session.getAttribute("user");
		model.addAttribute("user", user);
		model.addAttribute("activities", activityDao.getActivities());
		
		session.setAttribute("nextPageCustomer", "/customer/activities");

		return "customer/activities";
	}
	 
	@RequestMapping("/activityTypes")
	public String listActivityTypes(Model model, HttpSession session) {
		session.setAttribute("pagAnt", "/customer/activityTypes");

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
		return "redirect:/customer/myReservations";
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
		session.setAttribute("pagAnt", "/customer/activityInfo/"+activityId);

		UserDetails user = (UserDetails) session.getAttribute("user");
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
		
		System.out.println(activityTypeName);
		
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
		model.addAttribute("subscriptions", receiveInformationDao.getCustomerSubscriptions(user.getMail()));
		return "customer/mySubscriptions";
	}
	
	@RequestMapping("/payReservation/{id}")
	public String payReservation(@PathVariable("id") int reservationId) {
		Reservation reservation = reservationDao.getReservation(reservationId);
		reservation.setStatus(Status.PAID);
		reservationDao.updateReservation(reservation);
		
		return "redirect:/customer/myReservations";
	}

}
