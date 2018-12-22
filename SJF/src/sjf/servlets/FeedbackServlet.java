package sjf.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import amq.utils.JSFAgent;

/**
 * Servlet implementation class FeedbackServlet
 */
@WebServlet("/Services/FeedbackServlet")
public class FeedbackServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FeedbackServlet() {
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
		String resp = "Thanks for your feedback!";
		String id = (String)request.getParameter("id");
		String idi[] = id.split("_");
		if(idi.length==2) id = idi[0];
		String feedback = (String)request.getParameter("feedback");
		HttpSession session = request.getSession();
		if(feedback!=null && (feedback.equalsIgnoreCase("bad")||feedback.equalsIgnoreCase("wrong")||feedback.equalsIgnoreCase("-1"))) resp = "We will improve our system. Thx!";
		System.out.printf("\t[Info] Feedback ID=%s: %s\n", id, feedback);		
		JSFAgent jsfAgent = (JSFAgent)session.getAttribute("jsfAgent");
		if(jsfAgent!=null) jsfAgent.logFeedback((String)session.getAttribute("user"), id, feedback, (String)session.getAttribute("from"));
		
		// Handling feedback here
		String msg = String.format("{success:true, resp:'%s'}", resp);
		response.getWriter().write(msg); 
	}

}
