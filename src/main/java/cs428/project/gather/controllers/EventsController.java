package cs428.project.gather.controllers;

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
    @Autowired
    EventRepository eventRepo;

    @Autowired
    private EventDataValidator eventsDataValidator;

    @RequestMapping(value = "/api/events", method = RequestMethod.GET)
    public ResponseEntity<PaginatedResponseData<Event>> events(HttpServletRequest request, @RequestBody String rawData, BindingResult bindingResult) {
        EventsData eventsData = (new Gson()).fromJson(rawData, EventsData.class);

        List<Event> events = new ArrayList<Event>();
        /*
        // Generate dummy events
        for (int i=0; i < 33; i++) {
            events.add(  new Event("event #" + Integer.toString(i))  );
        }
        */

        // List<Event> events = eventRepo.findEventsWithinKmRange(eventsData.getLatitude(), eventsData.getLongitude(), eventsData.getRadiusKm());;

        return new ResponseEntity<PaginatedResponseData<Event>>(PaginatedResponseData.create(request, events), HttpStatus.OK);
    }
}
