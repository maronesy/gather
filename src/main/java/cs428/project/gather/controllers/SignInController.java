package cs428.project.gather.controllers;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.validation.*;

import cs428.project.gather.data.*;
import cs428.project.gather.data.model.*;
import cs428.project.gather.data.repo.*;
import cs428.project.gather.utilities.*;

@Controller("SignInController")
public class SignInController extends AbstractGatherController {
	public boolean authenticate(SignInData signInData) {
		Registrant user = registrantRepo.findOneByEmail(signInData.getEmail());
		return StringUtils.equals(signInData.getPassword(), user.getPassword());
	}

	@RequestMapping(value = "/rest/registrants/signin", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<RESTResponseData> signIn(HttpServletRequest request, @RequestBody String rawData, BindingResult bindingResult) {
		if (! nonAuthenticatedRequest(request, bindingResult)) return RESTResponseData.responseBuilder(bindingResult);

		SignInData signInData = SignInData.parseIn(rawData, signInDataValidator, bindingResult);
		if (bindingResult.hasErrors()) return RESTResponseData.responseBuilder(bindingResult);

		if (authenticate(signInData)) {
			String email = signInData.getEmail();
			System.out.println("\n" + email + " authenticated. \n");

			Registrant registrant = this.registrantRepo.findOneByEmail(email);
			ActorStateUtility.storeActorInSession(request, registrant);

			return new ResponseEntity<RESTResponseData>(new RESTSignInResponseData(0,"success",registrant.getDisplayName()),HttpStatus.ACCEPTED);

		} else {
			bindingResult.reject("-6", "The password is incorrect.  Please enter the correct password.");
			return RESTResponseData.responseBuilder(bindingResult);
		}
	}
}
