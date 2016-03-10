package cs428.project.gather.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

import cs428.project.gather.data.EventsData;
import cs428.project.gather.data.EventsResponseData;
import cs428.project.gather.data.RESTResponseData;
import cs428.project.gather.data.RegistrationData;
import cs428.project.gather.data.model.Event;
import cs428.project.gather.data.model.Registrant;
import cs428.project.gather.data.repo.EventRepository;
import cs428.project.gather.data.repo.RegistrantRepository;
import cs428.project.gather.utilities.ActorStateUtility;
import cs428.project.gather.utilities.ActorTypeHelper;
import cs428.project.gather.utilities.RedirectPathHelper;
import cs428.project.gather.validator.EventDataValidator;
import cs428.project.gather.validator.RegistrationDataValidator;

@Controller("eventController")
public class EventsController {

	@Autowired
	EventRepository eventRepo;

	@Autowired
	private EventDataValidator eventsDataValidator;
	
	@RequestMapping(value = "/api/events", method = RequestMethod.GET)
	public ResponseEntity<EventsResponseData> events(HttpServletRequest request, @RequestBody String rawData,
			BindingResult bindingResult) {

		Gson gson = new Gson();
		EventsData eventsData = gson.fromJson(rawData, EventsData.class);
		
		
		//List<Event> events = new ArrayList<Event>();
		List<Event> events = eventRepo.findEventsWithinKmRange(eventsData.getLatitude(), eventsData.getLongitude(), eventsData.getRadiusKm());;
		
		int results_per_page = 20;
		String maybe_page_num = request.getParameter("page");

		int page_num = (maybe_page_num == null) ? 1 : Integer.parseInt(maybe_page_num);
		int total_num_results   = events.size();
		int total_pages         = (total_num_results / results_per_page) + 1;

		String previous = (page_num <= 1) ? null : request.getRequestURL().toString() + "?page=" + (page_num-1);
		String next = (page_num >= total_pages) ? null : request.getRequestURL().toString() + "?page=" + (page_num+1);

		List<Event> results = events.subList((page_num-1)*results_per_page, page_num*results_per_page);
		
		
		return new ResponseEntity<EventsResponseData>(new EventsResponseData(previous, next, results), HttpStatus.OK);

	}


}
