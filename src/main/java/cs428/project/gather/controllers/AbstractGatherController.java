package cs428.project.gather.controllers;

import cs428.project.gather.data.model.*;
import cs428.project.gather.data.repo.*;
import cs428.project.gather.utilities.*;
import cs428.project.gather.validator.*;

import java.util.*;
import javax.servlet.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.*;
import org.springframework.http.*;

public abstract class AbstractGatherController {
	@Autowired
	protected RegistrantRepository registrantRepo;

	@Autowired
	protected EventRepository eventRepo;

	@Autowired
	protected CategoryRepository categoryRepo;

	@Autowired
	protected RegistrationDataValidator registrationDataValidator;

	@Autowired
	protected RegistrationUpdateDataValidator registrationUpdateDataValidator;

	@Autowired
	protected EventsQueryDataValidator eventsQueryDataValidator;

	@Autowired
	protected NewEventDataValidator newEventDataValidator;

	@Autowired
	protected EventIdDataValidator eventIdDataValidator;

	@Autowired
	protected SignInDataValidator signInDataValidator;

	protected boolean authenticatedRequest(HttpServletRequest request, Errors errors) {
		if (! ActorTypeHelper.isRegisteredUser(request)) {
			System.out.println("An anonymous user tried to access " + request.getServletPath());
			errors.reject("-7", "Incorrect User State. Only registered users can access " + request.getServletPath());
			return false;
		} return true;
	}

	protected boolean nonAuthenticatedRequest(HttpServletRequest request, Errors errors) {
		if (! ActorTypeHelper.isAnonymousUser(request)) {
			System.out.println("A non-anonymous user tried to access " + request.getServletPath());
			errors.reject("-7", "Incorrect User State. Only non-registered users can access " + request.getServletPath());
			return false;
		} return true;
	}

	protected Registrant getUser(HttpServletRequest request) {
		Actor actor = ActorStateUtility.retrieveActorFromRequest(request);
		return this.registrantRepo.findOne(actor.getActorID());
	}

	protected boolean isSessionAuthenticated(HttpServletRequest request) {
		return ActorStateUtility.retrieveAuthenticatedStateInRequest(request);
	}

	protected void invalidateSession(HttpServletRequest request, HttpServletResponse response) {
		SignOutHelper.invalidateSession(request);
		SignOutHelper.deleteSessionCookie(request, response);
	}
}
