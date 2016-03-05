package cs428.project.gather.data.model;

import org.apache.commons.lang3.StringUtils;


public enum ActorType
{
	ANONYMOUS_USER("anonymousUser"),
	REGISTERED_USER("registeredUser"),
	VENDOR("vendor");

	private final String value;

	private ActorType(String value)
	{
		this.value = value;
	}

	public String getValue()
	{
		return value;
	}

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
