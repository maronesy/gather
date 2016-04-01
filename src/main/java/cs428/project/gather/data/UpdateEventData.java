package cs428.project.gather.data;

import java.util.ArrayList;
import java.util.List;

import cs428.project.gather.data.model.Occurrence;
import cs428.project.gather.data.model.Registrant;

public class UpdateEventData extends NewEventData {
	private Long eventId;
	private List<Occurrence> occurrencesToAdd =  new ArrayList<Occurrence>();
	private List<Occurrence> occurrencesToRemove =  new ArrayList<Occurrence>();
	private List<Registrant> ownersToAdd = new ArrayList<Registrant>();
	private List<Registrant> ownersToRemove = new ArrayList<Registrant>();
	private List<Registrant> participantsToAdd = new ArrayList<Registrant>();
	private List<Registrant> participantsToRemove = new ArrayList<Registrant>();

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
	public Long getEventId()
	{
		return eventId;
	}

	public void setEventId(Long id)
	{
		this.eventId = id;
	}
}
