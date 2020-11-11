package org.blkj.sql.core.connection;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**支持Proxool配置多个连接池即数据源的辅助类*/
	
public class AppContextUtil 
{
	private static ApplicationContext applicationContext;
	static {
		if (applicationContext == null)
			applicationContext = rebuildApplicationContext();
	}
	/**
	 * 重新构建Spring应用上下文环境
	 *
	 * @return ApplicationContext
	 */
	public static ApplicationContext rebuildApplicationContext() {
		return new ClassPathXmlApplicationContext("resources/spring/spring.xml");
	}

	/**
	 * 获取Spring应用上下文环境
	 */
	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	/**
	 * 简单的上下文环境测试
	 */
	public static void main(String[] args) {
		rebuildApplicationContext();
		if (applicationContext == null) {
			System.out.println("ApplicationContext is null");
		} else {
			System.out.println("ApplicationContext is not null!");
		}
	}
}
/* Spring 配置多数据源实现数据库读写分离
 *  具体到开发中，如何方便的实现读写分离呢?目前常用的有两种方式：
　　1 第一种方式是我们最常用的方式，就是定义2个数据库连接，一个是MasterDataSource,另一个是SlaveDataSource。更新数据时我们读取MasterDataSource，查询数据时我们读取SlaveDataSource。
　　2 第二种方式动态数据源切换，就是在程序运行时，把数据源动态织入到程序中，从而选择读取主库还是从库。主要使用的技术是：annotation，Spring AOP，反射
 */

