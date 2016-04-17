package cs428.project.gather.controllers;

import cs428.project.gather.data.*;
import cs428.project.gather.data.form.*;
import cs428.project.gather.data.model.*;
import cs428.project.gather.utilities.ActorStateUtility;

import javax.servlet.http.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.*;
import org.springframework.http.*;

@Controller("SessionsController")
public class SessionsController extends AbstractGatherController {
	@RequestMapping(value="/rest/session")
	public ResponseEntity<RESTSessionResponseData> getSession(HttpServletRequest request, HttpServletResponse response) {
		if (isSessionAuthenticated(request)) {
			return new ResponseEntity<RESTSessionResponseData>(new RESTSessionResponseData(5,"Session Found",getUser(request).getDisplayName()), HttpStatus.OK);
		} else {
			return new ResponseEntity<RESTSessionResponseData>(new RESTSessionResponseData(-5,"Session Not Found"), HttpStatus.OK);
		}
	}

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

			return new ResponseEntity<RESTResponseData>(new RESTSessionResponseData(0,"success",registrant.getDisplayName()),HttpStatus.ACCEPTED);

		} else {
			bindingResult.reject("-6", "The password is incorrect.  Please enter the correct password.");
			return RESTResponseData.responseBuilder(bindingResult);
		}
	}

	@RequestMapping(value="/rest/registrants/signout", method = RequestMethod.POST)
	public ResponseEntity<RESTResponseData> signOut(HttpServletRequest request, HttpServletResponse response) {
		boolean isAuthed = isSessionAuthenticated(request);
		invalidateSession(request, response);
		if (!isAuthed) {
			return new ResponseEntity<RESTResponseData>(new RESTResponseData(-7,"User is not in authenticated state"),HttpStatus.BAD_REQUEST);
		} else {
			return new ResponseEntity<RESTResponseData>(new RESTResponseData(0,"success"),HttpStatus.OK);
		}
	}
}
