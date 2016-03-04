package cs428.project.gather.utilities;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;


public final class RedirectPathHelper
{
	private RedirectPathHelper()
	{
	}

	public static String buildRedirectPath(HttpServletRequest request, String resourcePath)
	{
		String dispatcherPath = getDispatcherPath(request);

		String redirectPath = "redirect:" + dispatcherPath + resourcePath;

		return redirectPath;
	}

	private static String getDispatcherPath(HttpServletRequest request)
	{
		String dipatcherPath = request.getRequestURI();

		String contextPath = request.getContextPath();
		dipatcherPath = StringUtils.removeStart(dipatcherPath, contextPath);

		String servletPath = request.getServletPath();
		dipatcherPath = StringUtils.removeEnd(dipatcherPath, servletPath);

		return dipatcherPath;
	}
}
