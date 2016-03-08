package cs428.project.gather.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;

import cs428.project.gather.data.RESTResponseData;
import cs428.project.gather.utilities.ActorStateUtility;


@Controller("sessionController")
public class SessionController
{
	@RequestMapping(value="/api/session")
	public ResponseEntity<RESTResponseData> getSession(HttpServletRequest request, HttpServletResponse response)
	{
		if(ActorStateUtility.retrieveAuthenticatedStateInRequest(request)){
			return new ResponseEntity<RESTResponseData>(new RESTResponseData(5,"Session Found"),HttpStatus.FOUND);
		}else{
			return new ResponseEntity<RESTResponseData>(new RESTResponseData(-5,"Session Not Found"),HttpStatus.NOT_FOUND);
		}
	}
}
