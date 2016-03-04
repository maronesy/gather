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

import cs428.project.gather.data.RegistrationData;
import cs428.project.gather.data.model.Registrant;
import cs428.project.gather.data.repo.RegistrantRepository;
import cs428.project.gather.utilities.ActorStateUtility;
import cs428.project.gather.utilities.ActorTypeHelper;
import cs428.project.gather.utilities.RedirectPathHelper;

@Controller("registerController")
public class RegisterController {

	@Autowired
	RegistrantRepository registrantRepo;

	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public String userRegistration(HttpServletRequest request) {
		String viewName = null;

		if (ActorTypeHelper.isAnonymousUser(request)) {
			viewName = "register";
		} else {
			viewName = RedirectPathHelper.buildRedirectPath(request, "/");
		}

		return viewName;
	}

	@RequestMapping(value = "/register", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public String signInProcessor(HttpServletRequest request, @RequestBody String rawData,
			BindingResult bindingResult) {

		String redirectPath = null;

		Gson gson = new Gson();
		RegistrationData registrationData = gson.fromJson(rawData, RegistrationData.class);

		if (ActorTypeHelper.isAnonymousUser(request)) {
			// RegistrationDataValidator.validate(registrationData,
			// bindingResult);

			if (bindingResult.hasErrors()) {
				redirectPath = "register";
			} else {
				Registrant newRegistrant = buildRegistrant(registrationData);

				Registrant savedRegistrantResult = this.registrantRepo.save(newRegistrant);

				ActorStateUtility.storeActorInSession(request, savedRegistrantResult);

				redirectPath = RedirectPathHelper.buildRedirectPath(request, "/");
			}
		} else {
			redirectPath = RedirectPathHelper.buildRedirectPath(request, "/invalid-request");
		}

		return redirectPath;
	}

	private Registrant buildRegistrant(RegistrationData registrationData) {
		Registrant newRegistrant = new Registrant();
		newRegistrant.setEmail(registrationData.getEmailAddress());
		newRegistrant.setPassword(registrationData.getPassword());
		newRegistrant.setDisplayName(registrationData.getDisplayName());
		return newRegistrant;
	}
}
