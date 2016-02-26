package cs428.project.gather.data.model;


import java.sql.Timestamp;

import javax.persistence.*;
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
}
