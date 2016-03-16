package cs428.project.gather.data.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

@Entity
public class Category {
	private @Id @GeneratedValue Long id;
	
	@Column(unique=true)
	private String name;
	
	@ManyToMany(mappedBy="preferences")
	private Set<Registrant> userPreferences = new HashSet<Registrant>();

	protected Category() {}

	public Category(String name) {
		this.setName(name);
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
