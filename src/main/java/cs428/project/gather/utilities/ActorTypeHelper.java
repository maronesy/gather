package cs428.project.gather.utilities;

import javax.servlet.http.HttpServletRequest;

import cs428.project.gather.data.model.Actor;
import cs428.project.gather.data.model.ActorType;


public final class ActorTypeHelper
{
	private ActorTypeHelper()
	{
	}

	public static boolean isAnonymousUser(HttpServletRequest request)
	{
		if(request == null)
		{
			throw new IllegalArgumentException("The request cannot be null.");
		}

		Actor actor = ActorStateUtility.retrieveActorFromRequest(request);

		boolean isAnonymousUser = isAnonymousUser(actor);

		return isAnonymousUser;
	}

	public static boolean isAnonymousUser(Actor actor)
	{
		if(actor == null)
		{
			throw new IllegalArgumentException("The actor cannot be null.");
		}

		ActorType actorType = actor.getActorType();

		boolean isAnonymousUser = isAnonymousUser(actorType);

		return isAnonymousUser;
	}

	public static boolean isAnonymousUser(ActorType actorType)
	{
		if(actorType == null)
		{
			throw new IllegalArgumentException("The actor type cannot be null.");
		}

		boolean isAnonymousUser = ActorType.ANONYMOUS_USER.equals(actorType);

		return isAnonymousUser;
	}

	public static boolean isRegisteredUser(HttpServletRequest request)
	{
		if(request == null)
		{
			throw new IllegalArgumentException("The request cannot be null.");
		}

		Actor actor = ActorStateUtility.retrieveActorFromRequest(request);

		boolean isRegisteredUser = isRegisteredUser(actor);

		return isRegisteredUser;
	}

	public static boolean isRegisteredUser(Actor actor)
	{
		if(actor == null)
		{
			throw new IllegalArgumentException("The actor cannot be null.");
		}

		ActorType actorType = actor.getActorType();

		boolean isRegisteredUser = isRegisteredUser(actorType);

		return isRegisteredUser;
	}

	public static boolean isRegisteredUser(ActorType actorType)
	{
		if(actorType == null)
		{
			throw new IllegalArgumentException("The actor type cannot be null.");
		}

		boolean isRegisteredUser = ActorType.REGISTERED_USER.equals(actorType);

		return isRegisteredUser;
	}
}
