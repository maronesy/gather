package cs428.project.gather.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

import cs428.project.gather.data.JoinEventData;
import cs428.project.gather.data.RESTResourceResponseData;
import cs428.project.gather.data.model.Actor;
import cs428.project.gather.data.model.Event;
import cs428.project.gather.data.model.Registrant;
import cs428.project.gather.data.repo.CategoryRepository;
import cs428.project.gather.data.repo.EventRepository;
import cs428.project.gather.data.repo.RegistrantRepository;
import cs428.project.gather.utilities.ActorStateUtility;
import cs428.project.gather.utilities.ActorTypeHelper;
import cs428.project.gather.validator.JoinEventDataValidator;

@Controller("joinController")
public class JoinController {

	@Autowired
	EventRepository eventRepo;

	@Autowired
	RegistrantRepository regRepo;

	@Autowired
	CategoryRepository categoryRepo;

	@Autowired
	private JoinEventDataValidator joinEventDataValidator;

	@RequestMapping(value = "/rest/join", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ResponseEntity<RESTResourceResponseData<Event>> joinEvent(HttpServletRequest request,
			@RequestBody String rawData, BindingResult bindingResult) {
		// TODO: Wrap this in TryCatch, report exception to frontend.
		JoinEventData joinEventData = (new Gson()).fromJson(rawData, JoinEventData.class);

		if (!ActorTypeHelper.isRegisteredUser(request)) {
			System.out.println("An anonymous user tried to add an event.");
			bindingResult.reject("-7", "Incorrect User State. Only registered users can add events.");
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

	private Event joinEvent(JoinEventData joinEventData, Registrant participant, Errors errors) {
		
		Long eventId = joinEventData.getEventId();
		Event joinedEvent = eventRepo.findOne(eventId);
		joinedEvent.addParticipant(participant);

		return joinedEvent;
	}
}
