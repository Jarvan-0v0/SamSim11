package blkjweb.utils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.json.JSONObject;
import blkjweb.service.Log4jService;

public class Console 
{
	public Console() { }
	
	//是否显示System.out.println输出的提示
	public static void showMessage(String message)
	{
		if (Const.isShowTip())
			System.out.println(message);
	}
	
	public static void showMessage(int message) 
	{
		showMessage(""+message);
	}
	public static void showMessage(Exception message)
	{
		showMessage(""+message);
	}
	public static void dshowMessage(Properties message)
	{
		showMessage(""+message);
	}
	public static void showMessage(JSONObject message)
	{
		showMessage(""+message);
	}
	public static void showMessage(Object message)
	{
		showMessage("" + message);
	}
	public static void showMessage(String [] sql)
	{
		if (sql == null || sql.length <= 0 )
			return;
		String str = "";
		for(int i=0; i<sql.length;i++)
			str = str +"_"+ sql[i];
		showMessage(str);
	}
	public static void showMessage(String className, String message)
	{
		String flagStr = Const.getVisitLog();
		if (flagStr.equalsIgnoreCase("D") ){ //写入数据库
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("name", className);;
			map.put("level", "Level.WARNING");
			map.put("message", message);
			map.put("status", "调试");
			map.put("type", "1");
			map.put("ip", "127.0.0.1");
			Log4jService.insert("log4j", map);
		}
		else if (flagStr.equalsIgnoreCase("F") ) //写入文件
			writeVisitInfo(className  + "_" + message);
		else 
			showMessage(className  + "_" + message);
	}
	public static void showMessage(Map<String,Object> map)//C 监控台显示； F文件；D数据库; 其他为关闭任何输出
	{
		String flagStr = Const.getVisitLog();
		if (flagStr.equalsIgnoreCase("D") ) //写入数据库
			Log4jService.insert("log4j", map);
		else if (flagStr.equalsIgnoreCase("F") ) //写入文件
			writeVisitInfo(map.toString());
		else  //默认监控台显示
			showMessage(map.toString());
	}
	
	//使用参数示例(Tool.class.getSimpleName(),e.getMessage(), e)
	public static void showMessage(String className,String message,Throwable e)
	{
		String flagStr = Const.getRuntimeLog();
		if (flagStr.equalsIgnoreCase("L") ) //Logger
			Logger.getLogger(className).log(Level.SEVERE,message,e);
		else if (flagStr.equalsIgnoreCase("F") ) //写入文件
			writeDebugInfo(className  + "_" + message);
		else if (flagStr.equalsIgnoreCase("D") ){ //写入数据库
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("name", className);
			map.put("level", "Level.SEVERE");
			map.put("message", message);
			map.put("status","异常");
			map.put("type", "1");
			map.put("ip", "127.0.0.1");
			Log4jService.insert("log4j", map);
		}
		else
			showMessage(className + "_" + message);
	}
	public static void showMessage(String className,Level level,String message)
	{
		String flagStr = Const.getRuntimeLog();
		if (flagStr.equalsIgnoreCase("L") ) 
			Logger.getLogger(className).log(Level.SEVERE,message);
		else if (flagStr.equalsIgnoreCase("F") ) //写入文件
			writeDebugInfo(className  + "_" + message);
		else if (flagStr.equalsIgnoreCase("D") ){ //写入数据库
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("name", className);
			map.put("level", level);
			map.put("message", message);
			map.put("status","异常");
			map.put("type", "1");
			map.put("ip", "127.0.0.1");
			Log4jService.insert("log4j", map);
		}
		else
			showMessage(className + "_" + message);
	}
	
    //////////////
	//记录访问信息到文件
	private static void writeVisitInfo(String message) {
		File logPath = new File(Const.getVisitLogsPath());
		writeOutput(message,"Visit_",logPath);
	}
	//记录调试信息到文件
	private static void writeDebugInfo(String message)
	{
		File logPath = new File(Const.getDebugLogsPath());
		writeOutput(message,"Debug_",logPath);
	}
	//写信息到文件
	private static void writeOutput(String message,String pre,File logPath){
		if (!logPath.exists()) {
			logPath.mkdirs();
		}
		Date theDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String fileName = pre + sdf.format(theDate) + ".log";//.txt
		sdf.applyPattern("HH:mm:ss");
		String nowTime = sdf.format(theDate);
		File logFile = new File(logPath, fileName);
		FileWriter fw = null;
		try {
			fw = new FileWriter(logFile, true);
			fw.write("Log:[" + nowTime + "] ");
			fw.write(message + "\r\n");
			fw.flush();
			fw.close();
		} catch (IOException ioe) {
			showMessage(Console.class.getName() + "_" + ioe.getMessage());
		}
	}
}
