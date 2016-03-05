package cs428.project.gather.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

import cs428.project.gather.data.RESTResponseData;
import cs428.project.gather.data.RegistrationData;
import cs428.project.gather.data.model.Registrant;
import cs428.project.gather.data.repo.RegistrantRepository;
import cs428.project.gather.utilities.ActorStateUtility;
import cs428.project.gather.utilities.ActorTypeHelper;
import cs428.project.gather.utilities.RedirectPathHelper;
import cs428.project.gather.validator.RegistrationDataValidator;

@Controller("registerController")
public class RegisterController {

	@Autowired
	RegistrantRepository registrantRepo;

	@Autowired
	private RegistrationDataValidator registrationDataValidator;
	
	@RequestMapping(value = "/api/register", method = RequestMethod.GET)
	public String userRegistration(HttpServletRequest request) {
		String viewName = null;

		if (ActorTypeHelper.isAnonymousUser(request)) {
			viewName = "register";
		} else {
			viewName = RedirectPathHelper.buildRedirectPath(request, "/");
		}

		return viewName;
	}

	@RequestMapping(value = "/api/register", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public RESTResponseData signInProcessor(HttpServletRequest request, @RequestBody String rawData,
			BindingResult bindingResult) {

		Gson gson = new Gson();
		RegistrationData registrationData = gson.fromJson(rawData, RegistrationData.class);

		if (ActorTypeHelper.isAnonymousUser(request)) {
			registrationDataValidator.validate(registrationData,bindingResult); 

			if (bindingResult.hasErrors()) {
				return new RESTResponseData(bindingResult);
			} else {
				
				Registrant newRegistrant = buildRegistrant(registrationData);

				Registrant savedRegistrantResult = this.registrantRepo.save(newRegistrant);

				ActorStateUtility.storeActorInSession(request, savedRegistrantResult);

				
				return new RESTResponseData(0, "success");
			}
		} else {
			return new RESTResponseData(-1,"Incorrect User State. Only Anonymous User can register");
		}
		
	}

	private Registrant buildRegistrant(RegistrationData registrationData) {
		Registrant newRegistrant = new Registrant();
		newRegistrant.setEmail(registrationData.getEmail());
		newRegistrant.setPassword(registrationData.getPassword());
		newRegistrant.setDisplayName(registrationData.getDisplayName());
		return newRegistrant;
	}
}
