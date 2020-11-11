package blkjweb.controller;

import java.util.*;
import javax.annotation.Resource;
import org.blkj.utils.ConvertTool;
import org.blkj.utils.PageTool;
import org.blkj.utils.StringTool;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import blkjweb.model.PDFTreeJson;
import blkjweb.model.video;
import blkjweb.service.SystemServiceImp;

@Controller
public class DocCenterCtrl extends AbstractBase
{
	@Resource
	private SystemServiceImp systemService; 

	//除系统文档外，其他文档的描述信息都存在此库表中。文档本身保存在相应的文件夹下
	private final String tableName = "folder";

	//初始化表格
	@RequestMapping(value="/doc_download_init", produces="application/json; charset=utf-8")  
	@ResponseBody //作用是将返回的对象作为响应，发送给页面
	public Object doc_center_init() 
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();

		//获取对应文件所在文件夹名称
		String folderId = pd.getString("FolderId");
		if (StringTool.isNullEmpty(folderId))
			return "";

		String fileType = pd.getString("FileType");
		String where  = "filetype='" + fileType + "'"; 

		if(folderId.startsWith("R1"))
			where  = "filetype < '50' "; 
		else if(folderId.startsWith("R2"))
			where  = "filetype > '50' ";

		where = where + " AND ispublic='1'";

		//获取prmNames传递的参数
		int pageSize = pd.getInt("rows");//取得每页要显示的行数. 是jqgrid自身的参数
		int page = pd.getInt("page"); //取得当前页数,这是jqgrid自身的参数   
		String sort = pd.getString("sidx");//排序列
		String order = pd.getString("sord");//排序方式
		int index = (page - 1) * pageSize; // 开始记录数   

		long totalRecord = systemService.queryCount(tableName,where); //数据库中满足条件的总记录数
		long  totalPage = totalRecord % pageSize == 0 ? totalRecord/pageSize : totalRecord/pageSize + 1; // 计算总页数

		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		lists= systemService.query(tableName,sort, order, where, new Object[] {index, pageSize});

		String rowList = ConvertTool.list2json((ArrayList<?>)lists);
		String json = "{\"page\":" + page +"," +
				" \"total\":" + totalPage +"," + 
				" \"records\":" + totalRecord +"," + 
				" \"rows\":" + rowList +"}";
		return json;
	}	

	//页面中左边的菜单PDF文件
	@RequestMapping(value="/PDFTreeList", produces="application/json; charset=utf-8")  
	@ResponseBody 
	public Object PDFTreeList() 
	{
		String where = "swfflag='1'";
		List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
		lists= systemService.query(tableName,"id,filename", where);
		List<PDFTreeJson> list = new ArrayList<>();

		if(!StringTool.isNull(lists)){
			Map<String, Object> map = new HashMap<String, Object>(); //LinkedHashMap
			for(int i= 0; i<lists.size(); i++) {
				map = lists.get(i);
				Iterator<Map.Entry<String, Object>> entries = map.entrySet().iterator();
				PDFTreeJson item = new PDFTreeJson();
				while(entries.hasNext() ){  
					Map.Entry<String, Object> entry = (Map.Entry<String, Object>)entries.next(); 
					String key = (String)entry.getKey();
					String value = (String)entry.getValue();
					if(key.equalsIgnoreCase("id"))
						item.setId(value);
					else if(key.equalsIgnoreCase("filename")) 
						item.setText(value);
					item.setImg("../scripts/images/Icon16/file_extension_pdf.png");
				}
				list.add(item);
			}
		}
					
		//数据转换
		String json = ConvertTool.list2json((ArrayList<?>)list);
		return json; 
		/*JSON的数据格式 
		   [ {"id":"1a.xlsx.pdf","text":"1a.xlsx.pdf","img":"./scripts/images/Icon16/file_extension_pdf.png"},
		   {} ]
		 */
	}

















	@RequestMapping(value="/append2")  
	@ResponseBody //作用是将返回的对象作为响应，发送给页面
	public Object append2() 
	{
		int result = 2;
		String code = "2";
		String message = "成功保存记录!";
		if (result <= 0){//不成功
			code = "-1";
			message = "保存记录失败!";
		}
		return message(code,message);
	}

	@RequestMapping(value="/getVideo")  
	@ResponseBody //作用是将返回的对象作为响应，发送给页面
	public Object getVideo() 
	{
		PageTool pd = new PageTool();
		pd = this.getPageData();
		String tableName = pd.getString("key");

		Map<String, Object> mapRecord = new HashMap<String, Object>();
		mapRecord = pd.getMap();


		List<Object> lists = new ArrayList<Object>();
		video obj =  new video();
		obj.setSrc("http://vjs.zencdn.net/v/oceans.mp4");
		obj.setType("video/mp4");
		lists.add(obj);

		obj =  new video();
		obj.setSrc("http://vjs.zencdn.net/v/oceans.webm");
		obj.setType("video/webm");
		lists.add(obj);

		obj =  new video();
		obj.setSrc("http://vjs.zencdn.net/v/oceans.ogv");
		obj.setType("video/ogg");
		lists.add(obj);
		//return lists;
		//播放过程中需要显示问题的时刻点。注意时间顺序
		List<Object> timeLast = new ArrayList<Object>();
		timeLast.add(30);//单位是秒
		timeLast.add(20);
		timeLast.add(3);

		Map<String,List<Object>> map = new HashMap<String,List<Object>>();
		map.put("src", lists);
		map.put("time", timeLast);

		return map;


	}









}
