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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import es.uji.TooPots.dao.ActivityTypeDao;
import es.uji.TooPots.dao.CertificateDao;
import es.uji.TooPots.dao.ImageDao;
import es.uji.TooPots.model.ActivityType;
import es.uji.TooPots.model.Certificate;
import es.uji.TooPots.model.Image;
import es.uji.TooPots.model.Status;
import es.uji.TooPots.model.UserDetails;

@Controller
public class UploadController {
	
	private ImageDao imageDao;
	
	@Autowired
	public void setImageDao(ImageDao imageDao) {
		this.imageDao = imageDao;
	}
	
	private CertificateDao certificateDao;
	
	@Autowired
	public void setCertificateDao(CertificateDao certificateDao) {
		this.certificateDao = certificateDao;
	}
	
	private ActivityTypeDao activityTypeDao;
	
	@Autowired
	public void setActivityTypeDao(ActivityTypeDao activityTypeDao) {
		this.activityTypeDao = activityTypeDao;
	}
	
	@Value("${upload.file.directory}")
	private String uploadDirectory;

	@RequestMapping(value="/upload", method=RequestMethod.GET)
	public String uploadFile(Model model, HttpSession session) {
		UserDetails user = (UserDetails) session.getAttribute("user");
		
		if (user == null || user.getUserType()!=1) {
			session.setAttribute("pagAnt", "/upload");
			return "redirect:/login";
		}
		model.addAttribute("session", session);
		return "upload";
	}
	
	@RequestMapping(value="/upload", method=RequestMethod.POST)
	public String processUploadFile(@RequestParam("file") MultipartFile[] files, HttpSession session,
			RedirectAttributes redirectAttributes) {

		
		try {
			UserDetails user = (UserDetails) session.getAttribute("user");
			StringBuilder paths = new StringBuilder(); 
			for (MultipartFile file : files) {
				if (file.isEmpty()) {
					redirectAttributes.addFlashAttribute("message", 
	                        "Please select a file to upload");
					return "redirect:/uploadStatus";
				}
				
				byte[] bytes = file.getBytes();
				Path path = Paths.get(uploadDirectory + "/pdfs/"+user.getMail());
				if (!Files.isDirectory(path)) {
					Files.createDirectories(path);
				}
				path = Paths.get(uploadDirectory + "/pdfs/"+user.getMail()+"/"+ file.getOriginalFilename());
				Files.write(path, bytes);
	
				Certificate certificate = new Certificate();
				
				certificate.setOwnerMail(((UserDetails) session.getAttribute("user")).getMail());
				certificate.setRoute("/pdfs/"+user.getMail()+"/" + file.getOriginalFilename());
				certificate.setActivityType("");
				certificate.setStatus(Status.PENDING);
				certificate.setFileName(file.getOriginalFilename());
			
				paths.append(uploadDirectory+"/pdfs/"+user.getMail()+"/" + file.getOriginalFilename()+"\n");
				
				certificateDao.addCertificate(certificate);
			}			
			redirectAttributes.addFlashAttribute("message", "You successfully uploaded:\n"+paths.toString());
			
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
			session.setAttribute("pagAnt", "/instructor/certificates");
			return "redirect:/login";
		}
		model.addAttribute("certificates", certificateDao.getInstructorCertificates(user.getMail()));
		model.addAttribute("session", session);
		return "/instructor/certificates";
	}
	
	@RequestMapping("/uploadImages")
	public String uploadImage(Model model, HttpSession session) {
		UserDetails user = (UserDetails) session.getAttribute("user");
		
		if (user == null || user.getUserType()!=1) {
			session.setAttribute("pagAnt", "/uploadImages");
			return "redirect:/login";
		}
		model.addAttribute("session", session);
		//model.addAttribute(attributeValue);
		
		return "/uploadImages";
	}
	
	@RequestMapping(value="/uploadImages", method=RequestMethod.POST)
	public String processUploadImage(HttpSession session, @RequestParam("file") MultipartFile[] files, RedirectAttributes redirectAttributes) {
		byte[] bytes;
		try {
			Image image = new Image();
			UserDetails user = (UserDetails) session.getAttribute("user");
			StringBuilder paths = new StringBuilder(); 
			for (MultipartFile file : files) {
				if (file.isEmpty()) {
					redirectAttributes.addFlashAttribute("message", 
	                        "Please select a image to upload");
					return "redirect:/uploadStatus";
				}
				bytes = file.getBytes();
				Path path = Paths.get(uploadDirectory + "/images/activities/"+user.getMail()+"/" + file.getOriginalFilename());
				Files.write(path, bytes);
				paths.append(uploadDirectory+"/images/activities/"+user.getMail()+"/" + file.getOriginalFilename()+"\n");
				
				image.setOwnerMail(user.getMail());
				image.setRoute("/images/activities/"+user.getMail()+"/" + file.getOriginalFilename());
				
				imageDao.addImage(image);
			}
			redirectAttributes.addFlashAttribute("message", "You successfully uploaded:\n"+paths.toString());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "redirect:/uploadStatus";
	}
	
	@RequestMapping("/uploadStatus")
	public String uploadStatus(Model model) {
		return "/uploadStatus";
	}
}
