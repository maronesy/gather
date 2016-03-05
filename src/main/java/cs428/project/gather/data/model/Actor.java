package cs428.project.gather.data.model;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.*;

public class Actor {
	protected final ActorType actorType;
	protected String actorID;

	public Actor(ActorType actorType) {
		this.actorType = actorType;
	}

	public Actor() {
		this.actorType = ActorType.ANONYMOUS_USER;
	}

	public ActorType getActorType() {
		return actorType;
	}

	public String getActorID() {
		return actorID;
	}

	public void setActorID(String actorID) {
		this.actorID = StringUtils.trimToNull(actorID);
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		builder.append(actorID);
		int hashCode = builder.toHashCode();
		return hashCode;
	}

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
