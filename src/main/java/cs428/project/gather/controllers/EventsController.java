package cs428.project.gather.controllers;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
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
    private EventsQueryDataValidator eventsQueryDataValidator;

    @Autowired
    private NewEventDataValidator newEventDataValidator;

    @RequestMapping(value = "/rest/events", method = RequestMethod.GET)
    public ResponseEntity<PaginatedResponseData<Event>> getNearbyEvents(HttpServletRequest request, @RequestBody String rawData, BindingResult bindingResult) {
        EventsQueryData eventsData = (new Gson()).fromJson(rawData, EventsQueryData.class);

//        List<Event> events = new ArrayList<Event>();
//        // Generate dummy events
//        for (int i=0; i < 21; i++) {
//            events.add(  new Event("event #" + Integer.toString(i))  );
//        }

		// // Calculate the upper and lower latitude bounds.
		double latitudeRadiusAdjustment = ONE_MILE_IN_DEGREES_LATITUDE * eventsData.getRadiusMi();
		Double latitudeLowerBound = new Double(eventsData.getLatitude() - latitudeRadiusAdjustment);
		Double latitudeUpperBound = new Double(eventsData.getLatitude() + latitudeRadiusAdjustment);

		// Calculate the upper and lower longitude bounds.
		double longitudeRadiusAdjustment = ONE_MILE_IN_DEGREES_LONGITUDE * eventsData.getRadiusMi();
		Double longitudeLowerBound = new Double(eventsData.getLongitude() - longitudeRadiusAdjustment);
		Double longitudeUpperBound = new Double(eventsData.getLongitude()  + longitudeRadiusAdjustment);

		//TODO: fix time stamp when new changes are ready
		Timestamp timeWindow = Timestamp.valueOf("2016-04-13 10:10:10.0");
		//DateTime dt = new DateTime().now().plusHours(hoursFromData);


         List<Event> events = eventRepo.findByLocationAndOccurrenceTimeWithin(latitudeLowerBound, latitudeUpperBound, longitudeLowerBound, longitudeUpperBound, timeWindow);
        //List<Event> events = eventRepo.findByLocationWithinKmRadius(eventsData.getLatitude(), eventsData.getLongitude(), eventsData.getRadiusMi());
        return PaginatedResponseData.createResponse(request, events);
    }

    @RequestMapping(value = "/rest/events", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<RESTResourceResponseData<Event>> addEvent(HttpServletRequest request, @RequestBody String rawData,
			BindingResult bindingResult) {
    	System.out.println("rawData: " + rawData);
    	
		Gson gson = new Gson();
		//TODO: Wrap this in TryCatch, report exception to frontend.
		NewEventData newEventData  = gson.fromJson(rawData, NewEventData.class);

		if (ActorTypeHelper.isRegisteredUser(request)) {
			newEventDataValidator.validate(newEventData,bindingResult);
			System.out.println("Validated: " + rawData);

			if (bindingResult.hasErrors()) {
                return RESTResourceResponseData.<Event>badResponse(bindingResult);
			} else {

				Actor actor = ActorStateUtility.retrieveActorFromRequest(request);
				Registrant owner = this.regRepo.findOne(actor.getActorID());
				Event newEvent = buildEvent(newEventData, owner);

				Event savedEventResult = this.eventRepo.save(newEvent);

				Coordinates callerLoc=newEventData.getCallerCoodinates();
				Coordinates eventLoc=newEventData.getEventCoodinates();
				double distanceFromCaller = GeodeticHelper.getDistanceBetweenCoordinates(callerLoc, eventLoc);

				System.out.println("DistanceFromCaller: " + distanceFromCaller);

				return new ResponseEntity<RESTResourceResponseData<Event>>(new RESTResourceResponseData(0, savedEventResult), HttpStatus.CREATED);
			}
		} else {
			System.out.println("An anonymous user tried to add an event.");
			bindingResult.reject("-7","Incorrect User State. Only registered users can add events.");
            return RESTResourceResponseData.<Event>badResponse(bindingResult);
		}
	}

	private Event buildEvent(NewEventData newEventData, Registrant owner) {
		Event newEvent = new Event(newEventData.getEventName());
		newEvent.setDescription(newEventData.getEventDescription());
		newEvent.setLocation(new Location(newEventData.getEventCoodinates()));

		if(!newEvent.addParticipant(owner)){
			//TODO: Error, unable to add participant
		}

		if(!newEvent.addOwner(owner)){
			//TODO: Error, unable to add owner
		}

		Occurrence occurrence = new Occurrence("",new Timestamp(newEventData.getEventTime()));
		if(!newEvent.addOccurrence(occurrence)){
			//TODO: Error, unable to add occurrence
		}

		//TODO: Figure out categories, set up ENUM?
		//Category category = new Category(newEventData.getEventCategory(),"");
		newEvent.setCategory(newEventData.getEventCategory());

		return newEvent;
	}
}
