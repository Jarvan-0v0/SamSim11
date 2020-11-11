package blkjweb.job;

import java.text.SimpleDateFormat;
import java.util.Date;

import blkjweb.utils.Console;
import blkjweb.utils.MysqlBack;

//此类供通过Spring配置文件来启动定时任务。
public class SimpleQuartzJob 
{
	public void runTask()
	{  
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
		String file = sdf.format(date) + ".sql";
		
		MysqlBack dbBack = new MysqlBack();
		if (dbBack.backup(file))
			Console.showMessage("Spring定时备份成功!"+date);
		else
			Console.showMessage("Spring定时备份失败!"+date);
	}  
}

/*
http://blog.csdn.net/zmx729618/article/details/51437929  
}*/



	// 设定备份时间 
	//String beginDate = sdf.format(date);
	//String beginTime = beginDate.substring(11, 16);
	//System.out.println("开始时间："+beginDate);
	//if (beginTime.equals("9:20")) {