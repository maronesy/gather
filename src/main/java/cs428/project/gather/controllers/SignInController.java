package cs428.project.gather.controllers;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.stereotype.Controller;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cs428.project.gather.data.SignInData;
import cs428.project.gather.data.model.Actor;
import cs428.project.gather.data.model.Registrant;
import cs428.project.gather.data.repo.RegistrantRepository;
import cs428.project.gather.utilities.ActorStateUtility;
import cs428.project.gather.utilities.ActorTypeHelper;
import cs428.project.gather.utilities.RedirectPathHelper;

import com.google.gson.Gson;

@Controller("signInController")
public class SignInController {
	// @Autowired
	// @Qualifier("signInDataValidator")
	// private SignInDataValidator signInDataValidator;
	//
	@Autowired
	RegistrantRepository registrantRepo;
	
	@ModelAttribute("signInData")
	public SignInData signInData() {
		SignInData signInData = new SignInData();
		
		return signInData;
	}

	@RequestMapping(value = "/sign-in", method = RequestMethod.GET)
	public String signIn(HttpServletRequest request) {
		String viewName = null;

		if (ActorTypeHelper.isAnonymousUser(request)) {
			viewName = "sign-in";
		} else {
			viewName = RedirectPathHelper.buildRedirectPath(request, "/invalid-request");
		}
		
		return viewName;
	}

	@RequestMapping(value = "/sign-in", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public String signInProcessor(HttpServletRequest request, @RequestBody String rawData,
			BindingResult bindingResult) {
		String redirectPath = null;

		Gson gson = new Gson();
		SignInData signInData = gson.fromJson(rawData, SignInData.class);

		if (ActorTypeHelper.isAnonymousUser(request)) {
			// signInDataValidator.validate(signInData, bindingResult);

			if (bindingResult.hasErrors()) {
				redirectPath = "sign-in";
			} else {
				Date authenticationDateTime = new Date();

				if (authenticate(signInData, authenticationDateTime)) {
					String email = signInData.getEmail();

					System.out.println("\n\n\n" + email + "\n\n\n");

					
					Registrant registrant = this.registrantRepo.findOneByEmail(email);
					//TODO Do something if registrant = null (i.e. not found). Unless this sort of thing should be handled in the authenticate stub.

					ActorStateUtility.storeActorInSession(request, registrant);

					redirectPath = RedirectPathHelper.buildRedirectPath(request, "/");
				} else {
					String errorCode = "invalid." + SignInData.PASSWORD_FIELD_NAME;
					bindingResult.rejectValue(SignInData.PASSWORD_FIELD_NAME, errorCode,
							"The password is invalid.  Please enter a valid password.");

					redirectPath = "sign-in";
				}
			}
		} else {
			redirectPath = RedirectPathHelper.buildRedirectPath(request, "/invalid-request");
		}

		return redirectPath;
	}

	private boolean authenticate(SignInData signInData, Date authenticationDateTime) {
		// boolean authenticated = false;
		//
		String email = signInData.getEmail();
		String password = signInData.getPassword();
		//
		// if(registrantDataAdapter.authenticate(username, password,
		// authenticationDateTime))
		// {
		// authenticated = true;
		// }
		//
		// return authenticated;
		return (!email.equals("") && !password.equals(""));
	}
}
