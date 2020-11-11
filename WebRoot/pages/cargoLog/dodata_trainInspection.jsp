<%@page import="java.awt.Window"%>
<%@page import="com.opensymphony.xwork2.util.location.Location"%>
<%@page import="cargo.tao.dao.UserDao"%>
<%@page import="cargo.tao.entity.User"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
<%
		try{
			UserDao udao = new UserDao();
			User u = new User();
			String[] data0 = request.getParameter("data1").split("\\*");
			String[] data00 = request.getParameter("data2").split("\\*");
			if(data0.length != 0){
				udao.table_delete("train_inspection_log");
				for(int i=0;i<data0.length;i++){
					String[] data = data0[i].split("_");
					u.setData(data);
					udao.trainInspection_insert(u,16);
				}
			}
			if(data00.length != 0){
				udao.table_delete("train_inspection_book");
				for(int i=0;i<data00.length;i++){
					String[] data = data00[i].split("_");
					u.setData(data);
					udao.trainInspection_insert(u,14);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			%><script type="text/javascript">window.location.href = "trainInspection.html";</script><%
		}
%>
</body>
</html>