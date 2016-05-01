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

/**
 * 
 * @author Team Gather
 * This class is the Events controller in charge of user related rest call such as add, registration,
 * getting registration info, and updating user info.
 * 
 */

@Controller("RegistrantsController")
public class RegistrantsController extends AbstractGatherController {
	
	/**
	 * 
	 * This method takes user information and adds a new registrant to the database based on the received data.
	 * 
	 * @param request: This variable is the request received from the frontend through the rest call to register the user
	 * @param rawData: This variable is the data field of the request received from the frontend containing email,
	 * 				   password, and display name.
	 * @param bindingResult: The controller makes this variable available any time an object is posted/put from
	 * 					     the frontend. It will contain errors if there was an error in binding the
	 * 					     object. The app continues to use its Error interface to report on validation
	 * 					     or other errors as we continue to process things. Those errors then get
	 * 					     reported back via the HTTPStatus or Status in our response data.
	 * @return: This method returns an http response through which the front end determines whether the registration
	 * 			was successful  
	 * 
	 */
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
	
	/**
	 * This method receives the user id through the http request and returns the display name for that user
	 * 
	 * @param request to get the user name of the current user
	 * @return the display name of the user
	 * 
	 */
	@RequestMapping(value = "/rest/registrants/displayname", method = RequestMethod.GET)
	public ResponseEntity<RESTPaginatedResourcesResponseData<String>> getRegistrantNames(HttpServletRequest request) {	
		List<String> allUserNames = registrantRepo.findAllDisplayNames();
		return RESTPaginatedResourcesResponseData.createResponse(request, allUserNames);
	}
	
	/**
	 * 
	 * This method takes the user id from the request and returns the user profile preferences to the frontend
	 * 
	 * @param request: This variable is the request received from the frontend through the rest call to get user profile info
	 * @param rawData: This variable is the data field of the request received from the frontend containing an empty array
	 * @param bindingResult: The controller makes this variable available any time an object is posted/put from
	 * 					     the frontend. It will contain errors if there was an error in binding the
	 * 					     object. The app continues to use its Error interface to report on validation
	 * 					     or other errors as we continue to process things. Those errors then get
	 * 					     reported back via the HTTPStatus or Status in our response data.
	 * @return: This method returns the user profile preferences to the frontend 
	 * 
	 */
	//TODO: This looks like it should just be a GET, not a PUT. We do nothing with any passed in data.
	@RequestMapping(value = "/rest/registrants/info", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<RESTResourceResponseData<Registrant>> getRegistrant(HttpServletRequest request, @RequestBody String rawData, BindingResult bindingResult) {
		if (! authenticatedRequest(request, bindingResult)) return RESTResourceResponseData.<Registrant> badResponse(bindingResult);
		return RESTResourceResponseData.createResponse(getUser(request), HttpStatus.OK);
	}

	/**
	 * 
	 * This method updates the user profile preferences
	 * 
	 * @param request: This variable is the request received from the frontend through the rest call to update the user profile info
	 * @param rawData: This variable is the data field of the request received from the frontend containing the data to be updated
	 * @param bindingResult: The controller makes this variable available any time an object is posted/put from
	 * 					     the frontend. It will contain errors if there was an error in binding the
	 * 					     object. The app continues to use its Error interface to report on validation
	 * 					     or other errors as we continue to process things. Those errors then get
	 * 					     reported back via the HTTPStatus or Status in our response data.
	 * @return: This method returns the new user profile preferences 
	 * 
	 */
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
