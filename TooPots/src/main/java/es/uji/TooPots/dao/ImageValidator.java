package es.uji.TooPots.dao;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import es.uji.TooPots.model.Image;

public class ImageValidator implements Validator{

	@Override
	public boolean supports(Class<?> clazz) {
		// TODO Auto-generated method stub
		return Image.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		// TODO Auto-generated method stub
		Image image = (Image) target;
		String extension = image.getRoute().substring(image.getRoute().lastIndexOf("."));
		if (!extension.toLowerCase().equals(".jpeg") && !extension.toLowerCase().equals(".jpg") && !extension.toLowerCase().equals(".png")) {
			errors.reject("Extension not supported");
		}
	}
}