package sjf.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import amq.utils.JSFAgent;

@WebServlet("/Services/RetireSession")
public class RSessionServlet extends HttpServlet{
	/**
     * @see HttpServlet#HttpServlet()
     */
    public RSessionServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.printf("\t[Test] doGet!\n");
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
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
		String msg = String.format("{success:true, resp:'Done!'}");
		response.getWriter().write(msg); 
	}

}
