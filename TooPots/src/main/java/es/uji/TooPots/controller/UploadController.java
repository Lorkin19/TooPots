package es.uji.TooPots.controller;

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
		return "upload";
	}
	
	@RequestMapping(value="/upload", method=RequestMethod.POST)
	public String processUploadFile(@RequestParam("file") MultipartFile file, HttpSession session, @ModelAttribute("activityType") String activityType) {
		try {
			
			byte[] bytes = file.getBytes();
			Path path = Paths.get(uploadDirectory + "/pdfs" + file.getOriginalFilename());
			Files.write(path, bytes);
			
			if (file.isEmpty()) {
				
			}
			
			Certificate certificate = new Certificate();
			
			certificate.setOwnerMail(((UserDetails) session.getAttribute("user")).getMail());
			certificate.setRoute(uploadDirectory + "/pdfs" + file.getOriginalFilename());
			certificate.setActivityType(activityType);
			
			certificateDao.addCertificate(certificate);
			
			return "";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}
}
