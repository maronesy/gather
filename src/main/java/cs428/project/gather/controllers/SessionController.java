package cs428.project.gather.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;

import cs428.project.gather.data.RESTSessionResponseData;
import cs428.project.gather.data.model.Actor;
import cs428.project.gather.data.repo.RegistrantRepository;
import cs428.project.gather.utilities.ActorStateUtility;


@Controller("sessionController")
public class SessionController
{
	@Autowired
	private RegistrantRepository registrantDataAdapter;
	
	@RequestMapping(value="/rest/session")
	public ResponseEntity<RESTSessionResponseData> getSession(HttpServletRequest request, HttpServletResponse response)
	{
		if(ActorStateUtility.retrieveAuthenticatedStateInRequest(request)){
			Actor actor = ActorStateUtility.retrieveActorFromRequest(request);
			String displayName = this.registrantDataAdapter.findOne(actor.getActorID()).getDisplayName();
			return new ResponseEntity<RESTSessionResponseData>(new RESTSessionResponseData(5,"Session Found",displayName),HttpStatus.FOUND);
		}else{
			return new ResponseEntity<RESTSessionResponseData>(new RESTSessionResponseData(-5,"Session Not Found"),HttpStatus.NOT_FOUND);
		}
	}
}
