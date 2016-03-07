package cs428.project.gather.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;

import cs428.project.gather.data.RESTResponseData;
import cs428.project.gather.utilities.SignOutHelper;


@Controller("signOutController")
public class SignOutController
{
	@RequestMapping(value="/api/sign-out")
	public ResponseEntity<RESTResponseData> signOut(HttpServletRequest request, HttpServletResponse response)
	{
		if(SignOutHelper.invalidateSession(request)
				&& SignOutHelper.deleteSessionCookie(request, response)){
			return new ResponseEntity<RESTResponseData>(new RESTResponseData(0,"success"),HttpStatus.OK); 
		};

		return new ResponseEntity<RESTResponseData>(new RESTResponseData(-1,"failed"),HttpStatus.BAD_REQUEST);
	}
}
