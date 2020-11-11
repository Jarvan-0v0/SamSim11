package org.blkj.config;

import java.io.*;
import java.util.Properties;   

/** 
 *  读取properties文件
 *
 *  Java直接读取properties文件时，中文会是乱码，这时可以用Java\jdk1.x.x\bin\native2ascii.exe工具。
 *  例如，把f1.properties转换成f2.properties。
 *   native2ascii -encoding gbk f1.properties f2.properties  
 */  
public class ReadPropertiesFile   
{   
	private Properties props;   
	private FileInputStream inputFile;   
	private FileOutputStream outputFile;   
	private String propsFile = "";//配置文件路径   
	/** *//**
	 * 初始化Configuration类
	 */  
	public ReadPropertiesFile()   
	{   
		props = new Properties();   
	}   

	/** *//**
	 * 初始化Configuration类
	 * @param filePath 要读取的配置文件的路径+名称
	 */  
	public ReadPropertiesFile(String filePath)   
	{   
		props = new Properties();
		propsFile = filePath;
		try {   
			inputFile = new FileInputStream(filePath);   
			//props.load(inputFile);
			props.load(new InputStreamReader(inputFile, "UTF-8")); 
			inputFile.close();   
		} catch (FileNotFoundException ex) {   
		
		} catch (IOException ex) {   
		
		}   
	}//end ReadConfigInfo()   

	//Application应用：配置文件在class下，即Src下; Web应用：取得的是WEB-INF的下级目录
	/**
		Class.getResourceAsStream() 会指定要加载的资源路径与当前类所在包的路径一致
    	ClassLoader.getResourceAsStream()  无论要查找的资源前面是否带'/' 都会从classpath的根路径下查找。
	 */
	public ReadPropertiesFile(ClassLoader classname,String filename)
	{	
		props = new Properties();
		try {  
			props.load(classname.getResourceAsStream(filename));
		} catch (Exception e) {  
			
		}
	}

	
	/** *//**
	 * 重载函数，得到key的值
	 * @param key 取得其值的键
	 * @return key的值
	 */  
	public String getValue(String key)   
	{   
		if(props != null && props.containsKey(key)){   
			String value = props.getProperty(key);//得到某一属性的值   
			return value;   
		}   
		else   
			return "";   
	}//end getValue()   

	/** *//**
	 * 重载函数，得到key的值
	 * @param fileName properties文件的路径+文件名
	 * @param key 取得其值的键
	 * @return key的值
	 */  
	public String getValue(String fileName, String key)   
	{   
		try {   
			String value = "";   
			inputFile = new FileInputStream(fileName);   
			props.load(inputFile);   
			inputFile.close(); 
			if (props != null && props.containsKey(key)){   
				value = props.getProperty(key); 
				//解决中文乱码
				if(value != null && value.length() != 0)
					value = new String(value.getBytes("ISO8859-1"), "UTF-8");
				return value;   
			}else  
				return value;   
		} catch (FileNotFoundException e) {   
			e.printStackTrace();   
			return "";   
		} catch (IOException e) {   
			e.printStackTrace();   
			return "";   
		} catch (Exception ex) {   
			
			return "";   
		}   
	}//end getValue()   

	
		
	
	/** *//**
	 * 清除properties文件中所有的key和其值
	 */  
	public void clear()   
	{   
		props.clear();   
	}//end clear();   

	/** *//**
	 * 改变或添加一个key的值，当key存在于properties文件中时该key的值被value所代替，
	 * 当key不存在时，该key的值是value
	 * @param key 要存入的键
	 * @param value 要存入的值
	 */  
	public void setValue(String key, String value)   
	{   
		props.setProperty(key, value);   
	}//end setValue()   

	/** *//**
	 * 将更改后的文件数据存入指定的文件中，该文件可以事先不存在。
	 * @param fileName 文件路径+文件名称
	 * @param description 对该文件的描述
	 */  
	public void saveFile(String fileName, String description)   
	{   
		try {   
			outputFile = new FileOutputStream(fileName);   
			props.store(outputFile, description);   
			outputFile.close();   
		} catch (FileNotFoundException e) {   
			e.printStackTrace();   
		} catch (IOException ioe){   
			ioe.printStackTrace();   
		}   
	}//end saveFile()   
	/** *//**
	 * 通过key得到value
	 * @param key
	 */  
	 public String getProp(String key) {   
		 String retVal = null;   
		 InputStream is = getClass().getResourceAsStream(propsFile);   
		 Properties props = new Properties();   
		 try {   
			 props.load(is);   
			 retVal = props.getProperty(key);   
			 if(retVal == null)retVal="";   
		 } catch (Exception e) {   
			 return null;   
		 }   
		 return retVal;   
	 }   

}//end class ReadConfigInfo
