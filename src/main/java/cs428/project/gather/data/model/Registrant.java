package cs428.project.gather.data.model;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

@Entity
public class Registrant extends Actor {
	private @Id @Column(name = "ID") @GeneratedValue Long id;
	private String username;
	private String password;
	private String displayName;
	private String email;
	private long reliability;
	private int defaultTimeWindow;
	private int defaultZip;
	private boolean isAdmin;

	@ManyToMany(mappedBy = "subscribers")
	private Set<Event> subscribedEvents = new HashSet<Event>();

	@ManyToMany(mappedBy = "owners")
	private Set<Event> ownedEvents = new HashSet<Event>();

	@ManyToMany(mappedBy = "participants")
	private Set<Event> joinedEvents = new HashSet<Event>();

	@ManyToMany
	private Set<Category> preferences = new HashSet<Category>();

	public Registrant() {
        super(ActorType.REGISTERED_USER);
	}

	public Registrant(String username, String password) {
        super(ActorType.REGISTERED_USER);
		this.username = username;
		this.password = password;
	}

	public Registrant(String review, int rating, Timestamp datetime) {
        super(ActorType.REGISTERED_USER);
	}
}
