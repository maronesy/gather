package cs428.project.gather.data.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.springframework.util.Assert;

import lombok.Data;

@Data
@Entity
public class Event {
	private @Id @Column(name="ID") @GeneratedValue Long id;

	private String description;
	
	@OneToOne
	private Location location;
	
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
	
	@ManyToMany
	private Set<Category> categories = new HashSet<Category>();
	
	protected Event() {}
	public Event(String description) {
		Assert.hasText(description);
		this.setDescription(description);
	}
	
	public Long getId() {
		return id;
	}
	
//	public void addOccurrence(Occurrence occurrence) {
//		Assert.notNull(occurrence);
//		this.occurrences.add(occurrence);
//	}
	
	public void addFeedback(Feedback feedback) {
		Assert.notNull(feedback);
		this.feedbacks.add(feedback);
	}
	
	public void addChangeLog(ChangeLog changeLog){
		Assert.notNull(changeLog);
		this.changeLog.add(changeLog);
	}
	
//	public Set<Occurrence> getOccurrences() {
//		return Collections.unmodifiableSet(occurrences);
//	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
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
}
