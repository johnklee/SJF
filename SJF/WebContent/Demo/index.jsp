<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Welcome ${sessionScope.user}</title>
<STYLE TYPE="text/css">  
    .accountIcon { background-image: url(../Images/account.png) !important; } 
    .logoutIcon  { background-image: url(../Images/logout.png) !important; }    
    .reportIcon  { background-image: url(../Images/report.png) !important; }
    .statR1Icon  { background-image: url(../Images/r1.png) !important; }  
    .newsIcon    { background-image: url(../Images/news.png) !important; } 
    .watchIcon    { background-image: url(../Images/watch.png) !important; }       
</STYLE>
<link rel="stylesheet" type="text/css" href="../js/ext-3.1.1/resources/css/ext-all.css"/>
<link rel="stylesheet" type="text/css" href="../css/text.css"/>
<script type="text/javascript" src="../js/ext-3.1.1/adapter/ext/ext-base.js"></script>
<script type="text/javascript" src="../js/ext-3.1.1/ext-all.js"></script>
<script type="text/javascript" src="index.js"></script>
<script type="text/javascript">
Ext.onReady(function(){
	Ext.Msg.alert('Info', 'Welcome ${sessionScope.user}!<br/>');
});
</script>
</head>
<body>
<div id="toolbar"></div>
<div id="hello-win"></div>
<!--  <div id="news"><b>Thanks for using our system!</b></div> -->
</body>
</html>