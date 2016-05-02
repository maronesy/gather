package cs428.project.gather.utilities;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import cs428.project.gather.data.model.Actor;

/**
 * 
 * @author Team Gather
 * Utility class to manage user states
 * 
 */
public final class ActorStateUtility {
	private static final String ACTOR_KEY_NAME = "actor";
	private static final String AUTHENTICATED_KEY_NAME = "authenticated";

	private ActorStateUtility() { }

	/**
	 * Retrieve the user from session based on the HTTP request
	 * 
	 * @param request: HTTP request
	 * @return: the user of this request.
	 * 
	 */
	public static Actor retrieveActorFromSession(HttpServletRequest request) {
		Actor actor = null;
		if (request == null) {
			throw new IllegalArgumentException("The request cannot be null.");
		}

		HttpSession session = request.getSession(false);
		if (session != null) {
			actor = (Actor)session.getAttribute(ACTOR_KEY_NAME);
		}
		return actor;
	}

	/**
	 * Store the user into session 
	 * 
	 * @param request: HTTP request
	 * @param the user of this request.
	 * 
	 */
	public static void storeActorInSession(HttpServletRequest request, Actor actor) {
		HttpSession session = null;
		if(request == null) {
			throw new IllegalArgumentException("The request cannot be null.");
		}

		if (actor == null) {
			session = request.getSession(false);
			if (session != null) {
				session.removeAttribute(ACTOR_KEY_NAME);
			}
		} else {
			session = request.getSession();
			session.setAttribute(ACTOR_KEY_NAME, actor);
		}
	}

	/**
	 * Retrieve the user from the HTTP request directly
	 * 
	 * @param request: HTTP request
	 * @return: the user of this request.
	 * 
	 */
	public static Actor retrieveActorFromRequest(HttpServletRequest request) {
		if(request == null) {
			throw new IllegalArgumentException("The request cannot be null.");
		}

		return (Actor)request.getAttribute(ACTOR_KEY_NAME);
	}

	/**
	 * Store the user into HTTP request directly 
	 * 
	 * @param request: HTTP request
	 * @param the user of this request.
	 * 
	 */
	public static void storeActorInRequest(HttpServletRequest request, Actor actor) {
		if (request == null) {
			throw new IllegalArgumentException("The request cannot be null.");
		}

		if (actor == null) {
			request.removeAttribute(ACTOR_KEY_NAME);
		} else {
			request.setAttribute(ACTOR_KEY_NAME, actor);
		}
	}

	/**
	 * Store the user's authentication state into HTTP request  
	 * 
	 * @param request: HTTP request
	 * @param the user of this request.
	 * 
	 */
	public static void storeAuthenticatedStateInRequest(HttpServletRequest request, Actor actor) {
		if (request == null) {
			throw new IllegalArgumentException("The request cannot be null.");
		}

		if (actor == null) {
			request.removeAttribute(AUTHENTICATED_KEY_NAME);
		} else {
			if (ActorTypeHelper.isAnonymousUser(actor)) {
				request.setAttribute(AUTHENTICATED_KEY_NAME, Boolean.FALSE);
			} else if (ActorTypeHelper.isRegisteredUser(actor)) {
				request.setAttribute(AUTHENTICATED_KEY_NAME, Boolean.TRUE);
			} else {
				// This is never expected.
				throw new IllegalStateException("An unrecognized actor type was encountered.");
			}
		}
	}
	
	/**
	 * Retrieve the user's authentication state into HTTP request  
	 * 
	 * @param request: HTTP request
	 * @return true if user is authenticated, false otherwise
	 * 
	 */
	public static boolean retrieveAuthenticatedStateInRequest(HttpServletRequest request) {
		return (boolean)request.getAttribute(AUTHENTICATED_KEY_NAME);
	}

}
