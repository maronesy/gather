package cs428.project.gather.utilities;

import javax.servlet.http.HttpServletRequest;

import cs428.project.gather.data.model.Actor;
import cs428.project.gather.data.model.ActorType;

/**
 * 
 * @author Team Gather
 * Utility class to manage user types
 * 
 */
public final class ActorTypeHelper
{
	private ActorTypeHelper()
	{
	}

	/**
	 * Check if the current user is an anonymous user based on request
	 * 
	 * @param request: HTTP request
	 * @return: if the user is anonymous
	 * 
	 */
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

	/**
	 * Check if the current user is an anonymous user for a given actor
	 * 
	 * @param actor: a user object
	 * @return: if the user is anonymous
	 * 
	 */
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

	/**
	 * Check if the current user is an anonymous user for a given actor type
	 * 
	 * @param actorType: a user type
	 * @return: if the user is anonymous
	 * 
	 */
	public static boolean isAnonymousUser(ActorType actorType)
	{
		if(actorType == null)
		{
			throw new IllegalArgumentException("The actor type cannot be null.");
		}

		boolean isAnonymousUser = ActorType.ANONYMOUS_USER.equals(actorType);

		return isAnonymousUser;
	}

	/**
	 * Check if the current user is an registered user based on request
	 * 
	 * @param request: HTTP request
	 * @return: if the user is registered
	 * 
	 */
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

	/**
	 * Check if the current user is an registered user for a given actor
	 * 
	 * @param actor: a user object
	 * @return: if the user is registered
	 * 
	 */
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

	/**
	 * Check if the current user is an registered user for a given actor type
	 * 
	 * @param actorType: a user type
	 * @return: if the user is registered
	 * 
	 */
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
