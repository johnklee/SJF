package sjf.filters;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;

@WebFilter(filterName="performance", urlPatterns={"/Services/*"},
	initParams={  
        @WebInitParam(name = "debug", value = "false")  
    })
public class PerformanceFilter implements Filter {
	private FilterConfig filterConfig;
	private boolean debug = false;

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		long begin = System.currentTimeMillis();
        chain.doFilter(request, response);
        if(debug) System.out.printf("\t[Test] URI='%s' process in %d milliseconds...\n", ((HttpServletRequest)request).getRequestURI(),
        		                                                               (System.currentTimeMillis() - begin));
       /* filterConfig.getServletContext().log("Request process in " +
                (System.currentTimeMillis() - begin) + " milliseconds");*/
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		this.filterConfig = fConfig;
		String debugStr = filterConfig.getInitParameter("debug");
		if(debugStr!=null && debugStr.equalsIgnoreCase("true")) debug = true;
	}
}
