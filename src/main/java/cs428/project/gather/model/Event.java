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
	private @Id @GeneratedValue Long id;
	private String description;
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "event_id")
	private Set<Occurrence> occurrences = new HashSet<Occurrence>();
	
	protected Event() {}
	public Event(String description) {
		Assert.hasText(description);
		this.setDescription(description);
	}
	
	public void add(Occurrence occurrence) {
		Assert.notNull(occurrence);
		this.occurrences.add(occurrence);
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
