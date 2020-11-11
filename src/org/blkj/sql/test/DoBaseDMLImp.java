package org.blkj.sql.test;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;

import org.blkj.utils.FileTool;
import org.blkj.utils.StringTool;

import blkjweb.utils.Const;

/**
 *  关于泛型：
 *  （1）public <T> void fromArrayToCollection(T[] a, Collection<T> c){ }
 *  （2）public void fromArrayToCollection(T[] a, Collection<T> c){ }  
 *    第一个方法相当于为该方法声明了一个类型为T的类，这个方法放到任意一个类中都不会报错的
 *    第二个方法相当于没有声明类型为T的类，这时必须在类上面加上泛型T
 */

public class DoBaseDMLImp <T> extends AbstractDBHandler <T> 
{
	private boolean autoCommit; //false禁止自动提交，true自动提交
    private boolean failCommit; //true表示提交失败
    private Savepoint savepoint;//事务设置回滚点
    
    private java.sql.Connection con; //保存为用户建立的数据库连接
    private java.sql.Statement stmt;  //提供基本的 SQL 操作. 适合静态SQL语句, 且传入的 SQL语句无法接受参数.
	private java.sql.PreparedStatement preStmt;//可以在 SQL 中传递参数, 适合多次使用的 SQL 语句.
	private java.sql.ResultSet rs;
	
	public DoBaseDMLImp()
	{ }
	
	public DoBaseDMLImp(Connection con)
	{
		this.con = con;
		autoCommit = true;
		failCommit = true;
	}
	
	//false禁止自动提交，true自动提交(默认)
    public void setAutoCommit(boolean autoCommit)
    {
    	try {
    		if(con != null)
    			con.setAutoCommit(autoCommit);
		} catch (SQLException e) {
			
		}
        this.autoCommit = autoCommit;
    }
    
    //提交事务
    public void commit() throws SQLException 
    {
        if (!con.isClosed()) {
            if (!autoCommit) {
                try {
                    con.commit();
                    autoCommit = true;
                    con.setAutoCommit(autoCommit);
                    failCommit = false;
                    con.close();
                } finally {
                    if (failCommit) {
                        rollback();
                    }
                }
            }
        }
    }
    
    //事务回滚
    public void rollback() 
    {
    	if(con == null)
    	    return;
    	try {
			if (!con.isClosed()) {
			    if (!this.autoCommit) {
			        if (failCommit) {
			            if (savepoint == null) {
			                con.rollback();
			                con.close();
			            } else {
			                con.rollback(savepoint);
			                con.close();
			           }
			            autoCommit = true;
			        }
			    }
			}
		} catch (SQLException e) {

		}
    }
   //1关闭resultSet    
    private void closeRs() throws SQLException
    {
    	try {
    		if(rs != null)
    			rs.close();
    	}catch (SQLException e) {
    		throw e;
    	}finally{
    		rs = null;//是否需要
    	}
    }
    //2关闭PreparedStatement PreStmt
    private void closePreStmt() throws SQLException 
    {
    	try {
    		if(preStmt != null)
    			preStmt.cancel();
    	}catch (SQLException e) {
    		throw e;
    	}finally{
    		try {
    			if(preStmt != null)
    				preStmt.close();
    		} catch (SQLException e) {
    			throw e;
    		}
    		preStmt = null;//是否需要	
    	}
    }

    //2关闭 Statement stmt
    private void closeStmt() throws SQLException 
    {
    	try {
    		if(stmt != null)
    			stmt.cancel(); //取消Statement中的SQL语句指定的数据库操作命令
    	}catch (SQLException e) {
    		throw e;
    	}finally{
    		try {
    			if(stmt != null)
    				stmt.close(); //关闭Statement语句指定的数据库连接
    		} catch (SQLException e) {
    			throw e;
    		}
    		stmt = null;//是否需要??
    	}
    }
 
    //3关闭数据库联接
    public void closeCon()  
    {
    	if(con == null)
    		return;
    	try {  	
    		
    	}finally {
    		try {
    			if (! con.isClosed()) 
    				con.close();//立即释放连接对象的数据库和JDBC资源
    		}catch (SQLException e) {
    		
    		}finally{
    			con = null;  //Make sure we don't close it twice
    		}
    	}
    }
    //判断数据库连接是否关闭,返回true表示已关闭，返回false表示未关闭.
    public boolean isClosed() throws SQLException
    {
    	if(con == null)
    		return true;
    	
        return con.isClosed();//已关闭，返回true
    }
    //返回数据库连接
    public Connection getCon() 
    {
        return this.con;
    }  
    
    /**
	 * Runs a parameterized SQL query and stuffs the first result into an object using reflection
	 * @param o target object to save into
	 * @param pfx (possibly empty) prefix string on SQL fields stripped before translating to
	 * class member names
	 * @param sql SQL query containing ? for parameters
	 * @param params array of objects to use as parameters
	 * @return -1 : query failed
	 * 0 : no fields to copy
	 * >0: number of copied fields
	 * @throws SQLException 
	 */
    public int doQueryOne(Object o, String sql, Object[] params) throws SQLException
    {
    	int c = -1;
    	if (StringTool.isNull(sql) || o == null )
    		return c;
    	try{
    		preStmt = con.prepareStatement(sql);
    		if (preStmt == null)
    			return c;
    		// stuff parameters in
    		if(params != null)
    			for (int i=0; i<params.length; i++){
    				preStmt.setObject(i + 1,params[i]);
    			}
    		if (preStmt.execute()){//用于执行返回多个结果集
    			rs = preStmt.getResultSet();//获得结果集
    		}else{
    			return c;
    		}
    		//MyLogger.showMessage(preStmt);
    		if (! rs.next()){
    			return c;
    		}
    		//直接对结果集映射到对象o
    		c = _recordMappingToObj(rs,o);
    	} finally {
    		this.closeRs();
    		this.closePreStmt();
    	}
    	return c;
    }
    /**操作对象一个表：从库表中读入若干记录，表名与参数T的名字相同 */
    public List<T> doQueryObjects(Class<T> type, String sql, String[] params) throws Exception
    {
    	//super.setType(type);
    	if (StringTool.isNull(sql))
    		return null;
    	try{
    		preStmt = con.prepareStatement(sql);
    		if (preStmt == null)
    			return null;
    		if(params != null)
    			for (int i=0; i<params.length; i++){
    				preStmt.setObject(i + 1,params[i]);
    			}
    		//resultSet = prepareStatement.executeQuery();
    		if (preStmt.execute()){//用于执行返回多个结果集
    			rs =preStmt.getResultSet();//获得第一个结果集
    		}else{
    			return null;
    		}
    		return createObjects(type, rs);
    	}catch (Exception e) {
    		
    	}finally {
    		this.closeRs();
    		this.closePreStmt();
    	}
    	return null;
    }
    /**
     * Creates a list of <T>s filled with values from the provided ResultSet
     * @param resultSet
     *            ResultSet that contains the result of the
     *            database-select-query
     * 
     * @return List of <T>s filled with values from the provided ResultSet
     */
    private List<T> createObjects(Class<T> type, ResultSet resultSet) throws Exception
    {
    	List<T> list = new ArrayList<T>();
    	while (resultSet.next()) {
    		T instance = (T)type.newInstance();
    		//Object
    		for (java.lang.reflect.Field field : type.getDeclaredFields()) {
    			// We assume the table-column-names exactly match the variable-names of T 
    			Object value = resultSet.getObject(field.getName());
    			PropertyDescriptor propertyDescriptor = new PropertyDescriptor(field.getName(), type);
    			java.lang.reflect.Method method = propertyDescriptor.getWriteMethod();
    			method.invoke(instance, value);
    		}
    		list.add(instance);
    	}
    	return list;
    }
   
    
    //////////////以上的方法经过验证///////////////////
    /////////////////////////////////
    
    /**判断满足特定条件的记录是否存在；返回值>=1 存在*/
	public int doGenericFactory(String sql, Object[] params) throws Exception
	{
		int c = -1;
		if (StringTool.isNull(sql))
			return c;
		try{
    		preStmt = con.prepareStatement(sql);
    		if (preStmt == null)
    			return c;
    		if(params != null)
    			for (int i=0; i<params.length; i++){
    				preStmt.setObject(i + 1,params[i]);
    			}
    		//resultSet = prepareStatement.executeQuery();
    		if (preStmt.execute()){//用于执行返回多个结果集
    			rs =preStmt.getResultSet();//获得第一个结果集
    		}else{
    			return c;
    		}
    	}catch (Exception e) {
    		c = -1;
    		
    	}finally {
    		this.closeRs();
    		this.closePreStmt();
    	}
		return c;
	}

    
	/**操作对象一个表：从库表中读入若干记录，表名与参数T的名字相同 */
	public List<T> doSelectObjects(Class<T> type, String sql) throws Exception
	{
		super.setType(type);
		if (StringTool.isNull(sql))
			return null;
		try{
    		preStmt = con.prepareStatement(sql);
    		if (preStmt == null)
    			return null;
    		
    		//resultSet = prepareStatement.executeQuery();
    		if (preStmt.execute()){//用于执行返回多个结果集
    			rs =preStmt.getResultSet();//获得第一个结果集
    		}else{
    			return null;
    		}
    		return createObjects(type, rs);
    	}catch (Exception e) {
    		
    	}finally {
    		this.closeRs();
    		this.closePreStmt();
    	}
		return null;
	}
	
	//@Override
	protected String createSelectQuery() {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT ");
		sb.append(super.getColumns(false));
		sb.append(" FROM ");
		/* We assume the table-name exactly matches the simpleName of T */
		sb.append(type.getSimpleName());
		return sb.toString();
	}
	/**操作对象一个表：从库表中读入所有记录，表名与参数T的名字相同   无where语句 */
	public  List<T> doSelectObjects(/*Class<T> type*/) throws Exception
	{
		//super.setType(type);
		try{
    		stmt = con.createStatement();;
    		rs = stmt.executeQuery(createSelectQuery());
			return createObjects(type,rs);
    	}catch (Exception e) {
    		
    	}finally {
    		this.closeRs();
    		this.closeStmt();
    	}
		return null;
	}

	
	/////////插入记录一条,并返回自增id -1表示插入失败
	/**
	 * Run an SQL insert query and return generated index
	 * @param sql SQL query with ? for parameters
	 * @param params array of objects for query parameters  flag = true;
	 * @return integer value of the index generated by the database
	 */
	public int doInsertGenIndex( String sql, Object[] params) throws Exception
	{
		int c = -1;
		if (StringTool.isNull(sql) )
			return c;

		try{
			preStmt = con.prepareStatement(sql,PreparedStatement.RETURN_GENERATED_KEYS);
			if (preStmt == null)
				return c;
			// stuff parameters in
			if( params != null){
				for (int i=0; i<params.length; i++)
					preStmt.setObject(i + 1,params[i]);
			}
			//resultSet = prepareStatement.executeQuery();
			if (preStmt.execute()){//用于执行返回多个结果集
				rs =preStmt.getResultSet();//获得第一个结果集

				if ( ! rs.next()) {
					return c;
				}

				//输出刚插入数据返回的自增ID值
				c = rs.getInt(1);  // XX can there be more than one index generated?
			}
		}catch (Exception e) {
			c = -1;
			
		}finally {
			this.closeRs();
			this.closePreStmt();
		}
		return c;
	}
	
	
	//获取指定表的所有记录 到LIST
	/**
	 *  Select处理，返回一个List;  同一个表
	 */
	public List<Object> doSelectObjects(String sql, String[] params) throws Exception
	{
		if (StringTool.isNull(sql))
			return null;
		try{
			preStmt = con.prepareStatement(sql);
			if (preStmt == null)
				return null;
			if( params != null){
				for (int i=0; i<params.length; i++)
					preStmt.setObject(i + 1,params[i]);
			}
			//resultSet = prepareStatement.executeQuery();
			if (preStmt.execute()){//用于执行返回多个结果集
				rs =preStmt.getResultSet();//获得第一个结果集
			}else{
				return null;
			}
			ArrayList<Object> list = new ArrayList<Object>();
			ResultSetMetaData rsmd = rs.getMetaData();
			int column = rsmd.getColumnCount();
			while (rs.next()) {
				ArrayList<Object> colList = new ArrayList<Object>();
				for (int i = 1; i <= column; i++) {
					colList.add(rs.getObject(i));
				}
				list.add(colList);
			}

			return list;//返回所有记录

		}catch (Exception e) {
			
		}finally {
			this.closeRs();
			this.closePreStmt();
		}
		return null;
	}
	

	/***************************更新操作**********************************************/
	// OK 数据库更新类操作方法。 用于执行INSERT、UPDATE、DELETE、REPLACE INTO、on duplicate等语句
	/** executeUpdate用于执行 INSERT、UPDATE 或 DELETE 语句以及 SQL DDL（数据定义语言）语句，例如 CREATE TABLE 和 DROP TABLE
	 *  对于 CREATE TABLE 或 DROP TABLE 等不操作行的语句，executeUpdate的返回值总为零。
	 *  对于INSERT、UPDATE 或 DELETE 语句.返回值是一个整数，指示受影响的行数（即更新计数）
	 *  如果库中没有任何记录,进行删除时,返回的值是0,此方法却返回false.
	 *  delFlag表示是否删除操作 true表示删除操作
	 */
	public boolean doExecuteUpdate(String sql, Object[] params,boolean delFlag) throws Exception
	{
		boolean temp = false;
		if (StringTool.isNull(sql))
			return temp;
		try{
			preStmt = con.prepareStatement(sql);
			if (preStmt == null)
				return temp;
			if( params != null){
				for (int i=0; i<params.length; i++)
					preStmt.setObject(i + 1,params[i]);
			}
			//返回值是更新的记录条数。返回值为0表示执行出错。
			int count = preStmt.executeUpdate(); 

			//对于insert,update等，用executeUpdate执行后如返回的值是0则表示操作失败，为正整数则成功
			//关于删除操作：对于delete from tableName;executeUpdate的执行结果是返回成功删除的记录个数（>=0）
			//对于truncate table tableName executeUpdate的执行结果总是0
			if( (delFlag) &&  (count >= 0))//delete操作
				temp= true;
			else//不是delete操作
				if (count > 0)
					temp= true;	

		} catch (Exception e) {
			
		}finally{
			this.closeRs();
			this.closePreStmt();
		}
		return temp;	
	}

	//////////////////////批量处理 可以涉及多个表
	/** delete和truncate table的最大区别是delete可以通过WHERE语句选择要删除的记录。但执行得速度不快。而且还可以返回被删除的记录数。
	 *  而truncate table无法删除指定的记录，而且不能返回被删除的记录。但它执行得非常快。
	 *  如果要删除表中的所有数据,建议使用truncate table, 尤其是表中有大量的数据, 使用truncate table是将表结构重新建一次速度要
	 *  比使用delete from快很多,而delete from是一行一行的删除,速度很慢. 
	 */
	/**执行批量删除操作*/
	public boolean doExecuteBatchDelete(String [] sql)throws Exception
	{
		return doEexcuteBatchSql(sql,true);//批量操作
	}

	public boolean doExecuteBatchDelete(ArrayList<String> arrayList)throws Exception 
	{
		int size = arrayList.size();
		if(size == 0)
			return false;
		int num = -1;
		String [] sql = new String [size];
		for(int i = 0; i < size; i++){
			sql[++num] = (String)arrayList.get(i);
		}
		return doEexcuteBatchSql(sql,true);//批量操作
	}

	/**
	 * 尽量使用：doEexcuteBatchSql(String []sql)
	 * 支持事务处理   批量执行不支持Select语句
	 *  sqlStr String[]  存放sql语句的数组
	 *  对于delete、replace语句来讲，如果没有满足条件的记录，返回值也是0，这里res[i] <= 0)表示操作有错
	 *  对insert，update, 返回0表示操作失败。但对于多表进行delete时，可能无法满足要求（一个表可以删除，另表不一定存在）
	 *  对于update,如果库中无相应的记录时,更新会出错,所以，在组装SQL语句时需要注意各个SQL语句的顺序
	 *  deleteFlag 删除标记 true
	 */
	private boolean doEexcuteBatchSql(String[] sqlStr, boolean deleteFlag) throws Exception
	{
		if (StringTool.isNull(sqlStr))
			return false;

		boolean flag = true;
		boolean autoCommit = true;
		Connection conn = null;
		try{
			conn = this.con;
			autoCommit=conn.getAutoCommit();//保存缺省事务提交状态
			conn.setAutoCommit(false);//禁止自动提交

			stmt = conn.createStatement();
			//使用PreparedStatement会稍有不同。它只能处理一段SQL语句，但可以带很多参数
			//2011oracle 应该去掉, 此代码不执行
			//stmt.executeUpdate("START TRANSACTION;");//批事务处理，若一次执行一条SQL语句时不需要
			for(int i = 0;i < sqlStr.length;i++){
				if(sqlStr[i] != null && sqlStr[i].length() != 0){
					stmt.addBatch(sqlStr[i]);
				}
			}
			//Batch执行完会返回一个int型的值，这个值就是执行的SQL语句数量，也就是批处理SQL语句组的下标值
			int[] res = stmt.executeBatch();//在mysql成功返回都是1. 对ORACLE的执行返回都是-2

			//查找有否有错，以便进行回退
			for(int i = 0;i < res.length;i++){
				if(! deleteFlag){//非delete语句
					//返回值为0，操作失败
					if(res[i] <= 0){//有错   
						conn.rollback(); //回滚
						flag = false;
						break;
					}
				}
				else//delete语句 
				{//返回值为0，也是操作成功
					if(res[i] < 0){//有错
						conn.rollback(); //回滚
						flag = false;
						break;
					}
				}		
			}
			if(flag)
				conn.commit();//提交

		}catch (SQLException e) {
			flag = false;
			try {
				if (conn != null) 
					conn.rollback();
			} catch (SQLException e1) {
				
			}
		}finally{

			if (conn != null){ 
				conn.setAutoCommit(autoCommit);//恢复缺省事务提交状态
				conn.close();
			}
			conn = null;
			this.closeRs();
			this.closeStmt();
		}
		return flag;	
	}
	
	public boolean doEexcuteBatchSql(String []sql) throws Exception
	{
		if (StringTool.isNull(sql))
			return false;

		boolean autoCommit = true;
		boolean flag = true;
		//批处理需要对连接句柄进行一些特殊处理。所以不直接使用dbConnection
		Connection conn = null;
		try {
			conn = this.con;

			autoCommit=conn.getAutoCommit();//保存缺省事务提交状态
			conn.setAutoCommit(false);//禁止自动提交
			stmt = conn.createStatement();
			for(int i = 0;i < sql.length; i++)
			{
				if(sql[i] != null && sql[i].length() != 0){
					stmt.addBatch(sql[i]);
				}
			}
			stmt.executeBatch();
			conn.commit();//提交

		} catch (SQLException e) {
			flag = false;
			if (conn != null){ 
				conn.rollback();
				conn.setAutoCommit(autoCommit);//恢复缺省事务提交状态
				conn.close();
			}
			conn = null;
			this.closeRs();
			this.closeStmt();
		}
		return flag;	
	}

/////////BLOB(Binary Large Object)对象可以保存一张图片或一个声音文件  没有验证 
/** 对Mysql:如存储的二进制文件过大，会使数据库的性能下降
	类型 大小(单位：字节)
　　TinyBlob 最大 255
　　Blob 最大 65K
　　MediumBlob 最大 16M
　　LongBlob 最大 4G
 */
	/**********含有BLOB对象的读写开始**********/
	/**
	 * 向数据库中插入一个新的BLOB对象(图片)
	 * @param infile     要输入的数据文件
	 * @throws java.lang.Exception
	 */
	public void blobInsert(String infile) throws Exception 
	{
		File file = null;
		FileInputStream fis = null;
		try {
			file = new File(infile);
			fis = new FileInputStream(file);
			preStmt = con.prepareStatement("insert into tmp(descs,pic) values(?,?)");

			preStmt.setString(1, file.getName()); // 把传过来的第一个参数设为文件名
			preStmt.setBinaryStream(2, fis, fis.available()); // 第二个参数为文件的内容

			preStmt.executeUpdate();

		} catch (Exception e) {
			
		}finally{
			fis.close();
			this.closeStmt();
		}
	}
	
	/**
	 * 从数据库中读出BLOB对象
	 *  @param outfile   输出的数据文件
	 * @param picID   要取的图片在数据库中的ID
	 * @throws java.lang.Exception
	 */
	public void blobRead(String outfile, int picID) throws Exception 
	{
		File file = null;
		FileOutputStream fos = null;
		InputStream is = null;
		byte[] Buffer = new byte[4096];
		try {
				preStmt = con.prepareStatement("select pic from tmp where id=?");
				preStmt.setInt(1, picID); //传入要取的图片的ID
				rs = preStmt.executeQuery();
				rs.next();

				file = new File(outfile);
				if (!file.exists()) {
					file.createNewFile(); // 如果文件不存在，则创建
				}
				fos = new FileOutputStream(file);
				is = rs.getBinaryStream("pic");
				int size = 0;
				while ((size = is.read(Buffer)) != -1) {
					fos.write(Buffer, 0, size);
				}

		} catch (Exception e) {
			
		}finally{
			fos.close();
			is.close();
			this.closeRs();
			this.closeStmt();
		}
	}
	/**********含有BLOB对象的读写结束**********/

	//从Java对象到数据库库表的方法--------------
	/**同一个表：将 list中若干记录批量插入与类名(参数T的名字）相同的表中 */
	public  void insertObjects(/*Class<T> type,*/ List<T> list) throws Exception 
	{
		//super.setType(type);
		try{

			preStmt = con.prepareStatement(createInsertQuery());
			for (T instance : list) {
				int i = 0;
				for (Field field : type.getDeclaredFields()) {
					PropertyDescriptor propertyDescriptor = new PropertyDescriptor(field.getName(), type);
					Method method = propertyDescriptor.getReadMethod();
					Object value = method.invoke(instance);
					preStmt.setObject(++i, value);
				}
				preStmt.addBatch();
			}
			preStmt.executeBatch();

		}catch (Exception e) {
			
		}finally{
			this.closeStmt();
		}
	}
	protected String createInsertQuery() {
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO ");
		sb.append(type.getSimpleName());
		sb.append("(");
		sb.append(super.getColumns(false));//表的列明
		sb.append(")");
		sb.append(" VALUES (");
		sb.append(super.getColumns(true));//?
		sb.append(")");
		//MyLogger.showMessage(sb.toString());
		return sb.toString();
	}
	

	///////////////////获取表中记录个数
	public int getRecordsCount(String sql,Object[] params)throws Exception 
	{
		int c = 0;
		if (StringTool.isNull(sql))
			return c;
		try{
	
				preStmt = con.prepareStatement(sql);

				if (preStmt == null)
					return c;
				// stuff parameters in
				if(params != null)
					for (int i=0; i<params.length; i++){
						preStmt.setObject(i + 1,params[i]);
					}

				if (preStmt.execute()){//用于执行返回多个结果集
					rs = preStmt.getResultSet();//获得第一个结果集
				}else{
					return c;
				}
				rs.last(); //光标指向查询结果集中最后一条记录
				c = rs.getRow(); //获取记录总数
				//rs.beforeFirst();//恢复指针的原始位置
		}catch (Exception e) {
			c = 0;
			
		}finally{
			this.closeRs();
			this.closePreStmt();
		}
		return c;
	}

	//执行count(*) 获取
	public int getRecordsCount(String sql) throws Exception
	{
		int c = 0;
		if (StringTool.isNull(sql))
			return c;
		try{
			preStmt = con.prepareStatement(sql);

			if (preStmt == null)
				return c;

			if (preStmt.execute()){//用于执行返回多个结果集
				rs = preStmt.getResultSet();//获得第一个结果集
			}else
				return c;

			while(rs.next())
				c = rs.getInt(1); //获取记录总数 因为结果集只有一行的	
		} catch (Exception e) {
			c = 0;
			
		}finally{
			this.closeRs();
			this.closePreStmt();
		}
		return c;
	}

	/**************************数据库备份与恢复*********************************************/
	/**为进行数据库备份与恢复准备参数*/
	//将MySql中的数据库导出到文件中备份  备份不成功的原因：查看是否将mysql的路径加入到：path
	public boolean backupMySqlToFile(String fileName)	{
		boolean result = true;

		//首先确保Constants.PATH_DBBACK目录存在,因为mysqldump不创建目录	
		String dbpath = Const.getPathDbback();
	
		if(! FileTool.mkdirs(dbpath))
			return result;

		/*Date theDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");//yyyy-MM-dd(HH-MM)
		String nowTime = sdf.format(theDate);
		String fileName = filepath + "DB_" + sdf.format(theDate) + ".sql";
		 */
		//注意mysqldump是调用mysql数据库的一个组件 mysqldump dbmshlg -uroot -p > 2010-4-1.sql
		String stmt = "mysqldump " + Const.getDATABASE() + " -u " + Const.getROOT() + " -p" + Const.getPASSWORD() + " --result-file=\"" + fileName+"\"";
		if ((Const.getOsType()).equalsIgnoreCase("Linux"))
		{
			String mysqldumpPath = Const.getDB_binPath();
			stmt = mysqldumpPath + "/mysqldump -u " + Const.getROOT() + " -p" + Const.getPASSWORD() + " " + Const.getDATABASE() + " -r " + fileName;
			/**String[] command = new String[]{"/usr/local/mysql/bin/mysqldump", "-u" + ContextListener.getROOT(), "-p" + ContextListener.getPASSWORD(),ContextListener.getDATABASE(), "-r"+fileName }; 
			Runtime.getRuntime().exec(command)*/
		}
		else
			stmt = "cmd /c " + stmt;

		//MyLogger.showMessage(stmt);
		//stmt:mysqldump dbmshlg -u root -p123456 --result-file=D:\HLGLogs\DB\DB_2010-07-16.sql
		
		//无cmd /c时，在windows service 2003下会出错 
		try {
			Runtime.getRuntime().exec(stmt); //行string指定的命令，然后停止
		}
		catch (Exception e) {
			
			result = false;
		}


		return result;
	} 

	//当用户恢复数据的时候，直接传一个上传的文件给这个方法，就可以对数据库进行恢复了
	public boolean restoreMysqlFromFile(String fileFullName) {

		boolean result = true;
		//mysql -u root -p123456 dbmshlg < D:\HLGLogs\DBback_2010-04-18(10).sql
		String stmt = "mysql -u " + Const.getROOT() + " -p" + Const.getPASSWORD() + " " + Const.getDATABASE() + " < \"" + fileFullName+"\"";
		String[] cmd = { "cmd", "/c", stmt};

		if ((Const.getOsType()).equalsIgnoreCase("Linux"))
		{
			String mysqldumpPath = Const.getDB_binPath();
			cmd = new String[]{mysqldumpPath+"/mysql", 
					Const.getDATABASE(),
					"-u" + Const.getROOT(),
					"-p" + Const.getPASSWORD(), 
					"-e", 
					" source "+fileFullName };  
			//仅是为调试输出用
			stmt = mysqldumpPath+"/mysql " +Const.getDATABASE()+
					" -u" + Const.getROOT()+
					" -p" + Const.getPASSWORD()+" -e"+" source "+fileFullName;
		}
		//MyLogger.showMessage(stmt);
	

		try
		{
			Runtime.getRuntime().exec(cmd);
		}
		catch (IOException e) {
			
			result = false;
		}

		/*	if (result)
		{
			String source = ContextListener.getPATH_BULLETINBACK();
			String dstPath = ContextListener.getBulletinAttachedPath();
			FileUtil.copyFolder(source, dstPath);
		}
		 */
		return result;
	}
}



