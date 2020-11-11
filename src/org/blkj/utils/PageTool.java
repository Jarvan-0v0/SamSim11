package org.blkj.utils;

import java.io.UnsupportedEncodingException;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartHttpServletRequest;

public class PageTool
{
	HttpServletRequest request;
	
	Map<String, Object> map = null;
	
	public PageTool() 
	{
		map = new HashMap<String, Object>();
	}
	//获取上传文件页面上非文件表格数据
	public PageTool(MultipartHttpServletRequest multipartRequest)
	{
		Map<?, ?> properties = multipartRequest.getParameterMap();
		Map<String, Object> returnMap = new HashMap<String, Object>(); 
		Iterator<?> entries = properties.entrySet().iterator(); 
		Map.Entry<?,?> entry; 
		String name = "";  
		String value = "";
		while (entries.hasNext()) {
			entry = (Map.Entry<?,?>) entries.next(); 
			name = (String) entry.getKey(); 
			Object valueObj = entry.getValue(); 
			if(null == valueObj){ 
				value = ""; 
			}else if(valueObj instanceof String[]){ 
				String[] values = (String[])valueObj;
				for(int i=0;i<values.length;i++){ 
					 value = values[i] + ",";
				}
				value = value.substring(0, value.length()-1); 
			}else{
				value = valueObj.toString(); 
			}
			returnMap.put(name, value); 
		}
		map = returnMap;
	}
	
	public PageTool(HttpServletRequest request)
	{
		this.request = request;
		System.out.println(request.getCharacterEncoding());
		
		Map<?, ?> properties = request.getParameterMap();
		System.out.println(properties);
		Map<String, Object> returnMap = new HashMap<String, Object>(); 
		Iterator<?> entries = properties.entrySet().iterator(); 
		Map.Entry<?,?> entry; 
		String name = "";  
		String value = "";
		//for (Map.Entry<?,?> entry : properties.entrySet()) {
		while (entries.hasNext()) {
			entry = (Map.Entry<?,?>) entries.next(); 
			name = (String) entry.getKey(); 
			Object valueObj = entry.getValue(); 
			if(null == valueObj){ 
				value = ""; 
			}else if(valueObj instanceof String[]){ 
				String[] values = (String[])valueObj;
				for(int i=0;i<values.length;i++){ 
					 value = values[i] + ",";
				}
				value = value.substring(0, value.length()-1); 
			}else{
				value = valueObj.toString(); 
			}
			returnMap.put(name, value); 
			System.out.println(name+" "+value+"PageTool");
		}
		map = returnMap;
		
	}
	
	public Map<String, Object> getMap() {
		return map;
	}
	
	public String getString(Object key) 
	{
		return (String)get(key);
	}
	public int getInt(Object key) 
	{
		String temp = (String)get(key);
		if (StringTool.isNumeric(temp))
			return Integer.parseInt(temp);
		else 
			return -1; 
	}

	public Object get(Object key) {
		Object obj = null;
		if(map.get(key) instanceof Object[]) {
			Object[] arr = (Object[])map.get(key);
			obj = request == null ? arr:(request.getParameter((String)key) == null ? arr:arr[0]);
		} else {
			obj = map.get(key);
		}
		return obj;
	}
	
	public Object put(String key, Object value) {
		return map.put(key, value);
	}

	public Object remove(Object key) {
		return map.remove(key);
	}

	public void clear() {
		map.clear();
	}
	
	public boolean isEmpty() {
		return map.isEmpty();
	}
	public int size() {
		return map.size();
	}
	
	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}
	
	public Collection<Object> values() {
		return map.values();
	}
	
	public void putAll(Map<String, ?> t) {
		map.putAll(t);
	}
	
	public Set<String> keySet() {
		return map.keySet();
	}
	
	public Set<?> entrySet() {
		return map.entrySet();
	}
	
}
