package cs428.project.gather.data.model;

import cs428.project.gather.data.form.*;
import cs428.project.gather.data.repo.*;

import java.util.*;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import com.fasterxml.jackson.annotation.*;

import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;

@Entity
public class Registrant extends Actor {
	private @Column(nullable=false) String password;
	private @Column(unique = true, nullable=false) String displayName;
	private @Column(unique = true, nullable=false) String email;
	private int defaultTimeWindow = 1;
	private int defaultZip = 90210;
	private @Column(nullable=false) boolean showEventsAroundZipCode = false;

	@JsonIgnore
	@ManyToMany(mappedBy = "subscribers", fetch = FetchType.EAGER)
	private Set<Event> subscribedEvents = new HashSet<Event>();

	@JsonIgnore
	@ManyToMany(mappedBy = "owners", fetch = FetchType.EAGER)
	private Set<Event> ownedEvents = new HashSet<Event>();

	@JsonIgnore
	@ManyToMany(mappedBy = "participants", fetch = FetchType.EAGER)
	private Set<Event> joinedEvents = new HashSet<Event>();

	@ManyToMany(fetch = FetchType.EAGER)
	private Set<Category> preferences = new HashSet<Category>();

	public Registrant() {
		super(ActorType.REGISTERED_USER);
	}

	public Registrant(String email, String password, String displayName, int defaultTimeWindow,
			int defaultZip) {
		super(ActorType.REGISTERED_USER);
		this.password = password;
		this.displayName = displayName;
		this.email = email;
		this.defaultTimeWindow = defaultTimeWindow;
		this.defaultZip = defaultZip;
	}

	public boolean equals(Registrant otherUser) {
		return (this.displayName.equals(otherUser.getDisplayName()) && this.email.equals(otherUser.getEmail()));
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@JsonIgnore
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public int getDefaultTimeWindow() {
		return defaultTimeWindow;
	}

	public void setDefaultTimeWindow(int defaultTimeWindow) {
		this.defaultTimeWindow = defaultTimeWindow;
	}

	public int getDefaultZip() {
		return defaultZip;
	}

	public void setDefaultZip(int defaultZip) {
		this.defaultZip = defaultZip;
	}

	public boolean getShowEventsAroundZipCode() {
		return showEventsAroundZipCode;
	}

	public void setShowEventsAroundZipCode(boolean showEventsAroundZipCode) {
		this.showEventsAroundZipCode = showEventsAroundZipCode;
	}

	public boolean joinEvent(Event event) {
		return joinedEvents.add(event);
	}

	@JsonIgnore
	public Set<Event> getJoinedEvents() {
		return Collections.unmodifiableSet(joinedEvents);
	}

	@JsonIgnore
	public Set<Event> getOwnedEvents() {
		return Collections.unmodifiableSet(ownedEvents);
	}

	@JsonIgnore
	public void setPreferences(Set<Category> preferences) {
		this.preferences = preferences;
	}

	@JsonIgnore
	public Set<Category> getPreferences() {
		return Collections.unmodifiableSet(preferences);
	}

	@JsonProperty("preferences")
	public List<String> getPreferencesList() {
		List<String> prefs = new ArrayList<String>();
		for (Category c : Collections.unmodifiableSet(preferences)) {
			prefs.add(c.getName());
		} return prefs;
	}

	public static Registrant buildRegistrantFrom(RegistrationData registrationData, CategoryRepository categoryRepo, Errors errors) {
		return (new Registrant()).updateUsing(registrationData, categoryRepo, errors);
	}

	public Event joinEvent(EventsQueryData joinEventData, EventRepository eventRepo, Errors errors) {
		Long eventId = joinEventData.getEventId();
		Event eventToJoin = eventRepo.findOne(eventId);
		if(eventToJoin == null){
			errors.reject("-5", "Event not found. Perhaps the event was removed by the owner.");
			return null;
		}
		if(!eventToJoin.addParticipant(this)){
			errors.reject("-8", "Server error. Failed to join event.");
			return null;
		}
		eventRepo.save(eventToJoin);
		return eventToJoin;
	}

	public Event removeEvent(EventsQueryData removeEventData, EventRepository eventRepo, Errors errors) {
		Long eventId = removeEventData.getEventId();
		Event targetEvent = eventRepo.findOne(eventId);
		if (! targetEvent.containsOwner(this, errors)){
			return targetEvent;
		}

		eventRepo.delete(targetEvent);
		return targetEvent;
	}

	public Registrant updateUsing(RegistrationData updateInfo, CategoryRepository categoryRepo, Errors errors) {
		if (updateInfo.getShowEventsAroundZipCode() != null) {
			setShowEventsAroundZipCode(updateInfo.getShowEventsAroundZipCode());
		}

		if (updateInfo.getEmail() != null) {
			System.out.println("Setting email:  " + updateInfo.getEmail());
			setEmail(updateInfo.getEmail());
		}

		if (updateInfo.getPassword() != null) {
			System.out.println("Setting password:  " + updateInfo.getPassword());
			setPassword(updateInfo.getPassword());
		}

		if (updateInfo.getDisplayName() != null) {
			System.out.println("Setting displayName:  " + updateInfo.getDisplayName());
			setDisplayName(updateInfo.getDisplayName());
		}

		if (updateInfo.getDefaultTimeWindow() > 0) {
			System.out.println("Setting defaultTimeWindow:  " + updateInfo.getDefaultTimeWindow());
			setDefaultTimeWindow(updateInfo.getDefaultTimeWindow());
		}

		if (updateInfo.getDefaultZip() > 0) {
			System.out.println("Setting defaultZip:  " + updateInfo.getDefaultZip());
			setDefaultZip(updateInfo.getDefaultZip());
		}

		if (updateInfo.getPreferences() != null) {
			Set<Category> newCategories = new HashSet<Category>();
			for (String categoryName : updateInfo.getPreferences()) {
				Category foundCategory = categoryRepo.findOneByName(categoryName);
				if (foundCategory == null) {
					errors.reject("-3", "Category '" + categoryName + "' does not exist.");
				} else {
					newCategories.add(foundCategory);
				}
			}
			setPreferences(newCategories);
		}

		return this;
	}

	public Event leaveEvent(EventsQueryData leaveEventData, EventRepository eventRepo, Errors errors) {
		Long eventId = leaveEventData.getEventId();
		Event eventToLeave = eventRepo.findOne(eventId);
		if(eventToLeave == null){
			errors.reject("-5", "Event not found. Perhaps the event was removed by the owner.");
			return null;
		}
		Set<Registrant> owners = eventToLeave.getOwners();
		if(owners.contains(this)){
			if(owners.size() < 2){
				errors.reject("-3", "Cannot leave event. You are the sole owner. Add a co-owner or remove the event.");
				return null;
			}
			else{
				eventToLeave.removeOwner(this);
			}
		}
		eventToLeave.removeParticipant(this);
		eventRepo.save(eventToLeave);
		return eventToLeave;
	}

	public boolean validateUserDependentFields(RegistrationData updateInfo, RegistrantRepository registrantRepo, Errors errors) {
		if (updateInfo.getPassword() != null && ! updateInfo.getOldPassword().equals(getPassword())) {
			errors.reject("-3", "The old password for confirmation is incorrect; cannot update to new password.");
			return false;
		}

		// We can only claim this email if there is no OTHER user that also has this email already
		if (updateInfo.getEmail() != null) {
			Registrant otherUser = registrantRepo.findOneByEmail(updateInfo.getEmail());
			if (otherUser != null && ! otherUser.equals(this)) {
				String message = "Field invalid-" + RegistrationData.EMAIL_FIELD_NAME;
				errors.reject("-4", message+":The email address already exists and claimed by another user.  Please enter another email address.");
				return false;
			}
		}

		// We can only claim this displayName if there is no OTHER user that also has this displayName already
		if (updateInfo.getDisplayName() != null) {
			Registrant otherUser = registrantRepo.findByDisplayName(updateInfo.getDisplayName());
			if (otherUser!=null) {
				String message = "Field invalid-" + RegistrationData.DISPLAY_NAME_FIELD_NAME;
				errors.reject("-4",message+":The display name already exists and claimed by another user.  Please enter another display name.");
				return false;
			}
		}

		return true;
	}
}
