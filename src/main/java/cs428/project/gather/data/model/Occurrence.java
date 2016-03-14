package cs428.project.gather.data.model;


import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.springframework.util.Assert;

@Entity
public class Occurrence {
	private @Id @GeneratedValue Long id;
	private String description;
	private Timestamp timestamp;

	private Occurrence() {}

	public Occurrence(String description, Timestamp datetime) {
		this.description = description;
		this.timestamp = datetime;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		Assert.hasText(description);
		this.description = description;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		Assert.notNull(timestamp);
		this.timestamp = timestamp;
	}

}
