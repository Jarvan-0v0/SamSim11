<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="java.sql.*"%>
<%@page import="jdbc.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>班计划</title>
<meta charset="utf-8" /> 
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0" />
	
</head>
<body>
<%
String zj = new String(request.getParameter("zj").getBytes("iso-8859-1"), "utf-8"); 
String fx = new String(request.getParameter("fx").getBytes("iso-8859-1"), "utf-8");
String cc = new String(request.getParameter("cc").getBytes("iso-8859-1"), "utf-8"); 
String ddsk = new String(request.getParameter("ddsk").getBytes("iso-8859-1"), "utf-8"); 
String d1 = new String(request.getParameter("d1").getBytes("iso-8859-1"), "utf-8"); 
String d2 = new String(request.getParameter("d2").getBytes("iso-8859-1"), "utf-8"); 
String d3 = new String(request.getParameter("d3").getBytes("iso-8859-1"), "utf-8"); 
String d4 = new String(request.getParameter("d4").getBytes("iso-8859-1"), "utf-8"); 
String d5 = new String(request.getParameter("d5").getBytes("iso-8859-1"), "utf-8"); 
String d6 = new String(request.getParameter("d6").getBytes("iso-8859-1"), "utf-8"); 
String d7 = new String(request.getParameter("d7").getBytes("iso-8859-1"), "utf-8"); 
String d8 = new String(request.getParameter("d8").getBytes("iso-8859-1"), "utf-8"); 
String d9 = new String(request.getParameter("d9").getBytes("iso-8859-1"), "utf-8"); 
String d10 = new String(request.getParameter("d10").getBytes("iso-8859-1"), "utf-8"); 
String d11 = new String(request.getParameter("d11").getBytes("iso-8859-1"), "utf-8"); 
String kc = new String(request.getParameter("kc").getBytes("iso-8859-1"), "utf-8"); 
String hj = new String(request.getParameter("hj").getBytes("iso-8859-1"), "utf-8"); 
String SU1 = new String(request.getParameter("SU1").getBytes("iso-8859-1"), "utf-8"); 
String SU2 = new String(request.getParameter("SU2").getBytes("iso-8859-1"), "utf-8"); 
String SU3 = new String(request.getParameter("SU3").getBytes("iso-8859-1"), "utf-8"); 
String SU4 = new String(request.getParameter("SU4").getBytes("iso-8859-1"), "utf-8"); 
String SU5 = new String(request.getParameter("SU5").getBytes("iso-8859-1"), "utf-8"); 
String SJ = new String(request.getParameter("SJ").getBytes("iso-8859-1"), "utf-8"); 
String ZSDD = new String(request.getParameter("ZSDD").getBytes("iso-8859-1"), "utf-8"); 
String ZSCF = new String(request.getParameter("ZSCF").getBytes("iso-8859-1"), "utf-8"); 
String ZSJC = new String(request.getParameter("ZSJC").getBytes("iso-8859-1"), "utf-8"); 
String ZTDD = new String(request.getParameter("ZTDD").getBytes("iso-8859-1"), "utf-8"); 
String ZTCF = new String(request.getParameter("ZTCF").getBytes("iso-8859-1"), "utf-8"); 
String ZTJC = new String(request.getParameter("ZTJC").getBytes("iso-8859-1"), "utf-8"); 
String ZTS = new String(request.getParameter("ZTS").getBytes("iso-8859-1"), "utf-8");
String ZZCS = new String(request.getParameter("ZZCS").getBytes("iso-8859-1"), "utf-8");
String ZS = new String(request.getParameter("ZS").getBytes("iso-8859-1"), "utf-8");
String ZYCS = new String(request.getParameter("ZYCS").getBytes("iso-8859-1"), "utf-8");
String TS = new String(request.getParameter("TS").getBytes("iso-8859-1"), "utf-8");
String DDLS = new String(request.getParameter("DDLS").getBytes("iso-8859-1"), "utf-8");
String CFLS = new String(request.getParameter("CFLS").getBytes("iso-8859-1"), "utf-8");
String JTLS = new String(request.getParameter("JTLS").getBytes("iso-8859-1"), "utf-8");
String BZLS = new String(request.getParameter("BZLS").getBytes("iso-8859-1"), "utf-8");
String XCS = new String(request.getParameter("XCS").getBytes("iso-8859-1"), "utf-8");
String ZCS = new String(request.getParameter("ZCS").getBytes("iso-8859-1"), "utf-8");
String PKS = new String(request.getParameter("PKS").getBytes("iso-8859-1"), "utf-8");
String BLCS = new String(request.getParameter("BLCS").getBytes("iso-8859-1"), "utf-8");
String ZDZS = new String(request.getParameter("ZDZS").getBytes("iso-8859-1"), "utf-8");
String CZ = new String(request.getParameter("CZ").getBytes("iso-8859-1"), "utf-8");
String CS = new String(request.getParameter("CS").getBytes("iso-8859-1"), "utf-8");
String PKCC = new String(request.getParameter("PKCC").getBytes("iso-8859-1"), "utf-8");
String KCLY = new String(request.getParameter("KCLY").getBytes("iso-8859-1"), "utf-8");
String XCDD = new String(request.getParameter("XCDD").getBytes("iso-8859-1"), "utf-8");
String CZ1 = new String(request.getParameter("CZ1").getBytes("iso-8859-1"), "utf-8");
String CS1 = new String(request.getParameter("CS1").getBytes("iso-8859-1"), "utf-8");
String DDCC = new String(request.getParameter("DDCC").getBytes("iso-8859-1"), "utf-8");
String XHAP = new String(request.getParameter("XHAP").getBytes("iso-8859-1"), "utf-8");
String ZCDD = new String(request.getParameter("ZCDD").getBytes("iso-8859-1"), "utf-8");
String QX = new String(request.getParameter("QX").getBytes("iso-8859-1"), "utf-8");
String KCLY1 = new String(request.getParameter("KCLY1").getBytes("iso-8859-1"), "utf-8");
String GYCC = new String(request.getParameter("GYCC").getBytes("iso-8859-1"), "utf-8");

User u=new User();
u.setZJ(zj);
u.setFX(fx);
u.setCC(cc);
u.setDDSK(ddsk);
u.setD1(d1);
u.setD2(d2);
u.setD3(d3);
u.setD4(d4);
u.setD5(d5);
u.setD6(d6);
u.setD7(d7);
u.setD8(d8);
u.setD9(d9);
u.setD10(d10);
u.setD11(d11);
u.setKC(kc);
u.setHJ(hj);
u.setFX1(SU1);
u.setCC1(SU2);
u.setCFSK1(SU3);
u.setBNCL1(SU4);
u.setHJ1(SU5);
u.setSJ(SJ);
u.setZSDD(ZSDD);
u.setZSCF(ZSCF);
u.setZSJC(ZSJC);
u.setZTDD(ZTDD);
u.setZTCF(ZTCF);
u.setZTJC(ZTJC);
u.setZTS(ZTS);
u.setZZCS(ZZCS);
u.setZS(ZS);
u.setTS(TS);
u.setZYCS(ZYCS);
u.setDDLS(DDLS);
u.setCFLS(CFLS);
u.setJTLS(JTLS);
u.setBZLS(BZLS);
u.setXCS(XCS);
u.setZCS(ZCS);
u.setPKS(PKS);
u.setBLCS(BLCS);
u.setZDZS(ZDZS);
u.setCZ(CZ);
u.setCS(CS);
u.setPKCC(PKCC);
u.setKCLY(KCLY);
u.setXCDD(XCDD);
u.setCZ1(CZ1);
u.setCS1(CS1);
u.setDDCC(DDCC);
u.setXHAP(XHAP);
u.setZCDD(ZCDD);
u.setQX(QX);
u.setKCLY1(KCLY1);
u.setGYCC(GYCC);
UserDao ud=new UserDao();
ud.insert(u);
%>
<%=XCDD %>
<script>
alert("111");
//window.location.href="bjh.html";
</script>
</body>
</html>