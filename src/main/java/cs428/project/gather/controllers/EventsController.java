package cs428.project.gather.controllers;

import java.sql.Timestamp;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Type;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import com.google.gson.*;

import cs428.project.gather.data.*;
import cs428.project.gather.data.model.*;
import cs428.project.gather.data.repo.*;
import cs428.project.gather.utilities.*;
import cs428.project.gather.validator.*;

@Controller("eventController")
public class EventsController {
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

		EventsQueryData queryParams = EventsQueryData.parseIn(rawData, eventsQueryDataValidator, bindingResult);
		if (bindingResult.hasErrors()) {
			return RESTPaginatedResourcesResponseData.badResponse(bindingResult);
		}

		List<Event> events = Event.queryForEvents(queryParams, eventRepo);
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


	@RequestMapping(value = "/rest/events/update", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<RESTResourceResponseData<Event>> updateEvent(HttpServletRequest request,
			@RequestBody String rawData, BindingResult bindingResult) {
		// TODO: Wrap this in TryCatch, report exception to frontend.
		GsonBuilder builder = new GsonBuilder();

				// Register an adapter to manage the date types as long values
		builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
			@Override
			public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
				return new Date(json.getAsJsonPrimitive().getAsLong());
			}
		});
		Gson gson = builder.create();
		UpdateEventData updateEventData = gson.fromJson(rawData, UpdateEventData.class);

		if (!ActorTypeHelper.isRegisteredUser(request)) {
			System.out.println("An anonymous user tried to add an event.");
			bindingResult.reject("-7", "Incorrect User State. Only registered users can add events.");
			return RESTResourceResponseData.<Event> badResponse(bindingResult);
		}

		newEventDataValidator.validate(updateEventData, bindingResult);
		System.out.println("Validated: " + rawData);
		if (bindingResult.hasErrors()) {
			return RESTResourceResponseData.<Event> badResponse(bindingResult);
		}

		Actor actor = ActorStateUtility.retrieveActorFromRequest(request);
		Registrant owner = this.regRepo.findOne(actor.getActorID());
		Event updatedResult = performUpdateEvent(updateEventData, owner, bindingResult);
		if (bindingResult.hasErrors()) {
			return RESTResourceResponseData.<Event> badResponse(bindingResult);
		}

		Event savedEventResult = this.eventRepo.save(updatedResult);
		Coordinates callerLoc = updateEventData.getCallerCoodinates();
		Coordinates eventLoc = updateEventData.getEventCoodinates();
		double distanceFromCaller = GeodeticHelper.getDistanceBetweenCoordinates(callerLoc, eventLoc);
		System.out.println("DistanceFromCaller: " + distanceFromCaller);

		return RESTResourceResponseData.createResponse(savedEventResult, HttpStatus.CREATED);
	}

	private Event performUpdateEvent(UpdateEventData updateEventData, Registrant owner, Errors errors) {
		Event targetEvent = this.eventRepo.findOne(updateEventData.getEventId());

		if(!eventContainsOwner(targetEvent,owner,errors)){
			return targetEvent;
		}

		targetEvent.setName(updateEventData.getEventName());
		targetEvent.setDescription(updateEventData.getEventDescription());
		targetEvent.setLocation(new Location(updateEventData.getEventCoodinates()));
		if(!updateEventData.getOccurrencesToAdd().isEmpty()){
			for(int i=0;i<updateEventData.getOccurrencesToAdd().size();i++){
				if (!targetEvent.addOccurrence(updateEventData.getOccurrencesToAdd().get(i))){
					String message = "Cannot update event. Failed to add a coccurrence.";
					errors.reject("-7", message);
				}
			}
		}
		if(!updateEventData.getParticipantsToAdd().isEmpty()){
			for(int i=0;i<updateEventData.getParticipantsToAdd().size();i++){
				if (!targetEvent.addParticipant(updateEventData.getParticipantsToAdd().get(i))){
					String message = "Cannot update event. Failed to add a participant.";
					errors.reject("-7", message);
				}
			}
		}
		if(!updateEventData.getOwnersToAdd().isEmpty()){
			for(int i=0;i<updateEventData.getOwnersToAdd().size();i++){
				if (!targetEvent.addOwner(updateEventData.getOwnersToAdd().get(i))){
					String message = "Cannot update event. Failed to add an owner.";
					errors.reject("-7", message);
				}
			}
		}

		if(!updateEventData.getOccurrencesToRemove().isEmpty()){
			for(int i=0;i<updateEventData.getOccurrencesToRemove().size();i++){
				if (!targetEvent.removeOccurrence(updateEventData.getOccurrencesToRemove().get(i))){
					String message = "Cannot update event. Failed to remove a coccurrence.";
					errors.reject("-8", message);
				}
			}
		}
		if(!updateEventData.getParticipantsToRemove().isEmpty()){
			for(int i=0;i<updateEventData.getParticipantsToRemove().size();i++){
				if (!targetEvent.removeParticipant(updateEventData.getParticipantsToRemove().get(i))){
					String message = "Cannot update event. Failed to remove a participant.";
					errors.reject("-8", message);
				}
			}
		}
		if(!updateEventData.getOwnersToRemove().isEmpty()){
			for(int i=0;i<updateEventData.getOwnersToRemove().size();i++){
				if (!targetEvent.removeOwner(updateEventData.getOwnersToRemove().get(i))){
					String message = "Cannot update event. Failed to add an owner.";
					errors.reject("-8", message);
				}
			}
		}

		Category category = this.categoryRepo.findByName(updateEventData.getEventCategory()).get(0);
		targetEvent.setCategory(category);

		return targetEvent;
	}

	private boolean eventContainsOwner(Event targetEvent, Registrant owner, Errors errors) {
		if(!targetEvent.getOwners().contains(owner)){
			String message = "Cannot update event. The request Registrant is not the event owner.";
			errors.reject("-9", message);
			return false;
		}
		return true;
	}

	@RequestMapping(value = "/rest/events/userJoined")
	public ResponseEntity<RESTPaginatedResourcesResponseData<Event>> getJoinedEventsList(HttpServletRequest request, BindingResult bindingResult){

		if (!ActorTypeHelper.isRegisteredUser(request)) {
			System.out.println("An anonymous user tried to obtain their joined event list.");
			bindingResult.reject("-7", "Incorrect User State. Only registered users can request their joined event list.");
			return RESTPaginatedResourcesResponseData.badResponse(bindingResult);
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
			bindingResult.reject("-7", "Incorrect User State. Only registered users can request their joined event list.");
			return RESTPaginatedResourcesResponseData.badResponse(bindingResult);
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
		Registrant owner = this.regRepo.findOne(actor.getActorID());

		// Adding user to participant list
		Event joinedEvent = removeEvent(removeEventData, owner, bindingResult);
		if (bindingResult.hasErrors()) {
			return RESTResourceResponseData.<Event> badResponse(bindingResult);
		}

		return RESTResourceResponseData.createResponse(joinedEvent, HttpStatus.OK);
	}

	private Event removeEvent(EventIdData removeEventData, Registrant owner, Errors errors) {

		//TODO: add an error check in case event or participant are not found
		Long eventId = removeEventData.getEventId();
		Event targetEvent = eventRepo.findOne(eventId);
		if(!eventContainsOwner(targetEvent,owner,errors)){
			return targetEvent;
		}
		eventRepo.delete(targetEvent);

		return targetEvent;
	}
}
