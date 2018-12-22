package sjf.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet Filter implementation class LoginFilter
 */
@WebFilter(filterName="loginFilter", urlPatterns={"/Demo/*", "/Services/*"})
public class LoginFilter implements Filter {
	FilterConfig config;

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		//System.out.printf("\t[Test] Login filter...'%s'\n", ((HttpServletRequest)request).getRequestURI());
		HttpServletRequest req = (HttpServletRequest) request;
		HttpSession session = ((HttpServletRequest)request).getSession();
		if(session.getAttribute("user")==null)
		{
			//chain.doFilter(request, response);
			/*RequestDispatcher rd = request.getRequestDispatcher("/login.jsp");
			rd.forward(request, response);*/
			RequestDispatcher loginPage = request.getRequestDispatcher("/Error/NotLogin.html");
			req.setAttribute("error", "Please login first!");
			loginPage.forward(req, response);
			//((HttpServletResponse)response).sendRedirect(String.format("%s/login.jsp", req.getContextPath()));
		}
		else chain.doFilter(request, response);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		this.config = fConfig;
	}

}
