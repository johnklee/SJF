<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Sentence Judge Framework Web Demo</title>
<link rel="stylesheet" type="text/css" href="js/ext-3.1.1/resources/css/ext-all.css" />
<script type="text/javascript" src="js/ext-3.1.1/adapter/ext/ext-base.js"></script>
<script type="text/javascript" src="js/ext-3.1.1/ext-all.js"></script>
<script type="text/javascript" src="login.js"></script>
<title>Welcome to NTNU Sentence Judge</title>
</head>
<body>
<br/>
<br/>
<br/>
<br/>
<center>
<%
    //String e = (String)request.getAttribute("error");   
    if(request.getAttribute("error")==null) {   
%> 
<!-- <b>Please login first!</b>&nbsp;<a href="/SJF/GuestLogin">Login as guest</a><br/> -->
<%
    } else {
%>
<font color='red'><b>Error</b>:&nbsp;&nbsp;${error}</font>
<%
    }
%>
<div id='form'></div>
<%
	ServletContext sc = pageContext.getSession().getServletContext();
	List<String> guest_sid = (List<String>)sc.getAttribute("guest_sid");
	if(guest_sid!=null && guest_sid.size()>0)
	{
		request.setAttribute("gc", guest_sid.size());
%>
	<b>Now we have ${gc} guest(s)</b>
<%
	}
%>
</center>
</body>
</html>