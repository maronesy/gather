package cs428.project.gather.data.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.springframework.util.Assert;

import lombok.Data;

@Data
@Entity
public class Event {
	private @Id @Column(name="ID") @GeneratedValue Long id;

	private String name;
	private String description;

	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
	private Location location;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "event_id")
	private List<Occurrence> occurrences = new ArrayList<Occurrence>();

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "event_id")
	private Set<Feedback> feedbacks = new HashSet<Feedback>();

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "event_id")
	private Set<ChangeLog> changeLog = new HashSet<ChangeLog>();

	@ManyToMany
	private Set<Registrant> subscribers = new HashSet<Registrant>();

	@ManyToMany
	private Set<Registrant> owners = new HashSet<Registrant>();

	@ManyToMany
	private Set<Registrant> participants = new HashSet<Registrant>();

	@ManyToOne(optional=false)
	private Category category;
	//private String category;

	public Event() {}

	public Event(String name) {
		setName(name);
	}

	// Setters and Getters

	public Long getId() {
		return id;
	}

	public void setLocation(Location location){
		this.location = location;
	}

	public boolean addOccurrence(Occurrence occurrence) {
		Assert.notNull(occurrence);
		return this.occurrences.add(occurrence);
	}

	public boolean addFeedback(Feedback feedback) {
		Assert.notNull(feedback);
		return this.feedbacks.add(feedback);
	}

	public boolean addChangeLog(ChangeLog changeLog){
		Assert.notNull(changeLog);
		return this.changeLog.add(changeLog);
	}

	public List<Occurrence> getOccurrences() {
		return Collections.unmodifiableList(occurrences);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		Assert.hasText(description);
		this.description = description;
	}

	public String getName(){
		return name;
	}

	public void setName(String name){
		Assert.hasText(name);
		this.name = name;
	}

	public Set<Feedback> getFeedbacks() {
		return Collections.unmodifiableSet(feedbacks);
	}

	public Set<ChangeLog> getChangeLog() {
		return Collections.unmodifiableSet(changeLog);
	}

	public Set<Registrant> getParticipants(){
		return Collections.unmodifiableSet(participants);
	}

	public boolean addParticipant(Registrant aUser){
		return participants.add(aUser);
	}

	public boolean addOwner(Registrant anOwner){
		return owners.add(anOwner);
	}
	public Location getLocation() {
		return this.location;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public Category getCategory(){
		return this.category;
	}

//	public String getCategory(){
//		return this.category;
//
//	}
//	
//	public void setCategory(String category){
//		this.category=category;
//
//	}
}
