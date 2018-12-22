package sjf.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jms.JMSException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import amq.utils.GlobalAMQ;
import amq.utils.JSFAgent;
import amq.utils.JSFAgent.Task;

/**
 * Servlet implementation class EvalSentServlet
 */
@WebServlet("/Services/EvalSentServlet")
public class EvalSentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public EvalSentServlet() {
    	super();
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
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		String article = request.getParameter("article");
		String topic = request.getParameter("topic");
		//String subject = request.getParameter("subject");
		System.out.printf("\t[Info] Topic=%s; Article:\n%s\n", topic, article);
		if(topic==null || topic.trim().isEmpty()) topic = GlobalAMQ.QueryTopic;		
		HttpSession session = request.getSession();
		ServletContext sc = request.getServletContext();
		JSFAgent jsfAgent = (JSFAgent)session.getAttribute("jsfAgent");
		String msg=""; 
		if(jsfAgent==null)
		{
			try
			{															
				String brokerURI = (String)sc.getInitParameter("brokerURI");
				if(brokerURI!=null)
				{
					System.out.printf("\t[Info] Initialize JSFAgent(%s)...\n", brokerURI);
					jsfAgent = new JSFAgent(brokerURI, String.format("WEB.%s", session.getId())); // tcp://140.112.31.76:61616
					System.out.printf("\t[Info] Send Service Heart Bean...\n");
					jsfAgent.serviceHB(null, 2000);					
					session.setAttribute("jsfAgent", jsfAgent);
				}
				else
				{
					msg = "{success:false,errors:{article:'BrokerURI error!'},msg:'服務器錯誤!'}";
				}
			}
			catch(JMSException e)
			{
				msg = String.format("{success:false,msg:'%s'}", e.getMessage());
			}
		}
				
	     
	    if(topic==null || topic.trim().isEmpty()){  
	        msg = "{success:false,errors:{topic:'文章類型不可為空!'},msg:'文章標題不可為空!'}";  
	    } else if(article==null || article.trim().isEmpty()){
	    	msg = "{success:false,errors:{article:'文章不可為空!'},msg:'文章不可為空!'}";
	    } else if(msg.trim().isEmpty()){
	    	System.out.printf("\t[Info] topic='%s'\n", topic);
	    	//System.out.printf("\t[Info] Subject=%s\n", subject);
	    	System.out.printf("\t[Info] Handling Article:\n%s\n", article);
	    	String sents[] = article.split("[,.?，？。！!：;:]");
	    	Set<String> sentSet = new HashSet<String>();
			for(String sent:sents) {
				sent = sent.trim();
				if(sent.isEmpty()) continue;
				sentSet.add(sent);
				System.out.printf("\t\tSub sent='%s'...\n", sent);
			}
			try
			{
				long ts = System.currentTimeMillis();
				List<String> sentList = new ArrayList<String>();
				sentList.addAll(sentSet);
				List<Task> taskList = jsfAgent.querySent(sentList, topic, 10000);						
				
				// Update original article and underline the sick sentence(s)
				int ti=0;
				for(Task task:taskList)
				{
					for(String r:task.resp)
					{
						if(r.split("\\|")[1].equalsIgnoreCase("Fail"))
						{
							article = article.replaceAll(task.sent, String.format("<span class=\"UNDERLINE\" id=\"err_%d-%d\">%s</span>", ts, ti, task.sent));
							break;
						}							
					}
					ti++;
				}
				article = article.replaceAll("\n", "<br/>");
				
				StringBuffer respHtmlBuf = new StringBuffer(String.format("%s<br/><br/><hr size='5' align='center' noshade width='90%%' color='0000ff'/><br/>", article));			
				//respHtmlBuf.append("<tr align='center'><th width='40%'>Sentence</th><th>Category</th><th>Result</th></tr>");
				StringBuffer feedbackIDsBuf = new StringBuffer();
				
				for(int i=0; i<sentList.size(); i++)
				{
					String feedbackID = String.format("%d-%d", ts, i);
					respHtmlBuf.append("<table border=\"0\" width='100%'>");
					Task t = taskList.get(i);
					String sent = sentList.get(i);					
					if(t.resp.size()>0)
					{
						boolean noServiceFlag = false;
						System.out.printf("\tSent='%s' (%s):\n", sent, t.tid);
						StringBuffer respBuf = new StringBuffer();
						StringBuffer fullRespBuf = new StringBuffer();
						respHtmlBuf.append(String.format("<tr><td valign='top' width='30%%'><b>%d. %s</b>", i+1, sent));					
						for(String r:t.resp) {
							fullRespBuf.append(String.format("%s\r\n", r));
							System.out.printf("\t\t%s\n", r);
							//respHtmlBuf.append(String.format("<li>%s</li><br/>", r));
							// amq.demo.SentConsumer1|Fail|Default|lt-004|砂 ?-> Replace:  '砂' with '沙' -> 沙 ? -> 您的意思是"沙\2"？
							String items[] = r.split("\\|"); // ServiceName|Result|Category|Reason|Suggestion
							if(items.length>1 && items[1].trim().equalsIgnoreCase("Pass")) continue;
							if(items.length==5) respBuf.append(String.format("<li><b>[%s]</b> %s</li>", items[2], items[4]));
							else {
								respBuf.append(String.format("<li>%s</li>", r));
								noServiceFlag = true;
							}
						}
						jsfAgent.logQuery(feedbackID, topic, (String)session.getAttribute("user"), sent, fullRespBuf.toString().trim(), (String)session.getAttribute("from"));
						
						if(respBuf.toString().trim().isEmpty())
						{
							respHtmlBuf.append("&nbsp;&nbsp;<img align='top' src='/SJF/Images/Pass.png'></td><td>&nbsp;</td>");
						}
						else if(noServiceFlag)
						{
							//respHtmlBuf.append("&nbsp;&nbsp;<img align='top' src='/SJF/Images/question.png'></td><td>&nbsp;</td>");
							respHtmlBuf.append(String.format("&nbsp;&nbsp;<img align='top' src='/SJF/Images/question.png'></td><td align='left' width='45%%'>%s</td>", respBuf.toString()));
						}
						else respHtmlBuf.append(String.format("&nbsp;&nbsp;<img align='top' src='/SJF/Images/Fail.png'></td><td align='left' width='45%%'>%s</td>", respBuf.toString()));
						
						//respHtmlBuf.append("<td align='right'><form action='/SJF/FeedbackServlet'>");
						respHtmlBuf.append("<td align='right' width='10%'>");
						//respHtmlBuf.append(String.format("<input type='text' name='feedback' id='%s_text' value='Good'>&nbsp;", feedbackID));
						//respHtmlBuf.append(String.format("<input type='image' src='/SJF/Images/feedback.png' id='%s' alt='feedback'>", feedbackID));
						respHtmlBuf.append(String.format("<input type='image' src='/SJF/Images/Thumb-up.png' id='%s_good' alt='feedback good'>&nbsp;&nbsp;|&nbsp;&nbsp;", feedbackID));
						respHtmlBuf.append(String.format("<input type='image' src='/SJF/Images/Thumb-down.png' id='%s_bad' alt='feedback bad'>", feedbackID));
						respHtmlBuf.append("</td>");						
						respHtmlBuf.append("</tr>");		
						if(feedbackIDsBuf.toString().trim().isEmpty()) feedbackIDsBuf.append(String.format("'%s'", feedbackID));
						else feedbackIDsBuf.append(String.format(",'%s'", feedbackID));
					}
					else{
						System.out.printf("\tSent='%s' has no response!\n", sent);
						respHtmlBuf.append(String.format("<tr><td valign='top' width='30%%'><b>%d. %s</b>", i+1, sent));
						respHtmlBuf.append("&nbsp;&nbsp;<img align='top' src='/SJF/Images/wait.png'></td><td><li>Not Ready!</li></td>");
						respHtmlBuf.append("<td align='right' width='10%'>");
						//respHtmlBuf.append(String.format("<input type='text' name='feedback' id='%s_text' value='Good'>&nbsp;", feedbackID));
						respHtmlBuf.append(String.format("<input type='image' src='/SJF/Images/Thumb-up.png' id='%s_good' alt='feedback good'>&nbsp;&nbsp;", feedbackID));
						respHtmlBuf.append(String.format("<input type='image' src='/SJF/Images/Thumb-down.png' id='%s_bad' alt='feedback bad'>", feedbackID));
						respHtmlBuf.append("</td>");						
						respHtmlBuf.append("</tr>");
						jsfAgent.logQuery(feedbackID, topic, "john", sent, "", (String)session.getAttribute("from"));
					}
					respHtmlBuf.append("</table>");
					respHtmlBuf.append("<hr>");
				}
				for(int i=0; i<10; i++) respHtmlBuf.append("<br/>");
				session.setAttribute("tasks", taskList);
		    	msg = String.format("{success:true, resp:\"%s\", feedback:[%s]}", respHtmlBuf.toString().trim().replaceAll("\"", "\\\\\""),
		    			                                                          feedbackIDsBuf.toString());  
		    	System.out.printf("\t[Info] Response:\n%s\n", msg);
			}
			catch(Exception e)
			{
				msg = String.format("{success:false, msg:'%s'}",  e.getMessage());
				e.printStackTrace();
			}
	    } 
	    response.getWriter().write(msg); 
	}
}
