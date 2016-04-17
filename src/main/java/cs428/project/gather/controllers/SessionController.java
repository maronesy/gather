package cs428.project.gather.controllers;

import cs428.project.gather.data.RESTSessionResponseData;
import cs428.project.gather.utilities.ActorStateUtility;

import javax.servlet.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.*;

@Controller("SessionController")
public class SessionController extends AbstractGatherController {
	@RequestMapping(value="/rest/session")
	public ResponseEntity<RESTSessionResponseData> getSession(HttpServletRequest request, HttpServletResponse response) {
		if (ActorStateUtility.retrieveAuthenticatedStateInRequest(request)) {
			return new ResponseEntity<RESTSessionResponseData>(new RESTSessionResponseData(5,"Session Found",getUser(request).getDisplayName()), HttpStatus.OK);
		} else {
			return new ResponseEntity<RESTSessionResponseData>(new RESTSessionResponseData(-5,"Session Not Found"), HttpStatus.OK);
		}
	}
}
