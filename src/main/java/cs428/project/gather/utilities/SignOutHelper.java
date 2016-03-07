package cs428.project.gather.utilities;

import javax.servlet.ServletContext;
import javax.servlet.SessionCookieConfig;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;


public final class SignOutHelper
{
	private SignOutHelper()
	{
	}

	public static boolean invalidateSession(HttpServletRequest request)
	{
		HttpSession session = request.getSession(false);
		if(session != null)
		{
			session.invalidate();
			return true;
		}
		return false;
	}

	public static boolean deleteSessionCookie(HttpServletRequest request, HttpServletResponse response)
	{
		ServletContext servletContext = request.getServletContext();
		SessionCookieConfig sessionCookieConfig = servletContext.getSessionCookieConfig();

		String sessionCookieName = sessionCookieConfig.getName();
		sessionCookieName = StringUtils.trimToNull(sessionCookieName);

		if(sessionCookieName != null)
		{
			Cookie sessionCookie = new Cookie(sessionCookieName, "expired");

			String sessionCookieDomain = sessionCookieConfig.getDomain();
			sessionCookie.setDomain(sessionCookieDomain);

			String sessionCookiePath = sessionCookieConfig.getPath();
			sessionCookie.setPath(sessionCookiePath);

			String sessionCookieComment = sessionCookieConfig.getComment();
			sessionCookie.setComment(sessionCookieComment);

			sessionCookie.setMaxAge(0);

			response.addCookie(sessionCookie);
			return true;
		}
		return false;
	}
}
