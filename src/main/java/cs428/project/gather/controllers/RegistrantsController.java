package cs428.project.gather.controllers;

import java.util.*;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.*;
import org.springframework.http.*;
import com.google.gson.Gson;

import cs428.project.gather.data.*;
import cs428.project.gather.data.model.*;
import cs428.project.gather.utilities.*;

@Controller("RegistrantsController")
public class RegistrantsController extends AbstractGatherController {
	@RequestMapping(value = "/rest/registrants", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<RESTResponseData> register(HttpServletRequest request, @RequestBody String rawData, BindingResult bindingResult) {
		if (! nonAuthenticatedRequest(request, bindingResult)) return RESTResponseData.responseBuilder(bindingResult);

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
		if (! authenticatedRequest(request, bindingResult)) return RESTResourceResponseData.<Registrant> badResponse(bindingResult);
		return RESTResourceResponseData.createResponse(getUser(request), HttpStatus.OK);
	}

	@RequestMapping(value = "/rest/registrants/update", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<RESTResourceResponseData<Registrant>> updateRegistrant(HttpServletRequest request, @RequestBody String rawData, BindingResult bindingResult) {
		if (! authenticatedRequest(request, bindingResult)) return RESTResourceResponseData.<Registrant> badResponse(bindingResult);

		// Use registrationUpdateDataValidator instead of registrationDataValidator in this case, so as to not bloat code with yet another form class
		RegistrationData registrationUpdate = RegistrationData.parseIn(rawData, registrationUpdateDataValidator, bindingResult);
		if (bindingResult.hasErrors()) return RESTResourceResponseData.<Registrant>badResponse(bindingResult);

		Registrant user = getUser(request);
		if (! user.validateUserDependentFields(registrationUpdate, registrantRepo, bindingResult)) return RESTResourceResponseData.<Registrant>badResponse(bindingResult);

		Registrant updatedRegistrant = user.updateUsing(registrationUpdate, categoryRepo, bindingResult);
		if (bindingResult.hasErrors()) return RESTResourceResponseData.<Registrant>badResponse(bindingResult);

		Registrant savedRegistrantResult = this.registrantRepo.save(updatedRegistrant);
		ActorStateUtility.storeActorInSession(request, savedRegistrantResult);
		return RESTResourceResponseData.createResponse(savedRegistrantResult, HttpStatus.CREATED);
	}
}
