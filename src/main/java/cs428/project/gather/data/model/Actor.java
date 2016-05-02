package cs428.project.gather.data.model;

import javax.persistence.*;
import org.apache.commons.lang3.builder.*;

/**
 * 
 * @author Team Gather
 * 
 * This class is superclass of the Registrant class providing the ActorType constructor and assigning 
 * IDs to registrants. 
 *
 */
@Entity
public class Actor {
	protected @Id @Column(name = "ID") @GeneratedValue long actorID;

	@Enumerated(EnumType.STRING)
	protected final ActorType actorType;

	public Actor(ActorType actorType) {
		this.actorType = actorType;
	}

	public Actor() {
		this.actorType = ActorType.ANONYMOUS_USER;
	}

	public ActorType getActorType() {
		return actorType;
	}

	public long getActorID() {
		return actorID;
	}

	/**
	 * This method overrides the hashCode method and returns to overwritten value
	 * @return int 
	 */
	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		builder.append(actorID);
		int hashCode = builder.toHashCode();
		return hashCode;
	}

	/**
	 * This method overrides the equals method which compares to objects
	 * 
	 * @return A boolean value indicating whether two objects were the same or not
	 */
	@Override
	public boolean equals(Object anotherObject) {
		boolean equal = false;

		if (anotherObject == this) {
			equal = true;

		} else if (anotherObject != null && anotherObject.getClass().equals(this.getClass())) {
			Actor anotherActor = (Actor) anotherObject;
			EqualsBuilder equalsBuilder = new EqualsBuilder();
			equalsBuilder.append(this.actorType, anotherActor.actorType);
			equalsBuilder.append(this.actorID, anotherActor.actorID);
			equal = equalsBuilder.isEquals();
		}
		return equal;
	}
}
