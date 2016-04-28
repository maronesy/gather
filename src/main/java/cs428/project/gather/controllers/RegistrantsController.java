package cs428.project.gather.controllers;

import cs428.project.gather.data.form.*;
import cs428.project.gather.data.model.*;
import cs428.project.gather.data.response.*;
import cs428.project.gather.utilities.*;

import java.util.*;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.*;
import org.springframework.http.*;

@Controller("RegistrantsController")
public class RegistrantsController extends AbstractGatherController {
	@RequestMapping(value = "/rest/registrants", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<RESTResponseData> register(HttpServletRequest request, @RequestBody String rawData, BindingResult bindingResult) {
		if (! nonAuthenticatedRequest(request, bindingResult)) return RESTResponseData.buildResponse(bindingResult);

		RegistrationData registrationData = RegistrationData.parseIn(rawData, registrationDataValidator, bindingResult);
		if (bindingResult.hasErrors()) return RESTResponseData.buildResponse(bindingResult);

		Registrant newRegistrant = Registrant.buildRegistrantFrom(registrationData, categoryRepo, bindingResult);
		if (bindingResult.hasErrors()) return RESTResponseData.buildResponse(bindingResult);

		Registrant savedRegistrantResult = this.registrantRepo.save(newRegistrant);
		ActorStateUtility.storeActorInSession(request, savedRegistrantResult);

		return new ResponseEntity<RESTResponseData>(new RESTResponseData(0,"success"), HttpStatus.CREATED);
	}

	@RequestMapping(value = "/rest/registrants/displayname", method = RequestMethod.GET)
	public ResponseEntity<RESTPaginatedResourcesResponseData<String>> getRegistrantNames(HttpServletRequest request) {	
		List<String> allUserNames = registrantRepo.findAllDisplayNames();
		return RESTPaginatedResourcesResponseData.createResponse(request, allUserNames);
	}
	
	//TODO: This looks like it should just be a GET, not a PUT. We do nothing with any passed in data.
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
