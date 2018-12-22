<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="amq.utils.JSFAgent.Task,java.util.*,java.io.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Evaluation result page</title>
</head>
<body>
<b>Evaluation result page:</b>&nbsp;&nbsp;<a href="index.jsp"><img src="../Images/return.png" alt="Return to index"></a><br/>
<%	
	response.setCharacterEncoding("UTF-8");
	response.setContentType("text/html; charset=UTF-8");
	//PrintWriter pw = response.getWriter();
	//OutputStream os = response.getOutputStream();
	//OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
	List<Task> taskList = (List<Task>)session.getAttribute("tasks");
	for(Task t:taskList)
	{		
		out.println(String.format("<b>- %s</b>:<br/>", t.sent));
		if(t.resp.size()>0)
		{
			for(String r:t.resp)out.println(String.format("&nbsp;&nbsp;%s<br/>", r));
		}
		else
		{
			out.println("&nbsp;&nbsp;No response!<br/>");
		}
		out.println("<br/>");
	}
	
%>
</body>
</html>