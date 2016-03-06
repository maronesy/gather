package cs428.project.gather.controllers;

import java.sql.Timestamp;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cs428.project.gather.data.RESTResponseData;
import cs428.project.gather.data.SignInData;
import cs428.project.gather.data.model.Registrant;
import cs428.project.gather.data.repo.RegistrantRepository;
import cs428.project.gather.utilities.ActorStateUtility;
import cs428.project.gather.utilities.ActorTypeHelper;
import cs428.project.gather.utilities.RedirectPathHelper;
import cs428.project.gather.validator.SignInDataValidator;

import com.google.gson.Gson;

@Controller("signInController")
public class SignInController {
	 @Autowired
	 @Qualifier("signInDataValidator")
	 private SignInDataValidator signInDataValidator;
	
	@Autowired
	RegistrantRepository registrantRepo;
	
	@ModelAttribute("signInData")
	public SignInData signInData() {
		SignInData signInData = new SignInData();
		
		return signInData;
	}

	@RequestMapping(value = "/api/sign-in", method = RequestMethod.GET)
	public String signIn(HttpServletRequest request) {
		String viewName = null;

		//TODO: None of this means anything for our project. We don't have these views.
		if (ActorTypeHelper.isAnonymousUser(request)) {
			viewName = "sign-in";
		} else {
			viewName = RedirectPathHelper.buildRedirectPath(request, "/invalid-request");
		}
		
		return viewName;
	}

	@RequestMapping(value = "/api/sign-in", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public RESTResponseData signInProcessor(HttpServletRequest request, @RequestBody String rawData,
			BindingResult bindingResult) {

		Gson gson = new Gson();
		SignInData signInData = gson.fromJson(rawData, SignInData.class);

		if (ActorTypeHelper.isAnonymousUser(request)) {
			signInDataValidator.validate(signInData, bindingResult);

			if (bindingResult.hasErrors()) {
				return new RESTResponseData(bindingResult);
			} else {

				if (authenticate(signInData)) {
					String email = signInData.getEmail();

					System.out.println("\n" + email + " authenticated. \n");
					
					Registrant registrant = this.registrantRepo.findOneByEmail(email);
					ActorStateUtility.storeActorInSession(request, registrant);
					
					return new RESTResponseData(0,"success");
				} else {
					String message = "invalid field-" + SignInData.PASSWORD_FIELD_NAME;
					bindingResult.reject("-6",
							message+"The password is invalid.  Please enter a valid password.");
					return new RESTResponseData(bindingResult);
					
				}
			}
		} else {
			return new RESTResponseData(-1,"Incorrect User State. Only Anonymous User can sign in");
		}

		
	}
	
	public boolean authenticate(SignInData signInData)
	{
		String email = signInData.getEmail();
		String suppliedPassword = signInData.getPassword();
		boolean passwordMatches = false;

		if(email == null)
		{
			throw new IllegalArgumentException("The email cannot be null.");
		}

		if(suppliedPassword == null)
		{
			throw new IllegalArgumentException("The supplied password cannot be null.");
		}

		Registrant user = registrantRepo.findOneByEmail(email);


		String storedPassword = user.getPassword();
		if(StringUtils.equals(suppliedPassword, storedPassword))
		{
			passwordMatches = true;

		}

		return passwordMatches;
	}

}
