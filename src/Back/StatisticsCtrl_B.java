package Back;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.blkj.utils.DateTool;
import org.blkj.utils.PageTool;
import org.blkj.utils.StringTool;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import blkjweb.controller.AbstractBase;
import blkjweb.service.SystemServiceImp;

@Controller
public class StatisticsCtrl_B extends AbstractBase
{
	@Resource
	private SystemServiceImp systemService; 

	private String _genRowCode(String key,String[] rowArry,int offset)
	{
		String row = "";
		for(int i = 0; i < rowArry.length; i++)	{
			if(key != null && key.equalsIgnoreCase(rowArry[i])){
		   		row = "row_" + i + offset;
				break;
			}
		}
		return row;
	}
	private void _genSumValue(Map<String, Object> resultMap,List<Map<String, Object>> lists,
			String[] rowArry,String colName,String fieldName,int offset)
	{
		for(int i=0; i<lists.size(); i++){
			Map<String, Object> map = new HashMap<String, Object>();
			map = lists.get(i);
			String fieldValue = (String)map.get(fieldName);
			Number value = (Number)map.get("sum");

			String rowValue = _genRowCode(fieldValue,rowArry,offset);
			if(!StringTool.isNullEmpty(rowValue))
				resultMap.put(colName + "-" + rowValue, value);
		}
	}
	private void _genPercentValue(long totalRecord,Map<String, Object> resultMap,List<Map<String, Object>> lists,
			String[] rowArry,String colName,String fieldName,int offset)
	{
		for(int i=0; i<lists.size(); i++){
			Map<String, Object> map = new HashMap<String, Object>();
			map = lists.get(i);
			String fieldValue = (String)map.get(fieldName);
			Number value = (Number)map.get("sum");
			//String valueStr = (long)value % totalRecord +"%";
			String valueStr = (value.longValue()*100)/totalRecord +"%";
			String rowValue = _genRowCode(fieldValue,rowArry,offset);
			if(!StringTool.isNullEmpty(rowValue))
				resultMap.put(colName + "-" + rowValue,valueStr);
		}
	}
	@RequestMapping(value="/ComReport_count", produces="application/json; charset=utf-8")  
	@ResponseBody
	public Object ComReport_count() 
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();

		String year = pd.getString("Year");
		int yearI = StringTool.stringToInt(year);

		Map<String, Object> resultMap = new HashMap<String, Object>();//结果集
		resultMap.put("JfirstYear", yearI);
		resultMap.put("JsecondYear", yearI - 1);
		resultMap.put("JthirdYear", yearI - 2);

		String tableName = "thandongjudge";
		String group = "zongjudge";//分类依据:总劣化等级
		String items = "zongjudge";//查询项

		//计算涵洞总数
		long totalRecord = systemService.queryCount("thandongbasicinfo"); //数据库中满足条件的总记录数

		int offset = 0;
		for( int out = 0 ; out< 3; out++)//For 3Y
		{
			String begin = yearI - out + "-01-01";
			String end = yearI - out + "-12-31";
			String where = "pingdingdate <='" + end + "'" +	" AND pingdingdate >='" + begin + "'" ;

			List<Map<String, Object>> listMaps = new ArrayList<Map<String, Object>>();
			listMaps = systemService.queryCount(tableName,items,where,group);

			String fieldName="zongjudge";
			String[] rowArry = {"AA","A1","B","C","D"};
			if(out == 1) offset = 5;
			else if(out == 2) offset = 10;

			//计算第一行：涵洞数量/座
			String colName = "col_1";
			if(!StringTool.isNull(listMaps))
				_genSumValue(resultMap,listMaps,rowArry,colName,fieldName,offset);

			//计算第二行：占全线涵洞比例
			colName = "col_2";
			if(!StringTool.isNull(listMaps))
				_genPercentValue(totalRecord,resultMap,listMaps,rowArry,colName,fieldName,offset);
		}

		if(!StringTool.isNull(resultMap))
			return resultMap;
		else
			return "";
	}

	/**
	 * 	输出格式：Jianzhi_total_kongshu;Jianzhi_total_xiaoji;Jianzhi_total_AA....
	*/
	private void _genSum(Map<String, Object> resultMap,String id)
	{
		int totalKongshu = 0;
		int totalXiaoji = 0;
		int AA = 0;
		int A1 = 0;
		int B = 0;
		int C = 0;
		int D = 0;
		int num = 0;
		String key = "";
		Object value = "";
		for (Entry<String, Object> entry: resultMap.entrySet()) {
			key = entry.getKey();
			value = entry.getValue();
			if(StringTool.isNull(value)) num = -1;
			else num = (Integer)value;

			if( num == -1) num = 0;
			
			if(key != null){
				if(key.contains("AA")) AA = AA + num;
				else if(key.contains("A1"))	A1 = A1 + num;
				else if(key.contains("B")) B = B + num;
				else if(key.contains("C")) C = C + num;
				else if(key.contains("D")) D = D + num;
				else if(key.contains("xiaoji"))	totalXiaoji = totalXiaoji + num;
				else if(key.contains("kongshu"))totalKongshu = totalKongshu + num;
			}
		}
		
		key = id + "total_kongshu";
		resultMap.put(key,totalKongshu);

		key = id + "total_xiaoji";
		resultMap.put(key,totalXiaoji);
		
		key = id + "total_AA";
		resultMap.put(key,AA);
		
		key = id + "total_A1";
		resultMap.put(key,A1);
		
		key = id + "total_B";
		resultMap.put(key,B);
		
		key = id + "total_C";
		resultMap.put(key,C);
		
		key = id + "total_D";
		resultMap.put(key,D);
	}
	/** 示例格式：8-普通高度钢筋混凝土梁=》:Jianzhi_8_kongshu；Jianzhi_8_xiaoji；Jianzhi_8_AA。。。。
 		返回的结果集：resultMap；传入参数： id = "Jianzhi_" 或  "JianzhiG_";
  */ 
	private void _genComponentID(Map<String, Object> resultMap,
						String id,String kuajing,String xiaojie,String judge)
	{
		//统计数量/空
		String key = id + kuajing + "_" + "kongshu";
		Integer num = (Integer)resultMap.get(key);
		if(! StringTool.isNull(num)) resultMap.put(key,num+1);
		else resultMap.put(key,1);

		//统计小计
		key = id + xiaojie + "xiaoji";
		num = (Integer)resultMap.get(key);
		if(! StringTool.isNull(num)) resultMap.put(key,num+1);
		else resultMap.put(key,1);

		//统计评价
		key = id + kuajing + "_" + judge;
		num = (Integer)resultMap.get(key);
		if(!StringTool.isNull(num)) resultMap.put(key,num+1);
		else resultMap.put(key,1); 
		//System.out.println("resultMap1:" + resultMap.toString());
	}
	//只统计简支梁的评价情况
	@RequestMapping(value="/JianzhiBridge_init") 
	@ResponseBody
	public Object JianzhiBridge_init() 
	{
		String year = DateTool.getYear();
		String begin = year + "-01-01";
		String end = year + "-12-31";
		/**
		 *  从桥梁劣化等级评价记录表(总)tbridgebasicinfojudge中检索 qiaohao、shuoshugongdui、qiaoliangjudge、pingdingdate等信息
		 */
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		//String items = "qiaohao,qiaoliangjudge";
		String items = "qiaohao,kongkuahao,zhuliangjudge";//
		String where = "pingdingdate <='" + end + "' AND pingdingdate >='" + begin + "'" ;
		lists = systemService.query("tbridgebasicinfojudge",items, where);

		//保存返回到页面的结果数据
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String id = "Jianzhi_";//1简支   
		String id2 = "JianzhiG_";//12:框构桥 

		if(lists != null)
		{//if1
			System.out.println("从tbridgebasicinfojudge中检索到满足时间关系的总记录数：" + lists.size() );
			for(int i=0; i<lists.size(); i++){//for1 处理每一条评价记录
				Map<String, Object> map = new HashMap<String, Object>();
				map = lists.get(i);
				Object value = "";
				String key = "";
				
				String qiaohao = "";
				Integer kongkuahao = 0;
				String judge = "";
				for (Entry<String, Object> entry: map.entrySet()) {//for1-21
					key = entry.getKey();
					value = entry.getValue();
					if("zhuliangjudge".equalsIgnoreCase(key))
						judge = (String)value;
					else if("qiaohao".equalsIgnoreCase(key))
						qiaohao = (String)value;
					else if("kongkuahao".equalsIgnoreCase(key))
						kongkuahao = (Integer)value;
				}//for1-21
				
				//System.out.println(qiaohao + "<=桥号（逐条记录处理获取到的数据）评价结果=>" + judge );
				if(StringTool.isNullEmpty(judge)){
					continue;//for1
				}
				System.out.println("桥号=>"+qiaohao + ":孔跨号=>" + kongkuahao +"评定结果=>"+ judge);

				//从tgirder数据库，获取给定桥号和孔跨号的主梁信息
				Map<String, Object> bridgeInfo = new HashMap<String, Object>();
				String items1 = "kuajing,lianggao,material,leixing,shoulixingshi";
				String where1 = "";//2018修订: "qiaohao='" + qiaohao + "' AND kongkuahao='" + kongkuahao +"'";
				bridgeInfo = systemService.queryOne4Items("tgirder",items1, where1);

				System.out.println();
				if (bridgeInfo != null ) {//bridgeInfo != null
					System.out.println("从桥基本信息表tbridgebasicinfo获取分类依据数据=>" + bridgeInfo.toString());
					//shoulixingshi（1简支、2连续 , 12:框构桥
					String shoulixingshi = (String)bridgeInfo.get("shoulixingshi");
					if(! "简支".equalsIgnoreCase(shoulixingshi) && !"框构桥".equalsIgnoreCase(shoulixingshi) )
						continue;//非简支梁和框构梁  for1

					//lianggao（1普通高度、2中高度、3低高度、4超低高度）
					String lianggao  = (String)bridgeInfo.get("lianggao");
					//leixing（1板梁;2桁梁、3T梁、4箱型梁,7T型刚构,8门式刚构,9斜腿刚构;
					String leixing  = (String)bridgeInfo.get("leixing");
					//material 1钢梁; 2钢筋混凝土、7钢混组合、4 预应力钢筋混凝土; 6普通桥梁钢、5低合金钢、3型钢混凝土、8钢管混凝、1钢梁）
					String material  = (String)bridgeInfo.get("material");
					//String kongshu = getKongshu(qiaohao);
					//kuajing（跨径(m)）
					int kuajing = (Integer)bridgeInfo.get("kuajing");

					if("框构桥".equals(shoulixingshi)){//shoulixingshi12:框构桥
						if (kuajing != -1){
							if ( kuajing == 4 || kuajing == 8 )
								_genComponentID(resultMap,id2,""+kuajing,"",judge);
							else if (kuajing >= 8 && kuajing < 10 )
								_genComponentID(resultMap,id2,"10","",judge);
							else if (kuajing >= 10 && kuajing < 12 )
								_genComponentID(resultMap,id2,"12","",judge);
							else if (kuajing >= 12 && kuajing < 16 )
								_genComponentID(resultMap,id2,"16","",judge);
							else if (kuajing >= 16 && kuajing < 20 )
								_genComponentID(resultMap,id2,"20","",judge);
							else if (kuajing >= 20 )
								_genComponentID(resultMap,id2,"00","",judge);
						}
					}
					else if("简支".equals(shoulixingshi)) {//1简支  /统计两次或统计一次. 框构桥与其他内容
						if( 9==kuajing && 
							"低高度".equals(lianggao) && //（1普通高度
							"钢筋混凝土".equals(material) ){//2钢筋混凝土 	
							_genComponentID(resultMap,id,""+kuajing,"9_",judge);//""表示小结的前缀
						}
						/**8-普通高度钢筋混凝土梁=》:Jianzhi_8_kongshu；
						   传入参数： id = "Jianzhi_";
						  */ 
						else if( 10==kuajing ){
							if("低高度".equals(lianggao) && //3低高度
							   "预应力钢筋混凝土".equals(material)&& //2钢筋混凝土
							  ("梁式桥--T梁".equals(leixing)) || ("T梁".equals(leixing)) )  //3T梁、
								_genComponentID(resultMap,id,kuajing + "-1","10-1_",judge);
							if("普通高度".equals(lianggao) && //3低高度
									   "钢筋混凝土".equals(material)&& //2钢筋混凝土
									  ("梁式桥--T梁".equals(leixing)) || ("T梁".equals(leixing)) )  //3T梁、
										_genComponentID(resultMap,id,kuajing + "-2","10-1_",judge);
							if("低高度".equals(lianggao) && //3低高度
									   "钢筋混凝土".equals(material)&& //2钢筋混凝土
									  ("梁式桥--T梁".equals(leixing)) || ("T梁".equals(leixing)) )  //3T梁、
										_genComponentID(resultMap,id,kuajing + "-3","10-1_",judge);
							if("低高度".equals(lianggao) && //3低高度
									   "钢筋混凝土".equals(material)&& //2钢筋混凝土
									  ("梁式桥--板梁".equals(leixing)) || ("板梁".equals(leixing)) )  //3T梁、
										_genComponentID(resultMap,id,kuajing + "-4","10-1_",judge);
							
						
						}
						else if( 12==kuajing ){
							if("低高度".equals(lianggao) && //1普通高度
								"钢筋混凝土".equals(material)&& //2钢筋混凝土
								("梁式桥--板梁".equals(leixing)) || ("板梁".equals(leixing)) )  //3T梁、
								_genComponentID(resultMap,id,kuajing + "-1","12-1_",judge);
							
						}
						else if( 16==kuajing){
							if("普通高度".equals(lianggao) && //1普通高度
								"预应力钢筋混凝土".equals(material)&& //4 预应力钢筋混凝土
								("梁式桥--T梁".equals(leixing)) || ("T梁".equals(leixing)) ) //3T梁、
								_genComponentID(resultMap,id,kuajing + "-1","16-1_",judge);
							if("低高度".equals(lianggao) && //3低高度
								"预应力钢筋混凝土".equals(material)&& //4 预应力钢筋混凝土
								("梁式桥--T梁".equals(leixing)) || ("T梁".equals(leixing)) )  //3T梁、
								_genComponentID(resultMap,id,kuajing + "-2","16-1_",judge);
							if("普通高度".equals(lianggao) && //3低高度
									"钢筋混凝土".equals(material)&& //4 预应力钢筋混凝土
									("梁式桥--T梁".equals(leixing)) || ("T梁".equals(leixing)) )  //3T梁、
									_genComponentID(resultMap,id,kuajing + "-3","16-1_",judge);
							if("低高度".equals(lianggao) && //3低高度
									"钢筋混凝土".equals(material)&& //4 预应力钢筋混凝土
									("梁式桥--T梁".equals(leixing)) || ("T梁".equals(leixing)) )  //3T梁、
									_genComponentID(resultMap,id,kuajing + "-4","16-1_",judge);
							
						}
						else if( 20==kuajing ){
						
							if("低高度".equals(lianggao) && //3低高度
								"钢筋混凝土".equals(material)&& //4 预应力钢筋混凝土
								("梁式桥--T梁".equals(leixing)) || ("T梁".equals(leixing)) )  //3T梁、
								_genComponentID(resultMap,id,kuajing + "-1","20-1_",judge);
							
						}
						else if( 24==kuajing ){
							if("普通高度".equals(lianggao) && //1普通高度
								"预应力钢筋混凝土".equals(material)&& //4 预应力钢筋混凝土
								("梁式桥--T梁".equals(leixing)) || ("T梁".equals(leixing)) ) //3T梁、
								_genComponentID(resultMap,id,kuajing + "-1","24-1_",judge);
							
						}
						else if( 32==kuajing ){
							if("普通高度".equals(lianggao) && //1普通高度
								"预应力钢筋混凝土".equals(material) && //4 预应力钢筋混凝土
								("梁式桥--T梁".equals(leixing)) || ("T梁".equals(leixing)) )  //3T梁
								_genComponentID(resultMap,id,kuajing + "-1","32-1_",judge);
							
						}
						else if( 48==kuajing ){
							if( ("梁式桥--桁梁".equals(leixing) || "桁梁".equals(leixing))&& //2桁梁
								("普通桥梁钢".equals(material) || "低合金钢".equals(material))  ) //1钢梁
								_genComponentID(resultMap,id,""+kuajing,"48-1_",judge);
						}
						else if( 64==kuajing ){
							if( ("梁式桥--桁梁".equals(leixing) || "桁梁".equals(leixing))&&  //2桁梁
								("普通桥梁钢".equals(material) || "低合金钢".equals(material))  ) //1钢梁
								_genComponentID(resultMap,id,""+kuajing,"48-1_",judge);
						}//选择“普通桥梁钢”、“低合金钢”这两种材料就代表是钢梁。
					}//1简支
					else //其他
						_genComponentID(resultMap,id,"other","other_",judge);
				}//bridgeInfo != null
				else
					System.out.println("从桥基本信息表tbridgebasicinfo获取分类依据数据为空");
			}//for1
			//所有分支全部处理完，需要执行合计(最后一行)
			_genSum(resultMap,id);
		}//if1
		else
			System.out.println("从tbridgebasicinfojudge中检索到满足时间关系的总记录数为0");

		if(!StringTool.isNull(resultMap))
			return resultMap;
		else
			return "";
	}
	
	private void _getAllBridgeBykuajing(Map<String, Object> resultMap)
	{
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		String items = "kuajing,shoulixingshi";
		lists= systemService.query("tgirder",items,"");

		if(lists != null){//if_1
		for(int i=0; i<lists.size(); i++){//for_1Y 处理每一条评价记录
				Integer kuajing = 0;
				String shoulixingshi = "";
				Integer num = 0;
				Map<String, Object> map = new HashMap<String, Object>();
				map = lists.get(i);
				for (Entry<String, Object> entry: map.entrySet()) {//for1-21 只有两个元素
					String key = entry.getKey();
					Object value = entry.getValue();

					if("kuajing".equals(key)) kuajing = (Integer)value;
					else if("shoulixingshi".equals(key)) shoulixingshi = (String)value;
				}

				if(!StringTool.isNullEmpty(shoulixingshi) 
					&& "框构桥".equals(shoulixingshi))//1简支、2连续 , 12:框构桥
				{
					num = (Integer)resultMap.get("kuanggou");
					if(!StringTool.isNull(num)) resultMap.put("kuanggou",num+1);
					else resultMap.put("kuanggou",1);
				}

				if(StringTool.isNull(kuajing) || kuajing == -1)
					continue;

				if( (kuajing == 8 || kuajing == 10 ||kuajing == 9||
					kuajing == 12 || kuajing == 16 ||
					kuajing == 20 || kuajing == 24 ||
					kuajing == 32 || kuajing == 48||kuajing == 64) )
				{
					num = (Integer)resultMap.get(kuajing);
					if(!StringTool.isNull(num)) resultMap.put(""+ kuajing,num+1);
					else resultMap.put(""+ kuajing,1);
				}
				else {
					num = (Integer)resultMap.get("other");
					if(!StringTool.isNull(num)) resultMap.put("other",num+1);
					else resultMap.put("other",1);
				}
			}//for_1Y
		}//if_1
	} 
	
	/**
	 * 针对不同跨度，统计各类病害的梁的数目
	 * id: Jnum_1_、Jnum_2_、Jnum_3_、
	 * judge: AA,A1,。。。
	 * dataMap（bridgeInfo）: kuajing,shoulixingshi
	 * 返回的结果集：resultMap（kongkuashuMap）
	*/
	private void _getBHBridgeNum(String id,String judge,Map<String, Object> dataMap, Map<String, Object> resultMap)
	{  
		int kuajing = -1;
		//shoulixingshi（1简支、2连续 , 12:框构桥
		String shoulixingshi = (String)dataMap.get("shoulixingshi");
		Object obj = dataMap.get("kuajing");//kuajing（跨径(m)）
		if(!StringTool.isNull(obj)) kuajing = (Integer)obj;//kuajing（跨径(m)）
		
		String key;
		Integer num = 0;
		if("框构桥".equalsIgnoreCase(shoulixingshi)){ //12框构桥
			if(! StringTool.isNull(judge)){
				key = id + "kuanggou" + "_" + judge;
				num = (Integer)resultMap.get(key);
				if(!StringTool.isNull(num)) resultMap.put(key,num+1);
				else resultMap.put(key,1);   
			}
		}
		else /*if(!StringTool.isNullEmpty(kuajing))*/{
			if( (kuajing != -1) &&
				(kuajing == 9 || kuajing == 10 ||
				kuajing == 12 || kuajing == 16 ||
				kuajing == 20 || kuajing == 24 ||
				kuajing == 32 ||kuajing == 48 || kuajing == 64)	)
			{
				key = id + kuajing + "_" + judge;
				num = (Integer)resultMap.get(key);
				if(!StringTool.isNull(num)) resultMap.put(key,num+1);
				else resultMap.put(key,1);
			}
			else{
				key = id + "other_" + judge;
				num = (Integer)resultMap.get(key);
				if(!StringTool.isNull(num))	resultMap.put(key,num+1);
				else resultMap.put(key,1);
			}
		}
		/*else{
	    		key = id + "other_" + judge;
	    		num = (Integer)resultMap.get(key);
	    		if(!StringTool.isNull(num)) 
	    			resultMap.put(key,num+1);
	    		else
	    			resultMap.put(key,1);
	    	}*/

		//累计同类的总和
		key = id + "heji_" + judge;
		num = (Integer)resultMap.get(key);//11.5添加
		if (!StringTool.isNull(num))
			resultMap.put(key,num + 1);
		else
			resultMap.put(key,1);
	}  
	/**
	 *针对不同跨度，统计各类病害的梁的总数目
	 *id:Jnum_1_、Jnum_2_、Jnum_3_
	 *nId：sum_
	 *kongkuashuMap：由于它包括合计的数目， 所以结果值应除以2或不统计含heji的项
	 *结果集：resultMapT
	 ***/ 
	private void _getTotalBHBridgeNum(String id,String nId,
			Map<String, Object> dataMap, Map<String, Object> resultMap)
	{ //nId = "sum_";  id = "Lnum_" + (out+1) + "_";

		int AA = 0;
		int A1 = 0;
		int B = 0;
		int C = 0;
		int D = 0;
		int num = -1;
		String key = "";
		Object value;
		for (Entry<String, Object> entry: dataMap.entrySet()) {
			key = entry.getKey();
			value = entry.getValue();

			if(key.contains("_heji_"))//不统计合计项
				continue;

			if(!StringTool.isNull(value)) num = (Integer)value;
			//num = StringTool.stringToInt(value);
			if( key == null || num == -1) continue;

			if(key.contains(id)){
				if(key.contains("AA")) AA = AA + num;
				else if(key.contains("A1"))	A1 = A1 + num;
				else if(key.contains("B")) B = B + num;
				else if(key.contains("C")) C = C + num;
				else if(key.contains("D")) D = D + num;
			}
		}
		key = id + nId + "AA";
		resultMap.put(key,AA);
		
		key = id + nId + "A1";
		resultMap.put(key,A1);
		
		key = id + nId + "B";
		resultMap.put(key,B);
		
		key = id + nId + "C";
		resultMap.put(key,C);
		
		key = id + nId + "D";
		resultMap.put(key,D);
	}
	
	/**
	 * 计算各类病害在全线中的比例关系
	 * 键值格式：Jnum_1_bili_AA
	 * id：Jnum_1_、Jnum_2_、Jnum_3_
	 * nId = "bili_"
	 * dataMap(resultMap)：统计每一类病害的(桥梁孔跨数/孔)的总数量
	 * totalBridgeNum：梁的总数
	 * 结果集：resultMap(resultMapT2)
	 */
	private void _getBiliBHBridgeNum(String id,String nId,
			Map<String, Object> dataMap/*存在病害的梁*/,int totalBridgeNum/*总梁数*/,
			Map<String, Object> resultMap)
	{  //nId = "bili_"; id = "Lnum_" + (out+1) + "_";
		int num = 0;
		String key = "";
		String keyId  = "";
		DecimalFormat df = new DecimalFormat("0.00");//格式化小数    

		for (Entry<String, Object> entry: dataMap.entrySet()) {
			key = entry.getKey();
			num = (Integer)entry.getValue();

			if( key == null || num == -1) continue;

			keyId = id + "sum_" + "AA";
			if (keyId.equals(key)){
				key = id + nId + "AA";
				resultMap.put(key, df.format((float)(num*100)/totalBridgeNum)+"%");
				continue;
			}
			keyId = id + "sum_" + "A1";
			if (keyId.equals(key)){
				key = id + nId + "A1";
				resultMap.put(key, df.format((float)(num*100)/totalBridgeNum)+"%");
				continue;
			}
			keyId = id + "sum_" + "B";
			if (keyId.equals(key)){
				key = id + nId + "B";
				resultMap.put(key, df.format((float)(num*100)/totalBridgeNum)+"%");
			}
			keyId = id + "sum_" + "C";
			if (keyId.equals(key)){
				key = id + nId + "C";
				resultMap.put(key, df.format((float)(num*100)/totalBridgeNum)+"%");
				continue;
			}
			keyId = id + "sum_" + "D";
			if (keyId.equals(key)){
				key = id + nId + "D";
				resultMap.put(key, df.format((float)(num*100)/totalBridgeNum)+"%");
				continue;
			}
		}
	}
	/**kongkuashuMap 同类有病的梁总和；totalBridgeNum：全部桥总数
	 * kongkuashuMap的Key格式为：
	 * Jnum_1_8_AA、Jnum_1_kuanggou_AA、Jnum_1_other_AA、Jnum_1_heji_AA  值为整数数字
	 * nId = Jquanxian_1_
	 * 结果集：resultMap(key: Jquanxian_1_：返回值有%
	 */
	private void _getQuanxianBiliBHBridgeNum(String nId, Map<String, Object> kongkuashuMap, int totalBridgeNum, Map<String, Object> resultMap)
	{   
		String key = "";
		String value = "";
		String keykuajing = "";
		Object valuekuajing = "";
		int valuekuajingT = 0;
		int valueT = -1;
		for (Entry<String, Object> entry: kongkuashuMap.entrySet()) 
		{
			keykuajing = entry.getKey();//Jnum_3_64_AA
			valuekuajing = entry.getValue();
			if(! StringTool.isNull(valuekuajing)) 
				valuekuajingT =(Integer)valuekuajing;	
			/*valuekuajing = (String)entry.getValue();
			if (StringTool.isNullEmpty(keykuajing) || StringTool.isNullEmpty(valuekuajing))
				continue;
			valuekuajingT = StringTool.stringToInt(valuekuajing) ;
			 */
			if (valuekuajingT == -1 || valuekuajingT == 0)
				continue;

			String [] reault = keykuajing.split("_"); //Jnum_1_8_AA
			if(StringTool.isNull(reault) || reault.length != 4)
				continue;

			String kuajing = reault[2];
			String judge = reault[3];
			//Jquanxian_1_8_AA;nId = "Jquanxian_" + (out+1) + "_";
			key =  nId + kuajing + "_" + judge; 

			value = (String)resultMap.get(key);
			valueT = StringTool.stringToInt(value);

			DecimalFormat df = new DecimalFormat("0.00");//格式化小数   

			if (valueT == -1)
				resultMap.put(key,df.format((float)(valuekuajingT*100)/totalBridgeNum) +"%");
			else
				resultMap.put(key,df.format((float)((valueT + valuekuajingT)*100)/totalBridgeNum) +"%");
		}
	}
	
	private void _getTongleiBiliBHBridgeNum(String id,String nId,
			Map<String, Object> kongkuashuMap,Map<String, Object> bridgeBykuajingMap,
			Map<String, Object> resultMap)
	{   //kuanggou //other
		//id = "Jnum_" + (out+1) + "_";   heji other kuanggou; nId "Jtonglei_" + (out+1) + "_";
		//id = "Lnum_" + (out+1) + "_";nId = "Ltonglei_" + (out+1) + "_";
		String keykuajing = "";
		Integer valuekuajing = 0;
		String key = "";
		Object value = "";
		int valueT = 0;
		DecimalFormat df = new DecimalFormat("0.00");//格式化小数   
		for (Entry<String, Object> entry: bridgeBykuajingMap.entrySet()) {
			keykuajing = entry.getKey();
			valuekuajing = (Integer)entry.getValue();
			if (StringTool.isNullEmpty(keykuajing) || StringTool.isNull(valuekuajing))
				continue;

			if (valuekuajing == -1 || valuekuajing == 0)
				continue;
			
			key =  id + keykuajing;//"Jnum_" + (out+1) + "_" + keykuajing
			value = kongkuashuMap.get(key + "_AA");
			if(!StringTool.isNull(value)) valueT = (Integer)value;
			else valueT = 0;
			if (valueT != -1 && valueT != 0){
				resultMap.put(nId + keykuajing + "_AA",df.format((float)(valueT*100)/valuekuajing) +"%");
				//continue;
			}

			value = kongkuashuMap.get(key + "_A1");
			if(!StringTool.isNull(value)) valueT = (Integer)value;
			else valueT = 0;
			if (valueT != -1 && valueT != 0){
				resultMap.put(nId + keykuajing + "_A1",df.format((float)(valueT*100)/valuekuajing) +"%");
				//continue;
			}

			value = kongkuashuMap.get(key + "_B");
			if(!StringTool.isNull(value)) valueT = (Integer)value;
			else valueT = 0;
			if (valueT != -1 && valueT != 0){
				resultMap.put(nId + keykuajing + "_B",df.format((float)(valueT*100)/valuekuajing) +"%");
				//continue;
			}

			value = kongkuashuMap.get(key + "_C");
			if(!StringTool.isNull(value)) valueT = (Integer)value;
			else  valueT = 0;
			if (valueT != -1 && valueT != 0){
				resultMap.put(nId + keykuajing + "_C",df.format((float)(valueT*100)/valuekuajing) +"%");
				//continue;
			}

			value = kongkuashuMap.get(key + "_D");
			if(!StringTool.isNull(value)) valueT = (Integer)value;
			else valueT = 0;
			if (valueT != -1 && valueT != 0){
				resultMap.put(nId + keykuajing + "_D",df.format((float)(valueT*100)/valuekuajing) +"%");
				//continue;
			}
		}
	}
	/**传入：resultMap  key: Jquanxian_1_：返回值有%
	 * 返回： nId = "Jquanxian_" + (out+1) + "_heji_";
	 */
	private void _getTongleiBiliHejiBHBridgeNum(String nId,int totalBridgeNum, Map<String, Object> resultMap)
	{  //nId = "Jquanxian_" + (out+1) + "_heji_"
		String keykuajing = "";
		//String valuekuajing = "";
		//int valuekuajingT = 0;
		String key = "";
		String value = "";
		int valueT = 0;
		
		Map<String, Object> resultMapT = new HashMap<String, Object>();
		DecimalFormat df = new DecimalFormat("0.00");//格式化小数   
		for (Entry<String, Object> entry:resultMap.entrySet()) {
			keykuajing = entry.getKey();
			//valuekuajing = (String)entry.getValue();
			if (StringTool.isNullEmpty(keykuajing)/*|| StringTool.isNullEmpty(valuekuajing)*/)
				continue;

			if (keykuajing.contains("AA"))
				key = nId + "AA";
			else if (keykuajing.contains("A1"))
				key = nId + "A1";
			else if (keykuajing.contains("B"))
				key = nId + "B";
			else if (keykuajing.contains("C"))
				key = nId + "C";
			else if (keykuajing.contains("D"))
				key = nId + "D";
			
			value = (String)resultMap.get(key);
			if(value != null && value.indexOf("%") != -1) 
			{
				value = value.substring(0,value.length() - 1);
				valueT = StringTool.stringToInt(value);
				valueT = valueT/100;
			}
			else
				valueT = StringTool.stringToInt(value);
			if (valueT == -1) 
				resultMapT.put(key,df.format((float)(100)/totalBridgeNum) +"%");
			else
				resultMapT.put(key,df.format((float)((valueT + 1)*100)/totalBridgeNum) +"%");
		}
		resultMap.putAll(resultMapT);
	}
		
	
	@RequestMapping(value="/Jianzhi3Bridge_init") 
	@ResponseBody
	public Object Jianzhi3Bridge_init() 
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();
		String year = pd.getString("Year");
	//	String end = year + "-12-31";
	//	String year = DateTool.getYear();
		int yearI = StringTool.stringToInt(year) - 2;
		
		//保存返回到页面的结果
		Map<String, Object> resultMap = new HashMap<String, Object>();
		/**(1):返回到页面的年度数据*/
		resultMap.put("JfirstYear", yearI);
		resultMap.put("JsecondYear", yearI + 1);
		resultMap.put("JthirdYear", yearI + 2);

		/**依据跨径、框构和其他，获取每一类别梁的总数量。 
		 * 主键为：kuanggou， other, 数字话的跨径（8,10，。。。）：值为对应的整形数字
		 */
		Map<String, Object> bridgeBykuajingMap = new HashMap<String, Object>();
		_getAllBridgeBykuajing(bridgeBykuajingMap);
		
		System.out.println("每一类别梁的总数量:"+bridgeBykuajingMap.toString());
		//{12=1, 24=1, 16=1, kuanggou=81, 8=1, 20=1, 64=1, 32=1, 10=1}

		//桥梁基本信息所含的记录数，即全线的桥数
		//int totalBridgeNum = (int)systemService.queryCount("tbridgebasicinfo");
		//某一线的桥梁总数  暂时不用
		//Map<String, Object> lineMap = new HashMap<String, Object>();
		/**全部梁的总数。 没有区分不同线路 */
		int totalBridgeNum = (int)systemService.queryCount("tgirder");
		System.out.println("全线的梁数:"+totalBridgeNum);
		
		/**存储：针对桥梁孔跨数/孔的数量的每一种类病害桥的数量*/
		Map<String, Object> kongkuashuMap = new HashMap<String, Object>();

		//处理三年的情况
		for( int out = 0 ; out< 3; out++)
		{//For 3Y
			String begin = yearI + out + "-01-01";
			String end = yearI + out + "-12-31";

			kongkuashuMap = new HashMap<String, Object>();
			
			/**获取存在病害的梁数量*/
			List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
			String items = "qiaohao,kongkuahao,zhuliangjudge";//
			String where = "pingdingdate <='" + end + "' AND pingdingdate >='" + begin + "'" ;
			lists= systemService.query("tbridgebasicinfojudge",items, where);

			String id = "";//统计   
			if(lists != null){//if_1
				System.out.println("存在病害的梁数量:"+lists.size());
				for(int i=0; i<lists.size(); i++){//for_1Y 处理每一条评价记录
					Map<String, Object> map = new HashMap<String, Object>();
					map = lists.get(i);

					Object value = "";
					String key = "";
					String qiaohao = "";
					String judge = "";
					Integer kongkuahao = 0;
					//String linename = "";
					for (Entry<String, Object> entry: map.entrySet()) {//for_1Y-1R
						key = entry.getKey();
						value = entry.getValue();
						if("zhuliangjudge".equalsIgnoreCase(key)) judge = (String)value;
						else if("qiaohao".equalsIgnoreCase(key)) qiaohao = (String)value;
						else if("kongkuahao".equalsIgnoreCase(key))	kongkuahao = (Integer)value;
						/*else if("linename".equals(key))
						linename = value;*/
					}//for_1Y-1R

					if(StringTool.isNullEmpty(judge)) continue;
					
					//获取某一线的桥梁总数 暂时不用
					/*if(lineMap != null && StringTool.isNullEmpty((String)lineMap.get("linename")))
					getTotalBridgeNum(lineMap,linename);
					 */
					/**获取给定梁信息*/
					Map<String, Object> bridgeInfo = new HashMap<String, Object>();
					String items1 = "kuajing,shoulixingshi";
					String where1 = "qiaohao='" + qiaohao + "' AND kongkuahao='" + kongkuahao +"'";
					bridgeInfo = systemService.queryOne4Items("tgirder",items1, where1); 
					System.out.println("给定梁信息:" + bridgeInfo.toString());

					/**(2):返回到页面的"桥梁孔跨数/孔"值： 统计每一种类病害的桥的数量*/
					/**结果集kongkuashuMap的Key格式为：
					 * Jnum_1_8_AA、Jnum_1_kuanggou_AA、Jnum_1_other_AA、Jnum_1_heji_AA
					 * 值为整数数字
					 * 
					 * 每次处理一条记录
					 */
					if (bridgeInfo != null ) {//bridgeInfo != null
						id = "Jnum_" + (out+1) + "_";
						_getBHBridgeNum(id,judge,bridgeInfo,kongkuashuMap);	
						System.out.println("统计不同跨径梁的不同病害数量:" + kongkuashuMap.toString());
					}//bridgeInfo != null
				}//for_1Y 
				
				System.out.println("统计不同跨径梁的不同病害数量（原始总和1）:" + resultMap.toString());
				/**保存到最终结果集中*/
				if(!StringTool.isNull(kongkuashuMap)) resultMap.putAll(kongkuashuMap);
				System.out.println("统计不同跨径梁的不同病害数量（原始总和2）:" + resultMap.toString());
				
				/**(3):返回到页面的"桥梁数量/座"的值： 统计每一类病害的(桥梁孔跨数/孔)的总数量. */
				/**resultMapT的Key格式为：Jnum_1_sum_AA， value为整数值*/
				Map<String, Object> resultMapT = new HashMap<String, Object>();//保留中间结果
				String nId = "sum_";
				id = "Jnum_" + (out+1) + "_";
				/**由于kongkuashuMap包括合计的数目， 所以结果值应除以2或不统计含heji的项*/
				_getTotalBHBridgeNum(id,nId,kongkuashuMap,resultMapT);
               
				System.out.println("统计不同跨径梁的不同病害数量（某年）:" + resultMapT.toString());
				if(!StringTool.isNull(resultMapT)) resultMap.putAll(resultMapT);
				
				System.out.println("每一类病害的阶段统计1:" + resultMap.toString());
				//统计全线桥梁总数
				/*	int totalBridgeNum = 0;
					int numT = 0;
					String valueT;
					for (Entry<String, Object> entry: lineMap.entrySet()) {
						valueT = (String)entry.getValue();
						numT = StringTool.stringToInt(valueT);
						if (numT != -1)
						totalBridgeNum = totalBridgeNum + numT;
					}*/

				/**（4）返回到页面的"占全线桥梁比例"的值：统计每一种类病害的（桥梁孔跨数/孔）的占全线（所有铁路线）的比例*/
				/**resultMapT2的Key格式为：Jnum_1_bili_AA， 值含有%  */
				if(totalBridgeNum > 0 )	{//全线的梁数
					Map<String, Object> resultMapT2 = new HashMap<String, Object>();//保留中间结果
					nId = "bili_";
					_getBiliBHBridgeNum(id,nId,resultMapT,totalBridgeNum,resultMapT2);
					
					System.out.println("统计比例关系（每一种类病害/占全线桥梁比例）:" + resultMapT2.toString());
					if(!StringTool.isNull(resultMapT2)) resultMap.putAll(resultMapT2);	
					System.out.println("阶段合2:" + resultMap.toString());
				}
				/**（5）返回到页面的"占同类桥梁比例"的值。
				 *  kongkuashuMap 同类有病的梁总和(含有合计)；
				 *  bridgeBykuajingMap：同类的全部桥总数
				 *  resultMapT的Key格式为：Jtonglei_1_8_AA; 返回值有%  
				*/
				resultMapT = new HashMap<String, Object>();
				nId = "Jtonglei_" + (out+1) + "_";
				_getTongleiBiliBHBridgeNum(id,nId,kongkuashuMap,bridgeBykuajingMap,resultMapT);
				System.out.println("统计同类桥梁比例（每一种类病害/占全线桥梁比例）:" + resultMapT.toString());
					
				nId = "Jtonglei_" + (out+1) + "_heji_";	//统计合计
				_getTongleiBiliHejiBHBridgeNum(nId,totalBridgeNum,resultMapT);
				
				if(!StringTool.isNull(resultMapT)) resultMap.putAll(resultMapT);
				System.out.println("统计同类桥梁比例（合计-每一种类病害/占全线桥梁比例）:" + resultMapT.toString());

				/**(6)返回到页面的"占全线桥梁比例"的值。 即统计全线桥梁比例
				 * kongkuashuMap 同类有病的梁总和；totalBridgeNum：全部桥总数
				 * kongkuashuMap的Key格式为：
				 * Jnum_1_8_AA、Jnum_1_kuanggou_AA、Jnum_1_other_AA、Jnum_1_heji_AA   值为整数数字
				 * 结果集：resultMapT  key: Jquanxian_1_：返回值有%
				 */ 
				if(totalBridgeNum > 0 )	{
					resultMapT = new HashMap<String, Object>();
					nId = "Jquanxian_" + (out+1) + "_";
					//Jquanxian_1_8_AA 
					resultMapT = new HashMap<String, Object>();
					_getQuanxianBiliBHBridgeNum(nId,kongkuashuMap,totalBridgeNum,resultMapT);
					
					//统计合计：Jquanxian_3_heji
					nId = "Jquanxian_" + (out+1) + "_heji_";
					_getTongleiBiliHejiBHBridgeNum(nId,totalBridgeNum,resultMapT);
					
					if(!StringTool.isNull(resultMapT))	
						resultMap.putAll(resultMapT);
				}

			}//if_1
		}//For 3Y
		
		if(resultMap  != null && resultMap .size() > 0)
			return resultMap;
		else
			return "";
	}
	

	
	
	
	//统计某一线的所有桥梁个数
	public void getTotalBridgeNum(Map<String, Object> resultMap,String linename)
	{
		String  where = "linename='" + linename + "'";
		long totalRecord = systemService.queryCount("tbridgebasicinfo",where);
		resultMap.put(linename,totalRecord);
	}
	
	private void getAllBridgeBykuajing2(Map<String, Object> resultMap)
	{
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		String items = "kuajing";
		lists= systemService.query("tgirder",items,"");

		if(lists != null){//if_1
			for(int i=0; i<lists.size(); i++){//for_1Y 处理每一条评价记录
				Integer kuajing = 0;
				Integer num = 0;
				Map<String, Object> map = new HashMap<String, Object>();
				map = lists.get(i);
				for (Entry<String, Object> entry: map.entrySet()) {//for1-21 只有两个元素
					String key = entry.getKey();
					Integer value = (Integer)entry.getValue();
					if("kuajing".equals(key))
						kuajing = value;
				}
				if(StringTool.isNull(kuajing) || kuajing == -1)
					continue;

				if( (kuajing == 40 || kuajing == 48 ||
						kuajing == 50 || kuajing == 64 ||
						kuajing == 72 || kuajing == 80 ||
						kuajing == 120 || kuajing == 180) )
				{
					num = (Integer)resultMap.get(kuajing);
					if(!StringTool.isNull(num)) 
						resultMap.put(""+kuajing,num+1);
					else
						resultMap.put(""+kuajing,1);
				}
				else {
					num = (Integer)resultMap.get("other");
					if(!StringTool.isNull(num)) 
						resultMap.put("other",num+1);
					else
						resultMap.put("other",1);
				}

			}//for_1Y
		}//if_1
	} 

	//针对不同跨度，统计各类病害的梁的数目
	private void getBHBridgeNum2(String id,String judge,
			Map<String, Object> dataMap,
			Map<String, Object> resultMap)
	{ 
		int kuajing = (Integer)dataMap.get("kuajing");//kuajing（跨径(m)）
		String key;
		Integer num = 0;
		if( (kuajing != -1) &&
				(kuajing == 40 || kuajing == 48 ||
				kuajing == 50 || kuajing == 64 ||
				kuajing == 72 || kuajing == 80 ||
				kuajing == 120 || kuajing == 180)  
				){
			key = id + kuajing + "_" + judge;
			num = (Integer)resultMap.get(key);
			if(!StringTool.isNull(num)) 
				resultMap.put(key,num+1);
			else
				resultMap.put(key,1);
		}
		else{
			key = id + "other_" + judge;
			num = (Integer)resultMap.get(key);
			if(!StringTool.isNull(num)) 
				resultMap.put(key,num+1);
			else
				resultMap.put(key,1);
		}


		//累计同类的总和
		key = id + "heji_" + judge;
		if (StringTool.isNull(num) || num == 0)
			resultMap.put(key,1);
		else
			resultMap.put(key,num+1);
	}  
	
	@RequestMapping(value="/JianzhiChart") 
	@ResponseBody
	public Object JianzhiChart()//删除response，就报错。原因不明 HttpServletResponse response
	{
		String year = DateTool.getYear();//

		//1简支、2连续 , 12:框构桥	
		String shoulixingshi = "简支";
		Map<String, Object> resultMap = new HashMap<String, Object>();

		_getJudge(resultMap,year,shoulixingshi);
		
		int [] date = {0,0,0,0,0};
		for (Entry<String, Object> entry: resultMap.entrySet()) {
			String key = entry.getKey();
			Integer value = (Integer)entry.getValue();
			if("AA".equalsIgnoreCase(key))
				date[0] = value;
			else if("A1".equalsIgnoreCase(key))
				date[1] = value;
			else if("B".equalsIgnoreCase(key))
				date[2] = value;
			else if("C".equalsIgnoreCase(key))
				date[3] = value;
			else if("D".equalsIgnoreCase(key))
				date[4] = value;
		}

		String [] color = new String[]{"#FF0000","#FFA500","#FFFF00","#0000FF","#00FF00"};
		String [] name = new String[]{"AA","A1","B","C","D"};//共五种情况：AA，A1,B,C,D
		JSONArray lists = new JSONArray();
		for(int i=0;i<5;i++)//每个分组有几个元素， 即每年要显示多少种情况
		{	//构建JSON 对象
			JSONObject barObj = new JSONObject();
			//构建series所需参数
			barObj.put("name", name[i]); //对应series.name
			barObj.put("year", year);//对应Y轴显示
			barObj.put("color", color[i]);

			JSONArray dataList = new JSONArray();//对应series.data
			dataList.add(date[i]);

			barObj.put("list",dataList);

			lists.add(barObj);
		}
		return lists;
	}

	
	@RequestMapping(value="/Jianzhi3Chart") 
	@ResponseBody
	public Object Jianzhi3Chart()
	{
		//1简支、2连续 , 12:框构桥	
		String shoulixingshi = "简支";
		String yearStr = DateTool.getYear();//
		int yearInt = StringTool.stringToInt(yearStr) - 2;
		
		int [][] date = {{10,11,12},{0,0,0},{0,0,0},{0,0,0},{0,0,0}};
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		for( int out = 0 ; out< 3; out++)//For 3Y
		{
			_getJudge(resultMap,yearInt - out + "",shoulixingshi);
			for (Entry<String, Object> entry: resultMap.entrySet()) {
				String key = entry.getKey();
				Integer value = (Integer)entry.getValue();
				if("AA".equalsIgnoreCase(key))
					date[0][out] = value;
				else if("A1".equalsIgnoreCase(key))
					date[1][out] = value;
				else if("B".equalsIgnoreCase(key))
					date[2][out] = value;
				else if("C".equalsIgnoreCase(key))
					date[3][out] = value;
				else if("D".equalsIgnoreCase(key))
					date[4][out] = value;
			}
		}
		String [] color = new String[]{"#FF0000","#FFA500","#FFFF00","#0000FF","#00FF00"};
		String [] name = new String[]{"AA","A1","B","C","D"};//共五种情况：AA，A1,B,C,D
		JSONArray lists = new JSONArray();
		for(int i=0;i<5;i++)//每个分组有几个元素， 即每年要显示多少种情况
		{	//构建JSON 对象
			JSONObject barObj = new JSONObject();
			//构建series所需参数
			barObj.put("name", name[i]); //对应series.name
			barObj.put("year", (yearInt - i));//对应Y轴显示
			barObj.put("color", color[i]);//如需要可以设置柱状图颜色

			JSONArray dataList = new JSONArray();//对应series.data
			for(int k=0; k < 3; k++)//总共有三个组，对应三个年
				dataList.add(date[i][k]);

			barObj.put("list",dataList);

			lists.add(barObj);
		}

		return lists;
	}

	
	@RequestMapping(value="/ComChart_count") 
	@ResponseBody
	public Object ComChart_count()
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();

		String year = pd.getString("Year");
		int yearInt = StringTool.stringToInt(year);

		String tableName = "thandongjudge";
		String group = "zongjudge";//分类依据:总劣化等级
		String items = "zongjudge";//查询项

		int [][] date = {{10,11,12},{0,0,0},{0,0,0},{0,0,0},{0,0,0}};
			
		Map<String, Object> resultMap = new HashMap<String, Object>();
		for( int out = 0 ; out< 3; out++)//For 3Y
		{
			String begin = yearInt - out + "-01-01";
			String end = yearInt - out + "-12-31";
			String where = "pingdingdate <='" + end + "'" +	" AND pingdingdate >='" + begin + "'" ;

			List<Map<String, Object>> listMaps = new ArrayList<Map<String, Object>>();
			listMaps = systemService.queryCount(tableName,items,where,group);

			String fieldName="zongjudge";
			String[] rowArry = {"AA","A1","B","C","D"};
			
			if(!StringTool.isNull(listMaps))
				_genJudgeValue(resultMap,listMaps,rowArry,fieldName);
			
			for (Entry<String, Object> entry: resultMap.entrySet()) {
				String key = entry.getKey();
				Integer value = (Integer)entry.getValue();
				if("AA".equalsIgnoreCase(key))
					date[0][out] = value;
				else if("A1".equalsIgnoreCase(key))
					date[1][out] = value;
				else if("B".equalsIgnoreCase(key))
					date[2][out] = value;
				else if("C".equalsIgnoreCase(key))
					date[3][out] = value;
				else if("D".equalsIgnoreCase(key))
					date[4][out] = value;
			}
		}
		
		//红橙黄蓝绿
		String [] color = new String[]{"#FF0000","#FFA500","#FFFF00","#0000FF","#00FF00"};
		String [] name = new String[]{"AA","A1","B","C","D"};//共五种情况：AA，A1,B,C,D
		JSONArray lists = new JSONArray();
		for(int i=0;i<5;i++)//每个分组有几个元素， 即每年要显示多少种情况
		{	//构建JSON 对象
			JSONObject barObj = new JSONObject();
			//构建series所需参数
			barObj.put("name", name[i]); //对应series.name
			barObj.put("year", (yearInt - i));//对应Y轴显示
			barObj.put("color", color[i]);//如需要可以设置柱状图颜色

			JSONArray dataList = new JSONArray();//对应series.data
			for(int k=0; k < 3; k++)//总共有三个组，对应三个年
				dataList.add(date[i][k]);

			barObj.put("list",dataList);

			lists.add(barObj);
		}

		return lists;
	}	
	
	private void _getJudge(Map<String, Object> resultMap, String year,String shoulixingshi)
	{
		String begin = year + "-01-01";
		String end = year + "-12-31";
		//获取到存在病害的桥梁
		List<Map<String, Object>> mapLists = new ArrayList<Map<String, Object>>();
		String items = "qiaohao,kongkuahao,zhuliangjudge";//
		String where = "pingdingdate <='" + end + "'" +	" AND pingdingdate >='" + begin + "'" ;
		mapLists = systemService.query("tbridgebasicinfojudge",items, where);

		if(mapLists != null){//if_1 //对每条记录进行处理
			for(int i=0; i<mapLists.size(); i++){//for_1Y 处理每一条评价记录
				Map<String, Object> map = new HashMap<String, Object>();
				map = mapLists.get(i);

				Object value1 = "";
				String key = "";
				String qiaohao = "";
				String judge = "";
				Integer kongkuahao = 0;
				for (Entry<String, Object> entry: map.entrySet()) {//for1-21
					key = entry.getKey();
					value1 = entry.getValue();
					if("zhuliangjudge".equalsIgnoreCase(key))
						judge = (String)value1;
					else if("qiaohao".equalsIgnoreCase(key))
						qiaohao = (String)value1;
					else if("kongkuahao".equalsIgnoreCase(key))
						kongkuahao = (Integer)value1;
				}

				if(StringTool.isNullEmpty(judge) || StringTool.isNullEmpty(qiaohao) )
					continue;

				//获取给定桥号的主梁信息
				Map<String, Object> bridgeInfo = new HashMap<String, Object>();
				String items1 = "shoulixingshi";
				String where1 = "qiaohao='" + qiaohao + "' AND kongkuahao='" + kongkuahao +"'";
				bridgeInfo = systemService.queryOne4Items("tgirder",items1, where1);

				//shoulixingshi（1简支、2连续 , 12:框构桥
				String temp = (String)bridgeInfo.get("shoulixingshi");
				if (temp != null && shoulixingshi.equals(temp)){
					Object obj = resultMap.get(judge);
					int value = 0;
					if(StringTool.isNull(obj))
						value = -1;
					else
						value = (Integer)obj;
					if (value == -1){
						resultMap.put(judge,1);
					}
					else
						resultMap.put(judge,value + 1);
				}
			}//for_1Y 
		}
	}
	
	private String _genRowCode(String key,String[] rowArry)
	{
		String row = "";
		for(int i = 0; i < rowArry.length; i++)	{
			if(key != null && key.equalsIgnoreCase(rowArry[i])){
				row = key;
				break;
			}
		}
		return row;
	}
	private void _genJudgeValue(Map<String, Object> resultMap,List<Map<String, Object>> lists,
								String[] rowArry,String fieldName)
	{
		for(int i=0; i<lists.size(); i++){
			Map<String, Object> map = new HashMap<String, Object>();
			map = lists.get(i);
			String fieldValue = (String)map.get(fieldName);
			Number value = (Number)map.get("sum");
           
			String key = _genRowCode(fieldValue,rowArry);
			if(!StringTool.isNullEmpty(key))
				resultMap.put(key,value.intValue());
		}
	}

	//以下代码目前没有用
	@RequestMapping(value="/LianxuChart") 
	@ResponseBody
	public Object LianxuChart()
	{
		String year = DateTool.getYear();//
		//1简支、2连续 , 12:框构桥	
		String shoulixingshi = "连续";
		Map<String, Object> resultMap = new HashMap<String, Object>();
		_getJudge(resultMap,year,shoulixingshi);

		int [] date = {0,0,0,0,0};
		for (Entry<String, Object> entry: resultMap.entrySet()) {
			String key = entry.getKey();
			Integer value = (Integer)entry.getValue();
			if("AA".equalsIgnoreCase(key))
				date[0] = value;
			else if("A1".equalsIgnoreCase(key))
				date[1] = value;
			else if("B".equalsIgnoreCase(key))
				date[2] = value;
			else if("C".equalsIgnoreCase(key))
				date[3] = value;
			else if("D".equalsIgnoreCase(key))
				date[4] = value;
		}

		String [] name = new String[]{"AA","A1","B","C","D"};//共五种情况：AA，A1,B,C,D
		JSONArray lists = new JSONArray();
		for(int i=0;i<5;i++)//每个分组有几个元素， 即每年要显示多少种情况
		{	//构建JSON 对象
			JSONObject barObj = new JSONObject();
			//构建series所需参数
			barObj.put("name", name[i]); //对应series.name
			barObj.put("year", year);//对应Y轴显示
			//barObj.put("color", "#FF0022");//如需要可以设置柱状图颜色

			JSONArray dataList = new JSONArray();//对应series.data
			dataList.add(date[i]);

			barObj.put("list",dataList);

			lists.add(barObj);
		}

		return lists;
	}

	@RequestMapping(value="/Lianxu3Chart") 
	@ResponseBody
	public Object Lianxu3Chart()
	{
		//1简支、2连续 , 12:框构桥	
		String shoulixingshi = "连续";
		String yearStr = DateTool.getYear();//
		int yearInt = StringTool.stringToInt(yearStr) - 2;

		Map<String, Object> resultMap1 = new HashMap<String, Object>();
		_getJudge(resultMap1,yearInt + "",shoulixingshi);

		Map<String, Object> resultMap2 = new HashMap<String, Object>();
		_getJudge(resultMap2, yearInt + 1 + "",shoulixingshi);

		Map<String, Object> resultMap3 = new HashMap<String, Object>();
		_getJudge(resultMap3, yearInt + 2 + "",shoulixingshi);

		String [] name = new String[]{"AA","A1","B","C","D"};//共五种情况：AA，A1,B,C,D

		int [][]date = {  
				{0,0,0},//对应三年的AA取值. 第一列对应第一年的数据。。。。
				{0,0,0},//对应三年的A1取值
				{0,0,0},
				{0,0,0},
				{0,0,0}
		};


		for (Entry<String, Object> entry: resultMap1.entrySet()) {
			String key = entry.getKey();
			Integer value = (Integer)entry.getValue();
			if("AA".equalsIgnoreCase(key))
				date[0][0] = value;
			else if("A1".equalsIgnoreCase(key))
				date[1][0] = value;
			else if("B".equalsIgnoreCase(key))
				date[2][0] = value;
			else if("C".equalsIgnoreCase(key))
				date[3][0] = value;
			else if("D".equalsIgnoreCase(key))
				date[4][0] = value;
		}
		for (Entry<String, Object> entry: resultMap2.entrySet()) {
			String key = entry.getKey();
			Integer value = (Integer)entry.getValue();
			if("AA".equalsIgnoreCase(key))
				date[0][1] = value;
			else if("A1".equalsIgnoreCase(key))
				date[1][1] = value;
			else if("B".equalsIgnoreCase(key))
				date[2][1] = value;
			else if("C".equalsIgnoreCase(key))
				date[3][1] = value;
			else if("D".equalsIgnoreCase(key))
				date[4][1] = value;
		}
		for (Entry<String, Object> entry: resultMap3.entrySet()) {
			String key = entry.getKey();
			Integer value = (Integer)entry.getValue();
			if("AA".equalsIgnoreCase(key))
				date[0][2] = value;
			else if("A1".equalsIgnoreCase(key))
				date[1][2] = value;
			else if("B".equalsIgnoreCase(key))
				date[2][2] = value;
			else if("C".equalsIgnoreCase(key))
				date[3][2] = value;
			else if("D".equalsIgnoreCase(key))
				date[4][2] = value;
		}

		JSONArray lists = new JSONArray();
		for(int i=0;i<5;i++)//每个分组有几个元素， 即每年要显示多少种情况
		{	//构建JSON 对象
			JSONObject barObj = new JSONObject();
			//构建series所需参数
			barObj.put("name", name[i]); //对应series.name
			barObj.put("year", (yearInt + i));//对应Y轴显示
			//barObj.put("color", "#FF0022");//如需要可以设置柱状图颜色

			JSONArray dataList = new JSONArray();//对应series.data
			for(int k=0; k < 3; k++)//总共有三个组，对应三个年
				dataList.add(date[i][k]);

			barObj.put("list",dataList);

			lists.add(barObj);
		}

		return lists;
	}

	/**
	 *  1.从桥梁劣化等级评价记录表(总)tbridgebasicinfojudge中检索
	 *  qiaohao、linename、shuoshugongdui、qiaoliangjudge、pingdingdate等信息
	 *  tbridgebasicinfo 含有linename信息
	 */
	/**
	 *  从主梁tgirder中检索
	 *  lianggao（1普通高度、2中高度、3低高度、4超低高度）
	 *  material（2钢筋混凝土、7钢混组合、4 预应力钢筋混凝土; 6普通桥梁钢、5低合金钢、3型钢混凝土、8钢管混凝、1钢梁）
	 *  leixing（2桁梁、3T梁、4箱型梁,7T型刚构,8门式刚构,9斜腿刚构;）
	 *  shoulixingshi（1简支、2连续 , 12:框构桥）  
	 */
	@RequestMapping(value="/LianxuBridge_init") 
	@ResponseBody
	public Object LianxuBridge_init() 
	{
		String year = DateTool.getYear();
		String begin = year + "-01-01";
		String end = year + "-12-31";
		/**
		 *  1.从桥梁劣化等级评价记录表(总)tbridgebasicinfojudge中检索
		 *  qiaohao、shuoshugongdui、qiaoliangjudge、pingdingdate等信息
		 */
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		String items = "qiaohao,kongkuahao,zhuliangjudge";//
		String where = "pingdingdate <='" + end + "'" +
				"AND pingdingdate >='" + begin + "'" ;
		lists= systemService.query("tbridgebasicinfojudge",items, where);

		//返回到页面的结果
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String id = "Lianxu_";//1简支   

		if(lists != null){//if1
			for(int i=0; i<lists.size(); i++)
			{//for1 处理每一条评价记录
				Map<String, Object> map = new HashMap<String, Object>();
				map = lists.get(i);

				Object value = "";
				String key = "";
				String qiaohao = "";
				String judge = "";
				Integer kongkuahao = 0;
				for (Entry<String, Object> entry: map.entrySet()) {//for1-21
					key = entry.getKey();
					value = entry.getValue();
					if("zhuliangjudge".equalsIgnoreCase(key))
						judge = (String)value;
					else if("qiaohao".equalsIgnoreCase(key))
						qiaohao = (String)value;
					else if("kongkuahao".equalsIgnoreCase(key))
						kongkuahao = (Integer)value;
				}//for1-21

				if(StringTool.isNullEmpty(judge)){
					continue;
				}

				//获取给定桥号的主梁信息
				Map<String, Object> bridgeInfo = new HashMap<String, Object>();
				String items1 = "kuajing,lianggao,material,leixing,shoulixingshi";
				String where1 = "qiaohao='" + qiaohao + "' AND kongkuahao='" + kongkuahao +"'";
				bridgeInfo = systemService.queryOne4Items("tgirder",items1, where1);

				if (bridgeInfo != null ) {//bridgeInfo != null
					//shoulixingshi（1简支、2连续 , 12:框构桥
					String shoulixingshi = (String)bridgeInfo.get("shoulixingshi");
					if(! "连续".equals(shoulixingshi))  
						continue;//非连续

					//leixing（1板梁;2桁梁、3T梁、4箱型梁,7T型刚构,8门式刚构,9斜腿刚构;
					String leixing  = (String)bridgeInfo.get("leixing");
					//material 1钢梁; 2钢筋混凝土、7钢混组合、4 预应力钢筋混凝土; 6普通桥梁钢、5低合金钢、3型钢混凝土、8钢管混凝、1钢梁）
					String material  = (String)bridgeInfo.get("material");
					//String kongshu = getKongshu(qiaohao);
					//kuajing（跨径(m)）
					int kuajing = (Integer)bridgeInfo.get("kuajing");

					if( "预应力钢筋混凝土".equals(material) && //4 预应力钢筋混凝土
							( "梁式桥--箱型梁".equals(leixing) ||"箱型梁".equals(leixing) ) ) //4箱型梁
					{
						if( 40==kuajing )
							_genComponentID(resultMap,id,""+kuajing,"40_",judge);	
						else if( 50==kuajing ||  72==kuajing )
							_genComponentID(resultMap,id,""+kuajing,"50_",judge);
						if(80==kuajing )
							_genComponentID(resultMap,id,""+kuajing,"80_",judge);
					}
					else  if( ( "梁式桥--桁梁".equals(leixing) ||"桁梁".equals(leixing) ) && //2桁梁
							("普通桥梁钢".equals(material) || "低合金钢".equals(material))  ) //1钢梁
					{
						if( 48==kuajing || 64==kuajing )
							_genComponentID(resultMap,id,""+kuajing,"80_",judge);
						else if(120==kuajing || 180==kuajing)
							_genComponentID(resultMap,id,""+kuajing,"120_",judge);
					}
					else //其他
						_genComponentID(resultMap,id,"other","other_",judge);
				}//bridgeInfo != null
			}//for1
			//所有分支全部处理完，需要执行合计
			_genSum(resultMap,id);
		}//if1
		if(resultMap  != null && resultMap .size() > 0)
			return resultMap;
		else
			return "";
	}
	
	@RequestMapping(value="/Lianxu3Bridge_init") 
	@ResponseBody
	public Object Lianxu3Bridge_init() 
	{
		String year = DateTool.getYear();
		int yearI = StringTool.stringToInt(year) - 2;
		//返回到页面的结果
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("LfirstYear", yearI);
		resultMap.put("LsecondYear", yearI + 1);
		resultMap.put("LthirdYear", yearI + 2);

		//获取每一类别的桥的总数量
		Map<String, Object> bridgeBykuajingMap = new HashMap<String, Object>();
		getAllBridgeBykuajing2(bridgeBykuajingMap);

		//桥梁基本信息所含的记录数，即全线的桥梁数
		int totalBridgeNum = (int)systemService.queryCount("tbridgebasicinfo");

		//每一类别病虫害的桥梁孔跨数/孔
		Map<String, Object> kongkuashuMap = new HashMap<String, Object>();

		//处理三年的情况
		for( int out = 0 ; out< 3; out++)
		{//For 3Y
			String begin = yearI + out + "-01-01";
			String end = yearI + out + "-12-31";

			//获取到存在病害的桥梁
			List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
			String items = "qiaohao,kongkuahao,zhuliangjudge";//linename,
			String where = "pingdingdate <='" + end + "'" +	"AND pingdingdate >='" + begin + "'" ;
			lists= systemService.query("tbridgebasicinfojudge",items, where);

			String id = "";//统计   
			if(lists != null){//if_1
				//对每条记录进行处理
				for(int i=0; i<lists.size(); i++){//for_1Y 处理每一条评价记录
					Map<String, Object> map = new HashMap<String, Object>();
					map = lists.get(i);

					Object value = "";
					String key = "";
					String qiaohao = "";
					String judge = "";
					Integer kongkuahao = 0;
					for (Entry<String, Object> entry: map.entrySet()) {//for1-21
						key = entry.getKey();
						value = entry.getValue();
						if("zhuliangjudge".equalsIgnoreCase(key))
							judge = (String)value;
						else if("qiaohao".equalsIgnoreCase(key))
							qiaohao = (String)value;
						else if("kongkuahao".equalsIgnoreCase(key))
							kongkuahao = (Integer)value;
					}//for1-21

					if(StringTool.isNullEmpty(judge)){
						continue;
					}

					//获取给定桥号的主梁信息
					Map<String, Object> bridgeInfo = new HashMap<String, Object>();
					String items1 = "kuajing";
					String where1 = "qiaohao='" + qiaohao + "' AND kongkuahao='" + kongkuahao +"'";
					bridgeInfo = systemService.queryOne4Items("tgirder",items1, where1); 

					//统计每一病害的桥梁孔跨数/孔的数量。 还要加上所在线信息信息
					if (bridgeInfo != null ) {//bridgeInfo != null
						id = "Lnum_" + (out+1) + "_";
						getBHBridgeNum2(id,judge,bridgeInfo,kongkuashuMap);	
					}//bridgeInfo != null
				}//for_1Y 

				//保存最终结果
				if(!StringTool.isNull(kongkuashuMap))
					resultMap.putAll(kongkuashuMap);

				//统计每一病害的桥梁孔跨数/孔的总数量. 表格的第一行
				Map<String, Object> resultMapT = new HashMap<String, Object>();//保留中间结果
				String nId = "sum_";
				id = "Lnum_" + (out+1) + "_";
				_getTotalBHBridgeNum(id,nId,kongkuashuMap,resultMapT);
				if(!StringTool.isNull(resultMapT))
					resultMap.putAll(resultMapT);

				//统计每一病害的桥梁孔跨数/孔的占全线（所有铁路线）的比例。  表格的第二行
				if(totalBridgeNum > 0 )	{
					Map<String, Object> resultMapT2 = new HashMap<String, Object>();//保留中间结果
					nId = "bili_";
					_getBiliBHBridgeNum(id,nId,resultMapT,totalBridgeNum,resultMapT2);
					if(!StringTool.isNull(resultMapT2))
						resultMap.putAll(resultMapT2);	
				}

				//统计同类桥梁比例 。 kongkuashuMap 同类有病的桥综述；bridgeBykuajingMap：同类的全部桥总数
				nId = "Ltonglei_" + (out+1) + "_";
				resultMapT = new HashMap<String, Object>();
				_getTongleiBiliBHBridgeNum(id,nId,kongkuashuMap,bridgeBykuajingMap,resultMapT);
				//统计合计
				nId = "Ltonglei_" + (out+1) + "_heji_";
				_getTongleiBiliHejiBHBridgeNum(nId,totalBridgeNum,resultMapT);
				if(!StringTool.isNull(resultMapT))
					resultMap.putAll(resultMapT);

				//统计全线桥梁比例 
				if(totalBridgeNum > 0 )	{
					nId = "Lquanxian_" + (out+1) + "_";
					//id = "Jnum_" + (out+1) + "_";
					resultMapT = new HashMap<String, Object>();
					_getQuanxianBiliBHBridgeNum(nId,kongkuashuMap,totalBridgeNum,resultMapT);
					//统计合计
					nId = "Lquanxian_" + (out+1) + "_heji_";
					_getTongleiBiliHejiBHBridgeNum(nId,totalBridgeNum,resultMapT);
					if(!StringTool.isNull(resultMapT))
						resultMap.putAll(resultMapT);
				}

			}//if_1
		}//For 3Y
		if(resultMap  != null && resultMap .size() > 0)
			return resultMap;
		else
			return "";
	}
}
/*返回页面的数据格式如下：
[{"name":"张飞1","year":2013,"list":[100,200,300,400],"color":"#FF0022"},
 {"name":"张飞2","year":2014,"list":[100,200,300,400],"color":"#FF0022"}
]
https://www.hcharts.cn/demo/highcharts/column-basic
 */
/**
 *  1.从桥梁劣化等级评价记录表(总)tbridgebasicinfojudge中检索
 *  qiaohao、shuoshugongdui、qiaoliangjudge、pingdingdate等信息
 */
/**
 *  2.从主梁tgirder中检索
 *  qiaohao、
 *  kuajing（跨径(m)）、
 *  lianggao（1普通高度、2中高度、3低高度、4超低高度）
 *  material（2钢筋混凝土、7钢混组合、4 预应力钢筋混凝土; 6普通桥梁钢、5低合金钢、3型钢混凝土、8钢管混凝、1钢梁）
 *  leixing（2桁梁、3T梁、4箱型梁;）
 *  shoulixingshi（1简支、2连续、12 框构）  
 *  
 *  //获取一座梁的所需数据
	public String getKongshu(String qiaohao)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		String items1 = "kongkuashu";
		String where1 = "qiaohao ='" + qiaohao + "'";
		map = systemService.queryOne("tbridgebasicinfo",items1,where1);

		if ( map != null)
			return (String)map.get("kongkuashu");
		else
			 return ""+ 0;
	}
 */


/**tbridgebasicinfo qiaohao kongkuashu孔跨数,
 */