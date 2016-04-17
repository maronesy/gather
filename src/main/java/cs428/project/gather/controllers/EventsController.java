package cs428.project.gather.controllers;

import cs428.project.gather.data.*;
import cs428.project.gather.data.form.*;
import cs428.project.gather.data.model.*;
import cs428.project.gather.utilities.*;

import java.sql.Timestamp;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.validation.*;
import org.springframework.web.bind.annotation.*;
import com.google.gson.*;

@Controller("EventsController")
public class EventsController extends AbstractGatherController {
	@RequestMapping(value = "/rest/events", method = RequestMethod.PUT)
	public ResponseEntity<RESTPaginatedResourcesResponseData<Event>> getNearbyEvents(HttpServletRequest request, @RequestBody String rawData, BindingResult bindingResult) {
		EventsQueryData queryParams = EventsQueryData.parseIn(rawData, eventsQueryDataValidator, bindingResult);
		if (bindingResult.hasErrors()) return RESTPaginatedResourcesResponseData.badResponse(bindingResult);

		List<Event> events = Event.queryForEvents(queryParams, eventRepo);
		return RESTPaginatedResourcesResponseData.createResponse(request, events);
	}

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

	@RequestMapping(value = "/rest/events/update", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<RESTResourceResponseData<Event>> updateEvent(HttpServletRequest request, @RequestBody String rawData, BindingResult bindingResult) {
		if (! authenticatedRequest(request, bindingResult)) return RESTResourceResponseData.<Event>badResponse(bindingResult);

		UpdateEventData updateEventData = UpdateEventData.parseIn(rawData, newEventDataValidator, bindingResult);
		if (bindingResult.hasErrors()) return RESTResourceResponseData.<Event>badResponse(bindingResult);

		Event updatedResult = Event.updateEventUsing(updateEventData, getUser(request), eventRepo, registrantRepo, categoryRepo, bindingResult);
		if (bindingResult.hasErrors()) return RESTResourceResponseData.<Event>badResponse(bindingResult);

		Event savedEventResult = this.eventRepo.save(updatedResult);
		System.out.println("DistanceFromCaller: " + updateEventData.distanceFromCaller());
		return RESTResourceResponseData.createResponse(savedEventResult, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/rest/events/userJoined")
	public ResponseEntity<RESTPaginatedResourcesResponseData<Event>> getJoinedEventsList(HttpServletRequest request) {
		if (!ActorTypeHelper.isRegisteredUser(request)) {
			return RESTPaginatedResourcesResponseData.badResponse("-7", "Incorrect User State. Only registered users can request their joined event list.");
		}
		List<Event> events = new ArrayList<Event>(getUser(request).getJoinedEvents());
		return RESTPaginatedResourcesResponseData.createResponse(request, events);
	}

	@RequestMapping(value = "/rest/events/userOwned")
	public ResponseEntity<RESTPaginatedResourcesResponseData<Event>> getOwnedEventsList(HttpServletRequest request){
		if (!ActorTypeHelper.isRegisteredUser(request)) {
			return RESTPaginatedResourcesResponseData.badResponse("-7", "Incorrect User State. Only registered users can request their owned event list.");
		}
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

		Event joinedEvent = getUser(request).removeEvent(removeEventData, eventRepo, bindingResult);
		if (bindingResult.hasErrors()) return RESTResourceResponseData.<Event>badResponse(bindingResult);

		return RESTResourceResponseData.createResponse(joinedEvent, HttpStatus.OK);
	}
}
