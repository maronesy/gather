package cs428.project.gather.data.model;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.*;

public class Actor {
    protected final ActorType actorType;
    protected String actorID;

    public Actor(ActorType actorType) {
        this.actorType = actorType;
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

        if(anotherObject == this) {
            equal = true;

        } else if(anotherObject != null && anotherObject.getClass().equals(this.getClass())) {
            Actor anotherActor = (Actor)anotherObject;
            EqualsBuilder equalsBuilder = new EqualsBuilder();
            equalsBuilder.append(this.actorType, anotherActor.actorType);
            equalsBuilder.append(this.actorID, anotherActor.actorID);
            equal = equalsBuilder.isEquals();
        } return equal;
    }

    public static boolean isAnonymousUser(HttpServletRequest request) {
        return ActorStateUtility.retrieveActorFromRequest(request).isAnonymousUser();
    }

    public boolean isAnonymousUser() {
        return actorTypeMatches(ActorType.ANONYMOUS_USER, actorType);
    }

    public static boolean isRegisteredUser(HttpServletRequest request) {
        return ActorStateUtility.retrieveActorFromRequest(request).isRegisteredUser();
    }

    public boolean isRegisteredUser() {
        return actorTypeMatches(ActorType.REGISTERED_USER, actorType);
    }

    private static boolean actorTypeMatches(ActorType type1, ActorType type2) {
        if (type1 == null || type2 == null) {
            throw new IllegalArgumentException("The actor type cannot be null.");
        } return type1.equals(type2);
    }
}
