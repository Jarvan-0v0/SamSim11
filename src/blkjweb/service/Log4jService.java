package blkjweb.service;

import java.util.Map;









import org.blkj.sql.DMLProcessor;
import org.blkj.utils.DateTool;

public class Log4jService  
{
	public static int insert(String tableName, Map<String, Object> mapRecord)
	{   
		mapRecord.put("time", DateTool.getTime());//操作时间
		DMLProcessor dbUtil = new DMLProcessor(); 
		int result = dbUtil.insertLogEvent(tableName, mapRecord);
		dbUtil.commit(); 
		return result;
	}
	
}
