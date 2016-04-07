package cs428.project.gather.data.model;

import cs428.project.gather.data.*;
import cs428.project.gather.data.repo.*;

import java.util.*;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import com.fasterxml.jackson.annotation.*;
import org.springframework.validation.Errors;

@Entity
public class Registrant extends Actor {
	private @Column(nullable=false) String password;
	private @Column(unique = true, nullable=false) String displayName;
	private @Column(unique = true, nullable=false) String email;
	private long reliability = 0;
	private int defaultTimeWindow = 1;
	private int defaultZip = 90210;

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

	public Registrant(String email, String password) {
		super(ActorType.REGISTERED_USER);
		this.email = email;
		this.password = password;
	}

	public Registrant(String email, String password, String displayName, long reliability,
			int defaultTimeWindow, int defaultZip) {
		super(ActorType.REGISTERED_USER);
		this.password = password;
		this.displayName = displayName;
		this.email = email;
		this.reliability = reliability;
		this.defaultTimeWindow = defaultTimeWindow;
		this.defaultZip = defaultZip;
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

	public long getReliability() {
		return reliability;
	}

	public void setReliability(long reliability) {
		this.reliability = reliability;
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

	public Event joinEvent(EventIdData joinEventData, EventRepository eventRepo, Errors errors) {
		Long eventId = joinEventData.getEventId();
		Event joinedEvent = eventRepo.findOne(eventId);
		joinedEvent.addParticipant(this);
		eventRepo.save(joinedEvent);
		return joinedEvent;
	}

	public Event removeEvent(EventIdData removeEventData, EventRepository eventRepo, Errors errors) {
		Long eventId = removeEventData.getEventId();
		Event targetEvent = eventRepo.findOne(eventId);
		if (! targetEvent.containsOwner(this, errors)){
			return targetEvent;
		}

		eventRepo.delete(targetEvent);
		return targetEvent;
	}

	public Registrant updateUsing(RegistrationData updateInfo, CategoryRepository categoryRepo, Errors errors) {
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
}
