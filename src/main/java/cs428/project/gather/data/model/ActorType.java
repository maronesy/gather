package cs428.project.gather.data.model;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * @author Team Gather
 *
 * It provides three different user types to the app which are anonymousUser, registerUser, and admin
 *
 */

public enum ActorType
{
	ANONYMOUS_USER("anonymousUser"),
	REGISTERED_USER("registeredUser"),
	ADMIN("admin");

	private final String value;

	private ActorType(String value)
	{
		this.value = value;
	}

	public String getValue()
	{
		return value;
	}

	/**
	 * This method compares the input and if it is equal to one of the actor types it sets the SelectedActorType
	 * to the that actor type
	 * 
	 * @param value passed in from the calling object
	 * @return returns the ActorType selectedActorType which is set in the for loop
	 * 
	 */
	public static ActorType fromValue(String value)
	{
		ActorType selectedActorType = null;

		if(value == null)
		{
			throw new IllegalArgumentException("The actor type value cannot be null.");
		}
		else
		{
			ActorType[] actorTypes = values();

			for(ActorType currentActorType : actorTypes)
			{
				String currentValue = currentActorType.getValue();

				if(StringUtils.equals(currentValue, value))
				{
					selectedActorType = currentActorType;

					break;
				}
			}

			if(selectedActorType == null)
			{
				throw new IllegalStateException("An unrecognized actor type value was encountered:  " + value + ".");
			}
		}

		return selectedActorType;
	}

	@Override
	public String toString()
	{
		return value;
	}
}
