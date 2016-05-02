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

/**
 * 
 * @author Team Gather
 * 
 * This is the Registrant class used for constructing registrant objects for users and their profile settings
 *
 */

@Entity
public class Registrant extends Actor {
	private @Column(nullable=false) String password;
	private @Column(unique = true, nullable=false) String displayName;
	private @Column(unique = true, nullable=false) String email;
	private @Column(nullable=false) int defaultTimeWindow = 1;
	private @Column(nullable=false) int defaultZip = 90210;
	private @Column(nullable=false) int defaultRadiusMi = 10;
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

	public int getDefaultRadiusMi() {
		return defaultRadiusMi;
	}

	public void setDefaultRadiusMi(int defaultRadiusMi) {
		this.defaultRadiusMi = defaultRadiusMi;
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

	/**
	 * @return This methods returns the user preference
	 */
	@JsonProperty("preferences")
	public List<String> getPreferencesList() {
		List<String> prefs = new ArrayList<String>();
		for (Category c : Collections.unmodifiableSet(preferences)) {
			prefs.add(c.getName());
		} return prefs;
	}

	/**
	 * 
	 * @param registrationData: information about the user
	 * @param categoryRepo: the category repository object
	 * @param errors: Used for reporting errors if the build registrant was unsuccessful
	 * @return the new Registrant
	 * 
	 */
	public static Registrant buildRegistrantFrom(RegistrationData registrationData, CategoryRepository categoryRepo, Errors errors) {
		return (new Registrant()).updateUsing(registrationData, categoryRepo, errors);
	}

	/**
	 * This method takes event and user info and add the user to the participant list of the event
	 * 
	 * @param joinEventData: Data passed in about the event desired to be joined
	 * @param eventRepo: The event repository object
	 * @param errors: Used for reporting errors if the join event was unsuccessful
	 * @return returns the event that was joined
	 * 
	 */
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
	
	/**
	 * This method removes an event from the database
	 * 
	 * @param removeEventData: Information about the event to be removed
	 * @param eventRepo: An event repository object
	 * @param errors: Used for reporting errors if the remove event was unsuccessful
	 * @return returns the events which was removed
	 * 
	 */
	public Event removeEvent(EventsQueryData removeEventData, EventRepository eventRepo, Errors errors) {
		Long eventId = removeEventData.getEventId();
		Event targetEvent = eventRepo.findOne(eventId);
		if (! targetEvent.containsOwner(this, errors)){
			return targetEvent;
		}

		eventRepo.delete(targetEvent);
		return targetEvent;
	}

	/**
	 * This method updates user info based on the passed in Registrant fields
	 * 
	 * @param updateInfo: Information about the registrant to be updates
	 * @param categoryRepo: a category object
	 * @param errors: Used for reporting errors if the update registrant was unsuccessful
	 * @return returns the updated registrant
	 * 
	 */
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

		if (updateInfo.getDefaultRadiusMi() > 0) {
			System.out.println("Setting defaultRadiusMi:  " + updateInfo.getDefaultRadiusMi());
			setDefaultRadiusMi(updateInfo.getDefaultRadiusMi());
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

	/**
	 * This method is used when the user want to leave an event
	 * 
	 * @param leaveEventData: Info about the event the user wants to leave
	 * @param eventRepo: An event repository object
	 * @param errors: Used for reporting errors if the user cannot leave event
	 * 
	 * @return returns the event that the user has left
	 * 
	 */
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

	/**
	 * This method validates the user the password, email, and display name before updating 
	 * 
	 * @param updateInfo: User information
	 * @param registrantRepo: a registrant repository object
	 * @param errors: Used for reporting errors if any of the fields is not valid
	 * @return returns true if all fields are validated
	 * 
	 */
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
