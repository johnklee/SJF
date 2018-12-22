package sjf.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import sjf.utils.TKit;

/**
 * Servlet implementation class GuestLoginServlet
 */
@WebServlet("/GuestLogin")
public class GuestLoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GuestLoginServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		ServletContext sc = request.getServletContext();
		String from = TKit.GetClientIpAddr(request);
		session.setAttribute("user", "guest");
		session.setAttribute("from", from);
		session.setMaxInactiveInterval(10*60); // 10 minutes
		List<String> guest_sid = (List<String>)sc.getAttribute("guest_sid");
		System.out.printf("\t[Test] Guest from %s...\n", from);
		if(guest_sid==null)
		{
			guest_sid = new ArrayList<String>();
			sc.setAttribute("guest_sid", guest_sid);
		}
		guest_sid.add(session.getId());
		System.out.printf("\t[Info] Guest login...(%d)\n", guest_sid.size());
		//RequestDispatcher rd = request.getRequestDispatcher("/Demo/index.jsp");
		//rd.forward(request, response);
		response.sendRedirect(String.format("%s/Demo/index.jsp", request.getContextPath()));
	}
}
