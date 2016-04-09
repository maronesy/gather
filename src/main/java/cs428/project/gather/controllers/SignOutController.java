package cs428.project.gather.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cs428.project.gather.data.RESTResponseData;
import cs428.project.gather.utilities.ActorStateUtility;
import cs428.project.gather.utilities.SignOutHelper;


@Controller("SignOutController")
public class SignOutController {
	@RequestMapping(value="/rest/registrants/signout", method = RequestMethod.POST)
	public ResponseEntity<RESTResponseData> signOut(HttpServletRequest request, HttpServletResponse response) {
		boolean isAuthed = ActorStateUtility.retrieveAuthenticatedStateInRequest(request);
		SignOutHelper.invalidateSession(request);
		SignOutHelper.deleteSessionCookie(request, response);
		if (!isAuthed) {
			return new ResponseEntity<RESTResponseData>(new RESTResponseData(-7,"User is not in authenticated state"),HttpStatus.BAD_REQUEST);
		}else{
			return new ResponseEntity<RESTResponseData>(new RESTResponseData(0,"success"),HttpStatus.OK);
		}

	}
}
