package cs428.project.gather.controllers;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cs428.project.gather.data.RESTResponseData;
import cs428.project.gather.data.RESTSignInResponseData;
import cs428.project.gather.data.SignInData;
import cs428.project.gather.data.model.Registrant;
import cs428.project.gather.data.repo.RegistrantRepository;
import cs428.project.gather.utilities.ActorStateUtility;
import cs428.project.gather.utilities.ActorTypeHelper;
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

	@RequestMapping(value = "/rest/registrants/signin", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<RESTResponseData> signIn(HttpServletRequest request, @RequestBody String rawData,
			BindingResult bindingResult) {

		Gson gson = new Gson();
		SignInData signInData = gson.fromJson(rawData, SignInData.class);
		if (ActorTypeHelper.isAnonymousUser(request)) {
			signInDataValidator.validate(signInData, bindingResult);

			if (bindingResult.hasErrors()) {
				return RESTResponseData.responseBuilder(bindingResult);
			} else {

				if (authenticate(signInData)) {
					String email = signInData.getEmail();

					System.out.println("\n" + email + " authenticated. \n");
					
					Registrant registrant = this.registrantRepo.findOneByEmail(email);
					ActorStateUtility.storeActorInSession(request, registrant);
					
					return new ResponseEntity<RESTResponseData>(new RESTSignInResponseData(0,"success",registrant.getDisplayName()),HttpStatus.ACCEPTED);
				} else {
					String message = "invalid field-" + SignInData.PASSWORD_FIELD_NAME;
					bindingResult.reject("-6",
							message+"The password is invalid.  Please enter a valid password.");
					return RESTResponseData.responseBuilder(bindingResult);
					
				}
			}
		} else {
			bindingResult.reject("-7","Incorrect User State. Only Anonymous User can sign in");
			return RESTResponseData.responseBuilder(bindingResult);
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
