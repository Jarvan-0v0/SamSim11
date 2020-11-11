
package org.blkj.utils;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import org.apache.commons.lang3.StringUtils;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.logging.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringTool implements java.io.Serializable 
{

	/**
	 * 将字符串分解成字符串数组
	 *
	 * @param str 待分解的字符串
	 * @param token 分隔符，如："|"、","、"."、"---"、" "等
	 * @return 返回字符串数组
	 */
	public static String[] stringToArray(String str, String token) {

		if(StringTool.isNullEmpty(str))
			return null;

		String[] ss = new String[0];
		List<String> a = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(str, token);
		while (st.hasMoreTokens()) {
			a.add(st.nextElement().toString());
		}
		if (a.size() > 0) {
			ss = new String[a.size()];
			for (int i = 0; i < ss.length; i++) {
				ss[i] = a.get(i).toString();
			}
		}
		return ss;
	}

	//java中判断字符串是否为数字的
	public static boolean isNumeric(String str)
	{
		if(isNullEmpty(str))
			return false;
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();   
	} 
	//-1表示错误
	public static int stringToInt(String temp)
	{   /* i = Integer.parseInt([String],[int radix]);
	 int i = Integer.valueOf(my_str).intValue()
	 */
		if(isNumeric(temp))
			return Integer.parseInt(temp);
		else
			return -1;
	}

	/**
	 * 检测字符串是否为空(null,"","null")
	 * @param s
	 * @return 为空则返回true，不否则返回false
	 */
	public static boolean isNullEmpty(String temp)
	{
		if ( temp == null || "null".equalsIgnoreCase(temp) || temp.length() == 0||temp=="" )
			return true;
		else
			return false;
	}

	public static boolean listIsNullEmpty(List<?> list)
	{
		if(list == null || list.size() == 0 )
			return true;
		else
			return false;
	}
	//判断对象是否为空或者传入集合对象的是否为空，以及判断数组的长度
	/** 
	 * Object、String、List、ArrayList、Map、HashMap、Vector、Hashtable、LinkedList、TreeSet
	 * Set、 Iterator、LinkedHashMap、LinkedHashSet、WeakHashMap、SortedMap等
	 * 空值检查
	 * @param pInput   要检查的字符串<br> 
	 * @return boolean 返回检查结果,如果传入的字符串为空,返回真<br> 
	 */  
	public static boolean isNull(Object object) {  
		//判断参数是否为null或''
		if (object == null || "null".equals(object) || "".equals(object)) {  
			return true;  
		} 
		else
			if (object instanceof String) {// 字符串"   "返回TRUE
				return StringUtils.isEmpty(StringUtils.trim((String)object));
				/* 字符串"   "返回FALSE
							  if ("java.lang.String".equals(object.getClass().getName())){//判断传入的参数的String类型的  
								//替换各种空格  u3000表示空格：
								String tmpInput = Pattern.compile("//r|//n|//u3000").matcher((String)object).replaceAll("");  
								// 匹配空  
								return Pattern.compile("^(//s)*$").matcher(tmpInput).matches();
				 */  
			} else {//方法类  
				Method method = null;  
				String newInput = "";  
				try {  
					// 访问传入参数的size方法  
					method = object.getClass().getMethod("size");  
					// 转换为String类型  
					newInput = String.valueOf(method.invoke(object));  
					// size为0的场合  
					if (Integer.parseInt(newInput) == 0) {  
						return true;  
					} else {  
						return false;  
					}  
				} catch (Exception e) {  
					try { // 访问传入参数的getItemCount方法   
						method = object.getClass().getMethod("getItemCount");  
						// 转换为String类型  
						newInput = String.valueOf(method.invoke(object));  
						// getItemCount为0的场合  
						if (Integer.parseInt(newInput) == 0)   
							return true;  
						else   
							return false;  
					} catch (Exception ex) {  
						try{// 判断传入参数的长度  长度为0的场合    
							if (Array.getLength(object) == 0)   
								return true;  
							else   
								return false;  
						} catch (Exception exx) {  
							try{  
								// 访问传入参数的hasNext方法  
								method = Iterator.class.getMethod("hasNext");  
								// 转换String类型  
								newInput = String.valueOf(method.invoke(object));  
								// 转换hasNext的值  
								if (!Boolean.valueOf(newInput))   
									return true;  
								else   
									return false;  
							} catch (Exception exxx) {  
								return false;  
							}  
						}  
					}  
				}  
			}  
	}  


	private static final long serialVersionUID = 1L;
	public StringTool() {  }


	void xbm(){};








	public static String intToString(int temp)
	{   
		//String s = String.valueOf(i);
		return Integer.toString(temp);
	}


	public String arryToString(Object[] list, String linkop) {
		String s = null;
		if (list != null) {
			if (list.length > 0) {
				s = list[0].toString();
				if (list.length > 1) {
					for (int i = 1; i < list.length; i++) {
						s = s + linkop + list[i];
					}
				}
			}
		}
		return s;
	}

	/**
	 * 判断字段名是否在字段名的数组中
	 */
	public static boolean isInFields(Object[] fields, Object fieldName) 
	{
		boolean b = false;
		if (fields != null)
			for (int i = 0; i < fields.length; i++) {
				if (fields[i].equals(fieldName)) {
					b = true;
					return b;
				}
			}
		return b;
	}







	/**
	 * 检测字符串是否不为空(null,"","null")
	 * @param s
	 * @return 不为空则返回true，否则返回false
	 */
	public static boolean notEmpty(String s){
		return s!=null && !"".equals(s) && !"null".equals(s);
	}



	/**
	 * 字符串转换为字符串数组
	 * @param str 字符串
	 * @param splitRegex 分隔符
	 * @return
	 */
	public static String[] str2StrArray(String str,String splitRegex){
		if(isNullEmpty(str)){
			return null;
		}
		return str.split(splitRegex);
	}

	/**
	 * 用默认的分隔符(,)将字符串转换为字符串数组
	 * @param str	字符串
	 * @return
	 */
	public static String[] str2StrArray(String str){
		return str2StrArray(str,",\\s*");
	}


	/**
	 * 验证邮箱
	 * @param email
	 * @return
	 */
	public static boolean checkEmail(String email){
		boolean flag = false;
		try{
			String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
			Pattern regex = Pattern.compile(check);
			Matcher matcher = regex.matcher(email);
			flag = matcher.matches();
		}catch(Exception e){
			flag = false;
		}
		return flag;
	}

	/**
	 * 验证手机号码
	 * @param mobiles
	 * @return
	 */
	public static boolean checkMobileNumber(String mobileNumber){
		boolean flag = false;
		try{
			Pattern regex = Pattern.compile("^(((13[0-9])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8})|(0\\d{2}-\\d{8})|(0\\d{3}-\\d{7})$");
			Matcher matcher = regex.matcher(mobileNumber);
			flag = matcher.matches();
		}catch(Exception e){
			flag = false;
		}
		return flag;
	}


	protected String preStr(String temp)
	{
		if( temp == null || 
				temp.length() == 0 || 
				temp.equalsIgnoreCase("null")
				)
			temp = "";

		return temp;
	}




	/**
	 * 将存贮在列表中的单个字符串输出为格式字符串
	 */
	public String listToString(AbstractList<?> list, String linkop) {
		String s = null;
		String ms = null;
		if (list != null) {
			if (list.size() == 1) {
				s = (String) list.get(0);
			}
			if (list.size() == 2) {
				s = (String) list.get(0) + " " + linkop + " " + (String) list.get(1);
			}
			if (list.size() >= 3) {
				for (int i = 1; i < list.size() - 1; i++) {
					ms = " " + linkop + " " + (String) list.get(i) + " " + linkop + " ";
				}
				s = (String) list.get(0) + ms + (String) list.get(list.size() - 1);
			}
		}
		return s;
	}



	/**
	 * 给字段值加上单引号，可用于插入语句的values部分
	 *
	 * @return 输出字符串
	 */
	public String arryToValues(Object[] list, String linkop) {
		String s = null;
		if (list != null) {
			if (list.length > 0) {
				s = "'" + list[0].toString() + "'";
				if (list.length > 1) {
					for (int i = 1; i < list.length; i++) {
						s = s + linkop + "'" + list[i].toString() + "'";
					}
				}
			}
		}
		return s;
	}

	public String[] arryToArray(String[] list, String l, String r) {
		if (list == null) {
			return null;
		}
		String s = null;
		for (int i = 0; i < list.length; i++) {
			s = l + list[i] + r;
			list[i] = s;
		}
		return list;
	}

	public String[] intArrayToStrArray(int[] intArray) {
		if (intArray == null) {
			return null;
		}
		String[] str = new String[intArray.length];
		for (int i = 0; i < intArray.length; i++) {
			str[i] = "" + intArray[i];
		}
		return str;
	}

	/**
	 * 匹配正则表达式
	 *
	 * @param pattern 正则表达式
	 * @param str 待匹配的字符串
	 * @return 返回匹配结果
	 */
	public static boolean matches(String pattern, String str) {
		return Pattern.matches(pattern, str);
	}


	/*可用于生成一个日志文件*/
	public void exceptionLogging(String logFile, String className,
			Exception exception) {
		java.util.logging.Logger logger = java.util.logging.Logger.getLogger(className);
		logger.setUseParentHandlers(false);
		FileHandler fh = null;
		try {
			fh = new FileHandler(logFile, 1000000, 1, true);
			fh.setFormatter(new SimpleFormatter());
			logger.addHandler(fh);
			logger.log(Level.INFO, " Uncaused Exception ", exception);
		} catch (SecurityException ex1) {
		} catch (IOException ex1) {
		}
	}

	/**
	 * 例33 将一个文件读入字节数组
	 *
	 */
	public byte[] getBytesFromFile(File file)  {
		byte[] s = null;
		InputStream is = null;
		try {
			is = new FileInputStream(file);

			long length = file.length();
			if (length > Integer.MAX_VALUE) {
				throw new IOException(file.getName() + " is too large , break.");
			}
			s = new byte[(int) length];
			int offset = 0;
			int numRead = 0;
			while (offset < s.length
					&& (numRead = is.read(s, offset, s.length - offset)) >= 0) {
				offset += numRead;
			}
			if (offset < s.length) {
				throw new IOException("Could not completely read file " + file.getName());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			if(is != null)
				try {
					is.close();
				} catch (IOException e) {
				}
		}
		return s;
	}

	/**
	 * 从一个文本文件中提取字符串
	 *
	 */
	public String getStringFromFile(File textFile) throws IOException {
		String s = new String(getBytesFromFile(textFile));
		return s;
	}

	/**
	 * 例136 从URL中读取文本
	 *
	 */
	public static  String getStringFromURL(URL url) throws IOException
	{
		String s;
		StringBuilder sb = new StringBuilder();
		InputStream is = url.openStream();

		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		while ((s = in.readLine()) != null) {
			if (!s.endsWith("\r\n")) {
				sb.append(s).append("\r\n");//还原换行符
			} else {
				sb.append(s);
			}
		}
		in.close();

		return sb.toString();
	}







}
