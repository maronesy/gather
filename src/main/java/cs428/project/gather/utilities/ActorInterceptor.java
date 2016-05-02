package cs428.project.gather.utilities;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import cs428.project.gather.data.model.Actor;

/**
 * 
 * @author Team Gather
 * This class extends the handler interceptor to get the session information for 
 * the user.
 * 
 */
public class ActorInterceptor extends HandlerInterceptorAdapter
{

	/**
	 * Intercept the HTTP request, and create a new Actor and save the user 
	 * into the session.
	 * 
	 * @param request: HTTP request
	 * @param response: HTTP response
	 * @param handler: request handler
	 * @return: always return true.
	 * 
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
	{
		Actor actor = ActorStateUtility.retrieveActorFromSession(request);

		if(actor == null)
		{
			actor = new Actor();
		}

		ActorStateUtility.storeActorInRequest(request, actor);

		ActorStateUtility.storeAuthenticatedStateInRequest(request, actor);

		return true;
	}

}
