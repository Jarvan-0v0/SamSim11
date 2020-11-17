/* HTML5
   sessionStorage为临时保存，而localStorage为永久保存 
   sessionStorage的生命周期为，某个用户浏览网站时，从进入到离开的这段时间。
   localStorage将数据保存在客户端本地的硬件设备（通常指硬盘，当然可以是其他的硬件设备）中，即是浏览器被关闭了，该数据仍然存在，下次打开浏览器访问网站时，仍然可以继续使用
*/
function login(){
	if(check()){
		var username = $("#username").val();
		var password = $("#password").val();
		var code = $("#validateCode").val();
		var workType = $("#workType").val();
		//var userid = $("#userID").val();userID:userid, 
		if(username.toLocaleLowerCase() == "admin")
			password = $.md5(password);// CryptoJS.MD5
		$.ajax({
			url:"login.do",
			data: {username:username, password:password, validateCode:code},  
			type: "POST",
			dataType: 'json',  
			cache: false,
			error:function(data){  
				tipDialog1('系统启动有错，请查看系统配置文件!');
			},  
			success:function(data){
				if("success" == data.result){
					//苹果手机不支持
					//sessionStorage.setItem("USERNAME", username);
					$.cookie("USERNAME", username);
					$.cookie("TIP", data.tip);
					
					var perStr = data.menuName;//权限编码 901111
					if(perStr == null || perStr.length == 0)
						tip("username","用户访问权限不对!",1,1);
					else{
						var authModuleId = perStr.substr(0,2);
						var authOperation = perStr.substr(2,4);
						$.cookie("AUTHMODULE",authModuleId);
						$.cookie("AUTHOP",authOperation);
						var leftMenu = "";
						if(workType == 0 && authModuleId == "90"){
							leftMenu = "admin_menu.json";
						}
						//else if(authModuleId == "90")
						//	leftMenu = "admin_menu.json";
						//else if(authModuleId  == "10")
						//	leftMenu = "norm_menu.json";
						else if(authModuleId=="20")
					        leftMenu = "Browser_menu.json";
						else if(workType == 1){
							leftMenu = "/menu/zhibanzz.json";
						}
						else if(workType == 2){
							leftMenu = "/menu/chezhandd.json";
						}
						else if(workType == 3){
							leftMenu = "/menu/tuofengdd.json";
						}
						else if(workType == 4){
							leftMenu = "/menu/fengweidd.json";
						}
						else if(workType == 5){
							leftMenu = "/menu/daodach.json";
						}
						else if(workType == 6){
							leftMenu = "/menu/chufach.json";
						}
						else if(workType == 7){
							leftMenu = "/menu/huojianzb.json";
						}
						else if(workType == 8){
							leftMenu = "/menu/liejianzb.json";
						}
						else if(workType == 9){
							leftMenu = "/menu/chezhanzb.json";
						}
						else if(workType == 10){
							leftMenu = "/menu/tuofengqz.json";
						}
						else if(workType == 11){
							leftMenu = "/menu/fengweiqz.json";
						}
						else if(workType == 12){
							leftMenu = "/menu/chezhantj.json";
						}
						else if(workType == 13){
							leftMenu = "/menu/zhulidd.json";
						}
						else if(workType == 14){
							leftMenu = "/menu/xinhaoy.json";
						}
						if(leftMenu.length == 0)
							tip("username","用户访问权限不对!",1,1);
						else{
							$.cookie("MENUNAME",leftMenu);
							window.location.href="/SamSim11/pages/facade/facade.html";
						}
					}
				}
				else if("loginerror" == data.result){
					tip("username","用户名或密码有误!",1,1);
				}
				else if("codeerror" == data.result){
					tip("validateCode","验证码输入有误!",1,1);
				}
			}
		});  
	}  
}
//客户端校验 side : 2   尾部显示
function check() {
	if ($("#username").val() == "") {
		tip("username","请输入用户名！",1,1);
		return false;
	} else {
		$("#username").val(jQuery.trim($('#username').val()));
	}
	if ($("#password").val() == "") {
		tip("password","请输入密码！",1,1);
		return false;
	}
	if ($("#validateCode").val() == "") {
		tip("validateCode","请输入验证码！",1,1);
		return false;
	}
	return true;
}

function sendPWByEmail() {
	var email =$("#get-pwd-input").val();
	if (isEmail(email) == false){
		tip("get-pwd-input","请输入合法邮箱地址！",1,1);
		return;
	}
	var username = $("#username").val();
		
	$.ajax({
		url:"sendPWByEmail.do",
		data: {username:username, email:email},  
		type: "POST",
		dataType: 'json',  
		cache: false,
		error:function(data){  
			tipDialog1('系统启动有错，请查看系统配置文件!');
		},  
		success:function(data){
			if("success" == data.result){
				tipDialog1('密码已发送到您注册的邮箱!');
			}
			else if("fail" == data.result){
				tipDialog1('邮件未能正确发送或用户信息有误，稍后重试!');
			}
			$('#findPwdModal').modal('hide');
		}
	});  
}


function register()
{
 	var email =$("#usernameR").val();
  	$.ajax({
  		    url:"register.do",
          	data: {email:email},  
  	        type: "POST",
          	dataType: 'json',  
       		cache: false,
          	error:function(data){  
          		alert("错");
          		//tipDialog('系统启动有错，请查看系统配置文件!');
          	},  
          	success:function(data){
          		alert("请查看信箱");
          		$('#reg-box').fadeOut('slow').addClass('hide');
          		$('#login-box').fadeIn('slow').removeClass('hide');
  		    }
     });  
 }
 function Right()
 {
	 // var url= "/pages/common/attention.html"; 
	  //DialogH5(url, "right55","关于", 303, 333);
	 var str = " ";
     tipDialog("用户协议", str);
 }