package cs428.project.gather.data.model;


import java.sql.Timestamp;

import javax.persistence.*;

import org.springframework.util.Assert;

import lombok.Data;

@Entity
public class Occurrence {
	private @Id @GeneratedValue Long id;
	private String description;
	private Timestamp datetime;

	@ManyToOne
	private Location location;

	private Occurrence() {}

	public Occurrence(String description, Timestamp datetime) {
		this.description = description;
		this.datetime = datetime;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		Assert.hasText(description);
		this.description = description;
	}

	public Timestamp getDatetime() {
		return datetime;
	}

	public void setDatetime(Timestamp datetime) {
		Assert.notNull(datetime);
		this.datetime = datetime;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		Assert.notNull(location);
		this.location = location;
	}
}
