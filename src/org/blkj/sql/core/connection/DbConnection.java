package org.blkj.sql.core.connection;

import java.io.Serializable;
import java.sql.*;

import javax.naming.*;
import javax.sql.DataSource;
import org.logicalcobwebs.proxool.ProxoolException;
import org.logicalcobwebs.proxool.ProxoolFacade;
import org.logicalcobwebs.proxool.admin.SnapshotIF;
import org.springframework.context.ApplicationContext;

import blkjweb.utils.Console;
import blkjweb.utils.Const;

/**
 * 支持连接池： jdbc-pool、proxool、c3p0最为常见的三种JDBC连接池技术
 */
//2017
public class DbConnection implements Serializable
{
	private static final long serialVersionUID = 1L;

	public DbConnection(){	}
	
	/**从数据库连接池返回一个连接 */
	synchronized public static Connection getDefaultCon() 
	{
		Connection connection = null;
		try{
			if("Proxool".equalsIgnoreCase(Const.getDBCONPOOL_TYPE()))
			{// 使用proxool数据库连接池   DBPool是在proxool.xml中配置的连接池别名  
				connection = DriverManager.getConnection("proxool.DBPool");	//创建数据库连接
				showSnapshotInfo();//获取连接池中的连接信息
			}
			else if("HikariCP".equalsIgnoreCase(Const.getDBCONPOOL_TYPE()))
			{
				
				try {
					ApplicationContext ctx = AppContextUtil.getApplicationContext();
					DataSource ds = (DataSource)ctx.getBean("dataSource");
					connection = ds.getConnection();
					showSnapshotInfo();//获取连接池中的连接信息
				} catch (SQLException e) {
					Console.showMessage(DbConnection.class.getSimpleName(), e.getMessage(), e);
				}
			}
			else//其他也需要修订，暂时没有用
				if("C3P0".equalsIgnoreCase(Const.getDBCONPOOL_TYPE()))
				{//C3P0数据库连接池用法   
					InitialContext ctx = new InitialContext();
					DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/DBPool");
					connection = ds.getConnection();
				}
				else
					if("jdbc-pool".equalsIgnoreCase(Const.getDBCONPOOL_TYPE()))	
					{ //Tomcat 从 7.0 开始引入一个新的模块：Tomcat jdbc pool 推荐使用 以jdni方式使用
						//加载驱动  
						Context ctx=new InitialContext();
						DataSource ds=(DataSource)ctx.lookup("java:comp/env/jdbc/Tomcat_JDBC_Pool");//通过JNDI查找DataSource
						connection = ds.getConnection();
					}
					else
						if("OCI".equalsIgnoreCase(Const.getDBCONPOOL_TYPE()))
						{//oracle 的 OCI方式		
							Class.forName("oracle.jdbc.driver.OracleDriver");
							connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:mmfcj", "oa", "oa2869188");
						}

		}catch (SQLException e) {
			Console.showMessage(DbConnection.class.getSimpleName(),e.getMessage(), e);
		} catch (NamingException e) {
			Console.showMessage(DbConnection.class.getSimpleName(),e.getMessage(), e);
		} catch (ClassNotFoundException e) {
			Console.showMessage(DbConnection.class.getSimpleName(), e.getMessage(), e);
		} 
		try {
			System.out.print(connection.getClientInfo().toString());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return connection;
	}
	
	
	///** 显示连接池信息 
	private static int activeCount = 0;
	private static void showSnapshotInfo(){        
		try{  
			SnapshotIF snapshot = ProxoolFacade.getSnapshot("DBPool", true);        
			int curActiveCount=snapshot.getActiveConnectionCount();//获得活动连接数        
			int availableCount=snapshot.getAvailableConnectionCount();//获得可得到的连接数        
			int maxCount=snapshot.getMaximumConnectionCount() ;//获得总连接数        
			if(curActiveCount!=activeCount)//当活动连接数变化时输出的信息        
			{   
				String temp = "活动连接数:  " + curActiveCount+"  可得到的连接数:  "+ availableCount + "  总连接数:  " + maxCount;
				
				activeCount=curActiveCount;        
			}        
		}catch(ProxoolException e){        
			Console.showMessage(DbConnection.class.getSimpleName(), e.getMessage(), e);
		}        
	}  

}
