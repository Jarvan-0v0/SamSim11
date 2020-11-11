package blkjweb.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import org.blkj.config.ReadPropertiesFile;
import org.blkj.encryption.DBDecrypt;
import org.blkj.utils.StringTool;
import org.springframework.web.WebApplicationInitializer;
import blkjweb.utils.Const;

/**最近设计了一个“权限管理系统”，在用户登录的时候就需要知道用户对资源所拥有的权限，
 * 如果每个用户在登录的时候去查询对应的表（8张），因此对数据库和系统都是极大的负担，
 * 因此想缓存用户权限相关表的数据，以后在其余用户登录的时候去找相关的缓存数据，而不是去查询表了*/

/*
 * servlet3.0+规范后，允许servlet，filter，listener不必声明在web.xml中，而是以硬编码的方式存在，实现容器的零配置。
 * ServletContainerInitializer：启动容器时负责加载相关配置.容器启动时会自动扫描当前服务中ServletContainerInitializer的实现类，并调用其onStartup方法
 * Spring为其提供了一个实现类：SpringServletContainerInitializer
 * 其中：WebApplicationInitializer才是我们需要关心的接口，我们只需要将相应的servlet，filter，listener等硬编码到该接口的实现类中即可
 * */

//原来方法：import javax.servlet.ServletContextEvent;import javax.servlet.ServletContextListener; 
//public class ContextListener implements ServletContextListener/*extends ContextLoaderListener*/
//解决No Spring WebApplicationInitializer types detected on classpath
public class ContextListener implements WebApplicationInitializer 
{
	private static final String SERVLET_PATH_ROOT = "/";//Servlet上下路径_根
	
	@Override
	public void onStartup(ServletContext arg0) throws ServletException 
	{
		//D:\programs\Tomcat8.0\webapps\easyui\
		String appDir = arg0.getRealPath(SERVLET_PATH_ROOT);
		String absPath = appDir + "WEB-INF" + Const.separator + "classes" + Const.separator ;

		Const.setAppAbsPath(appDir);
		
		ReadPropertiesFile config = new ReadPropertiesFile(absPath + "sysconfig.properties");
		if (config != null)	
		{
			String temp = config.getValue("logDir");
			Const.setLogDir(temp);
			
			temp = config.getValue("visitLog");
			if(! StringTool.isNullEmpty(temp))//不空
				Const.setVisitLog(temp);
			
			temp = config.getValue("runtimeLog");
			if(! StringTool.isNullEmpty(temp))//不空
				Const.setRuntimeLog(temp);

			temp = config.getValue("showTip");
			if(! StringTool.isNullEmpty(temp)){
				if (temp.equalsIgnoreCase("true"))
					Const.setShowTip(true);
				else
					Const.setShowTip(false);
			}
			//操作系统
			temp = config.getValue("os");
			Const.setOsType(temp);
			/*if (temp != null && temp.equalsIgnoreCase("Windows")){
				osType = temp;
				temp = config.readData("dir");
				if(temp != null && temp.length() != 0)
					logDir = temp;	
			}
			else
			{//Linux
				logDir = System.getProperty("user.home");
				temp = config.readData("LinuxMysqlbin");
				if(temp != null && temp.length() != 0)
					mysqldumpPath = temp;
			}*/
			
			
			//数据库备份
			temp = config.getValue("DB_Dir");
			Const.setDB_DIR(temp);
			temp = config.getValue("DB_binPath");
			Const.setDB_binPath(temp);
			
			temp = config.getValue("DBconnctionpool");
			System.out.println("连接池："+temp);
			Const.setDBCONPOOL_TYPE(temp);
			//非Proxool 或 Proxool读出的数据不对	
			/*if(!temp.equalsIgnoreCase("proxool")){
				config = new PropertiesConfig(absPath + "dbserver.properties");
				if (config != null)
				{
					ROOT = config.readData("USER");
					PASSWORD  = config.readData("PASSWORD");
					DATABASE  = config.readData("DBNAME");
					Log.debugLog("dbserver.properties:"+ PASSWORD+"_P—U_"+ROOT+"_U—D_"+DATABASE);
				}
			}
			*/
			
			temp = config.getValue("IP");
			Const.setIP(temp);
			temp = config.getValue("PORT");
			Const.setPORT(temp);
			
			temp = config.getValue("DBNAME");
			Const.setDATABASE(temp);
		    //数据库账号通过proxool加密访问所用代码
			String DBDecrypt = config.getValue("DBDecrypt");
			if(! StringTool.isNullEmpty(DBDecrypt))	{
				if (DBDecrypt.equalsIgnoreCase("true"))
					Const.setDB_ENCRYPT(true);
				else
					Const.setDB_ENCRYPT(false);
			}
			String password = config.getValue("password");//可能没有加密
			String root = config.getValue("root");
			if(Const.isDB_ENCRYPT()) {//Proxool中用户名密码加密
				DBDecrypt db = new DBDecrypt();
				root = db.decrypt(config.getValue("ROOT"));
				password = db.decrypt(config.getValue("PASSWORD"));
			}
			Const.setROOT(root);
			Const.setPASSWORD(password);
		
		
		}
		
		/*示例性代码
		 //Log4jConfigListener  
        servletContext.setInitParameter("log4jConfigLocation", "classpath:config/properties/log4j.properties");  
        servletContext.addListener(Log4jConfigListener.class);  
        //OpenSessionInViewFilter  
        OpenSessionInViewFilter hibernateSessionInViewFilter = new OpenSessionInViewFilter();  
        FilterRegistration.Dynamic filterRegistration = servletContext.addFilter(  
                "hibernateFilter", hibernateSessionInViewFilter);  
        filterRegistration.addMappingForUrlPatterns(  
                EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE), false, "/");  
        //DemoServlet  
        DemoServlet demoServlet = new DemoServlet();  
        ServletRegistration.Dynamic dynamic = servletContext.addServlet(  
                "demoServlet", demoServlet);  
        dynamic.setLoadOnStartup(2);  
        dynamic.addMapping("/demo_servlet");  
		 */
	}
	
	/*原来方法
	public void contextInitialized(ServletContextEvent event)
	{
		String appDir = event.getServletContext().getRealPath(SERVLET_PATH_ROOT);
		//D:\programs\Tomcat8.0\webapps\easyui\WEB-INF\classes\dbconfig.properties
		String absPath = appDir + "WEB-INF" + Const.separator + "classes" + Const.separator + "sysconfig.properties";
		
		ReadPropertiesFile config = new ReadPropertiesFile(absPath);
		if (config != null)	
		{
			String temp = config.readData("logDir");
			Const.setLogDir(temp);
			
			temp = config.readData("visitLog");
			if(! StringTool.isNullEmpty(temp))//不空
				Const.setVisitLog(temp);
			
			temp = config.readData("runtimeLog");
			if(! StringTool.isNullEmpty(temp))//不空
				Const.setRuntimeLog(temp);

			temp = config.readData("runtimeDisplay");
			if(! StringTool.isNullEmpty(temp))//不空
			{
				if (temp.equalsIgnoreCase("true"))
					Const.setRuntimeDisplay(true);
				else
					Const.setRuntimeDisplay(false);
			}
			
			//String showDebugTip = config.readData("showDebugTip");
			//Const.setShowDebugTip(StringTool.stringToInt(showDebugTip));//屏幕显示Logger.getLogger为1；写入文件为2；其他为关闭
		}
	  
	}
	public void contextDestroyed(ServletContextEvent event) { }
  */

}


/*  
   @Override  
    public void setServletContext(ServletContext arg0) {  
        //userMap容器存放 userList（用户数据）  
        List<User> userList = new ArrayList<User>();   
        try {  
            userList = userService.queryAllUser();  
            Map<String, Object> userMap = CachFactory.getInstance().createCach("userMap");  
			//一个工厂模式和单例模式的使用。文档最后有代码  
            for(User user:userList){  
                userMap.put(user.getUserid(), user);  
            }  
        } catch (Exception e) {  
            
        }  
    	MyLogger.showMessage(userList.size()); 
		MyLogger.showMessage("WEBINFDir0");
 */
/*
 * //Java应用中先要加载配置文件，否则谁知道你配置给谁用的
/*try {
	JAXPConfigurator.configure("D:\\Programs\\Tomcat 8.0\\webapps\\shirodemo\\WEB-INF\\classes\\proxool.xml", false);
	} catch (ProxoolException e) {
		MyLogger.showMessage("Proxool:"+e.getMessage()); 
	}
	//注册驱动类
	Class.forName("org.logicalcobwebs.proxool.ProxoolDriver"); 
 */