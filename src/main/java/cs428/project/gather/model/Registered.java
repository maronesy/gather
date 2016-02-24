package cs428.project.gather.model;

import javax.persistence.*;
import lombok.Data;

@Data
@Entity
public class Registered {
	private @Id @GeneratedValue Long id;
	private String firstName;
	private String lastName;
	private int age;
	private String location;

	private Registered() {}

	public Registered(String firstName, String lastName, int age, String location) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.age = age;
		this.location = location;
	}
}