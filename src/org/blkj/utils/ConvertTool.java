package org.blkj.utils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.beanutils.BeanUtils;
import org.blkj.utils.base.DateJsonValueProcessor;

import blkjweb.utils.Console;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.CycleDetectionStrategy;

public class ConvertTool {

	/**
	 * @param list list对象
	 * @return String 格式：[{},{}]
	 */
	public static String list2json(List<?> list) {
		if (StringTool.listIsNullEmpty(list))
			return "";

		StringBuilder json = new StringBuilder();
		json.append("[");
		if (list != null && list.size() > 0) {
			for (Object obj : list) {
				json.append(object2json(obj));
				json.append(",");
			}
			json.setCharAt(json.length() - 1, ']');
		} else {
			json.append("]");
		}
		return json.toString();
	}

	void xbm() {
	};

	public static int convertPDF2SWF(String sourcePath, String destPath, String fileName) throws IOException {
		// 目标路径不存在则建立目标路径
		File dest = new File(destPath);
		if (!dest.exists())
			dest.mkdirs();
		// 源文件不存在则返回
		File source = new File(sourcePath);
		if (!source.exists())
			return 0;
		// 调用pdf2swf命令进行转换
		String command = "";// "D:\Program Files\SWFTools\pdf2swf.exe" + " -o " + destPath + "" + fileName
		// +" -s flashversion=9" +" -t "+sourcePath
		// +" -s languagedir=D:\lifc\soft\Flex\FlexPaper\xpdf-chinese-simplified"+" -f";
		// +" -f";
		Process pro = Runtime.getRuntime().exec(command);
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(pro.getInputStream()));
		while (bufferedReader.readLine() != null)
			;
		try {
			pro.waitFor();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pro.exitValue();
	}

	/**
	 * @param obj 任意对象
	 * @return String
	 */
	public static String object2json(Object obj) {
		StringBuilder json = new StringBuilder();
		if (obj == null) {
			json.append("\"\"");
		} else if (obj instanceof String || obj instanceof Integer || obj instanceof Float || obj instanceof Boolean
				|| obj instanceof Short || obj instanceof Double || obj instanceof Long || obj instanceof BigDecimal
				|| obj instanceof BigInteger || obj instanceof Byte) {
			json.append("\"").append(string2json(obj.toString())).append("\"");
		} else if (obj instanceof Object[]) {
			json.append(array2json((Object[]) obj));
		} else if (obj instanceof List) {
			json.append(list2json((List<?>) obj));
		} else if (obj instanceof Map) {
			json.append(map2json((Map<?, ?>) obj));
		} else if (obj instanceof Set) {
			json.append(set2json((Set<?>) obj));
		} else if (obj instanceof Date) {// 需要单独处理，默认bean2json处理会将：“2015-1-1”处理为时间戳格式的JSON
			json.append(date2json(obj));
		} else {
			json.append(bean2json(obj));
		}

		return json.toString();
	}

	/**
	 * jqgrid主要是支持这php，而php和java在时间戳上有区别， php里面，时间戳用10位数字表示，精确到秒，
	 * java里面，时间戳用13位数字表示，精确到毫秒
	 * 
	 * @param bean bean对象
	 * @return String 实体类里包含Java.util.Date类型的属性，转换后，会变成这样（如果没做任何处理） "receiveTime": {
	 *         "date": 23, "day": 1, "hours": 17, "minutes": 51, "month": 4,
	 *         "seconds": 21, "time": 1463997081061, "timezoneOffset": -480, "year":
	 *         116 }
	 */
	public static String date2json(Object bean) {
		return "\"" + bean.toString() + "\"";
	}

	/**
	 * @param bean bean对象
	 * @return String 时间戳有问题
	 */
	public static String bean2json(Object bean) {
		StringBuilder json = new StringBuilder();
		json.append("{");
		PropertyDescriptor[] props = null;
		try {
			props = Introspector.getBeanInfo(bean.getClass(), Object.class).getPropertyDescriptors();
		} catch (IntrospectionException e) {
		}
		if (props != null) {
			for (int i = 0; i < props.length; i++) {
				try {
					String name = object2json(props[i].getName());
					String value = object2json(props[i].getReadMethod().invoke(bean));
					json.append(name);
					json.append(":");
					json.append(value);
					json.append(",");
				} catch (Exception e) {
				}
			}
			json.setCharAt(json.length() - 1, '}');
		} else {
			json.append("}");
		}
		return json.toString();
	}

	/**
	 * @param set 集合对象
	 * @return String
	 */
	public static String set2json(Set<?> set) {
		StringBuilder json = new StringBuilder();
		json.append("[");
		if (set != null && set.size() > 0) {
			for (Object obj : set) {
				json.append(object2json(obj));
				json.append(",");
			}
			json.setCharAt(json.length() - 1, ']');
		} else {
			json.append("]");
		}
		return json.toString();
	}

	/**
	 * @param map map对象
	 * @return String
	 */
	public static String map2json(Map<?, ?> map) {
		StringBuilder json = new StringBuilder();
		json.append("{");
		if (map != null && map.size() > 0) {
			for (Object key : map.keySet()) {
				json.append(object2json(key));
				json.append(":");
				json.append(object2json(map.get(key)));
				json.append(",");
			}
			json.setCharAt(json.length() - 1, '}');
		} else {
			json.append("}");
		}
		return json.toString();
	}

	/**
	 * @param array 对象数组
	 * @return String
	 */
	public static String array2json(Object[] array) {
		StringBuilder json = new StringBuilder();
		json.append("[");
		if (array != null && array.length > 0) {
			for (Object obj : array) {
				json.append(object2json(obj));
				json.append(",");
			}
			json.setCharAt(json.length() - 1, ']');
		} else {
			json.append("]");
		}
		return json.toString();
	}

	/**
	 * @param s 参数
	 * @return String
	 */
	public static String string2json(String s) {
		if (s == null)
			return "";
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			switch (ch) {
			case '"':
				sb.append("\\\"");
				break;
			case '\\':
				sb.append("\\\\");
				break;
			case '\b':
				sb.append("\\b");
				break;
			case '\f':
				sb.append("\\f");
				break;
			case '\n':
				sb.append("\\n");
				break;
			case '\r':
				sb.append("\\r");
				break;
			case '\t':
				sb.append("\\t");
				break;
			case '/':
				sb.append("\\/");
				break;
			default:
				if (ch >= '\u0000' && ch <= '\u001F') {
					String ss = Integer.toHexString(ch);
					sb.append("\\u");
					for (int k = 0; k < 4 - ss.length(); k++) {
						sb.append('0');
					}
					sb.append(ss.toUpperCase());
				} else {
					sb.append(ch);
				}
			}
		}
		return sb.toString();
	}

	/**
	 * 将Json:格式：{} 对象转换成Map Commons Collections 4 不工作。只能用3
	 * 
	 * @param jsonObject json对象
	 * @return Map对象
	 * @throws JSONException
	 */
	public static Map<String, Object> json2Map(String jsonString) {
		if (StringTool.isNullEmpty(jsonString))
			return null;

		JSONObject jsonObject = JSONObject.fromObject(jsonString);
		Map<String, Object> result = new HashMap<String, Object>();
		Iterator<?> iterator = jsonObject.keys();
		String key = null;
		String value = null;
		while (iterator.hasNext()) {
			key = (String) iterator.next();
			value = jsonObject.getString(key);
			result.put(key, value);
		}
		return result;
	}

	/**
	 * 将Json:格式：[{}] 对象转换成Map
	 */
	public static List<Map<String, Object>> json4Map(String jsonString) {
		JSONArray jsonArray = JSONArray.fromObject(jsonString);

		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		int size = 0;
		if (jsonArray != null) {
			size = jsonArray.size();
			for (int i = 0; i < size; i++)// 所有{}即[{}]
			{
				Map<String, Object> map = new HashMap<String, Object>();
				JSONObject jsonObject = jsonArray.getJSONObject(i);

				Iterator<?> iterator = jsonObject.keys();
				String key = null;
				String value = null;
				while (iterator.hasNext()) {// 每个{} 定义字段的所有信息
					key = (String) iterator.next();
					value = jsonObject.getString(key);
					map.put(key, value);
				}
				result.add(map);
			}
		}
		return result;
	}

	// 利用Introspector和PropertyDescriptor 将Bean --> Map
	public static Map<String, Object> bean2Map(Object obj) {
		if (obj == null) {
			return null;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			for (PropertyDescriptor property : propertyDescriptors) {
				String key = property.getName();
				// 过滤class属性
				if (!key.equals("class")) {
					// 得到property对应的getter方法
					Method getter = property.getReadMethod();
					Object value = getter.invoke(obj);

					map.put(key, value);
				}
			}
		} catch (Exception e) {
			Console.showMessage(ConvertTool.class.getSimpleName(), e.getMessage(), e);
		}
		return map;
	}

	/**
	 * BeanUtils.populate方法的限制：<br>
	 * The class must be public, and provide a public constructor that accepts no
	 * arguments. <br>
	 * This allows tools and applications to dynamically create new instances of
	 * your bean, <br>
	 * without necessarily knowing what Java class name will be used ahead of time
	 */
	// 利用org.apache.commons.beanutils 工具类实现 Map --> Bean
	public static void map2Bean_beanutils(Map<String, Object> map, Object obj) {
		if (map == null || obj == null) {
			return;
		}
		try {
			BeanUtils.populate(obj, map);
		} catch (Exception e) {
			Console.showMessage(ConvertTool.class.getSimpleName(), e.getMessage(), e);
		}
	}

	// 利用Introspector,PropertyDescriptor实现 Map --> Bean
	public static void map2Bean_Introspector(Map<String, Object> map, Object obj) {
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

			for (PropertyDescriptor property : propertyDescriptors) {
				String key = property.getName();
				if (map.containsKey(key)) {
					Object value = map.get(key);
					Method setter = property.getWriteMethod();
					setter.invoke(obj, value);
				}
			}

		} catch (Exception e) {
			Console.showMessage(ConvertTool.class.getSimpleName(), e.getMessage(), e);
		}
		return;
	}

	/* 功能：将List<Object>转换为对应的List<具体类> */
	public static <T extends Object> List<T> castList(List<Object> list, Class<T> clazz) {
		List<T> arrayList = new ArrayList<T>();

		if (list != null)
			for (int i = 0; i < list.size(); i++) {
				arrayList.add(clazz.cast(list.get(i)));
			}
		return arrayList;
	}

	public static String getISO(String szSource) {
		if (szSource == null || szSource.trim().equals(""))
			return "";
		try {
			return new String(szSource.getBytes("utf-8"), "ISO8859-1");
		} catch (Exception e) {
			Console.showMessage("ConvertTool:" + e.getMessage());
			return null;
		}
	}

	// 保证中文文件名有效
	public static String ChineseCharacter(String fileName) throws IOException {

		String prefix;
		String suffix;
		int dotIndex = fileName.lastIndexOf(".");

		suffix = fileName.substring(dotIndex + 1);
		prefix = fileName.substring(0, dotIndex);

		prefix = new String(prefix.getBytes(), "gb2312");
		suffix = new String(suffix.getBytes(), "gb2312");

		return prefix + "." + suffix;
	}

	// 将西欧字符转换为gb2312字符的转换方法
	public static final String getGBString(String src) {
		try {
			return new String(src.getBytes("ISO-8859-1"), "gb2312");
		} catch (java.io.UnsupportedEncodingException e) {
			return null;
		}
	}

	// 汉字是gbk或gb2312,转为UTF8
	public static String GBKtoUTF8(String s) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c >= 0 && c <= 255) {
				sb.append(c);
			} else {
				byte[] b;
				try {
					b = Character.toString(c).getBytes("UTF-8");
				} catch (Exception e) {
					Console.showMessage(StringTool.class.getSimpleName(), e.getMessage(), e);
					b = new byte[0];
				}
				for (int j = 0; j < b.length; j++) {
					int k = b[j];
					if (k < 0)
						k += 256;
					sb.append("%" + Integer.toHexString(k).toUpperCase());
				}
			}
		}
		return sb.toString();
	}

	public static String GBKtoISO(String szSource) {
		if (szSource == null || szSource.trim().equals(""))
			return "";
		try {
			return new String(szSource.getBytes("GBK"), "8859_1");
		} catch (Exception e) {
			return null;
		}
	}

	// 在IO输出中汉字是乱码，想看到再转GBK
	public static String ISOtoGBK(String szSource) {
		if (szSource == null || szSource.trim().equals(""))
			return "";
		try {
			return new String(szSource.getBytes("8859_1"), "GBK");
		} catch (Exception e) {
			return null;
		}
	}

	// List<Student> students = castList(result.get("out_refcursor"),
	// Student.class);
	public static <T> List<T> castList(Object obj, Class<T> clazz) {
		List<T> result = new ArrayList<T>();
		if (obj instanceof List<?>) {
			for (Object o : (List<?>) obj) {
				result.add(clazz.cast(o));
			}
			return result;
		}
		return null;
	}

	public Map<String, String> convertValueAttributes(final Map<String, Object> attributes) {
		final Map<String, String> result = new HashMap<String, String>();
		for (final Map.Entry<String, Object> entry : attributes.entrySet()) {
			result.put(entry.getKey(), String.valueOf(entry.getValue()));
		}
		return result;
	}

	public Map<String, Object> convertKeyAttributes(final Map<Object, Object> attributes) {
		final Map<String, Object> result = new HashMap<String, Object>();
		for (final Map.Entry<Object, Object> entry : attributes.entrySet()) {
			result.put(String.valueOf(entry.getKey()), entry.getValue());
		}
		return result;
	}

	/*
	 * Convert List<Map<String, Object>> to List<String> List<Map<String, Object>>
	 * mapList=new ArrayList<Map<String, Object>>();
	 * 
	 * List<Object> list=new ArrayList<Object>(); for(Map<String,Object>
	 * i:dataList){ list.addAll(i.values()); }
	 */

	public Map<String, Integer> convertMap(List<Map<String, Object>> input) {

		Map<String, Integer> dest = new HashMap<>();
		for (Map<String, Object> next : input) {
			for (Map.Entry<String, Object> entry : next.entrySet()) {
				dest.put(entry.getKey(), (Integer) entry.getValue());
			}
		}
		return dest;
	}

	// 用于把List<Object>转换成Map<String,Object>形式
	/**
	 * 用于把List<Object>转换成Map<String,Object>形式，便于存入缓存
	 * 
	 * @param keyName 主键属性
	 * @param list    集合
	 * @return 返回对象
	 */
	public static <T> Map<String, T> listToMap(String keyName, List<T> list) {
		Map<String, T> m = new HashMap<String, T>();
		try {
			for (T t : list) {
				PropertyDescriptor pd = new PropertyDescriptor(keyName, t.getClass());
				Method getMethod = pd.getReadMethod();// 获得get方法
				Object o = getMethod.invoke(t);// 执行get方法返回一个Object
				m.put(o.toString(), t);
			}
			return m;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param list list对象
	 * @return String
	 */
	public static JSONArray list2Aarry(List<?> list) {
		return JSONArray.fromObject(list);
	}

	/** */
	/**
	 * 从json数组中得到相应java数组
	 * 
	 * @param jsonString
	 * @return
	 */
	public static Object[] getObjectArray4Json(String jsonString) {
		JSONArray jsonArray = JSONArray.fromObject(jsonString);
		return jsonArray.toArray();

	}

	/**
	 * 从json数组中解析出java字符串数组
	 * 
	 * @param jsonString
	 * @return
	 */
	public static String[] getStringArray4Json(String jsonString) {

		JSONArray jsonArray = JSONArray.fromObject(jsonString);
		String[] stringArray = new String[jsonArray.size()];
		for (int i = 0; i < jsonArray.size(); i++) {
			stringArray[i] = jsonArray.getString(i);
		}
		return stringArray;
	}

	/** */
	/**
	 * 从json数组中解析出javaLong型对象数组
	 * 
	 * @param jsonString
	 * @return
	 */
	public static Long[] getLongArray4Json(String jsonString) {

		JSONArray jsonArray = JSONArray.fromObject(jsonString);
		Long[] longArray = new Long[jsonArray.size()];
		for (int i = 0; i < jsonArray.size(); i++) {
			longArray[i] = jsonArray.getLong(i);

		}
		return longArray;
	}

	/** */
	/**
	 * 从json数组中解析出java Integer型对象数组
	 * 
	 * @param jsonString
	 * @return
	 */
	public static Integer[] getIntegerArray4Json(String jsonString) {

		JSONArray jsonArray = JSONArray.fromObject(jsonString);
		Integer[] integerArray = new Integer[jsonArray.size()];
		for (int i = 0; i < jsonArray.size(); i++) {
			integerArray[i] = jsonArray.getInt(i);

		}
		return integerArray;
	}

	/**
	 * 从json数组中解析出java Integer型对象数组
	 * 
	 * @param jsonString
	 * @return
	 */
	public static Double[] getDoubleArray4Json(String jsonString) {

		JSONArray jsonArray = JSONArray.fromObject(jsonString);
		Double[] doubleArray = new Double[jsonArray.size()];
		for (int i = 0; i < jsonArray.size(); i++) {
			doubleArray[i] = jsonArray.getDouble(i);

		}
		return doubleArray;
	}

	/** */
	/**
	 * 将java对象转换成json字符串
	 * 
	 * @param javaObj
	 * @return
	 */
	public static String getJsonString4JavaPOJO(Object javaObj) {

		JSONObject json;
		json = JSONObject.fromObject(javaObj);
		return json.toString();

	}

	/** */
	/**
	 * 将java对象转换成json字符串,并设定日期格式
	 * 
	 * @param javaObj
	 * @param dataFormat
	 * @return
	 */
	public static String getJsonString4JavaPOJO(Object javaObj, String dataFormat) {

		JSONObject json;
		JsonConfig jsonConfig = configJson(dataFormat);
		json = JSONObject.fromObject(javaObj, jsonConfig);
		return json.toString();

	}

	/**
	 * JSON 时间解析器具
	 * 
	 * @param datePattern
	 * @return
	 */
	public static JsonConfig configJson(String datePattern) {
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setExcludes(new String[] { "" });
		jsonConfig.setIgnoreDefaultExcludes(false);
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		jsonConfig.registerJsonValueProcessor(Date.class, new DateJsonValueProcessor(datePattern));

		return jsonConfig;
	}

	/** */
	/**
	 * 
	 * @param excludes
	 * @param datePattern
	 * @return
	 */
	public static JsonConfig configJson(String[] excludes, String datePattern) {
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setExcludes(excludes);
		jsonConfig.setIgnoreDefaultExcludes(false);
		jsonConfig.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		jsonConfig.registerJsonValueProcessor(Date.class, new DateJsonValueProcessor(datePattern));

		return jsonConfig;
	}

	private Map<String, Object> jsonMap = new HashMap<String, Object>();
	private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

	public void clear() {
		jsonMap.clear();
	}

	/**
	 * 添加元素
	 * 
	 * @param key
	 * @param value 支持简单类型（即原生类型的包装器类）、bean对象、List<Object>、Map<String,Object>以及数组
	 * @return
	 */
	public Map<String, Object> put(String key, Object value) {
		jsonMap.put(key, value);
		return jsonMap;
	}

	// 判断是否要加引号
	private static boolean isNoQuote(Object value) {
		return (value instanceof Integer || value instanceof Boolean || value instanceof Double
				|| value instanceof Float || value instanceof Short || value instanceof Long || value instanceof Byte);
	}

	private static boolean isQuote(Object value) {
		return (value instanceof String || value instanceof Character);
	}

	/**
	 * javaBean与Map<String,Object>互转利用到了java的内省（ Introspector ）和反射（reflect）机制。 其思路为：
	 * 通过类 Introspector 来获取某个对象的 BeanInfo 信息，然后通过 BeanInfo 来获取属性的描
	 * 述器PropertyDescriptor，再利用属性描述器获取某个属性对应的 getter/setter 方法，然后通过反射机制
	 * 来getter和setter。
	 * 
	 */
	// 利用Introspector和PropertyDescriptor 将Bean --> Map
	public static Map<String, Object> Bean2Map(Object obj) {
		if (obj == null) {
			return null;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			for (PropertyDescriptor property : propertyDescriptors) {
				String key = property.getName();

				// 过滤class属性
				if (!key.equals("class")) {
					// 得到property对应的getter方法
					Method getter = property.getReadMethod();
					Object value = getter.invoke(obj);

					map.put(key, value);
				}

			}
		} catch (Exception e) {
			Console.showMessage(ConvertTool.class.getSimpleName(), e.getMessage(), e);
		}

		return map;
	}

	// 利用org.apache.commons.beanutils 工具类实现 Map --> Bean 结果保存在 obj
	public static void Map2Bean2(Map<String, Object> map, Object obj) {
		if (map == null || obj == null) {
			return;
		}
		try {
			BeanUtils.populate(obj, map);
		} catch (Exception e) {
			Console.showMessage(ConvertTool.class.getSimpleName(), e.getMessage(), e);
		}
	}

	// 利用Introspector,PropertyDescriptor实现 Map-->Bean 结果保存在 obj
	public static void Map2Bean(Map<String, Object> map, Object obj) {

		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			for (PropertyDescriptor property : propertyDescriptors) {
				String key = property.getName();
				if (map.containsKey(key)) {
					Object value = map.get(key);
					// 得到property对应的setter方法
					Method setter = property.getWriteMethod();
					setter.invoke(obj, value);
				}
			}
		} catch (Exception e) {
			Console.showMessage(ConvertTool.class.getSimpleName(), e.getMessage(), e);
		}
		return;
	}

	/**
	 * 从Map中装载数据
	 */
	public static ConvertTool fromObject(Map<String, Object> map) {
		ConvertTool json = new ConvertTool();
		if (map == null)
			return json;
		json.getMap().putAll(map);
		return json;
	}

	private static String getGetter(String property) {
		return "get" + property.substring(0, 1).toUpperCase() + property.substring(1, property.length());
	}

	public Map<String, Object> getMap() {
		return this.jsonMap;
	}

	public static String ArrayToStr(Object array) {
		if (!array.getClass().isArray())
			return "[]";
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		int len = Array.getLength(array);
		Object v = null;
		for (int i = 0; i < len; i++) {
			v = Array.get(array, i);
			if (v instanceof Date) {
				sb.append("'").append(formatter.format(v)).append("'").append(",");
			} else if (isQuote(v)) {
				sb.append("'").append(v).append("'").append(",");
			} else if (isNoQuote(v)) {
				sb.append(i).append(",");
			} else {
				sb.append(fromObject(v)).append(",");
			}
		}
		len = sb.length();
		if (len > 1)
			sb.delete(len - 1, len);
		sb.append("]");
		return sb.toString();
	}

	/** */
	/**
	 * 从json对象集合表达式中得到一个java对象列表
	 * 
	 * @param jsonString
	 * @param pojoClass
	 * @return
	 */
	public static List<Object> getList4Json(String jsonString, Class<?> pojoClass) {
		JSONArray jsonArray = JSONArray.fromObject(jsonString);
		JSONObject jsonObject;
		Object pojoValue;
		List<Object> list = new ArrayList<Object>();
		for (int i = 0; i < jsonArray.size(); i++) {
			jsonObject = jsonArray.getJSONObject(i);
			pojoValue = JSONObject.toBean(jsonObject, pojoClass);
			list.add(pojoValue);
		}
		return list;
	}

	/**
	 * 从一个JSON 对象字符格式中得到一个java对象
	 * 
	 * @param jsonString
	 * @param pojoCalss
	 * @return
	 */
	public static Object getObject4JsonString(String jsonString, Class<?> pojoCalss) {
		Object pojo;
		JSONObject jsonObject = JSONObject.fromObject(jsonString);
		pojo = JSONObject.toBean(jsonObject, pojoCalss);
		return pojo;
	}

	/**
	 * 从json HASH表达式中获取一个map，改map支持嵌套功能
	 * 
	 * @param jsonString
	 * @return
	 */
	public static Map<String, Object> getMap4Json(String jsonString) {
		JSONObject jsonObject = JSONObject.fromObject(jsonString);
		Iterator<?> keyIter = jsonObject.keys();
		String key;
		Object value;
		Map<String, Object> valueMap = new HashMap<String, Object>();
		while (keyIter.hasNext()) {
			key = (String) keyIter.next();
			value = jsonObject.get(key);
			valueMap.put(key, value);
		}
		return valueMap;
	}

	/**
	 * 从一个bean装载数据，返回一个JsonUtil对象。
	 * 
	 * @param object
	 * @return
	 */
	public static ConvertTool fromObject(Object bean) {
		ConvertTool json = new ConvertTool();
		if (bean == null)
			return json;
		Class<? extends Object> cls = bean.getClass();
		Field[] fs = cls.getDeclaredFields();
		Object value = null;
		String fieldName = null;
		Method method = null;
		int len = fs.length;
		for (int i = 0; i < len; i++) {
			fieldName = fs[i].getName();
			try {
				method = cls.getMethod(getGetter(fieldName), (Class[]) null);
				value = method.invoke(bean, (Object[]) null);
			} catch (Exception e) {
				// Log.debug(method.getName());
				// e.printStackTrace();
				continue;
			}
			json.put(fieldName, value);
		}
		return json;
	}

	public static String ListToStr(List<Object> list) {
		if (list == null)
			return null;
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		Object value = null;
		for (java.util.Iterator<Object> it = list.iterator(); it.hasNext();) {
			value = it.next();
			if (value instanceof Map) {
				sb.append(fromObject(value).toString()).append(",");
			} else if (isNoQuote(value)) {
				sb.append(value).append(",");
			} else if (isQuote(value)) {
				sb.append("'").append(value).append("'").append(",");
			} else {
				sb.append(fromObject(value).toString()).append(",");
			}
		}
		int len = sb.length();
		if (len > 1)
			sb.delete(len - 1, len);
		sb.append("]");
		return sb.toString();
	}

	/*
	 * 返回形如{’apple’:'red’,'lemon’:'yellow’}的字符串
	 */
	@SuppressWarnings("unchecked")
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		Set<Entry<String, Object>> set = jsonMap.entrySet();
		for (Entry<String, Object> entry : set) {
			Object value = entry.getValue();
			if (value == null) {
				continue;// 对于null值，不进行处理，页面上的js取不到值时也是null
			}
			sb.append("’").append(entry.getKey()).append("’:");
			if (value instanceof ConvertTool) {
				sb.append(value.toString());
			} else if (isNoQuote(value)) {
				sb.append(value);
			} else if (value instanceof Date) {
				sb.append("’").append(formatter.format(value)).append("’");
			} else if (isQuote(value)) {
				sb.append("’").append(value).append("’");
			} else if (value.getClass().isArray()) {
				sb.append(ArrayToStr((int[]) value));
			} else if (value instanceof Map) {
				sb.append(fromObject(value).toString());
			} else if (value instanceof List) {
				sb.append(ListToStr((List<Object>) value));
			} else {
				sb.append(fromObject(value).toString());
			}
			sb.append(",");
		}
		int len = sb.length();
		if (len > 1) {
			sb.delete(len - 1, len);
		}
		sb.append("}");
		return sb.toString();
	}

	public static Map<String, Object> removeNull(Map<String, Object> mapRecord) {
		for (String key : mapRecord.keySet()) {
			if (mapRecord.get(key) == null)
				mapRecord.remove(key);
		}
		return mapRecord;
	}
	public static Map<String, Object> changeNullSpace(Map<String, Object> mapRecord) {
		for (String key : mapRecord.keySet()) {
			if (mapRecord.get(key) == null)
			//	mapRecord.remove(key);
			mapRecord.put(key,"");
		}
		return mapRecord;
	}
	public static String Object2String(Object object) {
		String string = " ";
		if (object instanceof Date) {
			string = formatter.format(object);
		} else if(object==null){
			string ="";
		}else {
			string = object.toString();
		}
		return string;
	}

}
