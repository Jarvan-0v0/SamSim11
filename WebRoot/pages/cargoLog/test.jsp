<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="cargo.tao.dao.UserDao"%>
<%@ page import="cargo.tao.entity.User"%>
<%@ page import="java.sql.*"%>
<%@ page import="com.mysql.jdbc.Driver" %> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>test</title>
<script src="https://cdn.staticfile.org/jquery/1.10.2/jquery.min.js"></script>
</head>

<body>
<%
	UserDao a = new UserDao();
	a.table_copyFromTo("aa","bb");
	a.table_delete("aa");
%>
<script type="text/javascript">
	alert("本大爷成功了吗？");
</script>
</body>
</html>