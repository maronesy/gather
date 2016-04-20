package cs428.project.gather.data.form;

import cs428.project.gather.data.model.*;
import cs428.project.gather.utilities.GsonHelper;
import cs428.project.gather.validator.*;

import java.util.*;
import com.google.gson.*;
import org.springframework.validation.Errors;

public class UpdateEventData extends NewEventData {
	private Long eventId;
	private List<Occurrence> occurrencesToAdd =  new ArrayList<Occurrence>();
	private List<Occurrence> occurrencesToRemove =  new ArrayList<Occurrence>();
	private List<String> ownersToAdd = new ArrayList<String>();
	private List<String> ownersToRemove = new ArrayList<String>();
	private List<String> participantsToAdd = new ArrayList<String>();
	private List<String> participantsToRemove = new ArrayList<String>();

	public static UpdateEventData parseIn(String rawData, AbstractValidator validator, Errors errors) {
		System.out.println("rawData: " + rawData);
		Gson gson = GsonHelper.getGson();
		UpdateEventData updateEventData = gson.fromJson(rawData, UpdateEventData.class);
		updateEventData.validate(validator, errors);
		return updateEventData;
	}

	public void validate(AbstractValidator validator, Errors errors) {
		validator.validate(this, errors);
	}

	public List<Occurrence> getOccurrencesToAdd() {
		return occurrencesToAdd;
	}

	public List<Occurrence> getOccurrencesToRemove() {
		return occurrencesToRemove;
	}

	public List<String> getOwnersToAdd() {
		return ownersToAdd;
	}

	public List<String> getOwnersToRemove() {
		return ownersToRemove;
	}

	public List<String> getParticipantsToAdd() {
		return participantsToAdd;
	}

	public List<String> getParticipantsToRemove() {
		return participantsToRemove;
	}

	public Long getEventId() {
		return eventId;
	}

	public void setEventId(Long id) {
		this.eventId = id;
	}
}
