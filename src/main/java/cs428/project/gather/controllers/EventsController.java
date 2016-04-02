package cs428.project.gather.controllers;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import com.google.gson.Gson;

import cs428.project.gather.data.*;
import cs428.project.gather.data.model.*;
import cs428.project.gather.data.repo.*;
import cs428.project.gather.utilities.*;
import cs428.project.gather.validator.*;

@Controller("eventController")
public class EventsController {

	private static final double ONE_MILE_IN_DEGREES_LATITUDE = 0.014554;
	private static final double ONE_MILE_IN_DEGREES_LONGITUDE = 0.014457;

	@Autowired
	EventRepository eventRepo;

	@Autowired
	RegistrantRepository regRepo;

	@Autowired
	CategoryRepository categoryRepo;

	@Autowired
	private EventsQueryDataValidator eventsQueryDataValidator;

	@Autowired
	private NewEventDataValidator newEventDataValidator;
	
	@Autowired
	private JoinEventDataValidator joinEventDataValidator;

	@RequestMapping(value = "/rest/events", method = RequestMethod.PUT)
	public ResponseEntity<RESTPaginatedResourcesResponseData<Event>> getNearbyEvents(HttpServletRequest request,
			@RequestBody String rawData, BindingResult bindingResult) {
		System.out.println("rawData: " + rawData);
		EventsQueryData eventsData = (new Gson()).fromJson(rawData, EventsQueryData.class);

		eventsQueryDataValidator.validate(eventsData, bindingResult);

		// List<Event> events = new ArrayList<Event>();
		// // Generate dummy events
		// for (int i=0; i < 21; i++) {
		// events.add( new Event("event #" + Integer.toString(i)) );
		// }

		if (bindingResult.hasErrors()) {
			return RESTPaginatedResourcesResponseData.badResponse(bindingResult);
		}

		// // Calculate the upper and lower latitude bounds.
		double latitudeRadiusAdjustment = ONE_MILE_IN_DEGREES_LATITUDE * eventsData.getRadiusMi();
		Double latitudeLowerBound = new Double(eventsData.getLatitude() - latitudeRadiusAdjustment);
		Double latitudeUpperBound = new Double(eventsData.getLatitude() + latitudeRadiusAdjustment);

		// Calculate the upper and lower longitude bounds.
		double longitudeRadiusAdjustment = ONE_MILE_IN_DEGREES_LONGITUDE * eventsData.getRadiusMi();
		Double longitudeLowerBound = new Double(eventsData.getLongitude() - longitudeRadiusAdjustment);
		Double longitudeUpperBound = new Double(eventsData.getLongitude() + longitudeRadiusAdjustment);

		Timestamp timeWindow;
		if (eventsData.getHour() == -1) {
			// All events this year
			timeWindow = new Timestamp(DateTime.now().plusYears(1).getMillis());
		} else {
			// Events in the next <eventsData.getHour()> hours
			timeWindow = new Timestamp(DateTime.now().plusHours(eventsData.getHour()).getMillis());
		}

		List<Event> events = eventRepo.findByLocationAndOccurrenceTimeWithin(latitudeLowerBound, latitudeUpperBound,
				longitudeLowerBound, longitudeUpperBound, timeWindow);
		// List<Event> events =
		// eventRepo.findByLocationWithinKmRadius(eventsData.getLatitude(),
		// eventsData.getLongitude(), eventsData.getRadiusMi());
		return RESTPaginatedResourcesResponseData.createResponse(request, events);
	}

	@RequestMapping(value = "/rest/events", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<RESTResourceResponseData<Event>> addEvent(HttpServletRequest request,
			@RequestBody String rawData, BindingResult bindingResult) {
		// TODO: Wrap this in TryCatch, report exception to frontend.
		NewEventData newEventData = (new Gson()).fromJson(rawData, NewEventData.class);

		if (!ActorTypeHelper.isRegisteredUser(request)) {
			System.out.println("An anonymous user tried to add an event.");
			bindingResult.reject("-7", "Incorrect User State. Only registered users can add events.");
			return RESTResourceResponseData.<Event> badResponse(bindingResult);
		}

		newEventDataValidator.validate(newEventData, bindingResult);
		System.out.println("Validated: " + rawData);
		if (bindingResult.hasErrors()) {
			return RESTResourceResponseData.<Event> badResponse(bindingResult);
		}

		Actor actor = ActorStateUtility.retrieveActorFromRequest(request);
		Registrant owner = this.regRepo.findOne(actor.getActorID());
		Event newEvent = buildEvent(newEventData, owner, bindingResult);
		if (bindingResult.hasErrors()) {
			return RESTResourceResponseData.<Event> badResponse(bindingResult);
		}

		Event savedEventResult = this.eventRepo.save(newEvent);
		Coordinates callerLoc = newEventData.getCallerCoodinates();
		Coordinates eventLoc = newEventData.getEventCoodinates();
		double distanceFromCaller = GeodeticHelper.getDistanceBetweenCoordinates(callerLoc, eventLoc);
		System.out.println("DistanceFromCaller: " + distanceFromCaller);

		return RESTResourceResponseData.createResponse(savedEventResult, HttpStatus.CREATED);
	}

	private Event buildEvent(NewEventData newEventData, Registrant owner, Errors errors) {
		Event newEvent = new Event(newEventData.getEventName());
		newEvent.setDescription(newEventData.getEventDescription());
		newEvent.setLocation(new Location(newEventData.getEventCoodinates()));

		if (!newEvent.addParticipant(owner)) {
			String message = "Cannot create event. Failed to add creator as participant.";
			errors.reject("-7", message);
		}

		if (!newEvent.addOwner(owner)) {
			String message = "Cannot create event. Failed to add creator as owner.";
			errors.reject("-7", message);
		}

		Occurrence occurrence = new Occurrence("", new Timestamp(newEventData.getEventTime()));
		if (!newEvent.addOccurrence(occurrence)) {
			String message = "Cannot create event. Failed to add first occurrence to event.";
			errors.reject("-7", message);
		}

		Category category = this.categoryRepo.findByName(newEventData.getEventCategory()).get(0);
		newEvent.setCategory(category);

		return newEvent;
	}
	
	@RequestMapping(value = "/rest/events/userJoined")
	public ResponseEntity<RESTPaginatedResourcesResponseData<Event>> getJoinedEventsList(HttpServletRequest request){ 
		
		if (!ActorTypeHelper.isRegisteredUser(request)) {
			System.out.println("An anonymous user tried to obtain their joined event list.");
			return RESTPaginatedResourcesResponseData.badResponse("-7", "Incorrect User State. Only registered users can request their joined event list.");
		}
		
		Actor actor = ActorStateUtility.retrieveActorFromRequest(request);
		Registrant user = this.regRepo.findOne(actor.getActorID());
		
		List<Event> events = new ArrayList<Event>(user.getJoinedEvents());
		return RESTPaginatedResourcesResponseData.createResponse(request, events);
	}
	
	@RequestMapping(value = "/rest/events/userOwned")
	public ResponseEntity<RESTPaginatedResourcesResponseData<Event>> getOwnedEventsList(HttpServletRequest request, BindingResult bindingResult){
		
		if (!ActorTypeHelper.isRegisteredUser(request)) {
			System.out.println("An anonymous user tried to obtain their joined event list.");
			return RESTPaginatedResourcesResponseData.badResponse("-7", "Incorrect User State. Only registered users can request their owned event list.");
		}
		
		Actor actor = ActorStateUtility.retrieveActorFromRequest(request);
		Registrant user = this.regRepo.findOne(actor.getActorID());
		
		List<Event> events = new ArrayList<Event>(user.getOwnedEvents());
		return RESTPaginatedResourcesResponseData.createResponse(request, events);
	}

	@RequestMapping(value = "/rest/events/join", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<RESTResourceResponseData<Event>> joinEvent(HttpServletRequest request,
			@RequestBody String rawData, BindingResult bindingResult) {
		System.out.println("Validated: " + rawData);
		// TODO: Wrap this in TryCatch, report exception to frontend.
		EventIdData joinEventData = (new Gson()).fromJson(rawData, EventIdData.class);

		if (!ActorTypeHelper.isRegisteredUser(request)) {
			System.out.println("An anonymous user tried to add an event.");
			bindingResult.reject("-7", "Incorrect User State. Only registered users can join events.");
			return RESTResourceResponseData.<Event> badResponse(bindingResult);
		}
		
		
		// validating passed join data
		joinEventDataValidator.validate(joinEventData, bindingResult);
		
		System.out.println("Validated: " + rawData);
		
		if (bindingResult.hasErrors()) {
			return RESTResourceResponseData.<Event> badResponse(bindingResult);
		}

		Actor actor = ActorStateUtility.retrieveActorFromRequest(request);
		Registrant participant = this.regRepo.findOne(actor.getActorID());
		
		// Adding user to participant list
		Event joinedEvent = joinEvent(joinEventData, participant, bindingResult);
		if (bindingResult.hasErrors()) {
			return RESTResourceResponseData.<Event> badResponse(bindingResult);
		}

		return RESTResourceResponseData.createResponse(joinedEvent, HttpStatus.CREATED);
	}

	private Event joinEvent(EventIdData joinEventData, Registrant participant, Errors errors) {
		
		//TODO: add an error check in case event or participant are not found
		Long eventId = joinEventData.getEventId();
		Event joinedEvent = eventRepo.findOne(eventId);
		joinedEvent.addParticipant(participant);
		eventRepo.save(joinedEvent);

		return joinedEvent;
	}
	
	@RequestMapping(value = "/rest/events/remove", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<RESTResourceResponseData<Event>> removeEvent(HttpServletRequest request,
			@RequestBody String rawData, BindingResult bindingResult) {
		System.out.println("Validated: " + rawData);
		// TODO: Wrap this in TryCatch, report exception to frontend.
		EventIdData removeEventData = (new Gson()).fromJson(rawData, EventIdData.class);

		if (!ActorTypeHelper.isRegisteredUser(request)) {
			System.out.println("An anonymous user tried to add an event.");
			bindingResult.reject("-7", "Incorrect User State. Only registered users can remove events.");
			return RESTResourceResponseData.<Event> badResponse(bindingResult);
		}
		
		
		// validating passed join data
		joinEventDataValidator.validate(removeEventData, bindingResult);
		
		System.out.println("Validated: " + rawData);
		
		if (bindingResult.hasErrors()) {
			return RESTResourceResponseData.<Event> badResponse(bindingResult);
		}

		Actor actor = ActorStateUtility.retrieveActorFromRequest(request);
		Registrant participant = this.regRepo.findOne(actor.getActorID());
		
		// Adding user to participant list
		Event joinedEvent = removeEvent(removeEventData, participant, bindingResult);
		if (bindingResult.hasErrors()) {
			return RESTResourceResponseData.<Event> badResponse(bindingResult);
		}

		return RESTResourceResponseData.createResponse(joinedEvent, HttpStatus.OK);
	}

	private Event removeEvent(EventIdData removeEventData, Registrant participant, Errors errors) {
		
		//TODO: add an error check in case event or participant are not found
		Long eventId = removeEventData.getEventId();
		Event targetEvent = eventRepo.findOne(eventId);
		eventRepo.delete(targetEvent);
//		for (Registrant user : targetEvent.getParticipants()) {
//		     user.removeJoinedEvent(targetEvent);
//		}
//		for (Registrant user : targetEvent.getOnwers()) {
//		     user.removeOwnedEvent(targetEvent);
//		}
//		for (Registrant user : targetEvent.getSubscribers()) {
//		     user.removeSubscribedEvent(targetEvent);
//		}

		return targetEvent;
	}
}
