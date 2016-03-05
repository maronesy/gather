package cs428.project.gather.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;

import cs428.project.gather.utilities.SignOutHelper;


@Controller("signOutController")
public class SignOutController
{
	@RequestMapping(value="/sign-out")
	public String signOut(HttpServletRequest request, HttpServletResponse response)
	{
		SignOutHelper.invalidateSession(request);

		SignOutHelper.deleteSessionCookie(request, response);

		return "sign-out-complete";
	}
}
