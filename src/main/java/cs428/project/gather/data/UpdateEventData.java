package cs428.project.gather.data;

import cs428.project.gather.data.model.*;

import java.util.*;
import java.lang.reflect.Type;
import com.google.gson.*;
import cs428.project.gather.validator.*;
import org.springframework.validation.Errors;

public class UpdateEventData extends NewEventData {
	private Long eventId;
	private List<Occurrence> occurrencesToAdd =  new ArrayList<Occurrence>();
	private List<Occurrence> occurrencesToRemove =  new ArrayList<Occurrence>();
	private List<Registrant> ownersToAdd = new ArrayList<Registrant>();
	private List<Registrant> ownersToRemove = new ArrayList<Registrant>();
	private List<Registrant> participantsToAdd = new ArrayList<Registrant>();
	private List<Registrant> participantsToRemove = new ArrayList<Registrant>();

	public static UpdateEventData parseIn(String rawData, AbstractValidator validator, Errors errors) {
		System.out.println("rawData: " + rawData);
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
			// Register an adapter to manage the date types as long values
			@Override
			public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
				return new Date(json.getAsJsonPrimitive().getAsLong());
			}
		});
		Gson gson = builder.create();
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

	public List<Registrant> getOwnersToAdd() {
		return ownersToAdd;
	}

	public List<Registrant> getOwnersToRemove() {
		return ownersToRemove;
	}

	public List<Registrant> getParticipantsToAdd() {
		return participantsToAdd;
	}

	public List<Registrant> getParticipantsToRemove() {
		return participantsToRemove;
	}

	public Long getEventId() {
		return eventId;
	}

	public void setEventId(Long id) {
		this.eventId = id;
	}
}
