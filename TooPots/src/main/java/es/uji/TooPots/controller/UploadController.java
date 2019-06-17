package es.uji.TooPots.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import es.uji.TooPots.dao.CertificateDao;
import es.uji.TooPots.model.ActivityType;
import es.uji.TooPots.model.Certificate;
import es.uji.TooPots.model.UserDetails;

@Controller
public class UploadController {
	
	private CertificateDao certificateDao;
	
	@Autowired
	public void setCertificateDao(CertificateDao certificateDao) {
		this.certificateDao = certificateDao;
	}
	
	@Value("${upload.file.directory}")
	private String uploadDirectory;

	@RequestMapping(value="/upload", method=RequestMethod.GET)
	public String uploadFile(Model model) {
		model.addAttribute("activityType", new ActivityType());
		return "upload";
	}
	
	@RequestMapping(value="/upload", method=RequestMethod.POST)
	public String processUploadFile(@RequestParam("file") MultipartFile file, HttpSession session, @ModelAttribute("activityType") String activityType,
			RedirectAttributes redirectAttributes) {
		try {
			if (file.isEmpty()) {
				redirectAttributes.addFlashAttribute("message", 
                        "Please select a file to upload");
				return "redirect:/uploadStatus";
			}
			
			byte[] bytes = file.getBytes();
			Path path = Paths.get(uploadDirectory + "/pdfs" + file.getOriginalFilename());
			Files.write(path, bytes);

			Certificate certificate = new Certificate();
			
			certificate.setOwnerMail(((UserDetails) session.getAttribute("user")).getMail());
			certificate.setRoute(uploadDirectory + "/pdfs" + file.getOriginalFilename());
			certificate.setActivityType(activityType);
			
			certificateDao.addCertificate(certificate);
			
			redirectAttributes.addFlashAttribute("message", "You successfully uploaded '"+path+"'");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "redirect:/uploadStatus";
	}
	
	@RequestMapping(value="/instructor/certificates")
	public String listCertificates(Model model, HttpSession session) {
		UserDetails user = (UserDetails) session.getAttribute("user");
		if (user == null || user.getUserType()!=1) {
			return "/login";
		}
		model.addAttribute("certificates", certificateDao.getInstructorCertificates(user.getMail()));
		return "/instructor/certificates";
	}
}
