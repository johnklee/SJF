package sjf.listeners;

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import amq.utils.JSFAgent;

@WebListener()
public class STListener implements HttpSessionListener{

	@Override
	public void sessionCreated(HttpSessionEvent se) {
		HttpSession session = se.getSession();
		String user=null;
		if((user=(String)session.getAttribute("user"))==null)
		{
			//session.setAttribute("user", "guest");
			session.setMaxInactiveInterval(10*60); // 10 minutes
		}
		else System.out.printf("\t[Info] User login...%s\n", user);
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
		HttpSession session = se.getSession();
		ServletContext sc = se.getSession().getServletContext();
		JSFAgent jsfAgent = (JSFAgent)session.getAttribute("jsfAgent");
		if(jsfAgent!=null)
		{
			try
			{
				jsfAgent.exit();				
			}
			catch(Exception e){}
		}
		session.removeAttribute("jsfAgent");	
		List<String> guest_sid = (List<String>)sc.getAttribute("guest_sid");
		if(guest_sid!=null)
		{
			guest_sid.remove(session.getId());
			System.out.printf("\t[Info] Session(%s/%d) timeout!\n", session.getId(),                     
					                                                guest_sid.size());
		}
		session.removeAttribute("user");
	}
}
