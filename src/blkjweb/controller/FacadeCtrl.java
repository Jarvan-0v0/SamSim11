package blkjweb.controller;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.blkj.encryption.PasswordHash;
import org.blkj.enhance.EhcacheUtils;
import org.blkj.utils.PageTool;
import org.blkj.utils.SendEmail;
import org.blkj.utils.StringTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;

import blkjweb.service.*;
import blkjweb.shiro.ShiroTokenExt;
import blkjweb.utils.Console;
import blkjweb.utils.Const;
import blkjweb.utils.ValidateCode;

@Controller
public class FacadeCtrl extends AbstractBase
{
	@Resource
	private SystemServiceImp systemService;
	@Resource
	private UserServiceImp userService;

	//生成登录页面的验证码： 方法1
	@RequestMapping("/validateCode")
	public void validateCode(HttpServletRequest request, HttpServletResponse response)
	{
		// 设置浏览器不缓存本页
		response.setHeader("Cache-Control", "no-cache");
		// 生成验证码，写入用户session
		String verifyCode = ValidateCode.generateTextCode(ValidateCode.TYPE_NUM_ONLY, 4, null);
		
		request.getSession().setAttribute(Const.SESSION_VALIDATE_CODE, verifyCode);
		// 输出验证码给客户端
		response.setContentType("image/jpeg");
		BufferedImage bim = ValidateCode.generateImageCode(verifyCode, 90, 30, 3, true, Color.WHITE, Color.BLACK, null);
		try {
			ImageIO.write(bim, "JPEG", response.getOutputStream());
		} catch (IOException e) {
			Console.showMessage(e.getMessage());
		}
	}
	/*
		//生成验证码方法2
		@RequestMapping
		public void generate(HttpServletResponse response){
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			String code = ValidateCode.drawImg(output);
			Subject currentUser = SecurityUtils.getSubject();  
			Session session = currentUser.getSession();
			session.setAttribute(Const.SESSION_VALIDATE_CODE, code);
			try {
				ServletOutputStream out = response.getOutputStream();
				output.writeTo(out);
			} catch (IOException e) {
		    	MyLogger.showMessage(e.getMessage());
			}
	  }*/

	/**
      @ResponseBody标签作用是将返回的对象作为响应，发送给页面. 相当于转json  
               管理员的密码由前端直接利用MD5加密，其他密码由后台加密
               权限控制：目前根据不同权限，前端启动不同的菜单。理想做法：前端完全一样： 后端检查是否有权进行操作或根据权限动态生成菜单
	 */
	@RequestMapping(value="/login")  
	@ResponseBody
	public Object login() 
	{	//Shiro提供了 SecurityManager 实例的保存和获取的方法，以及创建Subject的方法
		Subject currentUser = SecurityUtils.getSubject();  
		Session session = currentUser.getSession();
				
		String sessionCode = (String)session.getAttribute(Const.SESSION_VALIDATE_CODE);		//获取session中的验证码

		PageTool pd = new PageTool();
		pd = this.getPageData();

		String submitCode = pd.getString("validateCode");

		String result = "success";	
		String role = "";
		String menuName = "";
		String tableName = "user";//保存非超级管理员的账户信息表
		Map<String,Object> map = new HashMap<String,Object>();
		//检查验证码 
		/*注掉此部分，取消验证码功能
		if ( StringUtils.isEmpty(submitCode) ||	
				!StringUtils.equals(sessionCode,submitCode.toLowerCase())){
			result = "codeerror"; //验证码不合法
		}
		else {//提交用户名/密码进行认证
		*/
			String username = pd.getString("username");
			String password = pd.getString("password");//暂不考虑加密
			//password = MD5Encrypter.encryptMD5(password);//b1a7caa440d1e61e100069b458f07b28=gly
			if(username.equals("AdMin"))//依据不同库表的名字采用不同的加密
				tableName = "admin";//保存超级管理员的账户信息表
			/*else
				password = PasswordHash.encrypt(password);
			去掉密码加密*/
			ShiroTokenExt token = new ShiroTokenExt(username, password,tableName);
			token.setRememberMe(true);

			//提交认证和凭据给身份验证系统
			//如果认证成功，会没有返回，也没有例外，通过。 如果认证失败，会拋出例外，可以在程序中捕获并处理
			try {//验证用户名和密码是否正确
				currentUser.login(token);//自动跳转到MonitorRealm的1、doGetAuthenticationInfo
			} 
			catch (AuthenticationException e) {
				result = "loginerror"; 	
			}
			
			/**用户名（user/admin）-> 角色(userrole) -> 权限(rolepermission)*/
			//或缺特定人员的角色编码和权限编码。桥梁系统只支持一个角色，一个角色只有一个权限
			if(currentUser.isAuthenticated()){//认证通过，合法用户  2、doGetAuthorizationInfo
				//是否有role1这个角色
				/* 授权检查。系统自动跳转到2、MonitorRealm的doGetAuthorizationInfo
		        if(currentUser.hasRole("r10")){
		        	System.out.println("有角色role1");
		        }else{
		        	System.out.println("没有角色role1");
		        }
		        if(currentUser.isPermitted("p10")){p10表示管理员的权限
				 */
				
				//这里不使用shiro的授权功能
				//获取授权用户名
				String user = (String)currentUser.getPrincipal();
				//依据用户编码(工号)获取其角色编码				
				Set<String> roleSet = new HashSet<String>();
				roleSet = userService.findRoles(user);//可能为null；（roleid FROM userrole）
				String roleCode = "";
				if (! StringTool.isNull(roleSet)){ /**获取角色编码*/
					Iterator<String> it = roleSet.iterator();
					while (it.hasNext()) {//
						String str = it.next();
						if(roleCode.length() == 0)
							roleCode = str;
						else
							roleCode = "," + str;
					}
				}
				session.setAttribute(Const.SESSION_USERROLECODE,roleCode);
				
				Set<String> roleName = new HashSet<String>();
				roleName = userService.findRoleName(roleSet);//依据角色编码获取角色名字（name FROM role）	
				if (! StringTool.isNull(roleName)){
			        /**获取角色中文名字*/  		
					Iterator<String> it = roleName.iterator();
					while (it.hasNext()) {//
						String str = it.next();
						if(role.length() == 0)
							role = str;
						else
							role = "," + str;
					}
					session.setAttribute(Const.SESSION_USERROLE, role);//服务端用(获取个人信息页面)
					role = username + "(" + role + ")";//传给页面的内容：格式为：用户名（角色名）

					 /**依据特定人的角色编码获取权限编码*/
					Set<String> persID = new HashSet<String>();
					persID = userService.findPermissions(roleSet);//(permissionid FROM rolepermission)
					if (! StringTool.isNull(persID)){
						it = persID.iterator();
						while (it.hasNext()) {
							String str = it.next();
							if(menuName.length() == 0)
								menuName = str;
							else
								menuName = "," + str;
						}
					}
					//权限编码. 用于服务端对用户是否具有访问摸操作的权限901111
					session.setAttribute(Const.SESSION_USERAUTH, menuName);
					
					//权限编码获取权限名字，这里是主页面的左菜单的名字	
					/*Set<String> persName = new HashSet<String>();
					persName = userService.findPersName(persID);
					if (! StringTool.isNull(persName)){
						it = persName.iterator();
						while (it.hasNext()) {
							String str = it.next();
							if(menuName.length() == 0)
								menuName = str;
							else
								menuName = "," + str;
						}
					}*/
				}

				session.removeAttribute(Const.SESSION_VALIDATE_CODE);
				session.setAttribute(Const.SESSION_USERNAME,username);
				
				//查询此人所述单位； 用于保证工队人员只能访问自己所属工队的内容
				String unitcode = "0";
				Map<String, Object> unitMap = new HashMap<String, Object>();
				String where = "id='" + username + "'";
				unitMap = systemService.queryOne4Items("userinfo","unitid", where);//最多一条记录
				if(! StringTool.isNull(unitMap))
					unitcode = (String)unitMap.get("unitid");
				session.setAttribute(Const.SESSION_UNITID,unitcode);
			}//认证通过，合法用户

			if (StringTool.isNull(role))
				role = "用户角色不详";
			session.setAttribute(Const.SESSION_LOG4JNAME,role);//用于日志写入
			
			if (StringTool.isNull(menuName))
				menuName = "";//权限不对
			
			//写日志信息
			String status = "成功";
			if(result.endsWith("error")) status = "失败";
			//操作用户 "管理员"、"超级管理员"、"普通用户"、"非法用户"
			map.put("name",role);
			map.put("level", "访问");//操作类别  "访问" "离开"
			map.put("type", "0");//操作类别  "访问" "离开"
			map.put("message", "用户登录");//操作描述或模块
			map.put("status",status);//操作状态 "成功" "失败"，异常
			map.put("ip", getRemoteIP());//ip地址
			Console.showMessage(map);

		//}//提交用户名/密码进行认证

		map = new HashMap<String,Object>();
		map.put("result", result);
		map.put("menuName", menuName);
		map.put("tip", role);
        
		return returnObject(new PageTool(), map);
	}

	//用户注销
	@RequestMapping(value="/logout")  
	@ResponseBody //作用是将返回的对象作为响应，发送给页面
	public Object logout() 
	{
		Map<String,Object> map = new HashMap<String,Object>();
		//shiro管理的session
		Subject currentUser = SecurityUtils.getSubject();  
		Session session = currentUser.getSession();

		//写日志信息
		map.put("name",session.getAttribute(Const.SESSION_LOG4JNAME));
		map.put("level", "离开");//操作类别  "访问" "离开"
		map.put("message", "用户离开");//操作描述或模块
		map.put("status","成功");//操作状态 "成功" "失败"
		map.put("ip", getRemoteIP());//ip地址
		map.put("type", "0");//操作类别  "访问" "离开"
		Console.showMessage(map);
		
		session.removeAttribute(Const.SESSION_LOG4JNAME);
		session.removeAttribute(Const.SESSION_USERNAME);
		session.removeAttribute(Const.SESSION_UNITID);
		session.removeAttribute(Const.SESSION_USERROLECODE);
		
		//shiro销毁登录
		Subject subject = SecurityUtils.getSubject(); 
		subject.logout();

		map = new HashMap<String,Object>();
		map.put("result", "logout");

		return returnObject(new PageTool(), map);
	}

	//获取用户个人信息,且只能修改密码
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/personInfo")
	@ResponseBody
	public Map<String, Object> getPersonInfo()
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();
		String id = pd.getString("id"); //用户编码
		String tableName = "userinfo";

		Map<String,Object> map = new HashMap<String,Object>();
		if(!StringTool.isNullEmpty(id)){
			map = systemService.queryOne(tableName,"id", id);//所有人员的信息都存放在userinfo表中
			if(id.equals("admin")){//管理员的用户名固定位：admin
				map.put("password","***");
			}
			else{
				tableName = "user";
				//map = systemService.queryOne(tableName,"id", id);//从user表中查找用户密码
				//map.put("password",PasswordHash.decrypt((String)map.get("password")));
			}
			/*
			String unitid = (String)map.get("unitid");//userinfo
			
			//对于单位的处理 public static Map<String,Object> convertMap
			List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
			
			lists = (List<Map<String, Object>>) EhcacheUtils.get("DANWEI");
			
			if (lists == null){
				String where = "";//获取所属工队的单位信息  danweileibie = 10
				//原型：List<Map<String, Object>> query(String table, String item, String where)
				lists = systemService.query("danwei","id,name", where);
				EhcacheUtils.put("DANWEI",lists);
			}
			
			if(StringTool.isNullEmpty(unitid))
				map.put("unitid","");
			else
				map.put("unitid", getNameByID4List(lists,"id",unitid, "name"));
			*/
			Subject currentUser = SecurityUtils.getSubject();  
			Session session = currentUser.getSession();
			map.put("role",session.getAttribute(Const.SESSION_USERROLE));
		}
		return map;
	}
   

	//用户（含管理员）只能修改密码
	@RequestMapping(value="/modifyPW")  
	@ResponseBody //将内容或者对象作为HTTP响应正文返回给页面；void Object
	public Object updatepw() 
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();//获取HttpServletRequest
		String id = pd.getString("id"); //用户编码
		String oldpw = pd.getString("oldpw");
		String newpw = pd.getString("newpw");
		String tableName = "user";
		if(id.equalsIgnoreCase("admin"))//依据不同库表的名字采用不同的加密
			tableName = "admin";
		else{
			oldpw = PasswordHash.encrypt(oldpw);
			newpw = PasswordHash.encrypt(newpw);
		}

		String info = "密码成功修改！";
		if (systemService.updatePW(tableName,id,oldpw,newpw) == -1){ //对指定用户的密码进行更新
			info = "密码修改失败！";
		}
		Map<String,String> map = new HashMap<String,String>();
		map.put("Message", info);
		return map;
		//return returnObject(new PageData(), map);
		/*	
				    ///方法二
					public void  updatepw( ){ 
						JSONObject jsonObject = new JSONObject();
						//jsonObject.accumulate("status", "success");
						jsonObject.accumulate("status", "error");
						jsonObject.accumulate("error", "信息添加失败！");
						String result = jsonObject.toString();
						MyLogger.showMessage("result:"+ result);
						printToClient(result);
					}
		 */
	}

	//找回密码
	@RequestMapping(value="/sendPWByEmail")  
	@ResponseBody
	public Object sendPWByEmail()
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();
		String username = pd.getString("username");
		String email = pd.getString("email"); 
		String tableName = "userinfo";
		String where = "id='" + username + "' AND email='" + email +"'";
		//List<Object> lists = new ArrayList<Object>();
		//lists = systemService.queryRaw(tableName,where);

		Map<String,Object> map = new HashMap<String,Object>();
		String result = "fail";
		if(systemService.query(tableName,where) != null){
			boolean sendFlag = false;
			String password = "***";
			tableName = "user";
			//从user表中查找用户密码
			map = systemService.queryOne(tableName, "id", username);
			if(map != null)
				password = PasswordHash.decrypt((String)map.get("password"));

			SendEmail mail =  new SendEmail();
			sendFlag = mail.sender(email,"密码见正文",password);

			if(sendFlag)
				result = "success";
		}
		map.put("result", result);

		return returnObject(new PageTool(), map);
	}

	//目前不用
	/**<li><a href="changeLocale.do?locale=zh_CN">中文</a></li>
		       <li><a href="changeLocale.do?locale=en">English</a></li>
	 */
	//动态切换语言
	@Autowired
	private LocaleResolver localeResolver;
	@RequestMapping("/changeLocale")
	public ModelAndView changeLocale(String locale,HttpServletRequest request,HttpServletResponse response)
	{
		Locale local = new Locale(locale);
		localeResolver.setLocale(request, response,local);
		ModelAndView mv = this.getModelAndView();
		mv.setViewName("login");
		return mv;
	}

}
