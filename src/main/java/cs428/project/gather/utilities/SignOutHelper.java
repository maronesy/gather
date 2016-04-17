package cs428.project.gather.utilities;

import javax.servlet.*;
import javax.servlet.http.*;
import org.apache.commons.lang3.StringUtils;

public final class SignOutHelper {
	private SignOutHelper() { }

	public static void invalidateSession(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if(session != null) {
			session.invalidate();
		}
	}

	public static void deleteSessionCookie(HttpServletRequest request, HttpServletResponse response) {
		ServletContext servletContext = request.getServletContext();
		SessionCookieConfig sessionCookieConfig = servletContext.getSessionCookieConfig();

		String sessionCookieName = sessionCookieConfig.getName();
		sessionCookieName = StringUtils.trimToNull(sessionCookieName);

		if(sessionCookieName != null) {
			Cookie sessionCookie = new Cookie(sessionCookieName, "expired");

			String sessionCookieDomain = sessionCookieConfig.getDomain();
			sessionCookie.setDomain(sessionCookieDomain);

			String sessionCookiePath = sessionCookieConfig.getPath();
			sessionCookie.setPath(sessionCookiePath);

			String sessionCookieComment = sessionCookieConfig.getComment();
			sessionCookie.setComment(sessionCookieComment);

			sessionCookie.setMaxAge(0);
			response.addCookie(sessionCookie);
		}
	}
}
