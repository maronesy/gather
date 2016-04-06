package cs428.project.gather.controllers;

import java.util.*;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.validation.Errors;
import com.google.gson.Gson;

import cs428.project.gather.data.*;
import cs428.project.gather.data.model.*;
import cs428.project.gather.data.repo.*;
import cs428.project.gather.utilities.*;
import cs428.project.gather.validator.*;

@Controller("registrantsController")
public class RegistrantsController {
	@Autowired
	RegistrantRepository registrantRepo;

	@Autowired
	CategoryRepository categoryRepo;

	@Autowired
	private RegistrationDataValidator registrationDataValidator;

	@Autowired
	private RegistrationUpdateDataValidator registrationUpdateDataValidator;

	private boolean authenticateRequest(HttpServletRequest request, Errors errors) {
		if (! ActorTypeHelper.isRegisteredUser(request)) {
			System.out.println("An anonymous user tried to access " + request.getRequestURL().toString());
			errors.reject("-7", "Incorrect User State. Only registered users can access " + request.getRequestURL().toString());
			return false;
		} return true;
	}

	private Registrant getUser(HttpServletRequest request) {
		Actor actor = ActorStateUtility.retrieveActorFromRequest(request);
		return this.registrantRepo.findOne(actor.getActorID());
	}

	@RequestMapping(value = "/rest/registrants", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<RESTResponseData> register(HttpServletRequest request, @RequestBody String rawData, BindingResult bindingResult) {
		if (! ActorTypeHelper.isAnonymousUser(request)) {
			bindingResult.reject("-7","Incorrect User State. Only Anonymous User can register");
			return RESTResponseData.responseBuilder(bindingResult);
		}

		RegistrationData registrationData = RegistrationData.parseIn(rawData, registrationDataValidator, bindingResult);
		if (bindingResult.hasErrors()) return RESTResponseData.responseBuilder(bindingResult);

		Registrant newRegistrant = Registrant.buildRegistrantFrom(registrationData, categoryRepo, bindingResult);
		if (bindingResult.hasErrors()) return RESTResponseData.responseBuilder(bindingResult);

		Registrant savedRegistrantResult = this.registrantRepo.save(newRegistrant);
		ActorStateUtility.storeActorInSession(request, savedRegistrantResult);

		return new ResponseEntity<RESTResponseData>(new RESTResponseData(0,"success"), HttpStatus.CREATED);
	}

	@RequestMapping(value = "/rest/registrants/info", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<RESTResourceResponseData<Registrant>> getRegistrant(HttpServletRequest request, @RequestBody String rawData, BindingResult bindingResult) {
		if (! authenticateRequest(request, bindingResult)) return RESTResourceResponseData.<Registrant> badResponse(bindingResult);
		return RESTResourceResponseData.createResponse(getUser(request), HttpStatus.OK);
	}

	@RequestMapping(value = "/rest/registrants/update", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<RESTResourceResponseData<Registrant>> updateRegistrant(HttpServletRequest request, @RequestBody String rawData, BindingResult bindingResult) {
		if (! authenticateRequest(request, bindingResult)) return RESTResourceResponseData.<Registrant> badResponse(bindingResult);

		// Use registrationUpdateDataValidator instead of registrationDataValidator in this case, so as to not bloat code with yet another form class
		RegistrationData registrationUpdate = RegistrationData.parseIn(rawData, registrationUpdateDataValidator, bindingResult);
		if (bindingResult.hasErrors()) return RESTResourceResponseData.<Registrant>badResponse(bindingResult);

		Registrant updatedRegistrant = getUser(request).updateUsing(registrationUpdate, categoryRepo, bindingResult);
		if (bindingResult.hasErrors()) return RESTResourceResponseData.<Registrant>badResponse(bindingResult);

		Registrant savedRegistrantResult = this.registrantRepo.save(updatedRegistrant);
		ActorStateUtility.storeActorInSession(request, savedRegistrantResult);
		return RESTResourceResponseData.createResponse(savedRegistrantResult, HttpStatus.CREATED);
	}
}
