package cs428.project.gather.model;


import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;
import lombok.Data;

@Entity
public class Registrant {
	private @Id @Column(name="ID") @GeneratedValue Long id;
	private String username;
	private String password;
	private String displayName;
	private String email;
	private long reliability ;
	private int defaultTimeWindow;
	private int defaultZip;
	private boolean isAdmin;

	@ManyToMany(mappedBy="subscribers")
	private Set<Event> subscribedEvents = new HashSet<Event>();
	
	@ManyToMany(mappedBy="owners")
	private Set<Event> ownedEvents = new HashSet<Event>();
	
	@ManyToMany(mappedBy="participants")
	private Set<Event> joinedEvents = new HashSet<Event>();
	
	@ManyToMany
	private Set<Category> preferences = new HashSet<Category>();
	
	private Registrant() {}

	public Registrant(String review, int rating, Timestamp datetime) {

	}
}
