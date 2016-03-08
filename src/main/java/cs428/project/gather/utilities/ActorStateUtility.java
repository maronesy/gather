package cs428.project.gather.utilities;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import cs428.project.gather.data.model.Actor;


public final class ActorStateUtility
{
	private static final String ACTOR_KEY_NAME = "actor";
	private static final String AUTHENTICATED_KEY_NAME = "authenticated";

	private ActorStateUtility()
	{
	}

	public static Actor retrieveActorFromSession(HttpServletRequest request)
	{
		Actor actor = null;

		if(request == null)
		{
			throw new IllegalArgumentException("The request cannot be null.");
		}

		HttpSession session = request.getSession(false);
		if(session != null)
		{
			actor = (Actor)session.getAttribute(ACTOR_KEY_NAME);
		}

		return actor;
	}

	public static void storeActorInSession(HttpServletRequest request, Actor actor)
	{
		HttpSession session = null;

		if(request == null)
		{
			throw new IllegalArgumentException("The request cannot be null.");
		}

		if(actor == null)
		{
			session = request.getSession(false);
			if(session != null)
			{
				session.removeAttribute(ACTOR_KEY_NAME);
			}
		}
		else
		{
			session = request.getSession();

			session.setAttribute(ACTOR_KEY_NAME, actor);
		}
	}

	public static Actor retrieveActorFromRequest(HttpServletRequest request)
	{
		if(request == null)
		{
			throw new IllegalArgumentException("The request cannot be null.");
		}

		Actor actor = (Actor)request.getAttribute(ACTOR_KEY_NAME);

		return actor;
	}

	public static void storeActorInRequest(HttpServletRequest request, Actor actor)
	{
		if(request == null)
		{
			throw new IllegalArgumentException("The request cannot be null.");
		}

		if(actor == null)
		{
			request.removeAttribute(ACTOR_KEY_NAME);
		}
		else
		{
			request.setAttribute(ACTOR_KEY_NAME, actor);
		}
	}

	public static void storeAuthenticatedStateInRequest(HttpServletRequest request, Actor actor)
	{
		if(request == null)
		{
			throw new IllegalArgumentException("The request cannot be null.");
		}

		if(actor == null)
		{
			request.removeAttribute(AUTHENTICATED_KEY_NAME);
		}
		else
		{
			if(ActorTypeHelper.isAnonymousUser(actor))
			{
				request.setAttribute(AUTHENTICATED_KEY_NAME, Boolean.FALSE);
			}
			else if(ActorTypeHelper.isRegisteredUser(actor))
			{
				request.setAttribute(AUTHENTICATED_KEY_NAME, Boolean.TRUE);
			}
			else
			{
				// This is never expected.
				throw new IllegalStateException("An unrecognized actor type was encountered.");
			}
		}
	}
	
	public static boolean retrieveAuthenticatedStateInRequest(HttpServletRequest request)
	{
		return (boolean)request.getAttribute(AUTHENTICATED_KEY_NAME);
	}
	
}
