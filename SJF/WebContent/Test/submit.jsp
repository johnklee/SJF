<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%  
    String subject = request.getParameter("subject");
	String article = request.getParameter("article");
    String msg="";  
    if(subject==null || subject.trim().isEmpty()){  
        msg = "{success:false,errors:{subject:'文章標題不可為空!'},msg:'文章標題不可為空!'}";  
    } else if(article==null || article.trim().isEmpty()){
    	msg = "{success:false,errors:{article:'文章不可為空!'},msg:'文章不可為空!'}";
    }else {  
        msg = "{success:true}";  
    }  
    response.getWriter().write(msg);  
%>