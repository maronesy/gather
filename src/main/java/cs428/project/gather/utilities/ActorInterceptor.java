package cs428.project.gather.utilities;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import cs428.project.gather.data.model.Actor;


public class ActorInterceptor extends HandlerInterceptorAdapter
{
//	@Autowired
//	@Qualifier("defaultAnonymousUserHolder")
//	private DefaultAnonymousUserHolder defaultAnonymousUserHolder;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception
	{
		Actor actor = ActorStateUtility.retrieveActorFromSession(request);

		if(actor == null)
		{
			//actor = cloneDefaultAnonymousUser();
			actor = new Actor();
		}

		ActorStateUtility.storeActorInRequest(request, actor);

		ActorStateUtility.storeAuthenticatedStateInRequest(request, actor);

		return true;
	}

//	private AnonymousUser cloneDefaultAnonymousUser()
//	{
//		AnonymousUser defaultAnonymousUser = defaultAnonymousUserHolder.getDefaultAnonymousUser();
//
//		String userID = defaultAnonymousUser.getUserID();
//		String actorID = defaultAnonymousUser.getActorID();
//
//		AnonymousUser anonymousUser = new AnonymousUser();
//
//		anonymousUser.setUserID(userID);
//		anonymousUser.setActorID(actorID);
//
//		return anonymousUser;
//	}
}
