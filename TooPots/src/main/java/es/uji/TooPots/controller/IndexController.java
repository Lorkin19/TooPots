package es.uji.TooPots.controller;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
	@Value("${upload.file.directory}")
	private String uploadDirectory;
	
	@RequestMapping("/")
    public String indexInit(Model model) {
		model.addAttribute("user", new UserDetails());
		Path path = Paths.get(uploadDirectory+"/images/carousel");
		File dir = new File(path.toString());
		String images[] = dir.list();
		
		for (int i = 0; i < images.length; i++) {
			images[i] = "/images/carousel/"+images[i];
			System.out.println(images[i]);	
		}
		
		model.addAttribute("images", images);
        return "index"; 
    }
}
