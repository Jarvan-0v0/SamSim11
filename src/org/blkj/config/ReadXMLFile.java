package org.blkj.config;

import java.io.File;
import java.util.*;

import org.dom4j.*;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.DefaultAttribute;

import blkjweb.utils.Console;

public class ReadXMLFile {
	
	//存储xml元素信息的容器   
	private static ArrayList<Leaf> elemList = new ArrayList<Leaf>();  


	public static void main(String args[]) throws Exception  
	{  
		ReadXMLFile test = new ReadXMLFile();  

		//使用SAX方式解析xml
		SAXReader reader = new SAXReader();  
		//把xml文件转换成文档对象
		Document doc = reader.read("D://proxool.xml");  

		//取得根节点
		Element root = doc.getRootElement(); 
		test.getElementList(root);

		//自动遍历节点属性
		//String x = test.getListString(elemList);  
	
	}  
	//;:user:Wz8gvD7s9Skl8un0iNCmZg==;:password:oJfTtchpM9WC/4Oqpu7FZQ==;:dbname:dbms2012;
	public String getAccount(String filename)
	{	//使用SAX方式解析xml
		SAXReader reader = new SAXReader();  
		String result= null;
		try {
			/**当Tomcat的安装路径有空格时，unknown protocol: d Nested exception: unknown protocol: d*/
			//把xml文件转换成文档对象 新的2013 
			File file = new File(filename);
			Document doc = reader.read(file);
			//Document doc = reader.read(filename);原来
			
			//取得根节点
			Element root = doc.getRootElement(); 
			getElementList(root);
			result = getListString(elemList);
			
		} catch (DocumentException e) {
			Console.showMessage(ReadXMLFile.class.getSimpleName(),e.getMessage(), e);
		}  
		return result; 
	}

	public String getListString(List<Leaf> elemList)  
	{  
		StringBuffer sb = new StringBuffer();  
		for (Iterator<Leaf> it = elemList.iterator(); it.hasNext();)  
		{  
			Leaf leaf = (Leaf)it.next();  
			///sb.append("xpath: " + leaf.getXpath()).append(", value: ").append(leaf.getValue());//原来的    
			if (!"".equals(leaf.getXattribute()))  
			{  
				//sb.append(", Attribute: ").append(leaf.getXattribute()); //原来的   
				//修订后
				sb.append(leaf.getXattribute());
				sb.append(":=");//\n
			} 
			//原来的  
			//sb.append("\n");//  
		}  
		return sb.toString();  
	}  

	/**  
	 * 获取节点所有属性值 
	 */ 
	public String getNoteAttribute(Element element)  
	{  
		String xattribute = "";  
		DefaultAttribute e = null;  
		List<?> list = element.attributes();  //可以获取到节点的属性
		for (int i = 0; i < list.size(); i++)  
		{  
			e = (DefaultAttribute)list.get(i);
			//xattribute += " [name = " + e.getName() + ", value = " + e.getText() + "]";//原来的  

			//修订后
			xattribute += e.getText() + "=";
			//修订后
		} 

		return xattribute;  
	}  

	public void getElementList(Element element)  
	{  
		List<?> elements = element.elements();  
		// 没有子元素   
		if (elements.isEmpty())  
		{  //原来的 
			/*String xpath = element.getPath();  
			String value = element.getTextTrim(); 
			elemList.add(new Leaf(getNoteAttribute(element), xpath, value)); 
			 */
			//修订后
			if( element.getName().equalsIgnoreCase("property") || 
				element.getName().equalsIgnoreCase("driver-url")
			  )
			{
				String xpath = element.getPath();  
				String value = element.getTextTrim();
				//Log.debug("Debug:"+ element.getName());
				elemList.add(new Leaf(getNoteAttribute(element), xpath, value));
			}
			//修订后
		}  
		else 
		{  // 有子元素  
			Iterator<?> it = elements.iterator();  
			while (it.hasNext())  
			{  
				Element elem = (Element)it.next();
				// 递归遍历   
				getElementList(elem);  
			}  
		}  
	}  
}  

/**  
 * xml节点数据结构  
 */ 
class Leaf  
{  
	//节点属性  
	private String xattribute;  

	//节点PATH  
	private String xpath;  

	//节点值  
	private String value;  

	public Leaf(String xattribute, String xpath, String value)  
	{  
		this.xattribute = xattribute;  
		this.xpath = xpath;  
		this.value = value;  
	}  

	public String getXpath()  
	{  
		return xpath;  
	}  

	public void setXpath(String xpath)  
	{  
		this.xpath = xpath;  
	}  

	public String getValue()  
	{  
		return value;  
	}  

	public void setValue(String value)  
	{  
		this.value = value;  
	}  

	public String getXattribute()  
	{  
		return xattribute;  
	}  

	public void setXattribute(String xattribute)  
	{  
		this.xattribute = xattribute;  
	}   
}