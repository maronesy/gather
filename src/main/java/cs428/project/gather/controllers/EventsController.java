package cs428.project.gather.controllers;

import cs428.project.gather.data.*;
import cs428.project.gather.data.form.*;
import cs428.project.gather.data.model.*;
import cs428.project.gather.data.response.*;
import cs428.project.gather.utilities.*;

import java.util.*;
import java.sql.Timestamp;
import org.joda.time.DateTime;
import javax.servlet.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.*;
import org.springframework.http.*;
import com.google.gson.*;

/**
 * 
 * @author Team Gather
 * This class is the Events controller in charge of event related rest call such as add, remove, and update event
 * 
 */

@Controller("EventsController")
public class EventsController extends AbstractGatherController {
	
	/**
	 * 
	 * This methods takes the user coordinates and retuns a list of al nearby events
	 * 
	 * @param request: This variable is the http request received from the frontend through the rest call to get 
	 * 				   nearBy events
	 * @param rawData: This variable is the data field of the request received from the frontend containing user
	 * 				   coordinates
	 * @param bindingResult:The controller makes this variable available any time an object is posted/put from
	 * 					    the frontend. It will contain errors if there was an error in binding the
	 * 					    object. The app continues to use its Error interface to report on validation
	 * 					    or other errors as we continue to process things. Those errors then get
	 * 					    reported back via the HTTPStatus or Status in our response data.
	 * @return: This method returns a list of nearby events that is obtained based on the user coordinates
	 * 		    passed into the controller 
	 * 
	 */
	@RequestMapping(value = "/rest/events", method = RequestMethod.PUT)
	public ResponseEntity<RESTPaginatedResourcesResponseData<Event>> getNearbyEvents(HttpServletRequest request, @RequestBody String rawData, BindingResult bindingResult) {
		EventsQueryData queryParams = EventsQueryData.parseIn(rawData, eventsQueryDataValidator, bindingResult);
		if (bindingResult.hasErrors()) return RESTPaginatedResourcesResponseData.badResponse(bindingResult);

		List<Event> events = Event.queryForEvents(queryParams, eventRepo, getUserAsOption(request), bindingResult);
		if (bindingResult.hasErrors()) return RESTPaginatedResourcesResponseData.badResponse(bindingResult);

		return RESTPaginatedResourcesResponseData.createResponse(request, events);
	}

	/**
	 * 
	 * This method takes info regarding a new event, adds it to the event list, and returns an update list of events 
	 * 
	 * @param request: This variable is the request received from the frontend through the rest call to add an event
	 * @param rawData: This variable is the data field of the request received from the frontend containing event info
	 * @param bindingResult: The controller makes this variable available any time an object is posted/put from
	 * 					     the frontend. It will contain errors if there was an error in binding the
	 * 					     object. The app continues to use its Error interface to report on validation
	 * 					     or other errors as we continue to process things. Those errors then get
	 * 					     reported back via the HTTPStatus or Status in our response data.
	 * @return: This method returns a list of updated events which includes the newly added event.
	 * 
	 */
	@RequestMapping(value = "/rest/events", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<RESTResourceResponseData<Event>> addEvent(HttpServletRequest request, @RequestBody String rawData, BindingResult bindingResult) {
		if (! authenticatedRequest(request, bindingResult)) return RESTResourceResponseData.<Event>badResponse(bindingResult);

		NewEventData newEventData = NewEventData.parseIn(rawData, newEventDataValidator, bindingResult);
		if (bindingResult.hasErrors()) return RESTResourceResponseData.<Event>badResponse(bindingResult);

		Event newEvent = Event.buildEventFrom(newEventData, getUser(request), categoryRepo, bindingResult);
		if (bindingResult.hasErrors()) return RESTResourceResponseData.<Event>badResponse(bindingResult);

		Event savedEventResult = this.eventRepo.save(newEvent);
		System.out.println("DistanceFromCaller: " + newEventData.distanceFromCaller());
		return RESTResourceResponseData.createResponse(savedEventResult, HttpStatus.CREATED);
	}

	/**
	 * 
	 * This method takes updated information about an existing event, updates the event info, and returns an updated
	 * event list. 
	 * 
	 * @param request: This variable is the request received from the frontend through the rest call to update an event
	 * @param rawData: This variable is the data field of the request received from the frontend containing updated event info
	 * @param bindingResult: The controller makes this variable available any time an object is posted/put from
	 * 					     the frontend. It will contain errors if there was an error in binding the
	 * 					     object. The app continues to use its Error interface to report on validation
	 * 					     or other errors as we continue to process things. Those errors then get
	 * 					     reported back via the HTTPStatus or Status in our response data.
	 * @return: This method returns a list of updated events which includes the newly added event.
	 * 
	 */
	@RequestMapping(value = "/rest/events/update", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<RESTResourceResponseData<Event>> updateEvent(HttpServletRequest request, @RequestBody String rawData, BindingResult bindingResult) {
		if (! authenticatedRequest(request, bindingResult)) return RESTResourceResponseData.<Event>badResponse(bindingResult);

		UpdateEventData updateEventData = UpdateEventData.parseIn(rawData, updateEventDataValidator, bindingResult);
		if (bindingResult.hasErrors()) return RESTResourceResponseData.<Event>badResponse(bindingResult);

		Event targetEvent = eventRepo.findOne(updateEventData.getEventId());

		Event updatedResult = targetEvent.updateEventUsing(updateEventData, getUser(request), registrantRepo, categoryRepo, bindingResult);
		if (bindingResult.hasErrors()) return RESTResourceResponseData.<Event>badResponse(bindingResult);

		Event savedEventResult = this.eventRepo.save(updatedResult);

		return RESTResourceResponseData.createResponse(savedEventResult, HttpStatus.CREATED);
	}

	/**
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/rest/events/userJoined")
	public ResponseEntity<RESTPaginatedResourcesResponseData<Event>> getJoinedEventsList(HttpServletRequest request) {
		BindingResult errors = new BindException(this, "errors");
		if (! authenticatedRequest(request, errors)) return RESTPaginatedResourcesResponseData.<Event>badResponse(errors);
		List<Event> events = new ArrayList<Event>(getUser(request).getJoinedEvents());
		return RESTPaginatedResourcesResponseData.createResponse(request, events);
	}


	@RequestMapping(value = "/rest/events/userOwned")
	public ResponseEntity<RESTPaginatedResourcesResponseData<Event>> getOwnedEventsList(HttpServletRequest request){
		BindingResult errors = new BindException(this, "errors");
		if (! authenticatedRequest(request, errors)) return RESTPaginatedResourcesResponseData.<Event>badResponse(errors);
		List<Event> events = new ArrayList<Event>(getUser(request).getOwnedEvents());
		return RESTPaginatedResourcesResponseData.createResponse(request, events);
	}


	@RequestMapping(value = "/rest/events/join", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<RESTResourceResponseData<Event>> joinEvent(HttpServletRequest request, @RequestBody String rawData, BindingResult bindingResult) {
		if (! authenticatedRequest(request, bindingResult)) return RESTResourceResponseData.<Event>badResponse(bindingResult);

		EventsQueryData joinEventData = EventsQueryData.parseIn(rawData, eventIdDataValidator, bindingResult);
		if (bindingResult.hasErrors()) return RESTResourceResponseData.<Event>badResponse(bindingResult);

		Event eventToJoin = getUser(request).joinEvent(joinEventData, eventRepo, bindingResult);
		if (bindingResult.hasErrors()) return RESTResourceResponseData.<Event>badResponse(bindingResult);

		return RESTResourceResponseData.createResponse(eventToJoin, HttpStatus.CREATED);
	}


	@RequestMapping(value = "/rest/events/leave", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<RESTResourceResponseData<Event>> leaveEvent(HttpServletRequest request, @RequestBody String rawData, BindingResult bindingResult) {
		if (! authenticatedRequest(request, bindingResult)) return RESTResourceResponseData.<Event>badResponse(bindingResult);

		EventsQueryData leaveEventData = EventsQueryData.parseIn(rawData, eventIdDataValidator, bindingResult);
		if (bindingResult.hasErrors()) return RESTResourceResponseData.<Event>badResponse(bindingResult);

		Event eventToLeave = getUser(request).leaveEvent(leaveEventData, eventRepo, bindingResult);
		if (bindingResult.hasErrors()) return RESTResourceResponseData.<Event>badResponse(bindingResult);

		return RESTResourceResponseData.createResponse(eventToLeave, HttpStatus.OK);
	}


	@RequestMapping(value = "/rest/events/remove", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<RESTResourceResponseData<Event>> removeEvent(HttpServletRequest request, @RequestBody String rawData, BindingResult bindingResult) {
		if (! authenticatedRequest(request, bindingResult)) return RESTResourceResponseData.<Event>badResponse(bindingResult);

		EventsQueryData removeEventData = EventsQueryData.parseIn(rawData, eventIdDataValidator, bindingResult);
		if (bindingResult.hasErrors()) return RESTResourceResponseData.<Event>badResponse(bindingResult);

		Event removedEvent = getUser(request).removeEvent(removeEventData, eventRepo, bindingResult);
		if (bindingResult.hasErrors()) return RESTResourceResponseData.<Event>badResponse(bindingResult);

		return RESTResourceResponseData.createResponse(removedEvent, HttpStatus.OK);
	}
}
