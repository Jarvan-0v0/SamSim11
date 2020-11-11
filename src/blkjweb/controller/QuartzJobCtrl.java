package blkjweb.controller;

import java.util.*;

import javax.annotation.Resource;

import org.blkj.utils.PageTool;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import blkjweb.job.QuartzJobManager;
import blkjweb.service.SystemServiceImp;

@Controller
public class QuartzJobCtrl extends AbstractBase
{
	@Resource
	private SystemServiceImp systemService;
	
	//新增任务
		@RequestMapping(value="/QuartzJob_addPlan")  
		@ResponseBody 
		public Object addDbBackup()
		{
			String code = "2";
			String message = "成功保存记录!";
			
			PageTool pd = new PageTool();
			pd = this.getPageData();
			String tableName = "jobplan";
			
			Map<String, Object> mapRecord = new HashMap<String, Object>();
			mapRecord = pd.getMap();
	        
			//int result = systemService.insert(tableName, mapRecord);
			//返回其系统自动产生的id值。 mapRecord中保留系统自动产生的id
			//doInsertGenIndex 系统自动产生id,并自动写入mapRecord,
			int result = systemService.doInsertGenIndex(tableName, mapRecord);
			if (result <= 0){//不成功
				code = "-1";
				message = "保存记录失败!";
			}
			else{//处理新任务
				new QuartzJobManager().addJob(mapRecord);
			}
			return message(code,message);
		}
	
	//删除计划
	@RequestMapping(value="/QuartzJob_deletePlan")  
	@ResponseBody //作用是将返回的对象作为响应，发送给页面
	public Object delete() 
	{
		String code = "2";
		String message = "成功删除!";
		
		PageTool pd = new PageTool();
		pd = this.getPageData();

		String id = pd.getString("KeyValue");
		String tableName = "jobplan";

		String where = "id='" + id +"'";
		int result = systemService.delete(tableName, where);

		if (result <= 0){//不成功
			code = "-1";
			message = "删除失败!";
		}
		else{//删除
			new QuartzJobManager().deleteJob(id);
		}
		
		return message(code,message);
	}
	
	
   
	
	
}

