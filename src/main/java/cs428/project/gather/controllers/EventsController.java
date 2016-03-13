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
    private EventDataValidator eventsDataValidator;

    @RequestMapping(value = "/api/getevents", method = RequestMethod.GET)
    public ResponseEntity<PaginatedResponseData<Event>> events(HttpServletRequest request, @RequestBody String rawData, BindingResult bindingResult) {
        EventsQueryData eventsData = (new Gson()).fromJson(rawData, EventsQueryData.class);
        
//        List<Event> events = new ArrayList<Event>();
//       
//        Generate dummy events
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
   
        // DEPRECATED return new ResponseEntity<PaginatedResponseData<Event>>(PaginatedResponseData.create(request, events), HttpStatus.OK);
        return PaginatedResponseData.createResponse(request, events);
    }
    
//    @RequestMapping(value = "/api/add", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
//	@ResponseBody
//	public ResponseEntity<RESTResponseData> createEvent(HttpServletRequest request, @RequestBody String rawData,
//			BindingResult bindingResult) {
//
//		Gson gson = new Gson();
//		EventsQueryData eventData = gson.fromJson(rawData, EventsQueryData.class);
//
//		if (!ActorTypeHelper.isAnonymousUser(request)) {
//			eventsDataValidator.validate(eventData,bindingResult); 
//
//			if (bindingResult.hasErrors()) {
//				return RESTResponseData.responseBuilder(bindingResult);
//			} else {
//				
//				Event newEvent = buildEvent(eventData);
//
//				Event savedEventResult = this.eventRepo.save(newEvent);
//
//				return new ResponseEntity<RESTResponseData>(new RESTResponseData(0,"success"),HttpStatus.CREATED);
//			}
//		} else {
//			bindingResult.reject("-7","Incorrect User State. Only Registered User can register");
//			return RESTResponseData.responseBuilder(bindingResult);
//		}
//		
//	}
//
//	private Event buildEvent(EventsQueryData eventData) {
//		Event newEvent = new Event();
//		newEvent.setDescription(eventData.getDescription());
//		
//		return newEvent;
//	}
		
}
