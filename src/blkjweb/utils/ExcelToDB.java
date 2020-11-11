package blkjweb.utils;

//代码来原于：http://sourceforge.net/projects/quickload/files/

import java.util.*;

import org.blkj.excel.core.Column;
import org.blkj.excel.core.TableData;
import org.blkj.utils.StringTool;


public class ExcelToDB 
{
	//批量写数据到数据库
	public ArrayList<String>  SQLAssemblyForBatchInsert(TableData tableData, String keySet) 
	{
		ArrayList<String> arrayList = new ArrayList<String>();
		if (tableData == null || tableData.size()==0)//无要插入的数据集
			return  arrayList; 

		String tableName = tableData.getTable().getName();//表名
		Collection<Column> colsa = tableData.getTable().getColumns();//表的字段集
		Collection<Map<String, Object>> data = tableData.getDataMap();//字段名及对应的值

		String insertIntoTable = "INSERT INTO " + tableName;
		boolean writeFlag = true;
		for (Map<String, Object> it : data)
		{//所有行的元素逐行进行处理开始
			writeFlag = true;

			String cols = "";
			String value = "";	
			for (Column it2 : colsa)//列名
			{
				String tempName = "";
				String tempValue = "";

				if (it2.getName().equals("INVALID_COLUMN")) {
					continue;
				}

				if (!"".equals(cols)) {
					cols += ",";
					value += ",";
				}

				tempName = it2.getName();
				cols += tempName;
				tempValue = (String)it.get(it2.getName());

				//如果主键没有值，则系统不处理，继续执行下一个循环
				if (keySet != null && keySet.contains(tempName) ){
					if (StringTool.isNull(tempValue) ){
						writeFlag = false;
						break;//第2个for 列名
					}
				}
				if (StringTool.isNull(tempValue) )
					tempValue="";

				value += "'"+tempValue+"'";
			}//列名
			if(writeFlag){
				String insertIntoColumns = " (" + cols + ") VALUES (" + value + ") ;";
				String sql = insertIntoTable + insertIntoColumns;
				arrayList.add(sql);
			}
		}
		return arrayList;
	}

	//批量写数据到数据库  "存在更新，不存在插入 使用duplicate语句" 
	public ArrayList<String> SQLAssemblyForBatchInsertUpdate(TableData tableData, String keySet) 
	{
		ArrayList<String> result = new ArrayList<String>();

		if (tableData == null || tableData.size()== 0)//无要插入的数据集
			return  result; 

		String tableName = tableData.getTable().getName();//excel表的表名
		Collection<Column> colsa = tableData.getTable().getColumns();//excel表的字段集
		Collection<Map<String, Object>> data = tableData.getDataMap();//excel表的字段名及对应的值

		String insertSQL = "INSERT INTO " + tableName; 
		String updateSQL = "ON DUPLICATE KEY UPDATE ";
		boolean writeFlag = true;

		//<String, Object> excel表的字段名及对应的值
		for (Map<String, Object> it : data)	{
			writeFlag = true;
			String inserCols = "";
			String updateCols = "";
			String value = "";	
			//colsa为excel表的字段集
			for (Column it2 : colsa){//第2个for 列名
				if (it2.getName().equals("INVALID_COLUMN")) {
					continue;
				}
				String tempName = "";
				String tempValue = "";
				tempName = it2.getName();//字段名
				tempValue = (String)it.get(tempName);//字段对应的值

				//如果主键没有值，则系统不处理，继续执行下一个循环 即对excel表的主键为空记录不进行处理
				if (keySet != null && keySet.contains(tempName) ){
					if (StringTool.isNull(tempValue) ){
						writeFlag = false;
						break;//第2个for 列名
					}
				}

				if (StringTool.isNull(tempValue))
					tempValue ="";
				else
					tempValue = tempValue.toLowerCase();//统一为小写字符

				//组装Insert子句
				if (! "".equals(inserCols)) {
					inserCols += ",";
					value += ",";
				}
				inserCols += tempName;

				value += "'" + tempValue + "'";

				//组装Update子句
				if( ! "".equals(updateCols))
					updateCols += ",";	
				updateCols += tempName + "='" + tempValue + "'";

			}//列名

			if(writeFlag){
				String sql =  insertSQL + " (" + inserCols + ") VALUES (" + value + ") " +
						updateSQL + updateCols + ";";
				//MyLogger.showMessage(sql);
				result.add(sql);
			}
		}
		return result;
	}
	
	
	
	

  ///2017  ///2017  ///2017  ///2017  ///2017  ///2017  ///2017  ///2017
	  ///2017  ///2017  ///2017  ///2017  ///2017  ///2017  ///2017  ///2017
	  ///2017  ///2017  ///2017  ///2017  ///2017  ///2017  ///2017  ///2017  ///2017
	//////////////////////////////////////////////////////

	//批量更新
	public ArrayList<String> batchUpdate(TableData tableData, String keySet) 
	{
		ArrayList<String> arrayList = new ArrayList<String>();

		if (tableData == null || tableData.size()==0)//无要插入的数据集
			return  arrayList; 

		String tableName = tableData.getTable().getName();//excel表的表名
		Collection<Column> colsa = tableData.getTable().getColumns();//excel表的字段集
		Collection<Map<String, Object>> data = tableData.getDataMap();//excel表的字段名及对应的值

		String sqlPre = "UPDATE " + tableName + " SET "; 
		boolean writeFlag = true;

		for (Map<String, Object> it : data)
		{
			writeFlag = true;
			String updateCols = "";
			String whereCols = "";
			for (Column it2 : colsa)//第2个for 列名
			{
				String tempName = "";
				String tempValue = "";

				//无效的列名
				if (it2.getName().equals("INVALID_COLUMN")) {
					continue;
				}

				tempName = it2.getName();//抬头
				tempValue = (String)it.get(it2.getName());

				if (StringTool.isNull(tempValue))//等于空或null
					tempValue = "";
				else
					tempValue = tempValue.toLowerCase();//统一为小写字符

				//如果主键没有值，则系统不处理，继续执行下一个循环
				if (keySet != null && keySet.contains(tempName) ){
					if ( StringTool.isNull(tempValue) )
					{
						writeFlag = false;
						break;//第2个for 列名
					}
					else
					{
						if( ! "".equals(whereCols))
							updateCols +=" AND ";	
						whereCols += tempName + "='"+tempValue+"'";
					}

				}
				else//组装Update子句
				{
					if( ! "".equals(updateCols))
						updateCols +=",";	

					updateCols += tempName + "='"+tempValue+"'";	
				}


			}//列名

			if(writeFlag){//记录下一条完整的SQL语句
				String sql =  sqlPre + updateCols + " WHERE " + whereCols;
				arrayList.add(sql);
			}
		}
		return arrayList;
	}


	//批量删除，仅涉一个表
	public ArrayList<String> batchDelete(TableData tableData, String keySet) 
	{
		//保留SQL语句
		ArrayList<String> arrayList = new ArrayList<String>();

		if (tableData == null || tableData.size()==0)//无要插入的数据集
			return  arrayList; 

		String tableName = tableData.getTable().getName();//excel表的表名
		//从excel表中读取字段及值
		Collection<Column> colsa = tableData.getTable().getColumns();//表的字段集
		Collection<Map<String, Object>> data = tableData.getDataMap();//字段名及对应的值

		String sqlTable = "DELETE FROM " + tableName +" WHERE ";

		boolean writeFlag = true;

		for (Map<String, Object> colData : data)
		{
			writeFlag = true;
			String whereTable = "";

			for (Column colName : colsa){//列名for
				String tempName = "";
				String tempValue = "";

				if (colName.getName().equals("INVALID_COLUMN")) {
					continue;
				}

				tempName = colName.getName();//获取excel文件的表头的名称
				tempValue = (String)colData.get(colName.getName());//获取对表头的列值
				if ( StringTool.isNull(tempValue) )
					tempValue="";
				else
					tempValue = tempValue.toLowerCase();//统一为小写字符
				//如果主键没有值，则系统不处理，继续执行下一个循环
				if (keySet != null && keySet.contains(tempName) ){
					if (StringTool.isNull(tempValue) )
					{
						writeFlag = false;
						break;//第2个for 列名
					}
					else
					{//组装where子句
						if (! "".equals(whereTable)) {
							whereTable += " AND ";
						}
						whereTable += tempName + "='"+ tempValue+"'";
					}
				}
			}//列名for

			if(writeFlag){
				String sql = sqlTable + whereTable;
				arrayList.add(sql);
			}
		}
		return arrayList;
	}

	//先删除表中所有元素，然后批量写数据到数据库
	public ArrayList<String> batchDeleteInsert(TableData tableData,String keySet,String fieldName) 
	{
		ArrayList<String> arrayList = new ArrayList<String>();
		if (tableData == null || tableData.size()==0)//无要插入的数据集
			return  arrayList; 

		String tableName = tableData.getTable().getName();//表名
		Collection<Column> colsa = tableData.getTable().getColumns();//表的字段集
		Collection<Map<String, Object>> data = tableData.getDataMap();//字段名及对应的值

		arrayList.add("DELETE FROM " + tableName);

		String insertIntoTable = "INSERT INTO " + tableName;

		boolean writeFlag = true;

		for (Map<String, Object> it : data)
		{//所有行的元素逐行进行处理开始
			writeFlag = true;

			String cols = "";
			String value = "";	
			for (Column it2 : colsa)//列名
			{
				String tempName = "";
				String tempValue = "";

				if (it2.getName().equals("INVALID_COLUMN")) {
					continue;
				}

				if (!"".equals(cols)) {
					cols += ",";
					value += ",";
				}

				tempName = it2.getName();
				cols += tempName;
				tempValue = (String)it.get(it2.getName());

				//如果主键没有值，则系统不处理，继续执行下一个循环
				if (keySet != null && keySet.contains(tempName) ){
					if (StringTool.isNull(tempValue) )
					{
						writeFlag = false;
						break;//第2个for 列名
					}
				}
				if (StringTool.isNull(tempValue) )
					tempValue="";

				value += "'"+tempValue+"'";
			}//列名

			if(writeFlag){
				String insertIntoColumns = " (" + cols + ") VALUES (" + value + ")";
				String sql = insertIntoTable + insertIntoColumns + ";";
				arrayList.add(sql);
			}
		}
		return arrayList;
	}


}
/*public boolean batchUpdateDeleteInsert(TableData tableData, String keySet,boolean opType)
{
if(opType)//完全性
return batchDeleteInsert(tableData, keySet,null);
else//增量型
return batchUpdateInsert(tableData, keySet); 
}*/

//2017////////////////////////////////////////////////////
/**批量写数据到数据库 不判断主键是否空等数据域取空值的情况。尽量少用*/
/*	public boolean batchInsertTableData(TableData tableData) 
{
if (tableData == null || tableData.size()==0)//无要插入的数据集
return  false; 

String tableName = tableData.getTable().getName();//表名
Collection<Column> colsa = tableData.getTable().getColumns();//表的字段集
Collection<Map<String, Object>> data = tableData.getDataMap();//字段名及对应的值


ArrayList<String> arrayList = new ArrayList<String>();

String insertIntoTable = "INSERT INTO " + tableName;

for (Map<String, Object> it : data)
{
String cols = "";
String value = "";	
for (Column it2 : colsa)//列名
{
String tempName = "";
String tempValue = "";

if (it2.getName().equals("INVALID_COLUMN")) {
continue;
}

if (!"".equals(cols)) {
cols += ",";
value += ",";
}

tempName = it2.getName();
cols += tempName;
tempValue = (String)it.get(it2.getName());

if ( strIsNullEmpty(tempValue) )
tempValue="";

value += "'"+tempValue+"'";
}//列名

String insertIntoColumns = " (" + cols + ") VALUES (" + value + ")";
String sql = insertIntoTable + insertIntoColumns + ";";

//	Log.debug("ExcelTo MysqlServer:"+sql);

arrayList.add(sql);
}

// return /thisDb.doExecuteBatchUpdate(arrayList);

}*/






/**
 * 操作特点：存在更新，不存在插入
 * 批量写数据到数据库 涉及到两个不同的表，其中tableData含的表为主表；table2Name为次表的表名
 */

/*public boolean batchUpdateInsertTableData(TableData tableData, String keySet,
String table2Name,HashMap<String,String> hashMap) 
{
boolean result = false;

if (tableData == null || tableData.size()==0)//无要插入的数据集
return  result; 

//主表
String table1Name = tableData.getTable().getName();//excel表的表名
//从excel表中读取字段及值
Collection<Column> colsa = tableData.getTable().getColumns();//表的字段集
Collection<Map<String, Object>> data = tableData.getDataMap();//字段名及对应的值

ArrayList<String> arrayList = new ArrayList<String>();

String insertTable1 = "INSERT INTO " + table1Name;
String insertTable2 = "INSERT INTO " + table2Name;
String updateSQL = "ON DUPLICATE KEY UPDATE ";

boolean writeFlag = true;
boolean execuFlag = false;

for (Map<String, Object> it : data)
{//所有行的元素逐行进行处理开始
writeFlag = true;

String inserTable1Cols = "";
String updateTable1Cols = "";
String table1Value = "";

String inserTable2Cols = "";
String updateTable2Cols = "";
String table2Value = "";

for (Column it2 : colsa)//每行元素中不同列处理的开始
{
if (it2.getName().equals("INVALID_COLUMN")) {
continue;
}

String tempName = "";//列名
String tempValue = "";//对应上述列名的具体值
String tempTable2Value = "";

tempName = it2.getName();//获取excel文件的表头的名称
tempValue = (String)it.get(tempName);//获取对表头的列值

//如果主键没有值，则系统不处理，继续执行下一个循环
if (keySet != null && keySet.contains(tempName) ){
if ( strIsNullEmpty(tempValue))
{
writeFlag = false;
break;
}
}
if ( strIsNullEmpty(tempValue))
tempValue="";//Excel无值得时，写入的默认值
else
tempValue = tempValue.toLowerCase();//统一为小写字符

tempTable2Value = tempValue;

//组装主表的Insert子句
if (! "".equals(inserTable1Cols)) {
inserTable1Cols += ",";
table1Value += ",";
}
inserTable1Cols += tempName;

table1Value += "'"+tempValue+"'";
//组装Update子句
if( ! "".equals(updateTable1Cols))
updateTable1Cols +=",";	
updateTable1Cols += tempName + "='"+tempValue+"'";

if(hashMap != null)
{
//组装table2Name内容 只含有hashMap的内容
Iterator<?> iter = hashMap.entrySet().iterator(); 
while (iter.hasNext()){ 
Map.Entry entry = (Map.Entry) iter.next(); //得到单个的entry
String key = (String)entry.getKey();//返回与此项对应的键
String val = (String)entry.getValue();//返回与此项对应的值
if(key.equalsIgnoreCase(tempName))
{
if (! "".equals(inserTable2Cols)) {
inserTable2Cols += ",";
table2Value += ",";
}
inserTable2Cols += val;

if ( strIsNullEmpty(tempTable2Value))
tempTable2Value = "";//Excel无值得时，写入的默认值
table2Value += "'"+tempTable2Value+"'";
//组装Update子句
if( ! "".equals(updateTable2Cols))
updateTable2Cols +=",";	
updateTable2Cols += val + "='"+tempTable2Value+"'";

break;
}
} 
}
}//每行元素中不同列处理的结束

if(writeFlag){
execuFlag = true;
String sql = insertTable1 + " (" + inserTable1Cols + ") VALUES (" + table1Value + ") " +
updateSQL + updateTable1Cols + ";";
//Log.debug(sql);
arrayList.add(sql);
sql =  insertTable2 + " (" + inserTable2Cols + ") VALUES (" + table2Value + ") " +
updateSQL + updateTable2Cols + ";";
//Log.debug(sql);
arrayList.add(sql);
}
}//所有行的元素逐行进行处理结束

if (execuFlag)//有要执行的语句
//result = thisDb.doExecuteBatchUpdate(arrayList);

return result;
}


//批量删除信息 涉及到两个不同的表
@SuppressWarnings("unchecked")
public boolean batchDeleteTableData(TableData tableData, String keySet,
String table2Name,HashMap<String,String> hashMap) 
{
boolean result = false;

if (tableData == null || tableData.size()==0)//无要插入的数据集
return  result; 

//主表
String table1Name = tableData.getTable().getName();//excel表的表名
//从excel表中读取字段及值
Collection<Column> colsa = tableData.getTable().getColumns();//表的字段集
Collection<Map<String, Object>> data = tableData.getDataMap();//字段名及对应的值


String sqlTable1 = "DELETE FROM " + table1Name + " WHERE ";
String sqlTable2 = "DELETE FROM " + table2Name  + " WHERE ";

boolean writeFlag = true;
boolean execuFlag = false;
//保留SQL语句
ArrayList<String> arrayList = new ArrayList<String>();
String sql="";

for (Map<String, Object> colData : data)
{
writeFlag = true;

String whereTable1 = "";
String whereTable2 = "";

for (Column colName : colsa){//列名for
if (colName.getName().equals("INVALID_COLUMN")) {
continue;
}

String tempName = "";
String tempValue = "";
tempName = colName.getName();//获取excel文件的表头的名称
tempValue = (String)colData.get(colName.getName());//获取对表头的列值
if (strIsNullEmpty(tempValue))
tempValue="";
else
tempValue = tempValue.toLowerCase();//统一为小写字符

//对主键进行处理
if (keySet != null && keySet.contains(tempName) ){
if (strIsNullEmpty(tempValue))
{//如果主键没有值，则系统不处理，继续执行下一个循环
writeFlag = false;
break;
}
else
{
if (! "".equals(whereTable1)) {
whereTable1 += " AND ";
}
whereTable1 += tempName + "='"+ tempValue+"'";

}
}
else//对非主键不进行任何处理
break;

if(hashMap != null)
{
//组装table2Name内容 只含有hashMap的内容
Iterator<Map.Entry<String, String>> iter = hashMap.entrySet().iterator(); 
while (iter.hasNext()){ 
Map.Entry<String, String> entry = (Map.Entry) iter.next(); //得到单个的entry
String key = (String)entry.getKey();//返回与此项对应的键
String val = (String)entry.getValue();//返回与此项对应的值
if(key.equalsIgnoreCase(tempName))
{//组装Update子句
if (! "".equals(whereTable2)) {
whereTable2 += " AND ";
}
if (strIsNullEmpty(tempValue))
tempValue="";
whereTable2 += val + "='"+ tempValue+"'";
}
} 
}

}//列名for

if(writeFlag){
sql = sqlTable1 + whereTable1  + ";";
arrayList.add(sql);
//Log.debug(sql);
sql = sqlTable2 + whereTable2 + ";";
//arrayList.add(sql);
// Log.debug(sql);     
execuFlag = true;
}
}

if (execuFlag)//有要执行的语句
//result = thisDb.doExecuteBatchUpdate(arrayList);

return result;
}
 */
//涉及两个以上表的操作，专门写出对应的方法
/*public boolean batchDeleteTutor(TableData tableData) 
{
boolean result = false;

if (tableData == null || tableData.size()==0)//无要插入的数据集
return  result; 

//主表
//	String table1Name = tableData.getTable().getName();//excel表的表名
//从excel表中读取字段及值
Collection<Column> colsa = tableData.getTable().getColumns();//表的字段集
Collection<Map<String, Object>> data = tableData.getDataMap();//字段名及对应的值


String sqlTable1 = "DELETE FROM teacher WHERE ";
String sqlTable2 = "DELETE FROM teacherpw WHERE ";
String sqlTable3 = "DELETE FROM teacherewzb WHERE ";

boolean writeFlag = true;
boolean execuFlag = false;
//保留SQL语句
ArrayList<String> arrayList = new ArrayList<String>();
String sql="";

for (Map<String, Object> colData : data)
{
writeFlag = true;

String whereTable = "";

for (Column colName : colsa){//列名for
String tempName = "";
String tempValue = "";

if (colName.getName().equals("INVALID_COLUMN")) {
continue;
}

tempName = colName.getName();//获取excel文件的表头的名称
//非主键不进行处理
if(! tempName.equalsIgnoreCase("id"))
break;

tempValue = (String)colData.get(colName.getName());//获取对表头的列值
if (Tool.isNull(tempValue))
tempValue="";
else
tempValue = tempValue.toLowerCase();//统一为小写字符

//如果主键没有值，则系统不处理，继续执行下一个循环
if (Tool.isNull(tempValue))
{
writeFlag = false;
break;
}
else
{
if (! "".equals(whereTable)) {
whereTable += " AND ";
}
whereTable += tempName + "='"+ tempValue+"'";
}
}//列名for

if(writeFlag){
sql = sqlTable1 + whereTable + ";";
arrayList.add(sql);

sql = sqlTable2 + whereTable + ";";
arrayList.add(sql);

sql = sqlTable3 + whereTable + ";";
arrayList.add(sql);

execuFlag = true;
}
}

if (execuFlag)
try {
result = thisDb.doExecuteBatchDelete(arrayList);
} catch (Exception e) {
// TODO Auto-generated catch block
e.printStackTrace();
}

return result;
}
 */
/////////////////////////////////////////////////////////////////////////////
/**下面的方法非通用方法**/
/*String key = "";
{
//获取动态创建的一直愿和推荐生原始信息表的主键
PropertiesConfig config = null; 
config = new PropertiesConfig(ContextListener.dbConfig);//获取数据库的
key = config.readData("key");	
}*/
