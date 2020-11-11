<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.sql.*"%>
<%@ page import="com.mysql.jdbc.Driver" %> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
<script src="https://cdn.staticfile.org/jquery/1.10.2/jquery.min.js"></script>
<link href="../scripts/css/framework.css" rel="stylesheet" />
<script src="../scripts/js/framework.js"></script>
<title>无标题文档</title>
<link rel="stylesheet" href="css/style.css" type="text/css" />
<link rel="stylesheet" href="css/bootstrap-theme.min.css" type="text/css" />
<link rel="stylesheet" href="css/bootstrap.min.css" type="text/css" />
<script src="https://cdn.staticfile.org/jquery/1.10.2/jquery.min.js"></script>
<script type="text/javascript">	
	var where = 0;												//用这个标记当前表格为哪个。where为0时表示cbTab1，where为1时表示cbTab2
	var sortFun = 0;											//用这个标记在save操作前究竟点了哪个按钮。sortFun为0时表示初始状态，为1时表示新增状态，为2时表示编辑状态
	function cb_ini(){							//初始化
		$("._cb_tab1 th").css({"text-align":"center","vertical-align":"middle","font-size":"17px"});
		$("._cb_tab1 td").css({"text-align":"center","vertical-align":"middle","background-color":"white","font-size":"16px"});
		$(".cBook_data div").eq(0).show().siblings().hide();
	}
	$(document).ready(function(){
		cb_ini();
		
	})
	/***********************************************起*******************************************************/
	var addCount = 0;					//计数，看当前是第几次点击增加按钮
	function btn_add(){
		sortFun = 1;
		if(where == 0){
			if(addCount == 0){
				$("input:checkbox").attr("checked",false);
			 	addCount = 1;
			}
			var obj = document.getElementById("cbTab1");
			obj.setAttribute("contenteditable", false);
			var trHTML = "<tr><td><input type='checkbox' name='checkData' onclick='inputClick()'></input></td><td>2020-</td><td>null</td><td>2020-</td><td>null</td><td>null</td><td>null</td><td>null</td><td>null</td><td>null</td><td>null</td><td>null</td><td>null</td><td>null</td><td>null</td><td>null</td>"
			var row = obj.insertRow(2);
			row.innerHTML = trHTML;
		 	$("input:checkbox[name=checkData]:first").attr("checked",true);
		 	$("input:checkbox[name=checkData]:not(:checked)").attr("onclick","return false");
		 	obj.setAttribute("contenteditable", true);
		 	cb_ini();
		 	inputClick();
		}
	}
	function btn_edit(){
		sortFun = 2;
		if(where == 0){
			var obj = document.getElementById("cbTab1");
			obj.setAttribute("contenteditable", true);
		}
	}
	function btn_save(){
		if(where == 0){				//当为扣修车及电报登记簿时且功能新增按钮被按下时执行以下代码
			var $objAdd = $("input:checkbox[name=checkData]:checked");
			var arrAdd = new Array();
			var count = $objAdd[0].parentNode.parentNode.childElementCount;
			var obj2 = document.form001;
			var list0 = "";
			var list1 = "";
			var list2 = "";
			var list3 = "";
			var list4 = "";
			var list5 = "";
			var list6 = "";
			var list7 = "";
			var list8 = "";
			var list9 = "";
			var list10 = "";
			var list11 = "";
			var list12 = "";
			var list13 = "";
			var list14 = "";
			var list15 = "";
			for(var i=0;i<$objAdd.length;i++){
				arrAdd[i] = new Array();
				for(var j=0;j<count-1;j++){
					arrAdd[i][j] = $objAdd[i].parentNode.parentNode.children[j+1].innerText;
				}
			}
			for(var i=0;i<$objAdd.length;i++){
				//这里将相关命令信息存入list中
				list1 += arrAdd[i][0] +"*";
				list2 += arrAdd[i][1] +"*";
				list3 += arrAdd[i][2] +"*";
				list4 += arrAdd[i][3] +"*";
				list5 += arrAdd[i][4] +"*";
				list6 += arrAdd[i][5] +"*";
				list7 += arrAdd[i][6] +"*";
				list8 += arrAdd[i][7] +"*";
				list9 += arrAdd[i][8] +"*";
				list10 += arrAdd[i][9] +"*";
				list11 += arrAdd[i][10] +"*";
				list12 += arrAdd[i][11] +"*";
				list13 += arrAdd[i][12] +"*";
				list14 += arrAdd[i][13] +"*";
				list15 += arrAdd[i][14] +"*";
			}
			//这里将相关命令信息list放入表单form001中
			obj2.data01.value = list1;
			obj2.data02.value = list2;
			obj2.data03.value = list3;
			obj2.data04.value = list4;
			obj2.data05.value = list5;
			obj2.data06.value = list6;
			obj2.data07.value = list7;
			obj2.data08.value = list8;
			obj2.data09.value = list9;
			obj2.data10.value = list10;
			obj2.data11.value = list11;
			obj2.data12.value = list12;
			obj2.data13.value = list13;
			obj2.data14.value = list14;
			obj2.data15.value = list15;
			//这里将表单form001即id为form2233的提交，此时数据转入后台dodata_cargoBook.jsp进行处理
			document.getElementById("form2233").submit();
			$("#cbTab1").find("td").attr("contentEditable",false);
			sortFun = 0;											//点击save时sortFun回归初始状态
		}
		addCount = 0;
	}
	function btn_delete(){
		var arrDelete = new Array();
		var listDelete = "";
		if(where == 0){
			var $objDelete = $("input:checkbox[name=checkData]:checked");
			for(var i=0;i<$objDelete.length;i++){
				arrDelete[i] = $objDelete[i].parentNode.parentNode.children[4].innerText;
			}
			for(var i=0;i<$objDelete.length;i++){				//将要删除的数据的车号车次数据用*隔开并存在一个字符串中
				listDelete += arrDelete[i] +"*";
			}
			var obj = document.form001;
			obj.data00.value = listDelete;
			document.getElementById("form2233").submit();
		}
	}
	function btn_cancel(){
		Replace();
		if(where == 0){
			
		}
	}
	function btn_print(){
		alert("本大爷是你想点就能点的？");
	}
	
	/***********************************************止*******************************************************/
	function inputClick(){
		var $obj = $("input:checkbox[name=checkData]");
		var $obj2 = $("input:checkbox[name=checkData]:checked")
		var count = $obj[0].parentNode.parentNode.childElementCount;
		for(var i=0;i<$obj.length;i++){
			for(var j=0;j<count;j++){
				 $obj[i].parentNode.parentNode.children[j].style.backgroundColor = "white";
			}
		}
		for(var i=0;i<$obj2.length;i++){
			for(var j=0;j<count;j++){
				 $obj2[i].parentNode.parentNode.children[j].style.backgroundColor = "yellow";
			}
		}
	}
	function btn_change1(){
		$(".cBook_data div").eq(1).show().siblings().hide()
		if($(".cb_srch p").html() == "********************************************当前位置：扣修车及电报登记簿"){
			$(".cb_srch p").html("********************************************当前位置：货检工作日志")
		}
		where = 1;
		
	}
	function btn_change2(){
		$(".cBook_data div").eq(0).show().siblings().hide();
		if($(".cb_srch p").html() == "********************************************当前位置：货检工作日志"){
			$(".cb_srch p").html("********************************************当前位置：扣修车及电报登记簿")
		}
		where = 0;
	}
</script>
</head>

<body>
<div class="cBook_all">
	<div class="tools_bar" style="margin-top: 1px; margin-bottom: 0px;">
	   	<div class="PartialButton" style="position:absolute;width:100%;">
			<script type="text/javascript">
			    $(document).ready(function () {
			        PartialButton("/pages/cargoLog/sam_cargoLog_button.json");  
			    });
			</script>
		</div>
	</div>
	<div class="cb_srch">
		<table class="form-find" style="height: 45px;position:absolute;z-index:2;">
			<tr>
		    	<th class="formTitle">车次：</th>
		        <td class="formValueShort"> <input id="cc" type="text" class="txt" style="height:25px;"/></td>
		        <th class="formTitle">车号：</th>
		        <td class="formValueShort"> <input id="ch" type="text" class="txt" style="height:25px;"/></td>
		        <td><input id="btnSearch" type="button" class="btnSearch" value="搜 索" onclick="btn_Search()" /> </td>
		        <td><p style="font-size:16px;">********************************************当前位置：扣修车及电报登记簿</p></td>
		    </tr>
		</table>
	</div>

	<div class="cBook_data">
		<div>
			<table id="cbTab1" border="1" class="_cb_tab1 CD">
				<tr style="font-size:16px;background-color:#ECE9D8;">
					<th rowspan="2" style="width:20rem">选择</th>
					<th rowspan="2" style="width:60rem">日期</th>
					<th rowspan="2" style="width:50rem">车次</th>
					<th rowspan="2" style="width:60rem">到发时间</th>
					<th rowspan="2" style="width:80rem">车种车号</th>
					<th rowspan="2" style="width:50rem">货物品名</th>
					<th rowspan="2" style="width:50rem">发站</th>
					<th rowspan="2" style="width:50rem">到站</th>
					<th rowspan="2" style="width:400rem">发现问题</th>
					<th colspan="2" style="width:40rem">处理情况</th>
					<th colspan="3" style="width:60rem">补封、电报登记</th>
					<th rowspan="2" style="width:50rem">主送单位</th>
					<th rowspan="2" style="width:50rem">货检员</th>
				</tr>
				<tr style="font-size:16px;background-color:#ECE9D8;">
					<th style="width:20rem">在列</th>
					<th style="width:20rem">甩车</th>
					<th style="width:20rem">补封号码</th>
					<th style="width:20rem">记号</th>
					<th style="width:20rem">电报号</th>
				</tr>
			<%  
						try{
							Class.forName("com.mysql.cj.jdbc.Driver");  ////驱动程序名
							String url = "jdbc:mysql://localhost:3306/book?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone = GMT"; //数据库名
							String username = "root";  //数据库用户名
							String password = "123456";  //数据库用户密码
							Connection conn = DriverManager.getConnection(url, username, password);  //连接状态
							if(conn != null){  
									Statement stmt = null;  
									ResultSet rs = null;  
									String sql = "SELECT * FROM cargobook order by 日期 desc";  //查询语句
									stmt = conn.createStatement();  
									rs = stmt.executeQuery(sql);  
									while (rs.next()) {
					%>
					<tr>  
						<td><input type="checkbox" name='checkData'	onclick="inputClick()"></input></td>  
						<td><%=rs.getString("日期") %></td>  
						<td><%=rs.getString("车次") %></td>  
						<td><%=rs.getString("到发时间") %></td>  
						<td><%=rs.getString("车种车号") %></td>  
						<td><%=rs.getString("货物品名") %></td>  
						<td><%=rs.getString("发站") %></td>    
						<td><%=rs.getString("到站") %></td>
						<td><%=rs.getString("发现问题") %></td>
						<td><%=rs.getString("在列") %></td>
						<td><%=rs.getString("甩车") %></td>
						<td><%=rs.getString("补封号码") %></td>
						<td><%=rs.getString("记号") %></td>
						<td><%=rs.getString("电报号") %></td>
						<td><%=rs.getString("主送单位") %></td>
						<td><%=rs.getString("货检员") %></td>
					</tr>
					<%
								}  
							}else{
								out.print("数据库连接失败！");
							}
						}catch(Exception e){
							out.print(e);
						}
					%>
			</table>
		</div>
		<div>
			<table id="cbTab2" border="1" class="_cb_tab1 CD">
				<tr style="font-size:16px;background-color:#ECE9D8;">
					<th colspan="24">
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						班级：2班&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						学号：398923453&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						实训编号：95223&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						日期：2020年8月23日
					</th>
				</tr>
				<tr style="font-size:16px;background-color:#ECE9D8;">
					<th rowspan="2" style="width:10rem;">选择</th>
					<th rowspan="2" style="width:20rem;">序号</th>
					<th colspan="2" style="width:80rem;">车次</th>
					<th rowspan="2" style="width:20rem;">股道</th>
					<th rowspan="2" style="width:20rem;">辆数</th>
					<th rowspan="2" style="width:20rem;">组别</th>
					<th colspan="2" rowspan="2" style="width:60rem;">值班员通知时间</th>
					<th colspan="2" style="width:60rem;">作业时间</th>
					<th rowspan="2" style="width:20rem;">作业端</th>
					<th rowspan="2" style="width:50rem;">车号</th>
					<th rowspan="2" style="width:20rem;">汇报情况</th>
					<th rowspan="2" style="width:20rem;">列车类别</th>
					<th colspan="3" style="width:120rem;">待检时间</th>
					<th colspan="2" style="width:60rem;">发车</th>
					<th rowspan="2" style="width:30rem;">重点车数</th>
					<th rowspan="2" style="width:20rem;">在列</th>
					<th rowspan="2" style="width:20rem;">甩车</th>
					<th rowspan="2" style="width:80rem;">备注</th>
				</tr>
				<tr style="font-size:16px;background-color:#ECE9D8;">
					<th style="width:40rem;">到达</th>
					<th style="width:40rem;">出发</th>
					<th style="width:30rem;">开始</th>
					<th style="width:30rem;">结束</th>
					<th style="width:20rem;">起</th>
					<th style="width:20rem;">止</th>
					<th style="width:80rem;">原因</th>
					<th style="width:30rem;">车次</th>
					<th style="width:30rem;">时间</th>
				</tr>
				<%  
						try{
							Class.forName("com.mysql.cj.jdbc.Driver");  ////驱动程序名
							String url = "jdbc:mysql://localhost:3306/book?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone = GMT"; //数据库名
							String username = "root";  //数据库用户名
							String password = "123456";  //数据库用户密码
							Connection conn = DriverManager.getConnection(url, username, password);  //连接状态
							if(conn != null){  
									Statement stmt = null;  
									ResultSet rs = null;  
									String sql = "SELECT * FROM cargolog order by 序号";  //查询语句
									stmt = conn.createStatement();  
									rs = stmt.executeQuery(sql);  
									while (rs.next()) {
					%>
					<tr>  
						<td><input type="checkbox" name='checkData'	onclick="inputClick()"></input></td>  
						<td><%=rs.getString("序号") %></td>  
						<td><%=rs.getString("到达") %></td>  
						<td><%=rs.getString("出发") %></td>  
						<td><%=rs.getString("股道") %></td>  
						<td><%=rs.getString("辆数") %></td>  
						<td><%=rs.getString("组别") %></td>    
						<td style="width:30rem;"><%=rs.getString("值班员通知时间1") %></td>
						<td style="width:30rem;"><%=rs.getString("值班员通知时间2") %></td>
						<td><%=rs.getString("开始") %></td>
						<td><%=rs.getString("结束") %></td>
						<td><%=rs.getString("作业端") %></td>
						<td><%=rs.getString("车号") %></td>
						<td><%=rs.getString("汇报情况") %></td>
						<td><%=rs.getString("列车类别") %></td>
						<td><%=rs.getString("起") %></td>
						<td><%=rs.getString("止") %></td>
						<td><%=rs.getString("原因") %></td>
						<td><%=rs.getString("车次") %></td>
						<td><%=rs.getString("时间") %></td>
						<td><%=rs.getString("重点车数") %></td>
						<td><%=rs.getString("在列") %></td>
						<td><%=rs.getString("甩车") %></td>
						<td><%=rs.getString("备注") %></td>
						
					</tr>
					<%
								}  
							}else{
								out.print("数据库连接失败！");
							}
						}catch(Exception e){
							out.print(e);
						}
					%> 
			</table>
		</div>
	</div>
</div>
<form id="form2233" name="form001" action="dodata_cargoBook.jsp" method="post" style="display:none;">
		日期：<input name="data01"></input>
		车次：<input name="data02"></input>
		到发时间：<input name="data03"></input>
		车种车号：<input name="data04"></input>
		货物品名：<input name="data05"></input>
		发站：<input name="data06"></input>
		到站：<input name="data07"></input>
		发现问题：<input name="data08"></input>
		在列：<input name="data09"></input>
		甩车：<input name="data10"></input>
		补封号码：<input name="data11"></input>
		记号：<input name="data12"></input>
		电报号：<input name="data13"></input>
		主送单位：<input name="data14"></input>
		货检员：<input name="data15"></input>
		需要删除命令的车种车号：<input name="data00">
		<hr>
</form>
</body>
</html>