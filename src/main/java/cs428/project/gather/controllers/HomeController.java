package cs428.project.gather.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("HomeController")
public class HomeController {
	
	/**
	 * 
	 * @return the index home page
	 */
	@RequestMapping(value = "/")
	public String index() {
		return "index";
	}
	
	/**
	 * 
	 * @return the registration form page
	 */
	@RequestMapping(value = "/registerform")
	public String register() {
		return "registerform";
	}

	@RequestMapping(value = "/registrants")
	public String registrants() {
		return "registrants";
	}

	@RequestMapping(value = "/zipcode")
	public String zipcode() {
		return "zipcode";
	}
}