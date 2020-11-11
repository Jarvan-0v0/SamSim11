package org.blkj.sql.test;

import java.sql.*;
import java.util.*;

import org.blkj.sql.core.connection.DbConnection;

import blkjweb.utils.Console;

/**
 * DMLProcessor供用户调用，BaseDMLImp执行真正的SQL操作
 * 执行某操作：获取连接，执行SQL、断开连接。
 * 
 * Class<?> 相当于 Class<? extends Object>，可以用任何由Object派生的类型代替
 * 
 * 使用反射机制的方法以do开始，效率不高 
 */

//用于事务型数据库，已经处理了异常，默认打开了事务，调用j.commit();执行并关闭事务。
public class DoDMLProcessor <T> implements java.io.Serializable 
{
	private static final long serialVersionUID = 1L;
	
	DoBaseDMLImp<T> baseDMLImp = null;
	
    //con 使用指定的数据库连接
	public DoDMLProcessor(Connection con) 
	{
		baseDMLImp = new DoBaseDMLImp<T>(con);
		baseDMLImp.setAutoCommit(false);
	}

 	//创建对象时，就创建了一个BaseDMLImp象，并开启非自动的事务。
	public DoDMLProcessor()
	{
        baseDMLImp = new DoBaseDMLImp<T>(DbConnection.getDefaultCon());
        /**
         * 从性能角度讲，应该注释掉此语句。即采用数据库的默认自动提交模式.此时用户代码无需this.commit()函数;
         */
        baseDMLImp.setAutoCommit(false);//false禁止自动提交; MySQL默认操作模式是自动提交模式
    }

	//执行。先提交事务、然后关闭连接，完成一次完整的事务性BaseDMLImp对象生命周期。
    public void commit()
    {
        try {
            if (! baseDMLImp.isClosed()){
                try {
                    baseDMLImp.commit();
                } catch (SQLException e) {
                    baseDMLImp.rollback();
        			Console.showMessage(DoDMLProcessor.class.getSimpleName(),e.getMessage(), e);
                } finally {
                    baseDMLImp.closeCon();
                }
            }
        } catch (SQLException e) {
			Console.showMessage(DoDMLProcessor.class.getSimpleName(),e.getMessage(), e);
        }
    }
    
    /**最多只有一条满足要求的记录的查询*/
	/**
	 * @return original target object or NULL on error
	 * @param target initialized object to overwrite with database data
	 * @param sql sql statement
	 * @param params array of parameters to the sql statement
	 */
	public Object doQueryOne(Object target, String sql, Object[] params)
	{
		 boolean commit = false;
         int result  = -1;
         try {
             if (!baseDMLImp.isClosed()) {
                 try {
                     result = baseDMLImp.doQueryOne(target, sql, params);
                     commit = true;
                 } catch (SQLException e) {
         			Console.showMessage(DoDMLProcessor.class.getSimpleName(),e.getMessage(), e);
                     baseDMLImp.rollback();
                 } finally {
                     if (!commit) {
                         baseDMLImp.closeCon();
                     }
                 }
             }
         } catch (SQLException e) {
 			Console.showMessage(DoDMLProcessor.class.getSimpleName(),e.getMessage(), e);
         }
      
		if (( result == 0)||(result == -1))
			target = null;
		return target;
	}
	
	/**操作对象一个表：从库表中读入若干记录，表名与参数T的名字相同 
	 * @throws Exception */
	public List<T> doQueryObjects(Class<T> type, String sql, String[] params) 
	{
		 boolean commit = false;
		 List<T> list = new ArrayList<T>();
         try {
             if (!baseDMLImp.isClosed()) {
                 try {
                	 list = baseDMLImp.doQueryObjects(type, sql, params);
                     commit = true;
                 } catch (Exception e) {
         			Console.showMessage(DoDMLProcessor.class.getSimpleName(),e.getMessage(), e);
                     baseDMLImp.rollback();
                 } finally {
                     if (!commit) {
                         baseDMLImp.closeCon();
                     }
                 }
             }
         } catch (SQLException e) {
 			Console.showMessage(DoDMLProcessor.class.getSimpleName(),e.getMessage(), e);
         }
		return list;
	}
	
	
  
	
	
}


