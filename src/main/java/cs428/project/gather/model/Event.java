package cs428.project.gather.model;



import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import org.springframework.util.Assert;

import lombok.Data;

@Data
@Entity
public class Event {
	private @Id @Column(name="ID") @GeneratedValue Long id;
	private String description;
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "event_id")
	private Set<Occurrence> occurrences = new HashSet<Occurrence>();
	
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
	
	public void add(Occurrence occurrence) {
		Assert.notNull(occurrence);
		this.occurrences.add(occurrence);
	}
	
	public void add(Feedback feedback) {
		Assert.notNull(feedback);
		this.feedbacks.add(feedback);
	}
	
	public Set<Occurrence> getOccurrences() {
		return Collections.unmodifiableSet(occurrences);
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
}
