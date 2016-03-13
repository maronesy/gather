package cs428.project.gather.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cs428.project.gather.data.Coordinates;
import cs428.project.gather.data.NewEventData;
import cs428.project.gather.utilities.GeodeticHelper;

import com.google.gson.Gson;

@Controller("newEventController")
public class MockNewEventController {

	@RequestMapping(value = "/api/new-event/add", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public String signInProcessor(HttpServletRequest request, @RequestBody String rawData,
			BindingResult bindingResult) {

		Gson gson = new Gson();
		System.out.println(rawData);
		NewEventData eventData  = gson.fromJson(rawData, NewEventData.class);
		Coordinates callerLoc=eventData.getCallerCoodinates();
		Coordinates eventLoc=eventData.getEventCoodinates();

		
		double distanceFromCaller = GeodeticHelper.getDistanceBetweenCoordinates(callerLoc, eventLoc);
		return "{\"eventName\":\""+eventData.getEventName()+
				"\",\"eventCategory\":\""+eventData.getEventCategory()+
				"\",\"eventDescription\":\""+eventData.getEventDescription()+
				"\",\"eventTime\":\""+eventData.getEventTime()+
				"\",\"distanceFromCaller\":"+Double.toString(distanceFromCaller)+
				",\"coordinates\":"+"{\"latitude\":"+Double.toString(eventLoc.getLatitude())+
				",\"longitude\":"+Double.toString(eventLoc.getLongitude())+"}"+
				"}";
		
	}


}
