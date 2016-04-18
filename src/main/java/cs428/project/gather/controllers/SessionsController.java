package cs428.project.gather.controllers;

import cs428.project.gather.data.*;
import cs428.project.gather.data.form.*;
import cs428.project.gather.data.model.*;
import cs428.project.gather.data.response.*;
import cs428.project.gather.utilities.ActorStateUtility;

import javax.servlet.http.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.*;
import org.springframework.http.*;

@Controller("SessionsController")
public class SessionsController extends AbstractGatherController {
	protected boolean authenticate(SignInData signInData, Errors errors) {
		Registrant user = registrantRepo.findOneByEmail(signInData.getEmail());
		boolean passwordMatches = StringUtils.equals(signInData.getPassword(), user.getPassword());
		if (! passwordMatches) errors.reject("-6", "The password is incorrect.  Please enter the correct password.");
		return passwordMatches;
	}

	protected Registrant createSession(SignInData signInData, HttpServletRequest request) {
		String email = signInData.getEmail();
		System.out.println("\n" + email + " authenticated. \n");
		Registrant registrant = this.registrantRepo.findOneByEmail(email);
		ActorStateUtility.storeActorInSession(request, registrant);
		return registrant;
	}

	@RequestMapping(value="/rest/session")
	public ResponseEntity<RESTSessionResponseData> getSession(HttpServletRequest request, HttpServletResponse response) {
		if (isSessionAuthenticated(request)) return RESTSessionResponseData.sessionResponse(5,"Session Found", getUser(request).getDisplayName(), HttpStatus.OK);
		return RESTSessionResponseData.sessionResponse(-5, "Session Not Found", HttpStatus.OK);
	}

	@RequestMapping(value = "/rest/registrants/signin", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<RESTResponseData> signIn(HttpServletRequest request, @RequestBody String rawData, BindingResult bindingResult) {
		if (! nonAuthenticatedRequest(request, bindingResult)) return RESTResponseData.buildResponse(bindingResult);

		SignInData signInData = SignInData.parseIn(rawData, signInDataValidator, bindingResult);
		if (bindingResult.hasErrors()) return RESTResponseData.buildResponse(bindingResult);
		if (! authenticate(signInData, bindingResult)) return RESTResponseData.buildResponse(bindingResult);

		Registrant registrant = createSession(signInData, request);
		return new ResponseEntity<RESTResponseData>(new RESTSessionResponseData(0,"success",registrant.getDisplayName()), HttpStatus.ACCEPTED);
	}

	@RequestMapping(value="/rest/registrants/signout", method = RequestMethod.POST)
	public ResponseEntity<RESTResponseData> signOut(HttpServletRequest request, HttpServletResponse response) {
		boolean isAuthed = isSessionAuthenticated(request);
		invalidateSession(request, response);
		if (!isAuthed) return RESTResponseData.response(-7, "User is not in authenticated state", HttpStatus.BAD_REQUEST);
		return RESTResponseData.OKResponse("success");
	}
}
