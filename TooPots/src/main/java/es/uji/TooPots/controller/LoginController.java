package es.uji.TooPots.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import es.uji.TooPots.dao.UserDao;
import es.uji.TooPots.model.UserDetails;

class UserValidator implements Validator{

	@Override
	public boolean supports(Class<?> clazz) {
		// TODO Auto-generated method stub
		return UserDetails.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		// TODO Auto-generated method stub
		UserDetails user = (UserDetails) target;
		if (user.getPassword().trim().equals("")) {
			errors.rejectValue("password", "At least 1 character.", "This field can't be empty.");
		}
		
		if (user.getMail().trim().equals("")){
			errors.rejectValue("mail", "At least 1 character.", "This field can't be empty.");
		}
	}
	
}

@Controller
public class LoginController {
	@Autowired
	private UserDao userDao;
	
	@RequestMapping("/login")
	public String login(Model model) {
		model.addAttribute("user", new UserDetails());
		return "login";
	}
	
	@RequestMapping(value="/login", method=RequestMethod.POST)
	public String checkLogin(@ModelAttribute("user") UserDetails user,
							BindingResult bindingResult, HttpSession session) {
		UserValidator userValidator = new UserValidator();
		userValidator.validate(user, bindingResult);
		if (bindingResult.hasErrors()) {
			return "login";
		}		
		user = userDao.loadUserByMail(user.getMail(), user.getPassword());
		if (user == null) {
			bindingResult.rejectValue("password", "badpw", "Incorrect Password");
			return "login";
		}
		session.setAttribute("user", user);
		return "redirect:/";
	}
	
	@RequestMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/";
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
