package blkjweb.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NameForChinese {

	public Map<String,String> colTitleToChinese(String tableName, List<String> headList)
	{ 
		Map<String,String> headMap = new HashMap<String,String>();//中文表头
		if (tableName.equalsIgnoreCase("user"))
		{
			for(int i = 0; i < headList.size(); i++){
				String temp = headList.get(i);
				if(temp.equalsIgnoreCase("id")) headMap.put("id","工号"); 
				else if(temp.equalsIgnoreCase("password")) headMap.put("password","密码");
				else if(temp.equalsIgnoreCase("name")) headMap.put("name","姓名");
				else if(temp.equalsIgnoreCase("phone")) headMap.put("phone","电话");
				else if(temp.equalsIgnoreCase("mobile")) headMap.put("mobile","手机");         
				else if(temp.equalsIgnoreCase("email")) headMap.put("email","email地址");
				else if(temp.equalsIgnoreCase("weixin")) headMap.put("weixin","微信号");
				else if(temp.equalsIgnoreCase("qq")) headMap.put("qq","QQ号");
				else if(temp.equalsIgnoreCase("description")) headMap.put("description","描述"); 
			}  
		}
		else if (tableName.equalsIgnoreCase("equipment"))
		{
			for(int i = 0; i < headList.size(); i++){
				String temp = headList.get(i);
				if(temp.equalsIgnoreCase("id")) headMap.put("id","资产编码"); 
				else if(temp.equalsIgnoreCase("name")) headMap.put("name","资产名称");
				else if(temp.equalsIgnoreCase("type")) headMap.put("type","型号");
				else if(temp.equalsIgnoreCase("sn")) headMap.put("sn","产品序号");         
				else if(temp.equalsIgnoreCase("price")) headMap.put("price","资产价格");
				else if(temp.equalsIgnoreCase("sale")) headMap.put("sale","销售厂商");
				else if(temp.equalsIgnoreCase("phone")) headMap.put("phone","销售电话");
				else if(temp.equalsIgnoreCase("contact")) headMap.put("contact","联络人");
				else if(temp.equalsIgnoreCase("guaran")) headMap.put("guaran","保固期限");
				else if(temp.equalsIgnoreCase("durable")) headMap.put("durable","耐用年限");
				else if(temp.equalsIgnoreCase("reason")) headMap.put("reason","取得原因");
				else if(temp.equalsIgnoreCase("buytime")) headMap.put("buytime","购买时间");
				else if(temp.equalsIgnoreCase("scraptime")) headMap.put("scraptime","报废时间");
				else if(temp.equalsIgnoreCase("buytime")) headMap.put("buytime","购买时间");
				else if(temp.equalsIgnoreCase("place")) headMap.put("place","存放位置");
				else if(temp.equalsIgnoreCase("managerid")) headMap.put("managerid","经办人工号");
				else if(temp.equalsIgnoreCase("status")) headMap.put("status","当前状态");
				else if(temp.equalsIgnoreCase("ip")) headMap.put("ip","ip地址");
				else if(temp.equalsIgnoreCase("mac")) headMap.put("mac","MAC地址");
				else if(temp.equalsIgnoreCase("description")) headMap.put("description","描述"); 
			}  

		}
		return headMap;
	}
}
