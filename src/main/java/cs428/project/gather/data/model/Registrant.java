package cs428.project.gather.data.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Registrant extends Actor {
	private @Column(nullable=false) String password;
	private @Column(unique = true, nullable=false) String displayName;
	private @Column(unique = true, nullable=false) String email;
	private long reliability = 0;
	private int defaultTimeWindow = 1;
	private int defaultZip = 90210;

	@JsonIgnore
	@ManyToMany(mappedBy = "subscribers")
	private Set<Event> subscribedEvents = new HashSet<Event>();

	@JsonIgnore
	@ManyToMany(mappedBy = "owners")
	private Set<Event> ownedEvents = new HashSet<Event>();

	@JsonIgnore
	@ManyToMany(mappedBy = "participants")
	private Set<Event> joinedEvents = new HashSet<Event>();

	@ManyToMany
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
}
