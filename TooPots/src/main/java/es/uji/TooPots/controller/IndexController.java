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

import es.uji.TooPots.model.UserDetails;

@Controller
public class IndexController {
	@RequestMapping("/")
    public String indexInit(Model model) {
		model.addAttribute("user", new UserDetails());
        return "index"; 
    }
}
