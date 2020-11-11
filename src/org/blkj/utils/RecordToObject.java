package org.blkj.utils;

import java.util.*;

import blkjweb.service.SystemServiceImp;
import blkjweb.utils.Console;
import blkjweb.utils.Const;

public class RecordToObject
{
	/**将数据 List<Map<String, Object>>转换 List<Object>。
	 * List<Map<String, Object>>中每个Map对象为一条记录的内容
	 * List<Object>中每个Object为一个Java对象。
	 */
	public List<Object> queryResult_obj2db(String table, List<Map<String, Object>> result)
	{/**依据对象的字段名进行赋值*/
		if(result == null || table == null)
			return null;
		List<Object> lists = new ArrayList<Object>();
		try {
			Class<?> c = Class.forName(Const.MODEL_PATH + table);
			for (Map<String, Object> rs : result){
				Object info = c.newInstance();
				int num = reflect_obj2db(info, rs);
				if (num > 0){
					lists.add(info);
				}
			}
		} catch (Exception e) {
			Console.showMessage(SystemServiceImp.class.getSimpleName(), e.getMessage(), e);
		}
		return lists;
	}
	/**依据数据库获取的字段名，对对象的字段名进行赋值 */
	public List<Object> queryResult_db2obj(String table, List<Map<String, Object>> result)
	{
		if(result == null || table == null)
			return null;
		
		List<Object> lists = new ArrayList<Object>();
		try {
			Class<?> c = Class.forName(Const.MODEL_PATH + table);
			for (Map<String, Object> rs : result){
				Object info = c.newInstance();
				int num = reflect_db2obj(info, rs);
				if (num > 0){
					lists.add(info);
				}
			}
		} catch (Exception e) {
			Console.showMessage(SystemServiceImp.class.getSimpleName(), e.getMessage(), e);
		}
		return lists;
	}

	//将从库表中检索到一条记录的值反射到对应Bean的字段，并返回此对象objRef
	public int reflect_obj2db(Object objRef, Map<String, Object> result)
	{	/**依据对象的字段名进行赋值*/
		int success = 0;
		//获取对象的所有字段   
		java.lang.reflect.Field [] fields = objRef.getClass().getDeclaredFields();  
		try {
			for (int i = 0; i < fields.length; i++) 
			{
				java.lang.reflect.Field javaField = null;
				javaField = fields[i];
				javaField.setAccessible(true);  
				String key = javaField.getName(); //对象的字段名 隐含与数据库表的字段名一致
				// handle each sql type to java type converstion
				if (javaField.getType() == java.util.Date.class) {//java.sql.Date 原来的; JAVA 对象中定义:java.util.Date
					javaField.set(objRef,result.get(key));
					++success;
				}
				else if (javaField.getType() == int.class) {//JAVA 对象中定义:int类型
					javaField.setInt(objRef, (int)result.get(key));
					++success;
				}
				else if (javaField.getType() == long.class) {//BigInteger 库表元数据
					java.math.BigInteger _v = (java.math.BigInteger)result.get(key);
					javaField.setLong(objRef, _v.longValue());
					++success;
				}
				else{//列为varchar()类型
					Object t = result.get(key);
					javaField.set(objRef, t);
					++success;
				}
			}
		} catch (Exception e) {
			Console.showMessage(RecordToObject.class.getSimpleName(),e.getMessage(), e);
		} 
		return success;
	}

	//将从库表中检索到一条记录的值反射到对应Bean的字段，并返回此对象objRef
	public int reflect_db2obj(Object objRef, Map<String, Object> result)
	{  /**依据数据库获取的字段名，对对象的字段名进行赋值 */
		
		int success = 0;		
		Set<String> key = result.keySet();
		try {
			for (Iterator<String> it = key.iterator(); it.hasNext();) {
				String k = (String) it.next();
				java.lang.reflect.Field javaField = null;
				//获取 Java对象中所定义的与数据库表中的字段名称javacol对象的属性
				javaField = objRef.getClass().getDeclaredField(k);
				javaField.setAccessible(true); //设置允许访问  
				
				// handle each sql type to java type converstion
				if (javaField.getType() == java.util.Date.class) {//java.sql.Date 原来的; JAVA 对象中定义:java.util.Date
					javaField.set(objRef,result.get(k));
					++success;
				}
				else if (javaField.getType() == int.class) {//JAVA 对象中定义:int类型
					javaField.setInt(objRef, (int)result.get(k));
					++success;
				}
				else if (javaField.getType() == long.class) {//BigInteger 库表元数据
					java.math.BigInteger _v = (java.math.BigInteger)result.get(key);
					javaField.setLong(objRef, _v.longValue());
					++success;
				}
				else{//列为varchar()类型
					Object t = result.get(k);
					javaField.set(objRef, t);
					++success;
				}
			}
		} catch (Exception e) {
			Console.showMessage(RecordToObject.class.getSimpleName(),e.getMessage(), e);
		} 
		return success;
	}

}
