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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

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

		UserDetails user = (UserDetails) session.getAttribute("user");

		redirectAttributes.addFlashAttribute("message", certificateDao.uploadCertificate(files,  user, uploadDirectory));
		
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
	
	/*@RequestMapping(value="/instructor/viewCertificate/{id}", method = RequestMethod.GET)
	public String viewCertificate(Model model, @PathVariable("id") int certificateId) {
		String route = certificateDao.getCertificate(certificateId).getRoute().replace(" ", "%20");
		
		UriComponents uC = UriComponentsBuilder.newInstance()
				.scheme("http").host("localhost").port(8090)
				.path(route).build();
		
		model.addAttribute("certificate", uC);
		model.addAttribute("prueba", "http://docs.google.com/gview?url=http://localhost:8090/"+uploadDirectory+route+"&embedded=true");
		return "/instructor/viewCertificate";
	}*/
	
	@RequestMapping("/uploadImages")
	public String uploadImage(Model model, HttpSession session) {
		UserDetails user = (UserDetails) session.getAttribute("user");
		
		if (user == null || user.getUserType()!=1) {
			session.setAttribute("pagAnt", "/uploadImages");
			return "redirect:/login";
		}
		model.addAttribute("session", session);
		//model.addAttribute(attributeValue);
		
		return "/instructor/uploadImages";
	}
	
	@RequestMapping(value="/uploadImages", method=RequestMethod.POST)
	public String processUploadImage(HttpSession session, @RequestParam("file") MultipartFile[] files, RedirectAttributes redirectAttributes) {
		UserDetails user = (UserDetails) session.getAttribute("user");
		
					redirectAttributes.addFlashAttribute("message", imageDao.uploadImage(files, user, uploadDirectory, 0));

		return "redirect:/uploadStatus";
	}
	
	@RequestMapping("/uploadStatus")
	public String uploadStatus(Model model) {
		return "/uploadStatus";
	}
}
