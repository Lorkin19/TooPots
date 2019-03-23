package es.uji.TooPots.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;

import es.uji.TooPots.dao.InstructorDao;


@Controller
@RequestMapping("/instructor")
public class InstructorController {
	
	private InstructorDao instructorDao;
	
	@Autowired
	public void setInstructorDao(InstructorDao instructorDao) {
        this.instructorDao = instructorDao;
    }
	
	@RequestMapping("/crear")
	public String listInstructor(Model model) {
		model.addAttribute("opciones");
		return "/instructor/list";
	}
}
